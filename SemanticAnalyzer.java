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

  public SemanticAnalyzer() {
    table = new HashMap<String, ArrayList<NodeType>>();
  }

  /* vistor methods */

  public void visit(NameTy exp, int level ) {

  }

  public void visit( SimpleVar exp, int level ) {

  }

  public void visit( IndexVar exp, int level ) {

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

  }

  public void visit( OpExp exp, int level ) {

  }

  public void visit( AssignExp exp, int level ) {

  }

  public void visit( IfExp exp, int level ) {

  }

  public void visit( WhileExp exp, int level ) {

  }

  public void visit( ReturnExp exp, int level ) {

  }

  public void visit( CompoundExp exp, int level ) {

  }

  public void visit( FunctionDec exp, int level ) {

  }

  public void visit( SimpleDec exp, int level ) {

  }

  public void visit( ArrayDec exp, int level ) {

  }

  public void visit( DecList exp, int level ) {

  }

  public void visit( VarDecList exp, int level ) {

  }

  public void visit( ExpList exp, int level ) {
    
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