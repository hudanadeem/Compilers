import java.io.*;
import absyn.*;

class CM {
  public static boolean SEMANTIC = true;
  public static boolean OUTPUT = true;
  static public void main(String argv[]) {  
      
    /* Start the parser */
    try {

      /* Set to not print tree if -a flag in command line */
      String inputFileName = null;
      for (int i = 0; i < argv.length; i++) {
        if (argv[i].equals("-a")) {
          SEMANTIC = false;
          OUTPUT = false;
        } else if (argv[i].equals("-s")) {
          SEMANTIC = true;
          OUTPUT = false;
        } else {
          inputFileName = argv[i];
          break;
        }
      }

      if (inputFileName == null) {
        System.out.println("Usage: java CM [-a or -s] <filename>");
        return;
      }

      parser p = new parser(new Lexer(new FileReader(inputFileName)));
      Absyn result = (Absyn)(p.parse().value);
      AbsynVisitor visitor = new ShowTreeVisitor();  

      // Print parse tree    
      if (OUTPUT == true && result != null) {
         System.out.println("The abstract syntax tree is:");

         result.accept(visitor, 0, false); 

         System.out.println("\n");
      }
      // Save parse tree to file
      else if (OUTPUT == false && result != null) {
          String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".abs";
          File outputFile = new File(outputFileName);
          PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile));
          System.setOut(fileOut); // Redirect output to the file

          result.accept(visitor, 0, false);

          fileOut.close(); // Close the file stream
          System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
      }

      if (!p.parseError && SEMANTIC) {
        SemanticAnalyzer analyzer = new SemanticAnalyzer();

          if (!OUTPUT) {
            String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".sym";
            File outputFile = new File(outputFileName);
            PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile));
            System.setOut(fileOut); // Redirect output to the file

            analyzer.visit(result);

            fileOut.close(); // Close the file stream
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
          }
          else {
            analyzer.visit(result);
          }
      }
        

    } catch (Exception e) {
      
      e.printStackTrace();
    }
  }
}
