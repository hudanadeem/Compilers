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
  private int level = 0; // 0 represents global scope

	private HashMap<String, ArrayList<NodeType>> table;

  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();
  }

  /* vistor methods */

  public void visit(NameTy nameTy, int level ) {
    System.out.println("Name ty visiting");
  }

  public void visit( SimpleVar simpleVar, int level ) {
    System.out.println("simple var visiting");
  }

  public void visit( IndexVar indexVar, int level ) {
    System.out.println("index var visiting");
  }

  public void visit( NilExp exp, int level ) {
    System.out.println("nil exp visiting");
  }

  public void visit( IntExp exp, int level ) {
    System.out.println("int exp visiting");
  }

  public void visit( BoolExp exp, int level ) {
    System.out.println("bool exp visiting");
  }

  public void visit( VarExp exp, int level ) {
    System.out.println("var exp visiting");
  }

  public void visit( CallExp exp, int level ) {
    System.out.println("call exp visiting");
    level++;
		exp.args.accept( this, level );
  }

  public void visit( OpExp exp, int level ) {
    System.out.println("op exp visiting");
    level++;
		if (exp.left != null)
			 exp.left.accept( this, level );
		if (exp.right != null)
			 exp.right.accept( this, level );
  }

  public void visit( AssignExp exp, int level ) {
    System.out.println("assign exp visiting");
    level++;
		exp.lhs.accept( this, level );
		exp.rhs.accept( this, level );
  }

  public void visit( IfExp exp, int level ) {
    System.out.println("if exp visiting");
    level++;
		exp.test.accept( this, level );
		exp.then.accept( this, level );
		if (exp.elseExp != null ) {
			exp.elseExp.accept( this, level );
		}
  }

  public void visit( WhileExp exp, int level ) {
    System.out.println("while exp visiting");
    level++;
		exp.test.accept( this, level );
		exp.body.accept( this, level );
  }

  public void visit( ReturnExp exp, int level ) {
    System.out.println("return exp visiting");
    level++;
		if (exp.exp != null ) {
			exp.exp.accept( this, level );
		}
  }

  public void visit( CompoundExp exp, int level ) {
    System.out.println("compound exp visiting");
    level++;
		exp.decs.accept( this, level );
		exp.exps.accept( this, level );
  }

  public void visit( FunctionDec exp, int level ) {
    System.out.println("function dec visiting");
    level++;
		exp.result.accept( this, level );
		exp.params.accept( this, level );
		exp.body.accept( this, level );
  }

  public void visit( SimpleDec exp, int level ) {
    System.out.println("simple dec visiting");
    insert(exp.name, new NodeType(exp.name, exp, level));
    level++;
		exp.typ.accept( this, level );
  }

  public void visit( ArrayDec exp, int level ) {
    System.out.println("array dec visiting");
    insert(exp.name, new NodeType(exp.name, exp, level));
    level++;
		exp.typ.accept( this, level );
  }

  public void visit( DecList decList, int level ) {
    System.out.println("dec list visiting");
    while( decList != null ) {
        if (decList.head != null) {
            decList.head.accept( this, level );
        }
        decList = decList.tail;
    }
  }

  public void visit( VarDecList varDecList, int level ) {
    System.out.println("var dec list visiting");
    while( varDecList != null ) {
        if (varDecList.head != null) {
            varDecList.head.accept( this, level );
        }
        varDecList = varDecList.tail;
    }
  }

  public void visit( ExpList expList, int level ) {
    System.out.println("exp list visiting");
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



}