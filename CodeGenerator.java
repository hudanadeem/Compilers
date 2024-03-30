import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	int mainEntry;							// keeps track if main entered
	int inputEntry, outputEntry;			// to access input and output later, with calls jump to the starts of these functions
	int globalOffset;						// 
	int pc = 0;	 							// address of next instruction
	// int	gp;									// global frame pointer
	// int fp;									// current stack frame pointer
	int emitLoc = 0;  						// 
	int highEmitLoc = 0;					// 
	// String codeStr = "";					// 

	// add constructor and all emitting routines
	public CodeGenerator( String fname ) {
		emitComment("C-Minus Compilation to TM Code");
		emitComment("File: " + fname);
	}

	/*** Wrapper for post-order traversal ***/
	public void visit(Absyn trees) {

		// generate the prelude
		emitComment("Standard prelude:");

		// fix this with variables
		emitRM("LD", 6, 0, 0, "load gp with maxaddress");
		emitRM("LDA", 5, 0, 6, "copy gp to fp");
		emitRM("ST", 0, 0, 0, "clear location 0");

		int savedLoc = emitSkip(1);

		// generate the i/o routines
		emitComment("Jump around i/o routines here");
		int savedLoc2 = emitSkip(0);
		emitBackup(savedLoc);
		emitRm_Abs("LDA", pc, savedLoc2, "");

		inputRoutine();
		outputRoutine();
		emitRM("LDA", 7, 7, 7, "jump around i/o code");

		emitComment("End of standard prelude.");

		// make a request to the visit method for DecList
		trees.accept(this, 0, false);

		// generate finale

	}

	/****** Visitor Methods ******/

	public void visit( NameTy nameTy, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( SimpleVar simpleVar, int frameOffset, boolean isAddr ) {
		if (isAddr) {
			// Compute address of simpleVar and save it to location frameOffset
			// e.g. 13: LDA 0, -2(5) and 14: ST 0, -4(5)
		} else {
			// Save the value of simpleVar to location frameOffset
			// e.g. 15: LD 0, -2(5) and 16: ST 0, -6(5)
		}
	}

	public void visit( IndexVar indexVar, int frameOffset, boolean isAddr ) {
		// naturally compute the address of an indexed variable and that value can be saved
		// directly to memory location when used in the left-hand side of assignexp
	}

	public void visit( NilExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( IntExp exp, int frameOffset, boolean isAddr ) {
		// save the value of the int to location frameOffset
		// E.g. 17: LDC 0, 3(0) and 18: ST 0, -7(5)
	}

	public void visit( BoolExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( VarExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( CallExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( OpExp exp, int frameOffset, boolean isAddr ) {
		exp.left.accept(this, frameOffset-1, false);
		exp.right.accept(this, frameOffset-2, false);
		// do the operation (e.g. addition) and save the result in location frameOffset
		// e.g. 19: LD 0, -6(5) and 20: LD 1, -7(5) 21: ADD 0, 0, 1 and 22: ST 0, -5(5)

		// register "0" is used heavily for the result, which needs to be saved to a memory location
		// as soon as possible
	}

	public void visit( AssignExp exp, int frameOffset, boolean isAddr ) {
		exp.lhs.accept(this, frameOffset-1, true)
		exp.rhs.accept(this, frameOffset-2, false);
		// do the assignment and save the result to location frameOffset
		// e.g. 23: LD 0, -4(5) and 24: LD 1, -5(5) and 25: ST 1, 0(0) 26: ST 1, -3(5)
	}

	public void visit( IfExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( WhileExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( ReturnExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( CompoundExp exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( FunctionDec exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( SimpleDec exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( ArrayDec exp, int frameOffset, boolean isAddr ) {
		
	}

	public void visit( DecList decList, int frameOffset, boolean isAddr ) {
        
    }
 
    public void visit( VarDecList varDecList, int frameOffset, boolean isAddr ) {
        
    }
 
    public void visit( ExpList expList, int frameOffset, boolean isAddr ) {
        
    }

	/******* I/O Routines *******/

	void inputRoutine() {
		emitComment("code for input routine");
		// change to variables
		emitRM("ST", 0, -1, 5, "store return");
		emitRO("IN", 0, 0, 0, "input");
		emitRM("LD", 7, -1, 5, "return to caller");
	}

	void outputRoutine() {
		emitComment("code for output routine");
		// change to variables
		emitRM("ST", 0, -1, 5, "store return");
		emitRM("LD", 0, -2, 5, "load output value");
		emitRO("OUT", 0, 0, 0, "output");
		emitRM("LD", 7, -1, 5, "return to caller");
	}

	/****** Emitting Routines ******/

	/* Generate certain kind of assembly instructions */
	void emitRO( String op, int r, int s, int t, String c ) {
		System.out.print( emitLoc + ": " + op + " " + r + ", " + s + ", " + t );
		System.out.println( "\t" + c );
		emitLoc++;
		if ( highEmitLoc < emitLoc ) {
			highEmitLoc = emitLoc;
		}
	}

	/* Generate certain kind of assembly instructions */
	void emitRM( String op, int r, int d, int s, String c ) {
		System.out.print( emitLoc + ": " + op + " " + r + ", " + d + "(" + s + ")" );
		System.out.println( "\t" + c );
		emitLoc++;
		if (highEmitLoc < emitLoc) {
			highEmitLoc = emitLoc;
		}
	}

	/* Generate certain kind of assembly instructions */
	void emitRm_Abs( String op, int r, int a, String c ) {
		System.out.print( emitLoc + ": " + op + " " + r + ", " + (a-(emitLoc+1)) + "(" + pc + ")" );
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
}