import java.util.ArrayList;
import java.util.HashMap;
// import java.util.Iterator;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	public static boolean parseError = false;
	private HashMap<String, ArrayList<NodeType>> table;

    public SemanticAnalyzer() {
        table = new HashMap<>();
    }
    public void analyze(Absyn root) {
        root.accept(this, 0);
    }

    @Override
    public void visit(NameTy exp, int level) {
        // Potentially perform type-related checks here, but no symbol table actions needed.
    }
    @Override
    public void visit(SimpleVar exp, int level) {
        // Check if the variable has been declared.
        if (!isDeclared(exp.name, level)) {
            System.err.println("Semantic Error: Variable '" + exp.name + "' has not been declared.");
        } else {
            System.out.println("Accessed SimpleVar: " + exp.name + " at level " + level);
        }
    }

    @Override
    public void visit(IndexVar exp, int level) {
        // Check if the array variable has been declared.
        if (!isDeclared(exp.name, level)) {
            System.err.println("Semantic Error: Array '" + exp.name + "' has not been declared.");
        } else {
            System.out.println("Accessed IndexVar: " + exp.name + " at level " + level);
        }
    }

    private boolean isDeclared(String name, int level) {
        ArrayList<NodeType> declarations = table.get(name);
        if (declarations == null) return false;
        // Check for a declaration at this or a higher (outer) scope level.
        return declarations.stream().anyMatch(nt -> nt.level <= level);
    }


    @Override
    public void visit(NilExp exp, int level) {
       
    }
    @Override
    public void visit(IntExp exp, int level) {
        
    }

    @Override
    public void visit(BoolExp exp, int level) {
        
    }
    @Override
    public void visit(VarExp exp, int level) {
        
    }

    @Override
    public void visit(CallExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added CallExp to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(OpExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added OpExp to table: " + exp.name + " at level " + level);
    }

    @Override
    public void visit(AssignExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added AssignExp to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(IfExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added IfExp to table: " + exp.name + " at level " + level);
    }

    @Override
    public void visit(WhileExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added WhileExp to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(ReturnExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added ReturnExp to table: " + exp.name + " at level " + level);
    }

    @Override
    public void visit(CompoundExp exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added CompoundExp to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(FunctionDec exp, int level) {
        // Similar logic as for SimpleDec
        ArrayList<NodeType> list = table.get(exp.func);
        if (list != null) {
            for (NodeType nt : list) {
                if (nt.level == level) {
                    System.err.println("Semantic Error: Function '" + exp.func + "' is already defined in this scope.");
                    return;
                }
            }
        }
        list = table.computeIfAbsent(exp.func, k -> new ArrayList<>());
        list.add(new NodeType(exp.func, exp, level));
        System.out.println("Added function declaration to table: " + exp.func + " at level " + level);
    }

    @Override
    public void visit(SimpleDec exp, int level) {
        // Check if the variable is already declared in the current scope
        ArrayList<NodeType> list = table.get(exp.name);
        if (list != null) {
            for (NodeType nt : list) {
                if (nt.level == level) {
                    System.err.println("Semantic Error: Variable '" + exp.name + "' is already defined in this scope.");
                    return;
                }
            }
        }
        // Add the new declaration to the symbol table
        list = table.computeIfAbsent(exp.name, k -> new ArrayList<>());
        list.add(new NodeType(exp.name, exp, level));
        System.out.println("Added variable declaration to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(ArrayDec exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added ArrayDec to table: " + exp.name + " at level " + level);
    }

    @Override
    public void visit(DecList exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added DecList to table: " + exp.name + " at level " + level);
    }
    @Override
    public void visit(VarDecList exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added VarDeclist to table: " + exp.name + " at level " + level);
    }

    @Override
    public void visit(ExpList exp, int level) {
        ArrayList<NodeType> types = table.getOrDefault(exp.name, new ArrayList<>());
        types.add(new NodeType(exp.name, exp, level));
        table.put(exp.name, types);
        System.out.println("Added ExpList to table: " + exp.name + " at level " + level);
    }

}