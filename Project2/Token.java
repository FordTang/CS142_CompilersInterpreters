package crux;

public class Token {
	
	public static enum Kind {
		AND("and"),
		OR("or"),
		NOT("not"),
		
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF(),
		
		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return"),
		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::");		
		
		private String default_lexeme;
		
		Kind()
		{
			default_lexeme = "";
		}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return default_lexeme != null;
		}
		
		// OPTIONAL: if you wish to also make convenience functions, feel free
		//           for example, boolean matches(String lexeme)
		//           can report whether a Token.Kind has the given lexeme
	}
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	
	// OPTIONAL: implement factory functions for some tokens, as you see fit
    
	public static Token EOF(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EOF;
		return tok;
	}
	
	public static Token Integer(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.INTEGER;
		tok.lexeme = lexeme;
		return tok;
	}
	
	public static Token Float(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.FLOAT;
		tok.lexeme = lexeme;
		return tok;
	}
	
	public static Token Identifier(String lexeme, int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.IDENTIFIER;
		tok.lexeme = lexeme;
		return tok;
	}
	
	/*
	public static Token OpenParen(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_PAREN;
		return tok;
	}
	
	public static Token CloseParen(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_PAREN;
		return tok;
	}
	
	public static Token OpenBrace(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_BRACE;
		return tok;
	}
	
	public static Token CloseBrace(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_BRACE;
		return tok;
	}
	
	public static Token OpenBracket(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.OPEN_BRACKET;
		return tok;
	}
	
	public static Token CloseBracket(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CLOSE_BRACKET;
		return tok;
	}
	
	public static Token Add(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.ADD;
		return tok;
	}
	
	public static Token Sub(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.SUB;
		return tok;
	}
	
	public static Token Mul(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.MUL;
		return tok;
	}
	
	public static Token Div(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.DIV;
		return tok;
	}
	
	public static Token Assign(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.ASSIGN;
		return tok;
	}
	
	public static Token Comma(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.COMMA;
		return tok;
	}
	
	public static Token SemiColon(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.SEMICOLON;
		return tok;
	}
	
	public static Token Colon(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.COLON;
		return tok;
	}
	
	public static Token Call(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.CALL;
		return tok;
	}
	
	public static Token GreaterThan(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.GREATER_THAN;
		return tok;
	}
	
	public static Token GreaterEqual(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.GREATER_EQUAL;
		return tok;
	}
	
	public static Token LessThan(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.LESS_THAN;
		return tok;
	}
	
	public static Token LesserEqual(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.LESSER_EQUAL;
		return tok;
	}
	
	public static Token NotEqual(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.NOT_EQUAL;
		return tok;
	}
	
	public static Token Equal(int linePos, int charPos)
	{
		Token tok = new Token(linePos, charPos);
		tok.kind = Kind.EQUAL;
		return tok;
	}
	*/
	
	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}
	
	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// TODO: based on the given lexeme determine and set the actual kind
		
		switch (lexeme)
		{
			case "and":
				this.kind = Kind.AND;
				this.lexeme = lexeme;
				break;
			case "or":
				this.kind = Kind.OR;
				this.lexeme = lexeme;
				break;
			case "not":
				this.kind = Kind.NOT;
				this.lexeme = lexeme;
				break;
			case "let":
				this.kind = Kind.LET;
				this.lexeme = lexeme;
				break;
			case "var":
				this.kind = Kind.VAR;
				this.lexeme = lexeme;
				break;
			case "array":
				this.kind = Kind.ARRAY;
				this.lexeme = lexeme;
				break;
			case "func":
				this.kind = Kind.FUNC;
				this.lexeme = lexeme;
				break;
			case "if":
				this.kind = Kind.IF;
				this.lexeme = lexeme;
				break;
			case "else":
				this.kind = Kind.ELSE;
				this.lexeme = lexeme;
				break;
			case "while":
				this.kind = Kind.WHILE;
				this.lexeme = lexeme;
				break;
			case "true":
				this.kind = Kind.TRUE;
				this.lexeme = lexeme;
				break;
			case "false":
				this.kind = Kind.FALSE;
				this.lexeme = lexeme;
				break;
			case "return":
				this.kind = Kind.RETURN;
				this.lexeme = lexeme;
				break;
			case "(":
				this.kind = Kind.OPEN_PAREN;
				this.lexeme = lexeme;
				break;
			case ")":
				this.kind = Kind.CLOSE_PAREN;
				this.lexeme = lexeme;
				break;
			case "{":
				this.kind = Kind.OPEN_BRACE;
				this.lexeme = lexeme;
				break;
			case "}":
				this.kind = Kind.CLOSE_BRACE;
				this.lexeme = lexeme;
				break;
			case "[":
				this.kind = Kind.OPEN_BRACKET;
				this.lexeme = lexeme;
				break;
			case "]":
				this.kind = Kind.CLOSE_BRACKET;
				this.lexeme = lexeme;
				break;
			case "+":
				this.kind = Kind.ADD;
				this.lexeme = lexeme;
				break;
			case "-":
				this.kind = Kind.SUB;
				this.lexeme = lexeme;
				break;
			case "*":
				this.kind = Kind.MUL;
				this.lexeme = lexeme;
				break;
			case "/":
				this.kind = Kind.DIV;
				this.lexeme = lexeme;
				break;
			case ">=":
				this.kind = Kind.GREATER_EQUAL;
				this.lexeme = lexeme;
				break;
			case "<=":
				this.kind = Kind.LESSER_EQUAL;
				this.lexeme = lexeme;
				break;
			case "!=":
				this.kind = Kind.NOT_EQUAL;
				this.lexeme = lexeme;
				break;
			case "==":
				this.kind = Kind.EQUAL;
				this.lexeme = lexeme;
				break;
			case ">":
				this.kind = Kind.GREATER_THAN;
				this.lexeme = lexeme;
				break;
			case "<":
				this.kind = Kind.LESS_THAN;
				this.lexeme = lexeme;
				break;
			case "=":
				this.kind = Kind.ASSIGN;
				this.lexeme = lexeme;
				break;
			case ",":
				this.kind = Kind.COMMA;
				this.lexeme = lexeme;
				break;
			case ";":
				this.kind = Kind.SEMICOLON;
				this.lexeme = lexeme;
				break;
			case ":":
				this.kind = Kind.COLON;
				this.lexeme = lexeme;
				break;
			case "::":
				this.kind = Kind.CALL;
				this.lexeme = lexeme;
				break;
			default:
				this.kind = Kind.ERROR;
				this.lexeme = "Unrecognized lexeme: " + lexeme;
		}
		
		/*
		if (lexeme == "and")
		{
			this.kind = Kind.AND;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "or")
		{
			this.kind = Kind.OR;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "not")
		{
			this.kind = Kind.NOT;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "let")
		{
			this.kind = Kind.LET;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "var")
		{
			this.kind = Kind.VAR;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "array")
		{
			this.kind = Kind.ARRAY;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "func")
		{
			this.kind = Kind.FUNC;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "if")
		{
			this.kind = Kind.IF;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "else")
		{
			this.kind = Kind.ELSE;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "while")
		{
			this.kind = Kind.WHILE;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "true")
		{
			this.kind = Kind.TRUE;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "false")
		{
			this.kind = Kind.FALSE;
			this.lexeme = lexeme;
		}
		
		else if (lexeme == "return")
		{
			this.kind = Kind.RETURN;
			this.lexeme = lexeme;
		}
		
		// if we don't match anything, signal error
		else
		{
			this.kind = Kind.ERROR;
			this.lexeme = "Unrecognized lexeme: " + lexeme;
		}
		*/
	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}
	
	// Return the lexeme representing or held by this token
	public String lexeme()
	{
		// TODO: implement
		return this.kind.toString();
	}
	
	public String toString()
	{
		// TODO: implement this
		if (this.kind == Kind.ERROR || this.kind == Kind.FLOAT || this.kind == Kind.IDENTIFIER || this.kind == Kind.INTEGER)
			return this.lexeme() + "(" + this.lexeme + ")(lineNum:" + this.lineNumber() + ", charPos:" + this.charPosition() + ")";
		return this.lexeme() + "(lineNum:" + this.lineNumber() + ", charPos:" + this.charPosition() + ")";
	}
	
	// OPTIONAL: function to query a token about its kind
	//           boolean is(Token.Kind kind)
	
	public boolean is(Token.Kind kind)
	{
		return kind == this.kind;
	}
	
	public Token.Kind kind()
	{
		return kind;
	}
	
	// OPTIONAL: add any additional helper or convenience methods
	//           that you find make for a clean design
	

}
