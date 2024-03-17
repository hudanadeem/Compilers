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

	// public static boolean parseError = false;
	private HashMap<String, ArrayList<NodeType>> table;
  final static int SPACES = 4;
  private int lastVisited = -1;
  private String currentFunc = "";


  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();
  }

  /******* Vistor Methods *******/

  public void visit(NameTy nameTy, int level ) {
  }

  public void visit( SimpleVar simpleVar, int level ) {
    lastVisited = lookup(simpleVar.name);
    // check if it has been declared
  }

  public void visit( IndexVar indexVar, int level ) {
    // if indexVar.index != int, report error where index is an expression
    indexVar.index.accept( this, level );
    if (!isInt(lastVisited)) {
      report_error("array index must be an integer");
    }

    // check if it has been declared

    lastVisited = lookup(indexVar.name);
  }

  public void visit( NilExp exp, int level ) {
  }

  public void visit( IntExp exp, int level ) {
    // exp type = int
    lastVisited = NameTy.INT;
  }

  public void visit( BoolExp exp, int level ) {
    // exp type = bool
    lastVisited = NameTy.BOOL;
  }

  public void visit( VarExp exp, int level ) {
    exp.variable.accept( this, level );
    // exp type = lookup(exp.name)
    
  }

  public void visit( CallExp exp, int level ) {
		exp.args.accept( this, level );

    // exp type = lookup(exp.func)
    lastVisited = lookup(exp.func);
  }

  public void visit( OpExp exp, int level ) {
    int ltype = -1, rtype = -1;

		if (exp.left != null) {
			 exp.left.accept( this, level );
    }
    ltype = lastVisited;

		if (exp.right != null) {
			 exp.right.accept( this, level );
    }
    rtype = lastVisited;
    
    switch( exp.op ) {

			case OpExp.PLUS:
			  if (!isInt(ltype) || !isInt(rtype)) {
          report_error("operands for addition must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.MINUS:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("operands for subtraction must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.TIMES:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("operands for multiplication must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.DIV:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("operands for division must be integers");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;

			case OpExp.EQ:
        if ((ltype != rtype) || isVoid(ltype) || isVoid(rtype)) {
          report_error("incompatable operands for boolean expression '=='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.NE:
			  if ((ltype != rtype) || isVoid(ltype) || isVoid(rtype)) {
          report_error("incompatable operands for boolean expression '!='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.LT:
			  if (!isInt(ltype) || !isInt(rtype)) {
          report_error("incompatable operands for boolean expression '<'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.LTE:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("incompatable operands for boolean expression '<='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.GT:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("incompatable operands for boolean expression '>'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.GTE:
        if (!isInt(ltype) || !isInt(rtype)) {
          report_error("incompatable operands for boolean expression '>='");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.NOT:
        if (!isBool(rtype)) {
          report_error("incompatable operands for boolean expression '~'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.AND:
        if (!isBool(ltype) || !isBool(rtype)) {
          report_error("incompatable operands for boolean expression '&&'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.OR:
        if (!isBool(ltype) || !isBool(rtype)) {
          report_error("incompatable operands for boolean expression '||'");
        }
        else {
          lastVisited = NameTy.BOOL;
        }
			  break;

			case OpExp.UMINUS:
        if (!isInt(rtype)) {
          report_error("operand must be of type integer");
        }
        else {
          lastVisited = NameTy.INT;
        }
			  break;
        
		  }
  }

  public void visit( AssignExp exp, int level ) {
		exp.lhs.accept( this, level );
    int ltype = lastVisited;
		exp.rhs.accept( this, level );
    int rtype = lastVisited;

    // if lhs type != rhs type, report error
    if (ltype != rtype) {
      report_error("mismatch types for assign expression");
    }
  }

  public void visit( IfExp exp, int level ) {
		exp.test.accept( this, level );
    // if test != bool, report error
    if (!isBool(lastVisited)) {
      report_error("condition for if statement must be of boolean type");
    } 

    indent(level);
    System.out.println("Entering a new block:");

		exp.then.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the block");

		if (!(exp.elseExp instanceof NilExp)) {
      indent(level);
      System.out.println("Entering a new block:");
      
			exp.elseExp.accept( this, level+1 );

      leaveScope(level+1);
      indent(level);
      System.out.println("Leaving the block");
		}
  }

  public void visit( WhileExp exp, int level ) {
    exp.test.accept( this, level+1 );
    // if type != bool or int, report error
    if (!isInt(lastVisited) && !isBool(lastVisited)) {
      report_error("condition for while expression must be of boolean type");
    }

    indent(level);
    System.out.println("Entering a new block:");

		exp.body.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the block");
  }

  public void visit( ReturnExp exp, int level ) {
		if (exp.exp != null ) {
			exp.exp.accept( this, level );
		}

    // if return type != lookup(function type), report error
    if (lastVisited != lookup(currentFunc)) {
      report_error("incompatable return type for function " + currentFunc);
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

    currentFunc = exp.func;
    insert(exp.func, new NodeType(exp.func, exp, level));
    
    exp.body.accept( this, level+1 );
		exp.params.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the scope for function " + exp.func);
    currentFunc = "";
  }

  public void visit( SimpleDec exp, int level ) {
    // exp.typ.accept( this, level );

    if (isVoid(exp.typ.typ)) {
      report_error("cannot declare variable of type 'void'");
    }
    insert(exp.name, new NodeType(exp.name, exp, level));
      
    // if exp type = void, report error
  }

  public void visit( ArrayDec exp, int level ) {

    if (isVoid(exp.typ.typ)) {
      report_error("cannot declare variable of type 'void'");
    }
    insert(exp.name, new NodeType(exp.name, exp, level));
  
		// exp.typ.accept( this, level );
    // if exp type = void, report error
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
  private void report_error(String message) {
    System.out.println("Type error: " + message);
  }

  /* Returns true if type is integer */
  private boolean isInt(int type) {
    return (type == NameTy.INT);
  }

  /* Returns true if type is boolean */
  private boolean isBool(int type) {
    return (type == NameTy.BOOL);
  }

  /* Returns true if type is void */
  private boolean isVoid(int type) {
    return (type == NameTy.VOID);
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
            System.out.println(entry.getKey() + ":" + type(node.def.typ));
            toRemove.add(new SimpleEntry<>(entry.getKey(), node));
        }
      }
    }

    // Remove items that were printed
    for (SimpleEntry<String, NodeType> entry : toRemove) {
        delete(entry.getKey(), entry.getValue());
    }
  }

  /* Looks up ArrayList with specific key */
  private int lookup(String key) {
    if (table.containsKey(key)) {
      ArrayList<NodeType> id = table.get(key);
      NodeType recent = id.get(id.size() - 1);
      return recent.def.typ.typ;
    } else {
      return -1;
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