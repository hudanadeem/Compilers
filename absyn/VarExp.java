package absyn;

public class VarExp extends Exp {
	public int pos;
	public Var variable;
	public String name;

	public VarExp( int pos, Var variable ) {
		this.pos = pos;
		this.variable = variable;
		this.name = variable.name;
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}