package absyn;
import java.lang.Integer;

public class IntExp extends Exp {
	public int pos;
	public int value;

	public IntExp( int pos, String value ) {
		this.pos = pos;
		if (value != null) {
			this.value = Integer.parseInt(value);
		}
	}

	public void accept( AbsynVisitor visitor, int level, boolean flag ) {
		visitor.visit( this, level, flag );
	}
}