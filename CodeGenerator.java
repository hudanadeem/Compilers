import absyn.*;
import java.util.HashMap;
import java.util.Map;



public class CodeGenerator implements AbsynVisitor {
    /* Register constants */
    public static final int pc = 7;	 			// register number of program counter
    public static final int gp = 6;				// register number of global frame pointer
    public static final int fp = 5;				// register number of current stack frame pointer
    public static final int ac = 0;				// register 0
    public static final int ac1 = 1;			// register 1

    /* Tracking variables */
    int mainEntry = 0;							// absolute address for main
    int inputEntry, outputEntry;			// to access input and output later, with calls jump to the starts of these functions
    int globalOffset = 0;					// next available loc after global frame
    int emitLoc = 0;  						// current instruction location
    int highEmitLoc = 0;					// next available space for new instructions
    int ofpFO = 0;							// frame pointer of the caller
    int retFO = 0;							// return address to go back to after callee is executed
    int initFO = 0;							// initial frame offset of function stack frame

    boolean global = true;					// keeps track of whether we are in global or not

    private Map<String, Integer> functionAddresses = new HashMap<>();

    public CodeGenerator( String fname ) {
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: " + fname);
        functionAddresses.put("input", inputEntry); // Assuming inputEntry is correctly set
        functionAddresses.put("output", outputEntry); // Assuming outputEntry is correctly set
    }

    /*** Wrapper for post-order traversal ***/
    public void visit(Absyn trees) {

        // Generate the prelude
        emitComment("Standard prelude:");

        emitRM(" LD", gp, 0, ac, "load gp with maxaddress");
        emitRM("LDA", fp, 0, gp, "copy gp to fp");
        emitRM(" ST", ac, 0, ac, "clear location 0");

        int savedLoc = emitSkip(1);

        // Generate the I/O routines
        emitComment("Jump around i/o routines here");

        inputRoutine();
        outputRoutine();

        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around i/o code");

        emitComment("End of standard prelude.");

        emitBackup(savedLoc2);

        // Make a request to the visit method for DecList
        trees.accept(this, -1, false);

        // Generate finale
        emitRM(" ST", fp, globalOffset+ofpFO, fp, "push ofp");
        emitRM("LDA", fp, globalOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRM_Abs("LDA", pc, mainEntry, "jump to main loc");
        emitRM(" LD", fp, ofpFO, fp, "pop frame");

        emitComment("End of execution.");
        emitRO("HALT", 0, 0, 0, "");

    }

    /****** Visitor Methods ******/

    public void visit( NameTy nameTy, int frameOffset, boolean isAddr ) {  // frameOffset is the stack pointer (sp) -- offset within the related stackframe or memory access
        
    }

    public void visit( SimpleVar simpleVar, int frameOffset, boolean isAddr ) {
        emitComment("-> id");
        emitComment("looking up id: " + simpleVar.name);

        if (isAddr) {
            // Compute address of simpleVar and save it to location frameOffset
            emitRM("LDA", ac, frameOffset, fp, "load id address");
            emitComment("<- id");
            emitRM(" ST", ac, frameOffset-2, fp, "op: push left");
        } else {
            // Save the value of simpleVar to location frameOffset
            emitRM(" LD", ac, frameOffset, fp, "load id value");
            emitComment("<- id");
            emitRM(" ST", ac, frameOffset-2, fp, "op: push left");
        }
    }

    public void visit( IndexVar indexVar, int frameOffset, boolean isAddr ) {
        // naturally compute the address of an indexed variable and that value can be saved
        // directly to memory location when used in the left-hand side of assignexp
        indexVar.index.accept( this, frameOffset-1, false );
    }

    public void visit( NilExp exp, int frameOffset, boolean isAddr ) {
        
    }

    public void visit( IntExp exp, int frameOffset, boolean isAddr ) {
        emitComment("-> constant");

        // Save the value of the int to location frameOffset
        emitRM("LDC", ac, exp.value, ac, "load const");
        emitComment("<- constant");
        emitRM(" ST", ac, frameOffset, fp, "op: push left");
    }

    public void visit( BoolExp exp, int frameOffset, boolean isAddr ) {

    }

    public void visit( VarExp exp, int frameOffset, boolean isAddr ) {
        exp.variable.accept( this, frameOffset, isAddr );
    }

    public void visit(CallExp exp, int frameOffset, boolean isAddr) {
        emitComment("-> call of function: " + exp.func);

        // Handle argument passing here (if your language design involves passing arguments)

        // Push frame pointer and set up new frame pointer
        emitRM("ST", fp, frameOffset + ofpFO, fp, "push ofp");
        emitRM("LDA", fp, frameOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");

        // Jump to function address
        Integer functionAddress = functionAddresses.get(exp.func);
        if (functionAddress != null) {
            emitRM_Abs("LDA", pc, functionAddress, "jump to function: " + exp.func);
        } else {
            emitComment("ERROR: Function " + exp.func + " not found.");
        }

        // Restore frame pointer after returning from the function call
        emitRM("LD", fp, ofpFO, fp, "pop frame");
        emitComment("<- call");
    }



    public void visit( OpExp exp, int frameOffset, boolean isAddr ) {
        emitComment("-> op");

        if (exp.left != null) {
            exp.left.accept(this, frameOffset-1, false);
        }
        exp.right.accept(this, frameOffset-2, false);

        // Do the operation and save the result in location frameOffset
        emitRM(" LD", ac, frameOffset-1, fp, "op: load left");
        emitRM(" LD", ac1, frameOffset-2, fp, "op: load left");

        if (exp.op == OpExp.PLUS) {
            emitRO("ADD", ac, ac, ac1, "op +");
        }
        else if (exp.op == OpExp.MINUS) {
            emitRO("SUB", ac, ac, ac1, "op -");
        }
        else if (exp.op == OpExp.TIMES) {
            emitRO("MUL", ac, ac, ac1, "op *");
        }
        else if (exp.op == OpExp.DIV) {
            emitRO("DIV", ac, ac, ac1, "op /");
        }

        emitComment("<- op");
        emitRM(" ST", ac, frameOffset, fp, "op: push left");

        // register "0" is used heavily for the result, which needs to be saved to a memory location
        // as soon as possible
    }

    public void visit( AssignExp exp, int frameOffset, boolean isAddr ) {
        exp.lhs.accept(this, frameOffset-1, true);
        exp.rhs.accept(this, frameOffset-2, false);

        // Do the assignment and save the result to location frameOffset
        emitRM(" LD", ac, frameOffset-3, fp, "");
        emitRM(" LD", ac1, frameOffset-4, fp, "");
        emitRM(" ST", ac1, ac, ac, "");
        emitRM(" ST", ac1, frameOffset, fp, "assign: store value");
    }

    public void visit(IfExp exp, int frameOffset, boolean isAddr) {
        emitComment("-> if");

        exp.test.accept(this, frameOffset, false);
        int elseJump = emitSkip(1); // Skip over then block if test is false
        exp.then.accept(this, frameOffset, false);

        int endJump = 0;
        if (!(exp.elseExp instanceof NilExp)) {
            endJump = emitSkip(1); // Skip over else block once then is done
            emitBackup(elseJump);
            emitRM_Abs("JEQ", ac, emitLoc, "Jump to else block");
            emitRestore();

            exp.elseExp.accept(this, frameOffset, false);
            
            emitBackup(endJump);
            emitRM_Abs("LDA", pc, emitLoc, "Jump to end of if block");
            emitRestore();
        } else {
            emitBackup(elseJump);
            emitRM_Abs("JEQ", ac, emitLoc, "Jump to end of if block if false");
            emitRestore();
        }

        emitComment("<- if");
}


    public void visit(WhileExp exp, int frameOffset, boolean isAddr) {
        int startLoc = emitLoc;
        emitComment("-> while: test");
        exp.test.accept(this, frameOffset, false);

        int jumpToEnd = emitSkip(1); 
        emitComment("-> while: body");
        exp.body.accept(this, frameOffset, false);
        emitRM_Abs("LDA", pc, startLoc, "jump back to start of while");

        int endLoc = emitLoc;
        emitBackup(jumpToEnd);
        emitRM_Abs("JEQ", ac, endLoc, "jump to end of while if test is false");
        emitRestore();

        emitComment("<- while");
    }

    public void visit( ReturnExp exp, int frameOffset, boolean isAddr ) {
        if (exp.exp != null ) {
            exp.exp.accept( this, frameOffset, false );
        }
    }

    public void visit(CompoundExp exp, int frameOffset, boolean isAddr) {
        emitComment("-> compound statement");
   
        if (exp.decs != null) {
            exp.decs.accept(this, frameOffset, false);
        }
        
        if (exp.exps != null) {
            exp.exps.accept(this, frameOffset, false);
        }

        emitComment("<- compound statement");
    }


    public void visit(FunctionDec exp, int frameOffset, boolean isAddr) {
        emitComment("processing function: " + exp.func);
        if (exp.func.equals("main")) {
            mainEntry = emitLoc; // Record the start address of the main function
        }

        // Record the function's start address for other functions
        functionAddresses.put(exp.func, emitLoc);

        // Jump around the function body (to be backfilled later)
        int jumpLoc = emitSkip(1);
        exp.body.accept(this, frameOffset, false);
        int afterFunc = emitLoc;

        // Backfill the jump around the function body
        emitBackup(jumpLoc);
        emitRM_Abs("LDA", pc, afterFunc, "jump around function body");
        emitRestore();
    }

    public void visit( SimpleDec exp, int frameOffset, boolean isAddr ) {
        if (global) {
            emitComment("allocating global var: " + exp.name);
            exp.setNestLevel(0);
            exp.setOffset(frameOffset);
        }
        else {
            emitComment("processing local var: "+ exp.name);
            exp.setNestLevel(1);
            exp.setOffset(frameOffset);
        }
    }

    public void visit( ArrayDec exp, int frameOffset, boolean isAddr ) {
        
    }

    public void visit( DecList decList, int frameOffset, boolean isAddr ) {
        while( decList != null ) {
            if (decList.head != null) {
                decList.head.accept( this, frameOffset, false );
            }
            decList = decList.tail;
        }
    }
 
    public void visit( VarDecList varDecList, int frameOffset, boolean isAddr ) {
        while( varDecList != null ) {
            if (varDecList.head != null) {
                varDecList.head.accept( this, frameOffset, false );
                frameOffset++;
            }
            varDecList = varDecList.tail;
        }
    }
 
    public void visit( ExpList expList, int frameOffset, boolean isAddr ) {
        while( expList != null ) {
            if (expList.head != null) {
                expList.head.accept( this, frameOffset, false );
            }
            expList = expList.tail;
        }
    }

    /******* I/O Routines *******/

    void inputRoutine() {
        emitComment("code for input routine");
        
        emitRM(" ST", ac, -1, fp, "store return");
        emitRO(" IN", 0, 0, 0, "input");
        emitRM(" LD", pc, -1, fp, "return to caller");
    }

    void outputRoutine() {
        emitComment("code for output routine");
        
        emitRM(" ST", ac, -1, fp, "store return");
        emitRM(" LD", ac, -2, fp, "load output value");
        emitRO("OUT", 0, 0, 0, "output");
        emitRM("LD", pc, -1, fp, "return to caller");
    }

    /****** Emitting Routines ******/

    /* Generate certain kind of assembly instructions */
    void emitRO( String op, int r, int s, int t, String c ) {
        System.out.print( " "+emitLoc + ":     " + op + "  " + r + ", " + s + ", " + t );
        System.out.println( "\t" + c );
        emitLoc++;
        if ( highEmitLoc < emitLoc ) {
            highEmitLoc = emitLoc;
        }
    }

    /* Generate certain kind of assembly instructions */
    void emitRM( String op, int r, int d, int s, String c ) {
        System.out.print( " "+emitLoc + ":     " + op + "  " + r + "," + d + "(" + s + ")" );
        System.out.println( "\t" + c );
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    /* Jump from location emitLoc to location a */
    void emitRM_Abs( String op, int r, int a, String c ) {
        System.out.print( " "+emitLoc + ":     " + op + "  " + r + ", " + (a-(emitLoc+1)) + "(" + pc + ")" );
        System.out.println( "\t" + c );
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    /* Maintains code space */
    int emitSkip( int distance ) {
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
        return i;
    }

    /* Maintains code space */
    void emitBackup( int loc ) {
        if (loc > highEmitLoc) {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    }

    /* Maintains code space */
    void emitRestore() {
        emitLoc = highEmitLoc;
    }

    /* Generate one line of comment */
    void emitComment( String c ) {
        System.out.println( "* " + c );
    }

    private int getFunctionAddress(String functionName) {
        return functionAddresses.getOrDefault(functionName, -1); // Returns -1 if function not found
    }
}