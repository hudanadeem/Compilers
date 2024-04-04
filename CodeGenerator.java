import absyn.*;
import java.util.HashMap;

public class CodeGenerator implements AbsynVisitor {
    /* Register constants */
    public static final int pc = 7;	 			// register number of program counter
    public static final int gp = 6;				// register number of global frame pointer
    public static final int fp = 5;				// register number of current stack frame pointer
    public static final int ac = 0;				// register 0
    public static final int ac1 = 1;			// register 1

    /* Tracking variables */
    int mainEntry = 0;						// absolute address for main
    int inputEntry, outputEntry;			// to access input and output later, with calls jump to the starts of these functions
    int globalOffset = 0;					// next available loc after global frame
    int emitLoc = 0;  						// current instruction location
    int highEmitLoc = 0;					// next available space for new instructions
    int ofpFO = 0;							// frame pointer of the caller
    int retFO = -1;							// return address to go back to after callee is executed
    int initFO = -2;						// initial frame offset of function stack frame
    int decOffset = 0;

    boolean global = true;					    // keeps track of whether we are in global or not
    private HashMap<String, VarDec> varTable;   // keeps track of local declared variables
    private HashMap<String, Integer> funTable;  // keeps track of function locations

    public CodeGenerator( String fname ) {
        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: " + fname);

        varTable = new HashMap<String, VarDec>();
        funTable = new HashMap<String, Integer>();

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

        inputEntry = emitSkip(0);
        insertFun("input", inputEntry);
        inputRoutine();

        outputEntry = emitSkip(0);
        insertFun("output", outputEntry);
        outputRoutine();

        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "jump around i/o code");

        emitComment("End of standard prelude.");

        emitBackup(savedLoc2);

        // Make a request to the visit method for DecList
        trees.accept(this, initFO, false);

        // Generate finale
        emitRM(" ST", fp, globalOffset+ofpFO, fp, "push ofp");
        emitRM("LDA", fp, globalOffset, fp, "push frame");
        emitRM("LDA", ac, retFO, pc, "load ac with ret ptr");
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
            emitRM("LDA", ac, lookupVar(simpleVar.name), fp, "load id address");
            emitComment("<- id");
            emitRM(" ST", ac, frameOffset, fp, "op: push left");
        } else {
            // Save the value of simpleVar to location frameOffset
            emitRM(" LD", ac, lookupVar(simpleVar.name), fp, "load id value");
            emitComment("<- id");
            emitRM(" ST", ac, frameOffset, fp, "op: push left");
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

    public void visit( CallExp exp, int frameOffset, boolean isAddr ) {
        emitComment("-> call of function: " + exp.func);
        exp.args.accept( this, frameOffset, false );

        // code to compute first arg
        // ST ac, frameOffset+initFO(fp)
        // code to compute second arg
        // ST ac, frameOffset+initFO-1(fp)

        emitRM(" ST", fp, frameOffset+ofpFO, fp, "push ofp");
        emitRM("LDA", fp, frameOffset, fp, "push frame");
        emitRM("LDA", ac, 1, pc, "load ac with ret ptr");
        emitRM_Abs("LDA", pc, lookupFun(exp.func), "jump to fun loc");
        emitRM(" LD", fp, ofpFO, fp, "pop frame");

        emitComment("<- call");
    }

    public void visit( OpExp exp, int frameOffset, boolean isAddr ) {
        emitComment("-> op");

        if (exp.left != null) {
            exp.left.accept(this, frameOffset-1, isAddr);
        }
        exp.right.accept(this, frameOffset-2, isAddr);

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
        else if (exp.op == OpExp.GT) {
            emitRO("JGT", ac, 2, pc, "br if true");
        }
        else if (exp.op == OpExp.GTE) {
            emitRO("JGE", ac, 2, pc, "br if true");
        }
        else if (exp.op == OpExp.LT) {
            emitRO("JLT", ac, 2, pc, "br if true");
        }
        else if (exp.op == OpExp.LTE) {
            emitRO("JLE", ac, 2, pc, "br if true");
        }
        else if (exp.op == OpExp.NE) {
            emitRO("JNE", ac, 2, pc, "br if true");
        }

        emitComment("<- op");
        emitRM(" ST", ac, frameOffset, fp, "op: push left");
    }

    public void visit( AssignExp exp, int frameOffset, boolean isAddr ) {
        exp.lhs.accept(this, frameOffset, true);
        exp.rhs.accept(this, frameOffset-1, isAddr);

        // Do the assignment and save the result to location frameOffset
        emitRM(" LD", ac1, frameOffset, fp, "load left");
        // emitRM(" LD", ac1, frameOffset-4, fp, "");
        // emitRM(" ST", ac1, ac, ac, "");
        emitRM(" ST", ac, ac, ac1, "assign: store value");
    }

    public void visit( IfExp exp, int frameOffset, boolean isAddr ) {
        exp.test.accept( this, frameOffset, isAddr );
        exp.then.accept( this, frameOffset, isAddr );

        if (!(exp.elseExp instanceof NilExp)) {
              exp.elseExp.accept( this, frameOffset, isAddr );
        }
    }

    public void visit( WhileExp exp, int frameOffset, boolean isAddr ) {
        emitComment("while: jump after body comes back here");

        int saveLoc = emitSkip(0);
        exp.test.accept( this, frameOffset, false );

        int saveLoc2 = emitSkip(1);
        exp.body.accept( this, frameOffset, false );

        emitRM_Abs("LDA", pc, saveLoc, "while: jump back to test");

        int saveLoc3 = emitSkip(0);

        emitBackup(saveLoc2);
        emitRM_Abs("JEQ", ac, saveLoc3, "while: jump to end");
        emitRestore();

        emitComment("<- while");
    }

    public void visit( ReturnExp exp, int frameOffset, boolean isAddr ) {
        if (exp.exp != null ) {
            exp.exp.accept( this, frameOffset, false );
        }
    }

    public void visit( CompoundExp exp, int frameOffset, boolean isAddr ) {
        emitComment("-> compound statement");
        exp.decs.accept( this, frameOffset, false );
        frameOffset = frameOffset - decOffset;
        exp.exps.accept( this, frameOffset, false );
        emitComment("<- compound statement");
    }

    public void visit( FunctionDec exp, int frameOffset, boolean isAddr ) {
        // Leave global scope 
        global = false;
        emitComment("processing function: " + exp.func);

        if (exp.func.equals("main")) {
            mainEntry = emitSkip(0);
        }

        insertFun(exp.func, emitSkip(0));

        exp.typ.accept( this, frameOffset, false );

        emitComment("jump around function body here");
        int savedLoc = emitSkip(1);

        emitRM(" ST", ac, retFO, fp, "store return");

        exp.body.accept( this, frameOffset, false );

        // exp.params.accept( this, frameOffset, false );

        // Re-enter global scope
        decOffset = 0;
        global = true;

        emitRM(" LD", pc, retFO, fp, "return to caller");

        int savedLoc2 = emitSkip(0);
        emitBackup(savedLoc);
        emitRM("LDA", pc, savedLoc2, pc, "jump around fn body");

        emitComment("<- funcdecl");

        emitBackup(savedLoc2);
    }

    public void visit( SimpleDec exp, int frameOffset, boolean isAddr ) {
        if (global) {
            emitComment("allocating global var: " + exp.name);
            globalOffset--;
            exp.setNestLevel(0);
            exp.setOffset(globalOffset);
        }
        else {
            emitComment("processing local var: "+ exp.name);
            exp.setNestLevel(1);
            exp.setOffset(frameOffset);
            insertVar(exp.name, exp);
            decOffset++;
        }
    }

    public void visit( ArrayDec exp, int frameOffset, boolean isAddr ) {
        if (global) {
            emitComment("allocating global var: " + exp.name);
            globalOffset = globalOffset - (exp.size + 1);
            exp.setNestLevel(0);
            exp.setOffset(globalOffset);
        }
        else {
            emitComment("processing local var: "+ exp.name);
            exp.setNestLevel(1);
            exp.setOffset(frameOffset);
            insertVar(exp.name, exp);
            decOffset++;
        }
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
                frameOffset--;
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
        inputEntry = emitLoc;
        emitComment("code for input routine");
        
        emitRM(" ST", ac, retFO, fp, "store return");
        emitRO(" IN", ac, ac, ac, "input");
        emitRM(" LD", pc, retFO, fp, "return to caller");
    }

    void outputRoutine() {
        outputEntry = emitLoc;
        emitComment("code for output routine");
        
        emitRM(" ST", ac, retFO, fp, "store return");
        emitRM(" LD", ac, initFO, fp, "load output value");
        emitRO("OUT", ac, ac, ac, "output");
        emitRM("LD", pc, retFO, fp, "return to caller");
    }

    /****** Emitting Routines ******/

    /* Generate certain kind of assembly instructions */
    void emitRO( String op, int r, int s, int t, String c ) {
        System.out.print( emitLoc + ":     " + op + "  " + r + ", " + s + ", " + t );
        System.out.println( "\t" + c );
        emitLoc++;
        if ( highEmitLoc < emitLoc ) {
            highEmitLoc = emitLoc;
        }
    }

    /* Generate certain kind of assembly instructions */
    void emitRM( String op, int r, int d, int s, String c ) {
        System.out.print( emitLoc + ":     " + op + "  " + r + ", " + d + "(" + s + ")" );
        System.out.println( "\t" + c );
        emitLoc++;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    /* Jump from location emitLoc to location a */
    void emitRM_Abs( String op, int r, int a, String c ) {
        System.out.print( emitLoc + ":     " + op + "  " + r + ", " + (a-(emitLoc+1)) + "(" + pc + ")" );
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

    /**** Helper Methods ****/

    /* Looks up location of variable with identifier key */
    private int lookupVar(String key) {
        if (varTable.containsKey(key)) {
            VarDec dec = varTable.get(key);
            return dec.offset;
        } else {
            return -1;
        }
    }

    /* Insert specified variable at specified key in table */
    private void insertVar(String key, VarDec dec) {

        // change location if already existss
        if (varTable.containsKey(key)) {
            VarDec newDec = varTable.get(key);
            newDec.offset = dec.offset;
        }
        else {
            varTable.put(key, dec);
        }
    }

    /* Looks up location of function with identifier key */
    private int lookupFun(String key) {
        if (funTable.containsKey(key)) {
            int loc = funTable.get(key);
            return loc;
        } else {
            return -1;
        }
    }

    /* Insert specified function at specified key in table */
    private void insertFun(String key, int loc) {

        // change location if already existss
        if (funTable.containsKey(key)) {
            int newLoc = funTable.get(key);
            newLoc = loc;
        }
        else {
            funTable.put(key, loc);
        }
    }
}