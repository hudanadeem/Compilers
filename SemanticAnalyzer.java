/*
  Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
  File Name: SemanticAnalyzer.java
  Purpose: Visitor class that maintains symbol tree and performs type checking
*/

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	private HashMap<String, ArrayList<NodeType>> table;
  final static int SPACES = 4;

  /* tracking variables */
  private int lastVisited = -1;   // represents type of current node
  private String currentFunc = "";  // keeps track of current function
  private boolean isArr = false;    // keeps track of whether exp node is equivalent to array or simple


  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();

    // Add pre-defined functions to hashtable
    FunctionDec input = new FunctionDec( -1, new NameTy( -1, NameTy.INT), "input", null, null );
    insert(input.func, new NodeType(input.func, input, -1));

    FunctionDec output = new FunctionDec( -1, new NameTy( -1, NameTy.VOID), "output", null, null );
    insert(output.func, new NodeType(output.func, output, -1));
  }

  /******* Vistor Methods *******/

  public void visit(NameTy nameTy, int level ) {
  }

  public void visit( SimpleVar simpleVar, int level ) {
    
    lastVisited = lookupType(simpleVar.name);
    
    // Check if variable has been declared
    if (lastVisited == -1) {
      report_error(simpleVar.pos, "variable '" + simpleVar.name + "' undefined");
    }

    if (isArray(lookupNode(simpleVar.name))) {
      isArr = true;
    }
    else {
      isArr = false;
    }
  }

  public void visit( IndexVar indexVar, int level ) {

    // Check if array index is integer
    indexVar.index.accept( this, level );
    if (!isInt(lastVisited, isArr)) {
      report_error(indexVar.pos, "array index must be an integer");
    }

    lastVisited = lookupType(indexVar.name);

    // Check if variable has been declared
    if (lastVisited == -1) {
      report_error(indexVar.pos, "variable '" + indexVar.name + "' undefined");
    }

    isArr = false;
  }

  public void visit( NilExp exp, int level ) {
  }

  public void visit( IntExp exp, int level ) {
    // Set current node type to int
    lastVisited = NameTy.INT;
    isArr = false;
  }

  public void visit( BoolExp exp, int level ) {
    // Set current node type to bool
    lastVisited = NameTy.BOOL;
    isArr = false;
  }

  public void visit( VarExp exp, int level ) {
    exp.variable.accept( this, level );
  }

  public void visit( CallExp exp, int level ) {
		exp.args.accept( this, level );

    // Set current node type to function type
    lastVisited = lookupType(exp.func);

    // Check if function being called has been declared
    if (lastVisited == -1) {
      report_error(exp.pos, "function '" + exp.func + "' cannot be called because it is undefined");
    }
  }

  public void visit( OpExp exp, int level ) {
    int ltype = -1, rtype = -1;
    boolean leftIsArr = false, rightIsArr = false;

    // Set type of left operand
		if (exp.left != null) {
			 exp.left.accept( this, level );
    }
    ltype = lastVisited;
    leftIsArr = isArr;

    // Set type of right operand
		if (exp.right != null) {
			 exp.right.accept( this, level );
    }
    rtype = lastVisited;
    rightIsArr = isArr;
    
    switch( exp.op ) {

			case OpExp.PLUS:
			  if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "operands for addition must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.MINUS:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "operands for subtraction must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.TIMES:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "operands for multiplication must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.DIV:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "operands for division must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.EQ:
        if ((ltype != rtype) || leftIsArr != rightIsArr || isVoid(ltype) || isVoid(rtype)) {
          report_error(exp.pos, "incompatable operands for boolean expression '=='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.NE:
			  if ((ltype != rtype) || leftIsArr != rightIsArr || isVoid(ltype) || isVoid(rtype)) {
          report_error(exp.pos, "incompatable operands for boolean expression '!='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.LT:
			  if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '<'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.LTE:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '<='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.GT:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '>'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.GTE:
        if (!isInt(ltype, leftIsArr) || !isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '>='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.NOT:
        if (!isBool(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '~'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.AND:
        if (!isBool(ltype, leftIsArr) || !isBool(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '&&'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.OR:
        if (!isBool(ltype, leftIsArr) || !isBool(rtype, rightIsArr)) {
          report_error(exp.pos, "incompatable operands for boolean expression '||'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.UMINUS:
        if (!isInt(rtype, rightIsArr)) {
          report_error(exp.pos, "operand must be of type integer");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;
        
		  }
  }

  public void visit( AssignExp exp, int level ) {
    // Set type of LHS variable
		exp.lhs.accept( this, level );
    int ltype = lastVisited;

    // Set type of RHS expression
		exp.rhs.accept( this, level );
    int rtype = lastVisited;

    // Check that LHS type = RHS type
    if (ltype != rtype && ltype != -1 && rtype != -1) {
      report_error(exp.pos, "mismatch types for assign expression");
    }
  }

  public void visit( IfExp exp, int level ) {
		exp.test.accept( this, level );

    // Check that condition is of boolean type 
    if (!isBool(lastVisited, isArr)) {
      report_error(exp.pos, "condition for if statement must be of boolean type");
    } 

    // If block
    indent(level);
    System.out.println("Entering a new if block:");

		exp.then.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the if block");

    // Else block
		if (!(exp.elseExp instanceof NilExp)) {
      indent(level);
      System.out.println("Entering a new else block:");
      
			exp.elseExp.accept( this, level+1 );

      leaveScope(level+1);
      indent(level);
      System.out.println("Leaving the else block");
		}
  }

  public void visit( WhileExp exp, int level ) {
    exp.test.accept( this, level+1 );
    
    // Check condition is of type boolean or integer
    if (!isInt(lastVisited, isArr) && !isBool(lastVisited, isArr)) {
      report_error(exp.pos, "condition for while expression must be of boolean type");
    }

    indent(level);
    System.out.println("Entering a new while block:");

		exp.body.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the while block");
  }

  public void visit( ReturnExp exp, int level ) {
		if (exp.exp != null ) {
			exp.exp.accept( this, level );
		}

    // Check that return type matches function type
    if (lastVisited != lookupType(currentFunc)) {
      report_error(exp.pos, "incompatable return type for function " + currentFunc);
    }
  }

  public void visit( CompoundExp exp, int level ) {
    exp.decs.accept( this, level );
    exp.exps.accept( this, level );
  }

  public void visit( FunctionDec exp, int level ) {
    exp.typ.accept( this, level+1 );

    indent(level);
    System.out.println("Entering the scope for function " + exp.func + ":");

    // Set value of current function
    currentFunc = exp.func;
    insert(exp.func, new NodeType(exp.func, exp, level));
    
    exp.params.accept( this, level+1 );
    exp.body.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the scope for function " + exp.func);
    currentFunc = "";
  }

  public void visit( SimpleDec exp, int level ) {
    // Check that variable has not been declared
    if (lookupNode(exp.name) != null) {
      report_error(exp.pos, "variable with identifier '" + exp.name + "' already exists");
    }
    else {
      insert(exp.name, new NodeType(exp.name, exp, level));
    }

    // Check that variable type is not void
    if (isVoid(exp.typ.typ)) {
      report_error(exp.pos, "cannot declare variable of type 'void'");
    }
  }

  public void visit( ArrayDec exp, int level ) {
    // Check that variable has not been declared
    if (lookupNode(exp.name) != null) {
      report_error(exp.pos, "variable with identifier '" + exp.name + "' already exists");
    }
    else {
      insert(exp.name, new NodeType(exp.name, exp, level));
    }

    // Check that variable type is not void
    if (isVoid(exp.typ.typ)) {
      report_error(exp.pos, "cannot declare variable of type 'void'");
    }
  }

  public void visit( DecList decList, int level ) {
    while( decList != null ) {
        if (decList.head != null) {
            decList.head.accept( this, level );
        }
        decList = decList.tail;
    }
  }

  public void visit( VarDecList varDecList, int level ) {
    while( varDecList != null ) {
        if (varDecList.head != null) {
            varDecList.head.accept( this, level );
        }
        varDecList = varDecList.tail;
    }
  }

  public void visit( ExpList expList, int level ) {
    while( expList != null ) {
        if (expList.head != null) {
            expList.head.accept( this, level );
        }
        expList = expList.tail;
		}
  }



  /*******  Helper Methods *******/

  /* Reports error */
  private void report_error(int line, String message) {
    System.err.println("Type error in line " + (line+1) + ": " + message);
  }

  /* Returns true if type is integer */
  private boolean isInt(int type, boolean arr) {
    return (type == NameTy.INT) && !arr;
  }

  /* Returns true if type is boolean */
  private boolean isBool(int type, boolean arr) {
    return (type == NameTy.BOOL) && !arr;
  }

  /* Returns true if type is void */
  private boolean isVoid(int type) {
    return (type == NameTy.VOID);
  }

  private boolean isArray(NodeType node) {
    return (node.def instanceof ArrayDec);
  }

  /* Prints each variable in specified scope then deletes from table */
  public void leaveScope(int scope) {

    List<SimpleEntry<String, NodeType>> toRemove = new ArrayList<>();
    Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();

    // Traverse table
    while(iter.hasNext()) {
      HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();

      // Traverse each ArrayList and only print if in scope
      for (NodeType node: entry.getValue()) {
        if (node.level == scope) {
            indent(scope);
            System.out.print(entry.getKey() + ":" + type(node.def.typ));
            if (isArray(node)) {
              System.out.println("[]");
            }else {
              System.out.println();
            }
            toRemove.add(new SimpleEntry<>(entry.getKey(), node));
        }
      }
    }

    // Remove items that were printed
    for (SimpleEntry<String, NodeType> entry : toRemove) {
        delete(entry.getKey(), entry.getValue());
    }
  }

  /* Looks up type of variable with identifier key */
  private int lookupType(String key) {
    if (table.containsKey(key)) {
      ArrayList<NodeType> id = table.get(key);
      NodeType recent = id.get(id.size() - 1);
      return recent.def.typ.typ;
    } else {
      return -1;
    }
  }

  /* Looks up node with identifier key */
  private NodeType lookupNode(String key) {
    if (table.containsKey(key)) {
      ArrayList<NodeType> id = table.get(key);
      NodeType recent = id.get(id.size() - 1);
      return recent;
    } else {
      return null;
    }
  }

  /* Insert specified node at specified key in table */
  private void insert(String key, NodeType node) {

    if (table.containsKey(key)) {
      ArrayList<NodeType> nodeList = table.get(key);
      nodeList.add(node);
    }
    else {
      ArrayList<NodeType> nodeList = new ArrayList<>();
      nodeList.add(node);
      table.put(key, nodeList);
    }
  }

  /* Deletes specified node at specified key in table */
  private void delete(String key, NodeType node) {
    if (table.containsKey(key)) {
      ArrayList<NodeType> nodeList = table.get(key);
      nodeList.remove(node);

      if (nodeList.isEmpty()) {
        table.remove(key);
      }
    }
  }

  /* Prints indentation based on scope */
  private void indent( int level ) {
		for ( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
	}

  /* Returns the string value of a NameTy type */
  private String type(NameTy type) {
    if (type.typ == NameTy.BOOL) {
      return "bool";
    } 
    if (type.typ == NameTy.INT) {
      return "int";
    }
    if (type.typ == NameTy.VOID) {
      return "void";
    }
    return null;
  }

}
