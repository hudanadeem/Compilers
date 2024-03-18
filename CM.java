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
    }
}
