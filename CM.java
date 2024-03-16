/*
  Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
  File Name: CM.java
*/
   
import java.io.*;
import absyn.*;
   
class CM {
  public static boolean SHOW_TREE = true;
  static public void main(String argv[]) {  
      
    /* Start the parser */
    try {

      /* Set to not print tree if -a flag in command line */
      String fileName = null;
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-a")) {
          SHOW_TREE = false;
        } else {
          fileName = argv[i];
          break;
        }
      }

      if (fileName == null) {
        System.out.println("Usage: java CM [-a] <filename>");
        return;
      }

      parser p = new parser(new Lexer(new FileReader(fileName)));
      Absyn result = (Absyn)(p.parse().value);      
      if (SHOW_TREE && result != null) {
         System.out.println("The abstract syntax tree is:");
         AbsynVisitor visitor = new ShowTreeVisitor();
         result.accept(visitor, 0); 
      }

      AbsynVisitor analyzer = new SemanticAnalyzer();
      result.accept(analyzer, 0);
      
    } catch (Exception e) {
      
      e.printStackTrace();
    }
  }
}
