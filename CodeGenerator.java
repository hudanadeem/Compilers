import absyn.*;

public class CodeGenerator implements AbsynVisitor {
	int mainEntry, globalOffset, pc;
	int emitLoc = 0;
	int highEmitLoc = 0;
	String codeStr = "";

	// add constructor and all emitting routines
	public CodeGenerator() {
		emitComment("C-Minus Compilation to TM Code");
		emitComment("File: ");
	}

	/*** Wrapper for post-order traversal ***/
	public void visit(Absyn trees) {

		// generate the prelude
		emitComment("Standard prelude:");

		// fix this with variables
		emitRM("LD", 6, 0, 0, "load gp with maxaddress");
		emitRM("LDA", 5, 0, 6, "copy gp to fp");
		emitRM("ST", 0, 0, 0, "clear location 0");

		// generate the i/o routines
		emitComment("Jump around i/o routines here");
		inputRoutine();
		outputRoutine();
		emitRM("LDA", 7, 7, 7, "jump around i/o code");

		emitComment("End of standard prelude.");

		// make a request to the visit method for DecList
		trees.accept(this, 0, false);

		// generate finale

	}

	/****** Visitor Methods ******/

	public void visit( NameTy nameTy, int level, boolean flag ) {
		
	}

	public void visit( SimpleVar simpleVar, int level, boolean flag ) {
		
	}

	public void visit( IndexVar indexVar, int level, boolean flag ) {
		
	}

	public void visit( NilExp exp, int level, boolean flag ) {
		
	}

	public void visit( IntExp exp, inxst level, boolean flag ) {
		
	}

	public void visit( BoolExp exp, int level, boolean flag ) {
		
	}

	public void visit( VarExp exp, int level, boolean flag ) {
		
	}

	public void visit( CallExp exp, int level, boolean flag ) {
		
	}

	public void visit( OpExp exp, int level, boolean flag ) {
		exp.left.accept(this, level, flag);
		exp.right.accept(this, level, flag);
		// codeStr += newtemp(exp) + "=" + newtemp(exp.left) + "+" + newtemp(exp.right);
		// emitCode(codeStr);
	}

	public void visit( AssignExp exp, int level, boolean flag ) {
		exp.rhs.accept(this, level, flag);
		// codeStr += newtemp(exp.lhs) + "=" + newtemp(exp.rhs);
		// emitCode(codeStr);
	}

	public void visit( IfExp exp, int level, boolean flag ) {
		
	}

	public void visit( WhileExp exp, int level, boolean flag ) {
		
	}

	public void visit( ReturnExp exp, int level, boolean flag ) {
		
	}

	public void visit( CompoundExp exp, int level, boolean flag ) {
		
	}

	public void visit( FunctionDec exp, int level, boolean flag ) {
		
	}

	public void visit( SimpleDec exp, int level, boolean flag ) {
		
	}

	public void visit( ArrayDec exp, int level, boolean flag ) {
		
	}

	public void visit( DecList decList, int level, boolean flag ) {
        
    }
 
    public void visit( VarDecList varDecList, int level, boolean flag ) {
        
    }
 
    public void visit( ExpList expList, int level, boolean flag ) {
        
    }

	/******* I/O Routines *******/

	void inputRoutine( void ) {
		emitComment("code for input routine");
		// change to variables
		emitRM("ST", 0, -1, 5, "store return");
		emitRO("IN", 0, 0, 0, "input");
		emitRM("LD", 7, -1, 5, "return to caller");
	}

	void outputRoutine(void) {
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
		System.out.print\n( "\t" + c );
		emitLoc++;
		if (highEmitLoc < emitLoc) {
			highEmitLoc = emitLoc;
		}
	}

	/* Generate certain kind of assembly instructions */
	void emitRm_Abs( String op, int r, int a, String c ) {
		System.out.print( emitLoc + ": " + op + " " + r + ", " + a-(emitLoc+1) + "(" pc + ")" );
		System.out.print\n( "\t" + c );
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
	void emitRestore( void ) {
		emitLoc = highEmitLoc;
	}

	/* Generate one line of comment */
	void emitComment( String c ) {
		System.out.println( "*" + c );
	}
}