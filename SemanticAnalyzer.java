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


  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();
  }

  /******* Vistor Methods *******/

  public void visit(NameTy nameTy, int level ) {
  }

  public void visit( SimpleVar simpleVar, int level ) {
    System.out.println("visiting simpleVar");
  }

  public void visit( IndexVar indexVar, int level ) {
    // if indexVar.index != int, report error where index is an expression
    System.out.println("visiting indexVar");
  }

  public void visit( NilExp exp, int level ) {
  }

  public void visit( IntExp exp, int level ) {
    // exp type = int
  }

  public void visit( BoolExp exp, int level ) {
    // exp type = bool
  }

  public void visit( VarExp exp, int level ) {
    exp.variable.accept( this, level );
    // exp type = lookup(exp.name)
  }

  public void visit( CallExp exp, int level ) {
		exp.args.accept( this, level );

    // exp type = lookup(exp.func)
  }

  public void visit( OpExp exp, int level ) {
		if (exp.left != null)
			 exp.left.accept( this, level );
		if (exp.right != null)
			 exp.right.accept( this, level );
  }

  public void visit( AssignExp exp, int level ) {
		exp.lhs.accept( this, level );
		exp.rhs.accept( this, level );

    // if lhs type != rhs type, report error
  }

  public void visit( IfExp exp, int level ) {
		exp.test.accept( this, level );
    // if test != bool, report error

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
  }

  public void visit( CompoundExp exp, int level ) {
    exp.exps.accept( this, level );
		exp.decs.accept( this, level );
  }

  public void visit( FunctionDec exp, int level ) {
    exp.typ.accept( this, level+1 );

    indent(level);
    System.out.println("Entering the scope for function " + exp.func + ":");

    insert(exp.func, new NodeType(exp.func, exp, level));
    
    exp.body.accept( this, level+1 );
		exp.params.accept( this, level+1 );

    leaveScope(level+1);
    indent(level);
    System.out.println("Leaving the scope for function " + exp.func);
  }

  public void visit( SimpleDec exp, int level ) {
    exp.typ.accept( this, level );

    insert(exp.name, new NodeType(exp.name, exp, level));
    // if exp type = void, report error
  }

  public void visit( ArrayDec exp, int level ) {

    insert(exp.name, new NodeType(exp.name, exp, level));
  
		exp.typ.accept( this, level );
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
  private ArrayList lookup(String key) {
    if (table.containsKey(key)) {
      return table.get(key);
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