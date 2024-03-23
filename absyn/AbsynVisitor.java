package absyn;

public interface AbsynVisitor {

  public void visit(NameTy exp, int value, boolean flag );

  public void visit( SimpleVar exp, int value, boolean flag );

  public void visit( IndexVar exp, int value, boolean flag );

  public void visit( NilExp exp, int value, boolean flag );

  public void visit( IntExp exp, int value, boolean flag );

  public void visit( BoolExp exp, int value, boolean flag );

  public void visit( VarExp exp, int value, boolean flag );

  public void visit( CallExp exp, int value, boolean flag );

  public void visit( OpExp exp, int value, boolean flag );

  public void visit( AssignExp exp, int value, boolean flag );

  public void visit( IfExp exp, int value, boolean flag );

  public void visit( WhileExp exp, int value, boolean flag );

  public void visit( ReturnExp exp, int value, boolean flag );

  public void visit( CompoundExp exp, int value, boolean flag );

  public void visit( FunctionDec exp, int value, boolean flag );

  public void visit( SimpleDec exp, int value, boolean flag );

  public void visit( ArrayDec exp, int value, boolean flag );

  public void visit( DecList exp, int value, boolean flag );

  public void visit( VarDecList exp, int value, boolean flag );

  public void visit( ExpList exp, int value, boolean flag );

}