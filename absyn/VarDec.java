package absyn;

abstract public class VarDec extends Dec {
	public int offset;	   // location relative to fp
	public int nestLevel;  // gp or the current fp
}