import java.io.*;
import absyn.*;

class CM {
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
                    System.out.println("The abstract syntax tree is:");
                    AbsynVisitor visitor = new ShowTreeVisitor();
                    result.accept(visitor, 0); 
                    // Perform semantic analysis
                    System.out.println("Entering the global scope:");
                    SemanticAnalyzer analyzer = new SemanticAnalyzer();
                    result.accept(analyzer, 1);
                    analyzer.leaveScope(1);
                    System.out.println("Leaving the global scope");
                } else {
                    // Only generate and save the syntax tree to an output file
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
    }
}

// /*
//   Created by: Isabella McIvor (1101334) , Huda Nadeem (11439431), Zuya Abro (1109843)
//   File Name: CM.java
// */
   
// import java.io.*;
// import absyn.*;
   
// class CM {
//   public static boolean SHOW_TREE = true;
//   static public void main(String argv[]) {  
      
//     try {

//       parser p = new parser(new Lexer(new FileReader(argv[0])));
//       Absyn result = (Absyn)(p.parse().value);      
//       if (result != null) {
//         if (SHOW_TREE) {

//           System.out.println("The abstract syntax tree is:");
//           AbsynVisitor visitor = new ShowTreeVisitor();
//           result.accept(visitor, 0); 
//         }

//         System.out.println("Entering the global scope:");
//         SemanticAnalyzer analyzer = new SemanticAnalyzer();
//         result.accept(analyzer, 1);
//         analyzer.leaveScope(1);
//         System.out.println("Leaving the global scope");
//       }
//     } catch (Exception e) {
      
//       e.printStackTrace();
//     }
//   }
// }
