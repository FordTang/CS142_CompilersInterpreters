package types;

import java.util.HashMap;

import crux.Symbol;
import ast.*;

public class TypeChecker implements CommandVisitor {
    
    private HashMap<Command, Type> typeMap;
    private StringBuffer errorBuffer;
    private boolean haveReturn;
    private String currentFunctionName;
    private Type currentReturnType;

    /* Useful error strings:
     *
     * "Function " + func.name() + " has a void argument in position " + pos + "."
     * "Function " + func.name() + " has an error in argument in position " + pos + ": " + error.getMessage()
     *
     * "Function main has invalid signature."
     *
     * "Not all paths in function " + currentFunctionName + " have a return."
     *
     * "IfElseBranch requires bool condition not " + condType + "."
     * "WhileLoop requires bool condition not " + condType + "."
     *
     * "Function " + currentFunctionName + " returns " + currentReturnType + " not " + retType + "."
     *
     * "Variable " + varName + " has invalid type " + varType + "."
     * "Array " + arrayName + " has invalid base type " + baseType + "."
     */

    public TypeChecker()
    {
        typeMap = new HashMap<Command, Type>();
        errorBuffer = new StringBuffer();
    }

    private void reportError(int lineNum, int charPos, String message)
    {
        errorBuffer.append("TypeError(" + lineNum + "," + charPos + ")");
        errorBuffer.append("[" + message + "]" + "\n");
    }

    private void put(Command node, Type type)
    {
        if (type instanceof ErrorType) {
            reportError(node.lineNumber(), node.charPosition(), ((ErrorType)type).getMessage());
        }
        typeMap.put(node, type);
    }
    
    public Type getType(Command node)
    {
        return typeMap.get(node);
    }
    
    public boolean check(Command ast)
    {
        ast.accept(this);
        return !hasError();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }

    @Override
    public void visit(ExpressionList node) {
        types.TypeList typeList = new types.TypeList();
        
        for (ast.Expression expression : node)
        {
        	expression.accept(this);
        	typeList.append(getType((Command) expression));
        }
        
        put(node, typeList);
    }

    @Override
    public void visit(DeclarationList node) {
        for (ast.Declaration declaration : node)
        	declaration.accept(this);
    }

    @Override
    public void visit(StatementList node) {
        for (ast.Statement statement : node)
        	statement.accept(this);
    }

    @Override
    public void visit(AddressOf node) {
        put(node, new types.AddressType(node.symbol().type()));
    }

    @Override
    public void visit(LiteralBool node) {
        put(node, new types.BoolType());
    }

    @Override
    public void visit(LiteralFloat node) {
        put(node, new types.FloatType());
    }

    @Override
    public void visit(LiteralInt node) {
        put(node, new types.IntType());
    }

    @Override
    public void visit(VariableDeclaration node) {
    	String varName = node.symbol().name();
    	Type varType = node.symbol().type();
    	if ((node.symbol().type()) instanceof types.VoidType)
    		put(node, new ErrorType("Variable " + varName + " has invalid type " + varType + "."));
    	put(node, varType);
    }

    @Override
    public void visit(ArrayDeclaration node) {
    	String arrayName = node.symbol().name();
    	Type baseType = node.symbol().type();
    	if (((types.ArrayType)node.symbol().type()).base() instanceof types.VoidType)
    		put(node, new ErrorType("Array " + arrayName + " has invalid base type " + baseType + "."));
    	put(node, baseType);
    }

    @Override
    public void visit(FunctionDefinition node) {
    	currentFunctionName = node.function().name();
    	haveReturn = false;
    	currentReturnType = ((types.FuncType)node.function().type()).returnType();
    	
        if (node.function().name().equals("main"))
        {
        	if (!(((types.FuncType)node.function().type()).returnType() instanceof types.VoidType))
        	{
        		put(node, new ErrorType("Function main has invalid signature."));
        		return;
        	}
        }
        
    	for (int i = 0; i < node.arguments().size(); i++)
    	{
    		if (node.arguments().get(i).type() instanceof types.VoidType)
    		{
    			put(node, new ErrorType("Function " + currentFunctionName + " has a void argument in position " + i + "."));
    			return;
    		}
    		else if (node.arguments().get(i).type() instanceof types.ErrorType)
    		{
    			put(node, new ErrorType("Function " + currentFunctionName + " has an error in argument in position " + i + ": " + ((ErrorType)node.arguments().get(i).type()).getMessage()));
    			return;
    		}
    	}
    	
    	visit(node.body());
    	
    	if (!(currentReturnType instanceof types.VoidType) && !haveReturn)
    	{
    		put(node, new ErrorType("Not all paths in function " + currentFunctionName + " have a return."));
    		return;
    	}
    	put(node, currentReturnType);
    	
    	//for (Statement statement : node.body())
        //{
        //	if (statement.
        //}
//        * "Not all paths in function " + currentFunctionName + " have a return."

    }

    @Override
    public void visit(Comparison node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).compare(getType((Command) node.rightSide())));
    }
    
    @Override
    public void visit(Addition node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).add(getType((Command) node.rightSide())));
    }
    
    @Override
    public void visit(Subtraction node) {
    	node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).sub(getType((Command) node.rightSide())));
    }
    
    @Override
    public void visit(Multiplication node) {
    	node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).mul(getType((Command) node.rightSide())));
    }
    
    @Override
    public void visit(Division node) {
    	node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).div(getType((Command) node.rightSide())));
    }
    
    @Override
    public void visit(LogicalAnd node) {
    	node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).and(getType((Command) node.rightSide())));
    }

    @Override
    public void visit(LogicalOr node) {
        node.leftSide().accept(this);
        node.rightSide().accept(this);
        put(node, getType((Command) node.leftSide()).or(getType((Command) node.rightSide())));
    }

    @Override
    public void visit(LogicalNot node) {
        node.expression().accept(this);
        put(node, getType((Command) node.expression()).not());        
    }
    
    @Override
    public void visit(Dereference node) {
        node.expression().accept(this);
        put(node, getType((Command) node.expression()).deref());
    }

    @Override
    public void visit(Index node) {
    	node.base().accept(this);
        node.amount().accept(this);
        put(node, getType((Command) node.base()).index(getType((Command) node.amount())));
    }

    @Override
    public void visit(Assignment node) {
    	node.destination().accept(this);
        node.source().accept(this);
        put(node, getType((Command) node.destination()).assign(getType((Command) node.source())));
    }

    @Override
    public void visit(Call node) {
    	node.arguments().accept(this);
    	put(node, currentReturnType);
    }

    @Override
    public void visit(IfElseBranch node) {
    	node.condition().accept(this);
    	Type condType = getType((Command)node.condition());
    	if (!(condType.equivalent(new types.BoolType())))
    	{
    		put(node, new ErrorType("IfElseBranch requires bool condition not " + condType + "."));
    		return;
    	}
    	visit(node.thenBlock());
    	visit(node.elseBlock());    	
    }

    @Override
    public void visit(WhileLoop node) {
    	node.condition().accept(this);
    	Type condType = getType((Command)node.condition());
    	if (!(condType instanceof types.BoolType))
    	{
    		put(node, new ErrorType("WhileLoop requires bool condition not " + condType + "."));
    		return;
    	}
    	visit(node.body());
    }

    @Override
    public void visit(Return node) {
    	haveReturn = true;
    	node.argument().accept(this);
    	Type retType = getType((Command)node.argument());
    	if (!(currentReturnType.equivalent(retType)))
    	{
    		put(node, new ErrorType("Function " + currentFunctionName + " returns " + currentReturnType + " not " + retType + "."));
    		return;
    	}
    	put (node, retType);
    	
    	
    }

    @Override
    public void visit(ast.Error node) {
        put(node, new ErrorType(node.message()));
    }
}
