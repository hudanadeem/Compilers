import java.util.ArrayList;
import java.util.ArrayDeque;

import java.util.HashMap;
// import java.util.Iterator;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	public static boolean parseError = false;
	private HashMap<String, ArrayList<NodeType>> table;
    private Deque<HashMap<String, ArrayList<NodeType>>> scopeStack = new ArrayDeque<>();


    public SemanticAnalyzer() {
        table = new HashMap<>();
    }

    private void enterScope() {
        scopeStack.push(new HashMap<>());
        System.out.println("Entering a new scope");
    }
    private void leaveScope() {
        scopeStack.pop();
        System.out.println("Leaving the current scope");
    }


    // private void addDecToTable(String name, Dec def, int level) {
    //     HashMap<String, ArrayList<NodeType>> currentScope = scopeStack.peek();
    //     if (currentScope == null) {
    //         enterScope(); // Ensure there's at least one scope
    //         currentScope = scopeStack.peek();
    //     }
    //     ArrayList<NodeType> defs = currentScope.getOrDefault(name, new ArrayList<>());
    //     defs.add(new NodeType(name, def, level));
    //     currentScope.put(name, defs);
    //     // Example output
    //     System.out.println(name + ": " + getTypeName(def) + " // Assuming getTypeName(Dec) returns the type as a String");
    // }
    
    @Override
    public void visit(NameTy exp, int level) {
    }

    @Override
    public void visit(SimpleVar exp, int level) {

        ArrayList<NodeType> list = table.get(exp.name);
        if (list == null || list.isEmpty()) {
            System.out.println("Semantic Error: Variable " + exp.name + " is not declared.");
            parseError = true;
            return;
        }

        // Now this line should work since dtype exists in SimpleVar
        exp.dtype = list.get(list.size() - 1).def; // Assuming the last one is the current scope.
    }



    @Override
    public void visit(IndexVar exp, int level) {

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

    }

    @Override
    public void visit(OpExp exp, int level) {

    }

    @Override
    public void visit(AssignExp exp, int level) {

    }

    @Override
    public void visit(IfExp exp, int level) {

    }


    @Override
    public void visit(WhileExp exp, int level) {
    }

    @Override
    public void visit(ReturnExp exp, int level) {
    }


    @Override
    public void visit(CompoundExp exp, int level) {

    }

    @Override
    public void visit(FunctionDec exp, int level) {
        enterScope();


        leaveScope();

    }

    @Override
    public void visit(SimpleDec exp, int level) {
        ArrayList<NodeType> list = table.get(exp.name);
        if (list == null) {
            list = new ArrayList<>();
            table.put(exp.name, list);
        }
        list.add(new NodeType(exp.name, exp, level));
    }


    @Override
    public void visit(ArrayDec exp, int level) {
    }


    @Override
    public void visit(DecList exp, int level) {
    }

    @Override
    public void visit(VarDecList exp, int level) {
    }


    @Override
    public void visit(ExpList exp, int level) {
    }

}