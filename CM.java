import java.io.*;
import absyn.*;

class CM {
<<<<<<< HEAD
    public static void main(String argv[]) {
        boolean showTree = true; // By default, we show the tree and perform semantic analysis
        String inputFileName = null;

        try {
            // Parse command line arguments
            for (String arg : argv) {
                if (arg.equals("-a")) {
                    showTree = false; // Skip semantic analysis and only generate the syntax tree
                } else {
                    inputFileName = arg; // Assume the remaining argument is the file name
                }
            }

            if (inputFileName == null) {
                System.out.println("No input file provided.");
                return;
            }

            parser p = new parser(new Lexer(new FileReader(inputFileName)));
            Absyn result = (Absyn) (p.parse().value);
            if (result != null) {
                if (showTree) {
                    //tree
                    System.out.println("The abstract syntax tree is:");
                    AbsynVisitor visitor = new ShowTreeVisitor();
                    result.accept(visitor, 0); 
                    // analysis
                    System.out.println("Entering the global scope:");
                    SemanticAnalyzer analyzer = new SemanticAnalyzer();
                    result.accept(analyzer, 1);
                    analyzer.leaveScope(1);
                    System.out.println("Leaving the global scope");
                } else {
                    // Only tree to an output file
                    String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".abs";
                    File outputFile = new File(outputFileName);
                    PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile));
                    System.setOut(fileOut); // Redirect output to the file
                    AbsynVisitor visitor = new ShowTreeVisitor();
                    result.accept(visitor, 0);
                    fileOut.close(); // Close the file stream
                    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out))); // Reset output to console
                    System.out.println("The abstract syntax tree is saved to " + outputFileName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
=======
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

         result.accept(visitor, 0); 

         System.out.println("\n");
      }
      // Save parse tree to file
      else if (OUTPUT == false && result != null) {
          String outputFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.')) + ".abs";
          File outputFile = new File(outputFileName);
          PrintStream fileOut = new PrintStream(new FileOutputStream(outputFile));
          System.setOut(fileOut); // Redirect output to the file

          result.accept(visitor, 0);

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

            System.out.println("Entering the global scope:");
            result.accept(analyzer, 1);
            analyzer.leaveScope(1);
            System.out.println("Leaving the global scope");

            fileOut.close(); // Close the file stream
            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
          }
          else {
            System.out.println("Entering the global scope:");
            result.accept(analyzer, 1);
            analyzer.leaveScope(1);
            System.out.println("Leaving the global scope");
          }
      }
        

    } catch (Exception e) {
      
      e.printStackTrace();
>>>>>>> ella-branch
    }
}
