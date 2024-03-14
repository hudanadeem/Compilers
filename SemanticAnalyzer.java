/*
  Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
  File Name: SemanticAnalyzer.java
  Purpose: Visitor class that maintains symbol tree and performs type checking
*/


import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Iterator;
import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {
	public static boolean parseError = false;

	private HashMap<String, ArrayList<NodeType>> table;

}