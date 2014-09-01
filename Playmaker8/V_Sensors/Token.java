
package V_Sensors;


/**
 *
 * @author virgile
 */




public class Token {
    
    
    int reference;
    int position;
    public TokenMap tokenMap;
    
    
    // If ref = 0, it is a Wildcard
    public Token(TokenMap t)
    {
        this.tokenMap = t;
        this.position = 0;
        this.reference = 0;
    }
    
    // From a String
    public Token(String str, int position, TokenMap t)
    {
        this.tokenMap = t;
        this.position = position;
        this.reference = t.getReference(position, str);
    }
    
    
    public Token(int reference, int position, TokenMap t)
    {
        
        this.tokenMap = t;
        this.position = position;
        this.reference = reference;
    }
    
    
    public Token copy () {
        
        
        Token a  = new Token (this.reference, this.position, this.tokenMap);
        
        return a;
    }
    

    
    @Override
    public String toString () {
        
        // Wildcard
        if (reference == 0)
        {
            return "*";
        }
        // Unknown
        if (reference == -1)
        {
            return "?";
        }
        
        // Classic Token
        Object res = tokenMap.getTokenList(position).get(reference-1);
        return String.valueOf(res);
    }
    
    
    public int getReference () {
        
        return reference;
    }
    
    
    public void setReference (int ref) {
        
        reference = ref;
    }
    
    
    public int getPosition () {
        
        return position;
    }
    
    
    public void setPosition (int ref) {
        
        position = ref;
    }
    
    
    public boolean isWildcard () {
        
        return (reference == 0);
    }
    
    
    public boolean isNotWildcard () {
        
        return (!isWildcard());
    }
     
    
    // Comparing Strings, could be speeded up by comparing references and positions
    public boolean match (Token target) {
         
         if (target.isWildcard())
             return true;
         
         if (this.isWildcard())
             return true;
         
         
         
         if(this.toString().compareTo(target.toString()) != 0)
             return false;
         
         
         return true;
    }
     
     
    public boolean match_exact (Token target) {
         
        
         
         if(this.toString().compareTo(target.toString()) != 0)
             return false;
         
         
         return true;
    }
}    
    

