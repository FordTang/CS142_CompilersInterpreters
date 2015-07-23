package types;

public class BoolType extends Type {
    
    public BoolType()
    {
        //throw new RuntimeException("implement operators");
    }
    
    @Override
    public String toString()
    {
        return "bool";
    }

    @Override
    public Type and(Type that)
    {
    	if (!(that instanceof BoolType))
            return super.and(that);
        return new BoolType();
    }
    
    @Override
    public Type or(Type that)
    {
    	if (!(that instanceof BoolType))
            return super.or(that);
        return new BoolType();
    }
    
    @Override
    public Type not()
    {
        return new BoolType();
    }
    
    @Override
    public Type assign(Type source)
    {
    	if (!equivalent(source))
    	{
    		return super.assign(source);
    	}
    	else 
    		return this;
    }
    
    @Override
    public boolean equivalent(Type that)
    {
        if (that == null)
            return false;
        if (!(that instanceof BoolType))
            return false;
        
        return true;
    }
}    
