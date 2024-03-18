package absyn;

public class FunctionDec extends Dec {
	public int pos;
<<<<<<< HEAD
	//public NameTy result;
=======
>>>>>>> ella-branch
	public String func;
	public VarDecList params;
	public Exp body;

	public FunctionDec( int pos, NameTy result, String func, VarDecList params, Exp body ) {
		this.pos = pos;
		this.typ = result;
		this.func = func;
		this.params = params;
		this.body = body;
	}

	public void accept( AbsynVisitor visitor, int level ) {
		visitor.visit( this, level );
	}
}