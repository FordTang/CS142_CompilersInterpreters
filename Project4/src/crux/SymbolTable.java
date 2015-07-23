package crux;

import java.util.Map;
import java.util.LinkedHashMap;

public class SymbolTable {
	private SymbolTable parent;
    private Map<String, Symbol> table;
    private int depth;
	
    public SymbolTable()
    {
        table = new LinkedHashMap<String, Symbol>();
        init();
        depth = 0;
    }
    
    public SymbolTable(SymbolTable parent)
    {
        table = new LinkedHashMap<String, Symbol>();
        //init();
        this.parent = parent;
        depth = parent.depth() + 1;
    }
    
    private void init()
    {
    	Symbol symbol = new Symbol("readInt");
    	table.put("readInt", symbol);
    	symbol = new Symbol("readFloat");
    	table.put("readFloat", symbol);
    	symbol = new Symbol("printBool");
    	table.put("printBool", symbol);
    	symbol = new Symbol("printInt");
    	table.put("printInt", symbol);
    	symbol = new Symbol("printFloat");
    	table.put("printFloat", symbol);
    	symbol = new Symbol("println");
    	table.put("println", symbol);
    }
    
    public int depth()
    {
    	return depth;
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
        Symbol symbol = table.get(name);
        if (symbol == null)
        {
        	if (parent != null)
        		symbol = parent.lookup(name);
        }
        if (symbol == null)
        	throw new SymbolNotFoundError(name);
        
        return symbol;
    }
       
    public Symbol insert(String name) throws RedeclarationError
    {
    	Symbol symbol = table.get(name);
    	if (symbol == null)
    	{
    		symbol = new Symbol(name);
    	    table.put(name, symbol);
    	    return symbol;
    	}
    	
    	throw new RedeclarationError(symbol);
    }
    
    public SymbolTable parent()
    {
    	return parent;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (parent != null)
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }
        
        for (Symbol s : table.values())
        {
            sb.append(indent + s.toString() + "\n");
        }
        return sb.toString();
    }
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(Symbol sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
