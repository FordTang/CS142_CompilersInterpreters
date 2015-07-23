package crux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    public static String studentName = "Ford Tang";
    public static String studentID = "46564602";
    public static String uciNetID = "FordT1";
    
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
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }
        
    private boolean expect(NonTerminal nt) throws IOException
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
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
              
// Parser ==========================================
    private Scanner scanner;
    private Token currentToken;
    
    public Parser(Scanner scanner) throws IOException
    {
        this.scanner = scanner;
        currentToken = scanner.next();
    	//throw new RuntimeException("implement this");
    }
    
    public void parse() throws IOException
    {
        initSymbolTable();
        try {
            program();
        } catch (QuitParseException q) {
            errorBuffer.append("SyntaxError(" + lineNumber() + "," + charPosition() + ")");
            errorBuffer.append("[Could not complete parsing.]");
        }
    }

 // designator := IDENTIFIER { "[" expression0 "]" } .
    public void designator() throws IOException
    {
        enterRule(NonTerminal.DESIGNATOR);

        tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
        while (accept(Token.Kind.OPEN_BRACKET))
        {
            expression0();
            expect(Token.Kind.CLOSE_BRACKET);
        }
        
        exitRule(NonTerminal.DESIGNATOR);
    }
    
    public void type() throws IOException
    {
    	enterRule(NonTerminal.TYPE);
    	
    	expect(NonTerminal.TYPE);
    	
    	exitRule(NonTerminal.TYPE);
	}
    
    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public void literal() throws IOException
    {
        enterRule(NonTerminal.LITERAL);
        
        expect(NonTerminal.LITERAL);
        
        exitRule(NonTerminal.LITERAL);
        
    	//throw new RuntimeException("implement this");
    }    

    public void op0() throws IOException
    {
    	enterRule(NonTerminal.OP0);
    	
    	expect(NonTerminal.OP0);
    	
    	exitRule(NonTerminal.OP0);
	}
    
    public void op1() throws IOException
    {
    	enterRule(NonTerminal.OP1);
    	
    	expect(NonTerminal.OP1);
    	
    	exitRule(NonTerminal.OP1);
	}
    
    public void op2() throws IOException
    {
    	enterRule(NonTerminal.OP2);
    	
    	expect(NonTerminal.OP2);
    	
    	exitRule(NonTerminal.OP2);
	}
    
    public void call_expression() throws IOException
    {
    	enterRule(NonTerminal.CALL_EXPRESSION);
    	
    	expect(Token.Kind.CALL);
    	tryResolveSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.OPEN_PAREN);
        expression_list();
        expect(Token.Kind.CLOSE_PAREN);
    	
    	exitRule(NonTerminal.CALL_EXPRESSION);
	}
    
    public void expression_list() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION_LIST);
    	
    	if (have(NonTerminal.EXPRESSION0))
    	{
    		expression0();
    		while (accept(Token.Kind.COMMA))
    			expression0();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION_LIST);
	}
    
    public void expression3() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION3);
    	
    	if (accept(Token.Kind.NOT))
    		expression3();
    	else if (accept(Token.Kind.OPEN_PAREN))
    	{
    		expression0();
    		expect(Token.Kind.CLOSE_PAREN);
    	}
    	else if (have(NonTerminal.DESIGNATOR))
    		designator();
    	else if (have(NonTerminal.CALL_EXPRESSION))
    		call_expression();
    	else if (have(NonTerminal.LITERAL))
    		literal();
    	else
    		throw new QuitParseException("Parser.expression3() problem!");
    	
    	
    	exitRule(NonTerminal.EXPRESSION3);
	}
    
    public void expression2() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION2);
    	
    	expression3();
    	while (have(NonTerminal.OP2))
    	{
    		op2();
    		expression3();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION2);
	}
    
    public void expression1() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION1);
    	
    	expression2();
    	while (have(NonTerminal.OP1))
    	{
    		op1();
    		expression2();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION1);
	}
    
    public void expression0() throws IOException
    {
    	enterRule(NonTerminal.EXPRESSION0);
    	
    	expression1();
    	if (have(NonTerminal.OP0))
    	{
    		op0();
    		expression1();
    	}
    	
    	exitRule(NonTerminal.EXPRESSION0);
	}
    
    public void parameter() throws IOException
    {
    	enterRule(NonTerminal.PARAMETER);
    	
    	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.COLON);
    	type();
    	
    	exitRule(NonTerminal.PARAMETER);
	}
    
    public void parameter_list() throws IOException
    {
    	enterRule(NonTerminal.PARAMETER_LIST);
    	
    	if (have(NonTerminal.PARAMETER))
    	{
    		parameter();
    		while (accept(Token.Kind.COMMA))
    			parameter();
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
	}
    
    public void variable_declaration() throws IOException
    {
    	enterRule(NonTerminal.VARIABLE_DECLARATION);
    	
    	expect(Token.Kind.VAR);
    	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.VARIABLE_DECLARATION);
	}
    
    public void array_declaration() throws IOException
    {
    	enterRule(NonTerminal.ARRAY_DECLARATION);
    	
    	expect(Token.Kind.ARRAY);
    	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.COLON);
    	type();
    	expect(Token.Kind.OPEN_BRACKET);
    	expect(Token.Kind.INTEGER);
    	expect(Token.Kind.CLOSE_BRACKET);
    	while (accept(Token.Kind.OPEN_BRACKET))
    	{
            expect(Token.Kind.INTEGER);
            expect(Token.Kind.CLOSE_BRACKET);
        }    	
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.ARRAY_DECLARATION);
	}
    
    public void function_definition() throws IOException
    {
    	enterRule(NonTerminal.FUNCTION_DEFINITION);
    	
    	expect(Token.Kind.FUNC);
    	tryDeclareSymbol(expectRetrieve(Token.Kind.IDENTIFIER));
    	expect(Token.Kind.OPEN_PAREN);
    	enterScope();
    	parameter_list();
    	expect(Token.Kind.CLOSE_PAREN);
    	expect(Token.Kind.COLON);
    	type();    	
    	statement_block();
    	exitScope();
    	
    	exitRule(NonTerminal.FUNCTION_DEFINITION);
	}
    
    public void declaration() throws IOException
    {
    	enterRule(NonTerminal.DECLARATION);
    	
    	if (have(NonTerminal.VARIABLE_DECLARATION))
    		variable_declaration();
    	else if (have(NonTerminal.ARRAY_DECLARATION))
    		array_declaration();
    	else if (have(NonTerminal.FUNCTION_DEFINITION))
    		function_definition();
    	else
    		throw new QuitParseException("Parser.declaration() problem!");
    	
    	exitRule(NonTerminal.DECLARATION);
    }
    
    public void declaration_list() throws IOException
    {
    	enterRule(NonTerminal.DECLARATION_LIST);
    	
    	while (have(NonTerminal.DECLARATION))
    		declaration();
    	
    	exitRule(NonTerminal.DECLARATION_LIST);
    }
    
    public void assignment_statement() throws IOException
    {
    	enterRule(NonTerminal.ASSIGNMENT_STATEMENT);
    	
    	expect(Token.Kind.LET);    	
    	designator();    	
    	expect(Token.Kind.ASSIGN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.ASSIGNMENT_STATEMENT);
	}
    
    public void call_statement() throws IOException
    {
    	enterRule(NonTerminal.CALL_STATEMENT);
    	
    	call_expression();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.CALL_STATEMENT);
	}
    
    public void if_statement() throws IOException
    {
    	enterRule(NonTerminal.IF_STATEMENT);
    	
    	expect(Token.Kind.IF);
    	expression0();
    	enterScope();
    	statement_block();
    	exitScope();
    	if (accept(Token.Kind.ELSE))
    	{
    		enterScope();
    		statement_block();
    		exitScope();
    	}
    	
    	exitRule(NonTerminal.IF_STATEMENT);
	}
    
    public void while_statement() throws IOException
    {
    	enterRule(NonTerminal.WHILE_STATEMENT);
    	
    	expect(Token.Kind.WHILE);
    	expression0();
    	enterScope();
    	statement_block();
    	exitScope();
    	
    	exitRule(NonTerminal.WHILE_STATEMENT);
	}
    
    public void return_statement() throws IOException
    {
    	enterRule(NonTerminal.RETURN_STATEMENT);
    	
    	expect(Token.Kind.RETURN);
    	expression0();
    	expect(Token.Kind.SEMICOLON);
    	
    	exitRule(NonTerminal.RETURN_STATEMENT);
	}
    
    public void statement_block() throws IOException
    {
    	enterRule(NonTerminal.STATEMENT_BLOCK);
    	
    	expect(Token.Kind.OPEN_BRACE);
    	statement_list();
    	expect(Token.Kind.CLOSE_BRACE);
    	
    	exitRule(NonTerminal.STATEMENT_BLOCK);
    }
    
    public void statement() throws IOException
    {
    	enterRule(NonTerminal.STATEMENT);
    	
    	if (have(NonTerminal.VARIABLE_DECLARATION))
    		variable_declaration();
    	else if (have(NonTerminal.CALL_STATEMENT))
    		call_statement();
    	else if (have(NonTerminal.ASSIGNMENT_STATEMENT))
    		assignment_statement();
    	else if (have(NonTerminal.IF_STATEMENT))
    		if_statement();
    	else if (have(NonTerminal.WHILE_STATEMENT))
    		while_statement();
    	else if (have(NonTerminal.RETURN_STATEMENT))
    		return_statement();
    	else
    		throw new QuitParseException("Parser.statement() problem!");
    	
    	exitRule(NonTerminal.STATEMENT);
    }
    
    public void statement_list() throws IOException
    {
    	enterRule(NonTerminal.STATEMENT_LIST);
    	
    	while (have(NonTerminal.STATEMENT))
    		statement();
    	
    	exitRule(NonTerminal.STATEMENT_LIST);
    }
    
	// program := declaration-list EOF .
    public void program() throws IOException
    {
        enterRule(NonTerminal.PROGRAM);
        
        declaration_list();
        expect(Token.Kind.EOF);
        
        exitRule(NonTerminal.PROGRAM);
        //throw new RuntimeException("implement all the grammar rules");
    }
}
