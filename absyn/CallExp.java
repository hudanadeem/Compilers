package absyn;

public class CallExp extends Exp {
	public int pos;
	public String func;
	public ExpList args;

	public CallExp( int pos, String func, ExpList args ) {
		this.pos = pos;
		this.func = func;
		this.args = args;
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}