package types;

public class AddressType extends Type {
    
    private Type base;
    
    public AddressType(Type base)
    {
        //throw new RuntimeException("implement operators");
        this.base = base;
    }
    
    public Type base()
    {
        return base;
    }    

    @Override
    public String toString()
    {
        return "Address(" + base + ")";
    }
    
    @Override
    public Type deref()
    {
    	return base;
    }
    
    @Override
    public Type assign(Type source)
    {
    	if (!equivalent(source))
    	{
    		return super.assign(source);
    	}
    	else
    		return base.assign(source);
    }
    
    @Override
    public Type add(Type that)
    {
    	return base.add(that);
    }
    
    @Override
    public Type sub(Type that)
    {
    	return base.sub(that);
    }
    
    @Override
    public Type mul(Type that)
    {
    	return base.sub(that);
    }
    
    @Override
    public Type div(Type that)
    {
    	return base.div(that);
    }
    
    @Override
    public Type and(Type that)
    {
    	return base.and(that);
    }
    
    @Override
    public Type or(Type that)
    {
    	return base.or(that);
    }
    
    @Override
    public Type not()
    {
    	return base.not();
    }
    
    @Override
    public Type compare(Type that)
    {
    	return base.compare(that);
    }
    
    @Override
    public Type index(Type that)
    {
    	if (!(that instanceof types.IntType) || !(base instanceof types.ArrayType))
    	{
    		return super.index(that);
    	}
    	return this;
    }
    
    @Override
    public Type call(Type args)
    {
    	return base.call(args);
    }
    
    @Override
    public boolean equivalent(Type that) {
        if (that == null)
            return false;
        if (!(that instanceof AddressType))
            return false;
        
        AddressType aType = (AddressType)that;
        return this.base.equivalent(aType.base);
    }
}
