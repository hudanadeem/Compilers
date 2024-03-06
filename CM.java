/*
  Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
  File Name: CM.java
*/
   
import java.io.*;
import absyn.*;
   
class CM {
  public final static boolean SHOW_TREE = true;
  static public void main(String argv[]) {    
    /* Start the parser */
    try {
      parser p = new parser(new Lexer(new FileReader(argv[0])));
      Absyn result = (Absyn)(p.parse().value);      
      if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         AbsynVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
      }
    } catch (Exception e) {
      
      e.printStackTrace();
    }
  }
}
