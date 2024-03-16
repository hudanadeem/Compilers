/*
  Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
  File Name: SemanticAnalyzer.java
  Purpose: Visitor class that maintains symbol tree and performs type checking
*/


import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Iterator;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	public static boolean parseError = false;
	private HashMap<String, ArrayList<NodeType>> table;
  final static int SPACES = 4;


  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();
  }

  /* vistor methods */

  public void visit(NameTy nameTy, int level ) {
  }

  public void visit( SimpleVar simpleVar, int level ) {
  }

  public void visit( IndexVar indexVar, int level ) {
  }

  public void visit( NilExp exp, int level ) {
  }

  public void visit( IntExp exp, int level ) {
  }

  public void visit( BoolExp exp, int level ) {
  }

  public void visit( VarExp exp, int level ) {
  }

  public void visit( CallExp exp, int level ) {
		exp.args.accept( this, level );
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
  }

  public void visit( IfExp exp, int level ) {
		exp.test.accept( this, level );

    indent(level);
    System.out.println("Entering a new block:");

		exp.then.accept( this, level+1 );

    indent(level);
    System.out.println("Leaving the block");

    indent(level);
    System.out.println("Entering a new block:");

		if (exp.elseExp != null ) {
			exp.elseExp.accept( this, level+1 );
		}

    indent(level);
    System.out.println("Leaving the block");
  }

  public void visit( WhileExp exp, int level ) {
    indent(level);
    System.out.println("Entering a new block:");

		exp.test.accept( this, level+1 );
		exp.body.accept( this, level+1 );

    indent(level);
    System.out.println("Leaving the block");
  }

  public void visit( ReturnExp exp, int level ) {
		if (exp.exp != null ) {
			exp.exp.accept( this, level );
		}
  }

  public void visit( CompoundExp exp, int level ) {
		exp.decs.accept( this, level );
		exp.exps.accept( this, level );
  }

  public void visit( FunctionDec exp, int level ) {
    indent(level);
    System.out.println("Entering the scope for function " + exp.func + ":");

    insert(exp.func, new NodeType(exp.func, exp, level));
    
		exp.result.accept( this, level+1 );
		exp.params.accept( this, level+1 );
		exp.body.accept( this, level+1 );

    indent(level);
    System.out.println("Leaving the scope for function " + exp.func);
  }

  public void visit( SimpleDec exp, int level ) {
    indent(level);
    System.out.println(exp.name + ":" + type(exp.typ));

    insert(exp.name, new NodeType(exp.name, exp, level));

		exp.typ.accept( this, level );
  }

  public void visit( ArrayDec exp, int level ) {
    indent(level);
    System.out.println(exp.name + ":" + type(exp.typ) + "[]");

    insert(exp.name, new NodeType(exp.name, exp, level));
  
		exp.typ.accept( this, level );
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



  /* helper methods */

  private ArrayList lookup(String key) {
    if (table.containsKey(key)) {
      return table.get(key);
    } else {
      return null;
    }
  }

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

  private void delete(String key, NodeType node) {
    if (table.containsKey(key)) {
      ArrayList<NodeType> nodeList = table.get(key);
      nodeList.remove(node);

      if (nodeList.isEmpty()) {
        table.remove(key);
      }
    }
  }

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