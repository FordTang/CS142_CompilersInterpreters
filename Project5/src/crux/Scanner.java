package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class Scanner implements Iterable<Token> {
	public static String studentName = "Ford Tang";
	public static String studentID = "46564602";
	public static String uciNetID = "FordT1";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;
	
	Scanner(Reader reader) throws IOException
	{
		lineNum = 1;
		charPos = 1;
		input = reader;
		nextChar = input.read();
	}	
	
	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.

	private int readChar() throws IOException
	{
		if (nextChar == '\n')
		{
			++lineNum;
			charPos = 1;
			nextChar = input.read();
		}
		
		else if (nextChar != -1)
		{
			++charPos;
			nextChar = input.read();
		}
		
		return nextChar;
	}

		

	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next() throws IOException
	{	
		while (nextChar != -1)
		{
			//System.out.println((char)nextChar);
			
			String lexeme = "";
			
			while (nextChar == ' ' || nextChar == '\n')
				readChar();
			
			if (nextChar == -1)
				return Token.EOF(lineNum, charPos);
			
			//if (nextChar == ' ' || nextChar == '\n')
			//	return new Token(lexeme, lineNum, charPos);
			
			if (nextChar == '_' || (nextChar >= 'A' && nextChar <= 'Z') || (nextChar >= 'a' && nextChar <= 'z'))
			{
				int charNum = charPos;
				lexeme += (char)nextChar;
				readChar();
				while (nextChar == '_' || (nextChar >= 'A' && nextChar <= 'Z') || (nextChar >= 'a' && nextChar <= 'z') || (nextChar >= '0' && nextChar <= '9'))
				{
					lexeme += (char)nextChar;
					readChar();
				}
				
				switch (lexeme)
				{
					case "and":
					case "or":
					case "not":
					case "let":
					case "var":
					case "array":
					case "func":
					case "if":
					case "else":
					case "while":
					case "true":
					case "false":
					case "return":
						return new Token(lexeme, lineNum, charNum);
					default:
						return Token.Identifier(lexeme, lineNum, charNum);
				}
			}
			
			if (nextChar >= '0' && nextChar <= '9')
			{
				//System.out.println((char)nextChar);
				int charNum = charPos;
				while (nextChar >= '0' && nextChar <= '9')
				{
					lexeme += (char)nextChar;
					readChar();
				}
				
				if (nextChar == '.')
				{
					lexeme += (char)nextChar;
					readChar();
					
					while (nextChar >= '0' && nextChar <= '9')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					
					return Token.Float(lexeme, lineNum, charNum);
				}
				return Token.Integer(lexeme, lineNum, charNum);
			}
			int charNum = charPos;
			lexeme += (char)nextChar;
			
			switch (lexeme)
			{
				case "(":
				case ")":
				case "{":
				case "}":
				case "[":
				case "]":
				case "+":
				case "-":
				case "*":
				case ",":
				case ";":
					readChar();
					return new Token(lexeme, lineNum, charNum);
				case "/":
					if (readChar() == '/')
					{
						int temp = lineNum + 1;
						while (lineNum != temp && nextChar != -1)
							readChar();
						lexeme = "";
						break;
					}
					else
						return new Token(lexeme, lineNum, charNum);
				case ">":
					if (readChar() == '=')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charNum);
				case "<":
					if (readChar() == '=')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charNum);
				case "!":
					if (readChar() == '=')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charNum);
				case "=":
					if (readChar() == '=')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charNum);
				case ":":
					if (readChar() == ':')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charNum);
				default:
					readChar();
					return new Token(lexeme, lineNum, charNum);
			}
		}
		
		return Token.EOF(lineNum, charPos);
		
		/*
		while (nextChar != -1)
		{
			while(nextChar == ' ' || nextChar == '\n')
				readChar();
			
			if (nextChar == '/')
			{
				if (readChar() == '/')
				{
					int temp = lineNum + 1;
					while (lineNum != temp || nextChar != -1)
						readChar();
				}
				else
					return Token.Div(lineNum, charPos); //check against comments
			}
			
			else if (nextChar == '(')
			{
				readChar();
				return Token.OpenParen(lineNum, charPos);
			}
			
			else if (nextChar == ')')
			{
				readChar();
				return Token.CloseParen(lineNum, charPos);
			}
			
			else if (nextChar == '{')
			{
				readChar();
				return Token.OpenBrace(lineNum, charPos);
			}
			
			else if (nextChar == '}')
			{
				readChar();
				return Token.CloseBrace(lineNum, charPos);
			}
			
			else if (nextChar == '[')
			{
				readChar();
				return Token.OpenBracket(lineNum, charPos);
			}
			
			else if (nextChar == ']')
			{
				readChar();
				return Token.CloseBracket(lineNum, charPos);
			}
			
			else if (nextChar == '+')
			{
				readChar();
				return Token.Add(lineNum, charPos);
			}
			
			else if (nextChar == '-')
			{
				readChar();
				return Token.Sub(lineNum, charPos);
			}
			
			else if (nextChar == '*')
			{
				readChar();
				return Token.Mul(lineNum, charPos);
			}
			
			else if (nextChar == ',')
			{
				readChar();
				return Token.Comma(lineNum, charPos);
			}
			
			else if (nextChar == ';')
			{
				readChar();
				return Token.SemiColon(lineNum, charPos);
			}
			
			else if (nextChar == ':')
			{
				if (readChar() == ':')
				{
					readChar();
					return Token.Call(lineNum, charPos);
				}
				return Token.Colon(lineNum, charPos);
			}
			
			else if (nextChar == '>')
			{
				if (readChar() == '=')
				{
					readChar();
					return Token.GreaterEqual(lineNum, charPos);
				}
				return Token.GreaterThan(lineNum, charPos);
			}
			
			else if (nextChar == '<')
			{
				if (readChar() == '=')
				{
					readChar();
					return Token.LesserEqual(lineNum, charPos);
				}
				return Token.LessThan(lineNum, charPos);
			}
			
			else if (nextChar == '!')
			{
				if (readChar() == '=')
				{
					readChar();
					return Token.NotEqual(lineNum, charPos);
				}
			}
			
			else if (nextChar == '=')
			{
				if (readChar() == '=')
				{
					readChar();
					return Token.Equal(lineNum, charPos);
				}
				return Token.Assign(lineNum, charPos);
			}
			
			else
			{
				String lexeme = "";
				if ((nextChar >= 'A' && nextChar <= 'Z') || (nextChar >= 'a' && nextChar <= 'z'))
				{
					while ((nextChar >= 'A' && nextChar <= 'Z') || (nextChar >= 'a' && nextChar <= 'z'))
					{
						lexeme += (char)nextChar;
						readChar();
					}
					return new Token(lexeme, lineNum, charPos);					
				}
				else if (nextChar >= '0' && nextChar <= '9')
				{
					while (nextChar >= '0' && nextChar <= '9')
					{
						lexeme += (char)nextChar;
						readChar();
					}
					if (nextChar == '.')
					{
						lexeme += (char)nextChar;
						readChar();
						while (nextChar >= '0' && nextChar <= '9')
						{
							lexeme += (char)nextChar;
							readChar();
						}
					}
					return new Token(lexeme, lineNum, charPos);	
				}
				else
				{
					lexeme += (char)nextChar;
					readChar();
					return new Token(lexeme, lineNum, charPos);
				}
			}
		}
		
		return Token.EOF(lineNum, charPos);
		*/
	}

	public Iterator<Token> iterator()
	{
		return null;
	}
	
	// OPTIONAL: any other methods that you find convenient for implementation or testing

}
