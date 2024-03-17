import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import absyn.*;

public class SemanticAnalyzer implements AbsynVisitor {

	private HashMap<String, ArrayList<NodeType>> table;
    final static int SPACES = 4;
    private int lastVisited = -1;
    private String currentFunc = "";

    public SemanticAnalyzer() {
        table = new HashMap<String, ArrayList<NodeType>>();
    }


    /*Vistor methods*/

    
    public void visit(NameTy exp, int level) {
    }

    public void visit(SimpleVar exp, int level) {

        lastVisited = lookup(exp.name);
    }

    public void visit(IndexVar exp, int level) {

        exp.index.accept(this, level);
        if (!isInt(lastVisited)) {
            report_error("array index must be an integer");
        }

        // check if it has been declared

        lastVisited = lookup(exp.name);
    }

    public void visit(NilExp exp, int level) {
    }

    public void visit(IntExp exp, int level) {
        lastVisited = NameTy.INT;

    }

    public void visit(BoolExp exp, int level) {
        lastVisited = NameTy.BOOL;

    }

    public void visit(VarExp exp, int level) {
        exp.variable.accept( this, level );
    }

    public void visit(CallExp exp, int level) {

        if (exp.args != null) {
            exp.args.accept(this, level); // This assumes args are processed elsewhere
        }

        // exp type = lookup(exp.func)
        lastVisited = lookup(exp.func);
        if ("input".equals(exp.func)) {
            lastVisited = NameTy.INT; // input() returns an integer
        } else if ("output".equals(exp.func)) {
            lastVisited = NameTy.VOID; // output() is void
        } else {
            // For user-defined functions, lookup their return type in the symbol table
            lastVisited = lookup(exp.func);
        }

    }

    public void visit(OpExp exp, int level) {

        int ltype = -1, rtype = -1;

		if (exp.left != null) {
			 exp.left.accept( this, level );
        }
        ltype = lastVisited;

            if (exp.right != null) {
                exp.right.accept( this, level );
        }
        rtype = lastVisited;
    
        switch( exp.op ) {

            case OpExp.PLUS:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("operands for addition must be integers");
                }
                else {
                    lastVisited = NameTy.INT;
                }
                break;

            case OpExp.MINUS:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("operands for subtraction must be integers");
                }
                else {
                    lastVisited = NameTy.INT;
                }
                break;

            case OpExp.TIMES:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("operands for multiplication must be integers");
                }
                else {
                    lastVisited = NameTy.INT;
                }
                break;

            case OpExp.DIV:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("operands for division must be integers");
                }
                else {
                    lastVisited = NameTy.INT;
                }
                break;

            case OpExp.EQ:
                if ((ltype != rtype) || isVoid(ltype) || isVoid(rtype)) {
                    report_error("incompatable operands for boolean expression '=='");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.NE:
                if ((ltype != rtype) || isVoid(ltype) || isVoid(rtype)) {
                    report_error("incompatable operands for boolean expression '!='");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.LT:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("incompatable operands for boolean expression '<'");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.LTE:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("incompatable operands for boolean expression '<='");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.GT:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("incompatable operands for boolean expression '>'");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.GTE:
                if (!isInt(ltype) || !isInt(rtype)) {
                    report_error("incompatable operands for boolean expression '>='");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.NOT:
                if (!isBool(rtype)) {
                    report_error("incompatable operands for boolean expression '~'");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.AND:
                if (!isBool(ltype) || !isBool(rtype)) {
                    report_error("incompatable operands for boolean expression '&&'");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.OR:
                if (!isBool(ltype) || !isBool(rtype)) {
                    report_error("incompatable operands for boolean expression '||'");
                }
                else {
                    lastVisited = NameTy.BOOL;
                }
                break;

            case OpExp.UMINUS:
                if (!isInt(rtype)) {
                    report_error("operand must be of type integer");
                }
                else {
                    lastVisited = NameTy.INT;
                }
                break;
                
        }
    }


    public void visit(AssignExp exp, int level) {
        exp.lhs.accept( this, level );
        int ltype = lastVisited;

        exp.rhs.accept( this, level );
        int rtype = lastVisited;

        // if lhs type != rhs type, report error
        if (ltype != rtype) {
            report_error("mismatch types for assign expression");
        }

    }

    public void visit(IfExp exp, int level) {

        exp.test.accept( this, level );
        // if test != bool, report error
        if (!isBool(lastVisited)) {
            report_error("condition for if statement must be of boolean type");
        } 

        boolean thenHasContent = exp.then instanceof CompoundExp && (((CompoundExp) exp.then).decs != null || ((CompoundExp) exp.then).exps != null);
        if (thenHasContent) {
            indent(level);
            System.out.println("Entering a new block:");
        }

        exp.then.accept(this, level + 1);

        if (thenHasContent) {
            indent(level);
            System.out.println("Leaving the block");
        }

        if (exp.elseExp != null && !(exp.elseExp instanceof NilExp)) {
            boolean elseHasContent = exp.elseExp instanceof CompoundExp && (((CompoundExp) exp.elseExp).decs != null || ((CompoundExp) exp.elseExp).exps != null);
            if (elseHasContent) {
                indent(level);
                System.out.println("Entering a new block:");
            }

            exp.elseExp.accept(this, level + 1);

            if (elseHasContent) {
                indent(level);
                System.out.println("Leaving the block");
            }
        }
}

    public void visit(WhileExp exp, int level) {
        exp.test.accept(this, level + 1);
        if (!isInt(lastVisited) && !isBool(lastVisited)) {
            report_error("condition for while expression must be of boolean type");
        }

        boolean bodyHasContent = exp.body instanceof CompoundExp && (((CompoundExp) exp.body).decs != null || ((CompoundExp) exp.body).exps != null);
        if (bodyHasContent) {
            indent(level);
            System.out.println("Entering a new block:");
        }

        exp.body.accept(this, level + 1);

        if (bodyHasContent) {
            indent(level);
            System.out.println("Leaving the block");
        }
    }


    public void visit(ReturnExp exp, int level) {
        // Check if there is an expression to return
        if (exp.exp != null) {
            exp.exp.accept(this, level);  // Visit the return expression to determine its type
        }

        // Lookup the expected return type for the current function
        int expectedReturnType = lookup(currentFunc);  // This assumes currentFunc is correctly set to the name of the currently visited function
        if (expectedReturnType == -1) {
            // Handle error: Function not found in the table. This shouldn't normally happen if the table is maintained correctly.
            report_error("Function '" + currentFunc + "' not found in symbol table");
        } else if (exp.exp != null && lastVisited != expectedReturnType) {
            // If there's a return expression and its type doesn't match the function's declared return type, report an error
            report_error("Incompatible return type for function '" + currentFunc + "'");
        }
        // If exp.exp is null, this would mean a 'return;' statement in a void function, which should be compatible with a void return type
        // assuming your implementation sets `lastVisited` correctly in `visit(IntExp exp, int level)` and other visit methods for expressions.
    }



    public void visit(CompoundExp exp, int level) {
        // Check if there are any declarations or expressions
        boolean hasContent = (exp.decs != null && exp.decs.head != null) || (exp.exps != null && exp.exps.head != null);
        
        if (hasContent) {
            indent(level);
            System.out.println("Entering a new block:");
        }
        
        if (exp.decs != null) {
            exp.decs.accept(this, level);
        }
        
        if (exp.exps != null) {
            exp.exps.accept(this, level);
        }
        
        if (hasContent) {
            indent(level);
            System.out.println("Leaving the block");
        }
    }


    public void visit(FunctionDec exp, int level) {
       
        String funcReturnType = type(exp.typ); 
        // Convert NameTy to a string representation ("int", "bool", "void")

        indent(level);
        System.out.println("Entering the scope for function " + exp.func + ": " + funcReturnType);

        currentFunc = exp.func;
        insert(exp.func, new NodeType(exp.func, exp, level));

        // Increment level for parameters and body scope
        int newLevel = level + 1;

        // Visit parameters, if any
        if (exp.params != null) {
            VarDecList params = exp.params;
            while (params != null) {
                if (params.head != null) {
                    params.head.accept(this, newLevel); // This assumes your parameter nodes are visitable in the same way
                }
                params = params.tail;
            }
        }
        // Visit the body of the function
        if (exp.body != null) {
            exp.body.accept(this, newLevel); // Assuming CompoundExp or similar structure for the body
        }

        leaveScope(newLevel);
        indent(level);
        System.out.println("Leaving the scope for function " + exp.func);
        currentFunc = "";
    }


    public void visit(SimpleDec exp, int level) {

        // exp.typ.accept( this, level );

        if (isVoid(exp.typ.typ)) {
        report_error("cannot declare variable of type 'void'");
        }
        insert(exp.name, new NodeType(exp.name, exp, level));
        
        // if exp type = void, report error

    }

    public void visit(ArrayDec exp, int level) {

        if (isVoid(exp.typ.typ)) {
            report_error("cannot declare variable of type 'void'");
        }
        insert(exp.name, new NodeType(exp.name, exp, level));
  
		// exp.typ.accept( this, level );
        // if exp type = void, report error
    }

    public void visit(DecList exp, int level) {

        while( exp != null ) {
            if (exp.head != null) {
                exp.head.accept( this, level );
            }
            exp = exp.tail;
        }
    }

    public void visit(VarDecList exp, int level) {

        while( exp != null ) {
            if (exp.head != null) {
                exp.head.accept( this, level );
            }
            exp = exp.tail;
        }
    }


    public void visit(ExpList exp, int level) {

        while( exp != null ) {
            if (exp.head != null) {
                exp.head.accept( this, level );
            }
            exp = exp.tail;
		}
    }


    /*Helper Methods */

    /* Reports error */
    private void report_error(String message) {
        System.out.println("Type error: " + message);
    }

    /* Returns true if type is integer */
    private boolean isInt(int type) {
        return (type == NameTy.INT);
    }

    /* Returns true if type is boolean */
    private boolean isBool(int type) {
        return (type == NameTy.BOOL);
    }

    /* Returns true if type is void */
    private boolean isVoid(int type) {
        return (type == NameTy.VOID);
    }

    /* Prints each variable in specified scope then deletes from table */
    public void leaveScope(int scope) {

        List<SimpleEntry<String, NodeType>> toRemove = new ArrayList<>();
        Iterator<HashMap.Entry<String, ArrayList<NodeType>>> iter = table.entrySet().iterator();

        // Traverse table
        while(iter.hasNext()) {
        HashMap.Entry<String, ArrayList<NodeType>> entry = iter.next();

        // Traverse each ArrayList and only print if in scope
        for (NodeType node: entry.getValue()) {
            if (node.level == scope) {
                indent(scope);
                System.out.println(entry.getKey() + ":" + type(node.def.typ));
                toRemove.add(new SimpleEntry<>(entry.getKey(), node));
            }
        }
        }

        // Remove items that were printed
        for (SimpleEntry<String, NodeType> entry : toRemove) {
            delete(entry.getKey(), entry.getValue());
        }
    }

    /* Looks up ArrayList with specific key */
    private int lookup(String key) {
        if (table.containsKey(key)) {
        ArrayList<NodeType> id = table.get(key);
        NodeType recent = id.get(id.size() - 1);
        return recent.def.typ.typ;
        } else {
        return -1;
        }
    }
    
    /* Insert specified node at specified key in table */
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

    /* Deletes specified node at specified key in table */
    private void delete(String key, NodeType node) {
        if (table.containsKey(key)) {
        ArrayList<NodeType> nodeList = table.get(key);
        nodeList.remove(node);

        if (nodeList.isEmpty()) {
            table.remove(key);
        }
        }
    }

    /* Prints indentation based on scope */
    private void indent( int level ) {
            for ( int i = 0; i < level * SPACES; i++ ) System.out.print( " " );
        }

    /* Returns the string value of a NameTy type */
    private String type(NameTy type) {
        if (type.typ == NameTy.BOOL) {
        return "bool";
        } 
        if (type.typ == NameTy.INT) {
        return "int";
        }
        if (type.typ == NameTy.VOID) {
        return "void";
        }
        return null;
    }

}