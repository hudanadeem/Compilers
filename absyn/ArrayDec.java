package absyn;
import java.lang.Integer;

public class ArrayDec extends VarDec {
	public int pos;
	public String name;
	public int size;

	public ArrayDec( int pos, NameTy typ, String name, String size ) {
		this.pos = pos;
		this.typ = typ;
		this.name = name;
		if (size != null) {
			this.size = Integer.parseInt(size);
		}
	}

	public void setNestLevel(int level) {
		this.nestLevel = level;
	}

	public void setOffset(int offset) {
		this.offset = offset;	// frameOffset from visit parameter
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}