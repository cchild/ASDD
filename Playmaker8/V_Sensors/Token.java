/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;


/**
 *
 * @author virgile
 */




public class Token {
    
    
    
    public static final int
	TEST_TOKEN = 1;
    
    
    int reference;
    int position;
    public static TokenMap tokenMap = null;
    
    
    public Token(TokenMap t)
    {
        this.tokenMap = t;
        this.position = 0;
        this.reference = 0;
    }
    
    
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
    
//    public Token(String str, Sensor S)
//    {
//        this.reference = str;
//        this.position = ;
//    }
    
    public String toString () {
        
        if (reference == 0)
        {
            return "*";
        }
        if (reference == -1)
        {
            return "?";
        }
        
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
    

