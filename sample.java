// import java.util.ArrayList;
// import java.util.HashMap;
// // import java.util.Iterator;
// import absyn.*;

// public class SemanticAnalyzer implements AbsynVisitor {

// 	public static boolean parseError = false;
// 	private HashMap<String, ArrayList<NodeType>> table;

//     public SemanticAnalyzer() {
//         table = new HashMap<>();
//     }

//     @Override
//     public void visit(NameTy exp, int level) {
//         // Set the type of the type expression based on the encountered type.
//         if (exp.typ == NameTy.INT) {
//             exp.type = INT; // Set the internal representation of the type to INTEGER.
//         } else if (exp.typ == NameTy.BOOL) {
//             exp.type = BOOL; // Set the internal representation of the type to BOOLEAN.
//         }
//     }

//     @Override
//     public void visit(SimpleVar exp, int level) {
//         // Check if the variable has been declared.
//         if (!isDeclared(exp.name, level)) {
//             System.err.println("Semantic Error: Variable '" + exp.name + "' has not been declared.");
//         } else {
//             System.out.println("Accessed SimpleVar: " + exp.name + " at level " + level);
//         }
//     }

//     @Override
//     public void visit(IndexVar exp, int level) {
//         // Check if the array variable has been declared.
//         if (!isDeclared(exp.name, level)) {
//             System.err.println("Semantic Error: Array '" + exp.name + "' has not been declared.");
//         } else {
//             System.out.println("Accessed IndexVar: " + exp.name + " at level " + level);
//         }
//     }

//     private boolean isDeclared(String name, int level) {
//         ArrayList<NodeType> declarations = table.get(name);
//         if (declarations == null) return false;
//         // Check for a declaration at this or a higher (outer) scope level.
//         return declarations.stream().anyMatch(nt -> nt.level <= level);
//     }


//     @Override
//     public void visit(NilExp exp, int level) {
//     }

//     @Override
//     public void visit(IntExp exp, int level) {
//         exp.type = INT; 
//         System.out.println("IntExp: " + exp.value + " with type: INTEGER");
//     }


//     @Override
//     public void visit(BoolExp exp, int level) {
//         exp.type = BOOL;
//         System.out.println("BoolExp: " + exp.value + " with type: BOOLEAN");
//     }
//     @Override
//     public void visit(VarExp exp, int level) {
//         if (!isDeclared(exp.name, level)) {
//             System.err.println("Semantic Error: Variable '" + exp.name + "' is used before declaration.");
//         } else {
//             // If checking types is part of your semantic analysis, you might retrieve the variable's type
//             // from the symbol table and set it on the VarExp for later use:
//             NodeType nodeType = findNodeType(exp.name, level);
//             if (nodeType != null) {
//                 exp.type = nodeType.def; // Assuming 'def' holds type information
//             }
//             System.out.println("Variable accessed: " + exp.name + " at level " + level);
//         }
//     }
//     private NodeType findNodeType(String name, int level) {
//         // This method would search the symbol table for the most recent declaration
//         // of 'name' accessible at 'level', and return its NodeType.
//         ArrayList<NodeType> types = table.get(name);
//         if (types != null) {
//             // Iterate in reverse to find the most recent declaration in scope.
//             for (int i = types.size() - 1; i >= 0; i--) {
//                 if (types.get(i).level <= level) {
//                     return types.get(i);
//                 }
//             }
//         }
//         return null; // Not found
//     }
//     @Override
//     public void visit(CallExp exp, int level) {
//         // Check if the function has been declared
//         ArrayList<NodeType> funcDecls = table.get(exp.func);
//         if (funcDecls == null || funcDecls.isEmpty()) {
//             System.err.println("Semantic Error: Function '" + exp.func + "' is not declared.");
//             return;
//         }

//         // Assuming the last declaration is the one we're interested in (in case of overloading in different scopes)
//         NodeType funcDecl = funcDecls.get(funcDecls.size() - 1);
//         if (!(funcDecl.def instanceof FunctionDec)) {
//             System.err.println("Semantic Error: '" + exp.func + "' is not a function.");
//             return;
//         }

//         FunctionDec funcDef = (FunctionDec) funcDecl.def;

//         // Check if the number of arguments matches
//         if (funcDef.params.size() != exp.args.size()) {
//             System.err.println("Semantic Error: Function '" + exp.func + "' called with incorrect number of arguments.");
//             return;
//         }

//         // Iterate over the arguments and check their types against the function's parameters
//         for (int i = 0; i < exp.args.size(); i++) {
//             VarDec paramDec = funcDef.params.get(i); // Assuming this is how you get a parameter declaration
//             Exp argExp = exp.args.get(i); // The actual argument expression

//             // You need to evaluate the type of the argument expression and compare it to the parameter's type
//             // This is a simplified example. You would have more complex logic here to actually evaluate and compare types
//             ExpType argType = evaluateExpType(argExp);
//             if (!typeEqual(argType, paramDec.typ.type)) {
//                 System.err.println("Semantic Error: Type mismatch in arguments for function '" + exp.func + "'.");
//                 return;
//             }
//         }

//         // All checks passed, you can optionally set the type of the CallExp to the return type of the function
//         exp.type = funcDef.typ.type; // Assuming the FunctionDec has a 'typ' field for return type
//     }

//     private ExpType evaluateExpType(Exp exp) {
//         // This method should determine the type of an expression
//         // Implementing this method depends on your language's type system and AST structure
//         // Placeholder return value
//         return null;
//     }

//     private boolean typeEqual(ExpType type1, ExpType type2) {
//         // This method checks if two types are equal
//         // Implementing this method depends on how types are represented in your system
//         // Placeholder return value
//         return type1.equals(type2);
//     }

//     @Override
//     public void visit(OpExp exp, int level) {
//         if (exp.oper == OpExp.OR) { // Assuming OpExp.OR represents the 'or' operator
//             // Evaluate the types of the left and right expressions
//             ExpType leftType = evaluateExpType(exp.left);
//             ExpType rightType = evaluateExpType(exp.right);

//             // Check if both operands are of boolean type
//             if (!typeEqual(leftType, BOOL) || !typeEqual(rightType, BOOL)) {
//                 throw new TypeError("Type error in 'or' expression: both operands must be boolean");
//             }

//             // If both are boolean, the result is also boolean
//             exp.type = BOOL; 
//             System.out.println("'Or' expression type set to boolean");
//         } else {
//             // Handle other binary operations...
//         }
//     }

//     @Override
//     public void visit(AssignExp exp, int level) {
//         NodeType varType = lookup(exp.lhs.name); // Retrieve the variable's declared type from the symbol table
//         ExpType expType = evaluateExpType(exp.rhs); // Evaluate the type of the RHS expression

//         if (!typeEqual(varType, expType)) {
//             throw new TypeError("Type error in assignment to " + exp.lhs.name);
//         }

//         exp.type = varType; // Set the type of the statement to the variable's type
//     }

//     @Override
//     public void visit(IfExp exp, int level) {
//         ExpType conditionType = evaluateExpType(exp.test);
//         if (!typeEqual(conditionType, BOOL)) {
//             throw new TypeError("Type error in if statement condition: expected boolean but got " + conditionType);
//         }

//         // Proceed to check the then and else parts of the statement
//         exp.thenPart.accept(this, level + 1);
//         if (exp.elsePart != null) {
//             exp.elsePart.accept(this, level + 1);
//         }
//     }


//     @Override
//     public void visit(WhileExp exp, int level) {
//         ExpType conditionType = evaluateExpType(exp.test);
//         if (!typeEqual(conditionType, BOOL)) {
//             System.err.println("Semantic Error: Condition in 'while' statement must be boolean.");
//         }

//         // Enter new scope for the loop body if your language design requires it
//         enterScope();
//         exp.body.accept(this, level + 1);
//         exitScope();
//     }

//     @Override
//     public void visit(ReturnExp exp, int level) {
//         // Assume 'currentFunctionReturnType' is accessible and holds the return type of the current function
//         if (exp.exp != null) { // If there is a return value
//             ExpType returnType = evaluateExpType(exp.exp);
//             if (!typeEqual(returnType, currentFunctionReturnType)) {
//                 System.err.println("Semantic Error: Return expression type does not match function return type.");
//             }
//         } else if (!typeEqual(currentFunctionReturnType, VOID)) { // No return value provided, but function expects one
//             System.err.println("Semantic Error: Function expects a return value.");
//         }
//     }


//     @Override
//     public void visit(CompoundExp exp, int level) {
//         // Enter a new scope for the block
//         enterScope();
//         // Visit declarations
//         for (VarDec dec : exp.decs) {
//             dec.accept(this, level + 1);
//         }
//         // Visit statements
//         for (Stmt stmt : exp.stmts) {
//             stmt.accept(this, level + 1);
//         }
//         // Exit the block's scope
//         exitScope();
//     }

//     @Override
//     public void visit(FunctionDec exp, int level) {
//         // Similar logic as for SimpleDec
//         ArrayList<NodeType> list = table.get(exp.func);
//         if (list != null) {
//             for (NodeType nt : list) {
//                 if (nt.level == level) {
//                     System.err.println("Semantic Error: Function '" + exp.func + "' is already defined in this scope.");
//                     return;
//                 }
//             }
//         }
//         list = table.computeIfAbsent(exp.func, k -> new ArrayList<>());
//         list.add(new NodeType(exp.func, exp, level));
//         System.out.println("Added function declaration to table: " + exp.func + " at level " + level);
//     }

//     @Override
//     public void visit(SimpleDec exp, int level) {
//         // Assuming 'exp.typ' is a NameTy object that has already been visited and has its type set.
//         // Insert the variable and its type into the symbol table.
//         insert(exp.name, exp.typ.type);
//         System.out.println("Declared variable: " + exp.name + " with type: " + exp.typ.type);
//     }

//     @Override
//     public void visit(ArrayDec exp, int level) {
//         // Assuming 'exp.size' is the size of the array and 'exp.typ' is the type of elements.
//         // The 'makeTypeNode' logic would be specific to how you're representing types.
//         TypeNode arrayType = makeTypeNode("array", exp.size, exp.typ.type);
//         insert(exp.name, arrayType);
//         System.out.println("Declared array: " + exp.name + " of type: " + arrayType);
//     }


//     @Override
//     public void visit(DecList exp, int level) {
//         for (Dec dec : exp) { // Assuming DecList can be iterated over
//             dec.accept(this, level);
//         }
//     }

//     @Override
//     public void visit(VarDecList exp, int level) {
//         for (VarDec varDec : exp) { // Assuming VarDecList can be iterated over
//             varDec.accept(this, level);
//         }
//     }


//     @Override
//     public void visit(ExpList exp, int level) {
//         for (Exp expression : exp) { // Assuming ExpList can be iterated over
//             expression.accept(this, level);
//             // After visiting, you may want to check expression.type for type checking,
//             // especially if this list is part of a function call or similar structure.
//         }
//     }

// }