package absyn;

public class IfExp extends Exp {
	public int pos;
	public Exp test;
	public Exp then;
	public Exp elseExp;

	public IfExp( int pos, Exp test, Exp then, Exp elseExp ) {
		this.pos = pos;
		this.test = test;
		this.then = then;
		this.elseExp = elseExp;
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}