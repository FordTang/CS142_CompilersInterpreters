package crux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import ast.Command;
import types.*;

public class Parser {
    public static String studentName = "Ford Tang";
    public static String studentID = "46564602";
    public static String uciNetID = "FordT1";
  
// Typing System ===================================
    
    private Type tryResolveType(String typeStr)
    {
        return Type.getBaseType(typeStr);
    }
    
 // Parser ==========================================
    private Scanner scanner;
    private Token currentToken;
    
    public Parser(Scanner scanner) throws IOException
    {
        this.scanner = scanner;
        currentToken = scanner.next();
    	//throw new RuntimeException("implement this");
    }
    
    public ast.Command parse() throws IOException
    {
        initSymbolTable();
        try {
            return program();
        } catch (QuitParseException q) {
            return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
        }
    }

// Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }
    
    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind());
    }
    
    private boolean accept(Token.Kind kind) throws IOException
    {
        if (have(kind)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }
    
    private boolean accept(NonTerminal nt) throws IOException
    {
        if (have(nt)) {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }

    private boolean expect(Token.Kind kind) throws IOException
    {
        if (accept(kind))
            return true;
        String errormessage = reportSyntaxError(kind);
        throw new QuitParseException(errormessage);
        //return false;
    }
    
    private boolean expect(NonTerminal nt) throws IOException
    {
        if (accept(nt))
            return true;
        String errormessage = reportSyntaxError(nt);
        throw new QuitParseException(errormessage);
        //return false;
    }
    
    private Token expectRetrieve(Token.Kind kind) throws IOException
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
        
    private Token expectRetrieve(NonTerminal nt) throws IOException
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }
   
 // Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();
    
    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
     
    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }
    
    public String errorReport()
    {
        return errorBuffer.toString();
    }
    
    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }
    
    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        public QuitParseException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    private int lineNumber()
    {
        return currentToken.lineNumber();
    }
    
    private int charPosition()
    {
        return currentToken.charPosition();
    }           
    
 // Grammar Rule Reporting ==========================================
    private int parseTreeRecursionDepth = 0;
    private StringBuffer parseTreeBuffer = new StringBuffer();

    public void enterRule(NonTerminal nonTerminal) {
        String lineData = new String();
        for(int i = 0; i < parseTreeRecursionDepth; i++)
        {
            lineData += "  ";
        }
        lineData += nonTerminal.name();
        //System.out.println("descending " + lineData);
        parseTreeBuffer.append(lineData + "\n");
        parseTreeRecursionDepth++;
    }
    
    private void exitRule(NonTerminal nonTerminal)
    {
        parseTreeRecursionDepth--;
    }
    
    public String parseTreeReport()
    {
        return parseTreeBuffer.toString();
    }
    
// SymbolTable Management ==========================
    private SymbolTable symbolTable;
    
    private void initSymbolTable()
    {
        symbolTable = new SymbolTable();
    }
    
    private void enterScope()
    {
        symbolTable = new SymbolTable(symbolTable);
    }
    
    private void exitScope()
    {
        symbolTable = symbolTable.parent();
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        //System.out.println(name);
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }
    
// Grammar Rules =====================================================
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public ast.Expression literal() throws IOException
    {
        ast.Expression expr;
        enterRule(NonTerminal.LITERAL);
        
        Token tok = expectRetrieve(NonTerminal.LITERAL);
        expr = Command.newLiteral(tok);
        
        exitRule(NonTerminal.LITERAL);
        return expr;
    }

    // program := declaration-list EOF .
    public ast.DeclarationList program() throws IOException
    {
    	ast.DeclarationList declarationList;
    	
    	enterRule(NonTerminal.PROGRAM);
    	
    	declarationList = declaration_list();
        expect(Token.Kind.EOF);
        
        exitRule(NonTerminal.PROGRAM);
        return declarationList;
        //throw new RuntimeException("add code to each grammar rule, to build as ast.");
    }    
    
    // designator := IDENTIFIER { "[" expression0 "]" } .
    public ast.Expression designator() throws IOException
    {
    	ast.Expression expression;
    	
        enterRule(NonTerminal.DESIGNATOR);

        Token token = expectRetrieve(Token.Kind.IDENTIFIER);
        Symbol symbol = tryResolveSymbol(token);
        
        expression = new ast.AddressOf(token.lineNumber(), token.charPosition(), symbol);
        
        while (accept(Token.Kind.OPEN_BRACKET))
        {
            expression = new ast.Index(currentToken.lineNumber(), currentToken.charPosition(), expression, expression0());
            expect(Token.Kind.CLOSE_BRACKET);
        }
        
        exitRule(NonTerminal.DESIGNATOR);
        
        return expression;
    }
    
    // type := IDENTIFIER .
    public types.Type type() throws IOException
    {
    	enterRule(NonTerminal.TYPE);
    	
    	Type type = tryResolveType(expectRetrieve(NonTerminal.TYPE).lexeme());
    	exitRule(NonTerminal.TYPE);
    	
    	return type;
	}

    // op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
    public Token op0() throws IOException
    {
    	enterRule(NonTerminal.OP0);
    	
    	Token token = expectRetrieve(NonTerminal.OP0);
    	
    	exitRule(NonTerminal.OP0);
    	
    	return token;
	}
    
    // op1 := "+" | "-" | "or" .
    public Token op1() throws IOException
    {
    	enterRule(NonTerminal.OP1);
    	
    	Token token = expectRetrieve(NonTerminal.OP1);
    	
    	exitRule(NonTerminal.OP1);
    	
    	return token;
	}
    
    // op2 := "*" | "/" | "and" .
    public Token op2() throws IOException
    {
    	enterRule(NonTerminal.OP2);
    	
    	Token token = expectRetrieve(NonTerminal.OP2);
    	
    	exitRule(NonTerminal.OP2);
    	
    	return token;
	}
    
    // call-expression := "::" IDENTIFIER "(" expression-list ")" .
    public ast.Call call_expression() throws IOException
    {    	
    	enterRule(NonTerminal.CALL_EXPRESSION);
    	
    	Token token = expectRetrieve(Token.Kind.CALL);
    	Symbol sym = tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.OPEN_PAREN);
        ast.ExpressionList args = expression_list();
        expect(Token.Kind.CLOSE_PAREN);
    	
    	exitRule(NonTerminal.CALL_EXPRESSION);
    	
    	return new ast.Call(token.lineNumber(), token.charPosition(), sym, args);
	}
    
    // expression-list := [ expression0 { "," expression0 } ] .
    public ast.ExpressionList expression_list() throws IOException
    {
    	ast.ExpressionList expressionList = new ast.ExpressionList(currentToken.lineNumber(), currentToken.charPosition());
    	
    	enterRule(NonTerminal.EXPRESSION_LIST);
    	
    	if (have(NonTerminal.EXPRESSION0))
    	{
    		expressionList.add(expression0());
    		while (accept(Token.Kind.COMMA))
    			expressionList.add(expression0());
    	}
    	
    	exitRule(NonTerminal.EXPRESSION_LIST);
    	
    	return expressionList;
	}
    
    /* expression3 := "not" expression3
    | "(" expression0 ")"
    | designator
    | call-expression
    | literal .
    */
    public ast.Expression expression3() throws IOException
    {
    	ast.Expression expression;
    	
    	enterRule(NonTerminal.EXPRESSION3);
    	
    	if (have(Token.Kind.NOT))
    	{
    		Token token = expectRetrieve(Token.Kind.NOT);
    		expression = new ast.LogicalNot(token.lineNumber(), token.charPosition(), expression3());
    	}
    	else if (accept(Token.Kind.OPEN_PAREN))
    	{
    		expression = expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	}
    	else if (have(NonTerminal.DESIGNATOR))
    		expression = new ast.Dereference(currentToken.lineNumber(), currentToken.charPosition(), designator());
    	else if (have(NonTerminal.CALL_EXPRESSION))
    		expression = call_expression();
    	else if (have(NonTerminal.LITERAL))
    		expression = literal();
    	else
    		throw new QuitParseException("Parser.expression3() problem!");
    	
    	
    	exitRule(NonTerminal.EXPRESSION3);
    	
    	return expression;
	}
    
    
    // expression2 := expression3 { op2 expression3 } .
    public ast.Expression expression2() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION2);
    	
    	ast.Expression expression = expression3();
    	while (have(NonTerminal.OP2))
    	{
    		Token op = op2();
    		expression = ast.Command.newExpression(expression, op, expression3());
    	}
    	
    	exitRule(NonTerminal.EXPRESSION2);
    	
    	return expression;
	}
    
    // expression1 := expression2 { op1  expression2 } .
    public ast.Expression expression1() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION1);
    	
    	ast.Expression expression = expression2();
    	while (have(NonTerminal.OP1))
    	{
    		Token op = op1();
    		expression = ast.Command.newExpression(expression, op, expression2());
    	}
    	
    	exitRule(NonTerminal.EXPRESSION1);
    	
    	return expression;
	}
    
    // expression0 := expression1 [ op0 expression1 ] .
    public ast.Expression expression0() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION0);
    	
    	ast.Expression expression = expression1();
    	if (have(NonTerminal.OP0))
    	{
    		Token op = op0();
    		expression = ast.Command.newExpression(expression, op, expression1());
    	}
    	
    	exitRule(NonTerminal.EXPRESSION0);
    	
    	return expression;
	}
    
    // parameter := IDENTIFIER ":" type .
    public Symbol parameter() throws IOException
    {
    	enterRule(NonTerminal.PARAMETER);
    	
    	Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.COLON);
    	symbol.setType(type());
    	
    	exitRule(NonTerminal.PARAMETER);
    	
    	return symbol;
	}
    
    // parameter-list := [ parameter { "," parameter } ] .
    public List<Symbol> parameter_list() throws IOException
    {
    	List<Symbol> parameterList = new LinkedList<Symbol>();
    	
    	enterRule(NonTerminal.PARAMETER_LIST);
    	
    	if (have(NonTerminal.PARAMETER))
    	{
    		parameterList.add(parameter());
    		while (accept(Token.Kind.COMMA))
    			parameterList.add(parameter());
    	}
    	
    	/*
    	if (!have(Token.Kind.CLOSE_PAREN))
    	{
    		parameter();
    		while (accept(Token.Kind.COMMA))
    			parameter();
    	}
    	
    	*/
    	
    	exitRule(NonTerminal.PARAMETER_LIST);
    	
    	return parameterList;
	}
    
    // variable-declaration := "var" IDENTIFIER ":" type ";"
    public ast.VariableDeclaration variable_declaration() throws IOException
    {    	
    	enterRule(NonTerminal.VARIABLE_DECLARATION);
    	
    	Token token = expectRetrieve(Token.Kind.VAR);
    	Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.COLON);
    	symbol.setType(type());
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.VARIABLE_DECLARATION);
    	
    	return new ast.VariableDeclaration(token.lineNumber(), token.charPosition(), symbol);
	}
    
    // array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
    public ast.ArrayDeclaration array_declaration() throws IOException
    {    	
    	enterRule(NonTerminal.ARRAY_DECLARATION);
    	
    	Token token = expectRetrieve(Token.Kind.ARRAY);
    	Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	
    	expect(Token.Kind.COLON);
    	Type type = type();
    	expect(Token.Kind.OPEN_BRACKET);
    	Token extent = expectRetrieve(Token.Kind.INTEGER);
    	symbol.setType(new types.ArrayType(Integer.parseInt(extent.lexeme()), type));
    	expect(Token.Kind.CLOSE_BRACKET);
    	while (accept(Token.Kind.OPEN_BRACKET))
    	{
            expect(Token.Kind.INTEGER);
            expect(Token.Kind.CLOSE_BRACKET);
        }    	
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.ARRAY_DECLARATION);
    	
    	return new ast.ArrayDeclaration(token.lineNumber(), token.charPosition(), symbol);
	}
    
    // function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
    public ast.FunctionDefinition function_definition() throws IOException
    {    	
    	enterRule(NonTerminal.FUNCTION_DEFINITION);
    	
    	Token func = expectRetrieve(Token.Kind.FUNC);
    	Symbol symbol = tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	
    	expect(Token.Kind.OPEN_PAREN);
    	enterScope();
    	List<Symbol> args = parameter_list();
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);
    	TypeList typelist = new TypeList();
    	for (Symbol arg : args)
    		typelist.add(arg.type());
    	symbol.setType(new FuncType(typelist, type()));
    	ast.StatementList body = statement_block();
    	exitScope();
    	
    	exitRule(NonTerminal.FUNCTION_DEFINITION);
    	
    	return new ast.FunctionDefinition(func.lineNumber(), func.charPosition(), symbol, args, body);
	}
    
    // declaration := variable-declaration | array-declaration | function-definition .
    public ast.Declaration declaration() throws IOException
    {
    	ast.Declaration declaration;
    	
    	enterRule(NonTerminal.DECLARATION);
    	
    	if (have(NonTerminal.VARIABLE_DECLARATION))
    		declaration = variable_declaration();
    	else if (have(NonTerminal.ARRAY_DECLARATION))
    		declaration = array_declaration();
    	else if (have(NonTerminal.FUNCTION_DEFINITION))
    		declaration = function_definition();
    	else
    		throw new QuitParseException("Parser.declaration() problem!");
    	
    	exitRule(NonTerminal.DECLARATION);
    	
    	return declaration;
    }
    
    // declaration-list := { declaration } .
    public ast.DeclarationList declaration_list() throws IOException
    {
    	ast.DeclarationList declarationList = new ast.DeclarationList(currentToken.lineNumber(), currentToken.charPosition());
    	
    	enterRule(NonTerminal.DECLARATION_LIST);
    	
    	while (have(NonTerminal.DECLARATION))
    		declarationList.add(declaration());
    	
    	exitRule(NonTerminal.DECLARATION_LIST);
    	
    	return declarationList;
    }
    
    // assignment-statement := "let" designator "=" expression0 ";"
    public ast.Assignment assignment_statement() throws IOException
    {
    	enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	
    	Token token = expectRetrieve(Token.Kind.LET);
    	ast.Expression dest = designator();
    	expect(Token.Kind.ASSIGN);
    	ast.Expression source = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	
    	return new ast.Assignment(token.lineNumber(), token.charPosition(), dest, source);
	}
    
    // call-statement := call-expression ";"
    public ast.Call call_statement() throws IOException
    {
    	ast.Call call;
    	
    	enterRule(NonTerminal.CALL_STATEMENT);
    	
    	call = call_expression();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.CALL_STATEMENT);
    	
    	return call;
	}
    
    // if-statement := "if" expression0 statement-block [ "else" statement-block ] .
    public ast.IfElseBranch if_statement() throws IOException
    {    	
    	enterRule(NonTerminal.IF_STATEMENT);
    	
    	Token token = expectRetrieve(Token.Kind.IF);
    	ast.Expression cond = expression0();
    	enterScope();
    	ast.StatementList thenBlock = statement_block();
    	exitScope();
    	ast.StatementList elseBlock = new ast.StatementList(currentToken.lineNumber(), currentToken.charPosition());
    	if (accept(Token.Kind.ELSE))
    	{
    		enterScope();
    		elseBlock = statement_block();
    		exitScope();
    	}
    	
    	exitRule(NonTerminal.IF_STATEMENT);
    	
    	return new ast.IfElseBranch(token.lineNumber(), token.charPosition(), cond, thenBlock, elseBlock);
	}
    
    // while-statement := "while" expression0 statement-block .
    public ast.WhileLoop while_statement() throws IOException
    {
    	enterRule(NonTerminal.WHILE_STATEMENT);
    	
    	Token token = expectRetrieve(Token.Kind.WHILE);
    	ast.Expression cond = expression0();
    	enterScope();
    	ast.StatementList body = statement_block();
    	exitScope();
    	
    	exitRule(NonTerminal.WHILE_STATEMENT);
    	
    	return new ast.WhileLoop(token.lineNumber(), token.charPosition(), cond, body);
	}
    
    // return-statement := "return" expression0 ";" .
    public ast.Return return_statement() throws IOException
    {
    	enterRule(NonTerminal.RETURN_STATEMENT);
    	
    	Token token = expectRetrieve(Token.Kind.RETURN);
    	ast.Expression arg = expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.RETURN_STATEMENT);
    	
    	return new ast.Return(token.lineNumber(), token.charPosition(), arg);
	}
    
    // statement-block := "{" statement-list "}" .
    public ast.StatementList statement_block() throws IOException
    {
    	ast.StatementList statementList;
    	
    	enterRule(NonTerminal.STATEMENT_BLOCK);
    	
    	expect(Token.Kind.OPEN_BRACE);
    	statementList = statement_list();
    	expect(Token.Kind.CLOSE_BRACE);
    	
    	exitRule(NonTerminal.STATEMENT_BLOCK);
    	
    	return statementList;
    }
    
    /*
    statement := variable-declaration
           | call-statement
           | assignment-statement
           | if-statement
           | while-statement
           | return-statement .
    */
    public ast.Statement statement() throws IOException
    {
    	ast.Statement statement;
    	
    	enterRule(NonTerminal.STATEMENT);
    	
    	if (have(NonTerminal.VARIABLE_DECLARATION))
    		statement = variable_declaration();
    	else if (have(NonTerminal.CALL_STATEMENT))
    		statement = call_statement();
    	else if (have(NonTerminal.ASSIGNMENT_STATEMENT))
    		statement = assignment_statement();
    	else if (have(NonTerminal.IF_STATEMENT))
    		statement = if_statement();
    	else if (have(NonTerminal.WHILE_STATEMENT))
    		statement = while_statement();
    	else if (have(NonTerminal.RETURN_STATEMENT))
    		statement = return_statement();
    	else
    		throw new QuitParseException("Parser.statement() problem!");
    	
    	exitRule(NonTerminal.STATEMENT);
    	
    	return statement;
    }
    
    // statement-list := { statement } .
    public ast.StatementList statement_list() throws IOException
    {
    	ast.StatementList statementList;
    	
    	statementList = new ast.StatementList(currentToken.lineNumber(), currentToken.charPosition());
    	
    	enterRule(NonTerminal.STATEMENT_LIST);
    	
    	while (have(NonTerminal.STATEMENT))
    		statementList.add(statement());
    	
    	exitRule(NonTerminal.STATEMENT_LIST);
    	
    	return statementList;
    }
}
