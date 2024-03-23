import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {
	
	final static int SPACES = 4;

	private void indent( int level ) {
		for ( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
	}

	public void visit( NameTy nameTy, int level, boolean flag ) {
		indent( level );
		if (nameTy.typ == nameTy.VOID) {
			System.out.println( "TypeDec: void" );
		}
		else if (nameTy.typ == nameTy.INT) {
			System.out.println( "TypeDec: int" );
		}
		else if (nameTy.typ == nameTy.BOOL) {
			System.out.println( "TypeDec: bool" );
		}
	}

	public void visit( SimpleVar simpleVar, int level, boolean flag ) {
		indent( level );
		System.out.println( "SimpleVar: " + simpleVar.name );
	}

	public void visit( IndexVar indexVar, int level, boolean flag ) {
		indent(level);
		System.out.println( "IndexVar: " + indexVar.name );
	}

	public void visit( NilExp exp, int level, boolean flag ) {
		indent( level );
		// System.out.println( "NilExp: " );
	}

	public void visit( IntExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "IntExp: " + exp.value ); 
	}

	public void visit( BoolExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "BoolExp: " + exp.value );
	}

	public void visit( VarExp exp, int level, boolean flag ) {
		exp.variable.accept( this, level, false );
	}

	public void visit( CallExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "CallExp: " + exp.func );
		level++;
		exp.args.accept( this, level, false );
	}

	public void visit( OpExp exp, int level, boolean flag ) {
		indent( level );
		System.out.print( "OpExp:" );
		switch( exp.op ) {
			case OpExp.PLUS:
			  System.out.println( " + " );
			  break;
			case OpExp.MINUS:
			  System.out.println( " - " );
			  break;
			case OpExp.TIMES:
			  System.out.println( " * " );
			  break;
			case OpExp.DIV:
			  System.out.println( " / " );
			  break;
			case OpExp.EQ:
			  System.out.println( " == " );
			  break;
			case OpExp.NE:
			  System.out.println( " != " );
			  break;
			case OpExp.LT:
			  System.out.println( " < " );
			  break;
			case OpExp.LTE:
			  System.out.println( " <= " );
			  break;
			case OpExp.GT:
			  System.out.println( " > " );
			  break;
			case OpExp.GTE:
			  System.out.println( " >= " );
			  break;
			case OpExp.NOT:
			  System.out.println( " ~ " );
			  break;
			case OpExp.AND:
			  System.out.println( " && " );
			  break;
			case OpExp.OR:
			  System.out.println( " || " );
			  break;
			case OpExp.UMINUS:
			  System.out.println( " - " );
			  break;
			default:
			  System.out.println( "Unrecognized operator at line " + exp.row + " and column " + exp.col);
		  }
		  level++;
		  if (exp.left != null)
			 exp.left.accept( this, level, false );
		  if (exp.right != null)
			 exp.right.accept( this, level, false );
	}

	public void visit( AssignExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "AssignExp: " );
		level++;
		exp.lhs.accept( this, level, false );
		exp.rhs.accept( this, level, false );
	}

	public void visit( IfExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "IfExp: " );
		level++;
		exp.test.accept( this, level, false );
		exp.then.accept( this, level, false );
		if (exp.elseExp != null ) {
			exp.elseExp.accept( this, level, false );
		}
	}

	public void visit( WhileExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "WhileExp: " );
		level++;
		exp.test.accept( this, level, false );
		exp.body.accept( this, level, false );
	}

	public void visit( ReturnExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "ReturnExp: " );
		level++;
		if (exp.exp != null ) {
			exp.exp.accept( this, level, false );
		}
	}

	public void visit( CompoundExp exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "CompoundExp: " );
		level++;
		exp.decs.accept( this, level, false );
		exp.exps.accept( this, level, false );
	}

	public void visit( FunctionDec exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "FunctionDec: " + exp.func );
		level++;
		exp.typ.accept( this, level, false );
		exp.params.accept( this, level, false );
		exp.body.accept( this, level, false );
		exp.typ.accept( this, level, false );

	}

	public void visit( SimpleDec exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "SimpleDec: " + exp.name );
		level++;
		exp.typ.accept( this, level, false );
	}

	public void visit( ArrayDec exp, int level, boolean flag ) {
		indent( level );
		System.out.println( "ArrayDec: " + exp.name );
		level++;
		exp.typ.accept( this, level, false );
	}

	public void visit( DecList decList, int level, boolean flag ) {
        while( decList != null ) {
            if (decList.head != null) {
                decList.head.accept( this, level, false );
            }
            decList = decList.tail;
        }
    }
 
    public void visit( VarDecList varDecList, int level, boolean flag ) {
        while( varDecList != null ) {
            if (varDecList.head != null) {
                varDecList.head.accept( this, level, false );
            }
            varDecList = varDecList.tail;
        }
    }
 
    public void visit( ExpList expList, int level, boolean flag ) {
        while( expList != null ) {
            if (expList.head != null) {
                expList.head.accept( this, level, false );
            }
            expList = expList.tail;
		}
    }
}