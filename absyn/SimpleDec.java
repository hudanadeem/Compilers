package absyn;

public class SimpleDec extends VarDec {
	public int pos;
	public String name;

	public SimpleDec( int pos, NameTy typ, String name ) {
		this.pos = pos;
		this.typ = typ;
		this.name = name;
	}

	public void setNestLevel(int level) {
		this.nestLevel = level;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}