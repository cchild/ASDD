/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;

/**
 *
 * @author virgile
 */




public class Token {
    
    
    
    public static final int
	TEST_TOKEN = 1;
    
    
    String reference;
    int position;
    
    
    public Token(String str)
    {
        this.reference = str;
        this.position = 0;
    }
    
    
    
//    public Token(String str, Sensor S)
//    {
//        this.reference = str;
//        this.position = ;
//    }
    
    public String toString () {
        
        return reference;
    }
    
    
    
    

    


public static void main (String[] args) {
        
        
        //System.out.println("Hello");
        
        //Token a = new Token("Test");
        
        TokenMap t = new TokenMap();
        
        System.out.println(t.TokenTypes.toString());
        System.out.println("Call First, 0");
        t.setToken("First", 0);
        System.out.println(t.TokenTypes.toString());
        System.out.println("Call Second, 1");
        t.setToken("Second", 1);
        System.out.println(t.TokenTypes.toString());
        String target = "Second";
        int b = 1;
        System.out.println("Call " + target + " , " + b);
        //System.out.println("Searching Second in list at index "+b);
        t.setToken(target, b);
        System.out.println(t.TokenTypes.toString());
        System.out.println("Call Third, 2");
        t.setToken("Third", 2);
        
        //System.out.println(t.TokenTypes.get(0));
        //System.out.println(t.TokenTypes.get(1));
        System.out.println("Final TokenMap : " + t.TokenTypes.toString());
        //System.out.println(t);  returns Token.TokenMap@7f31245a
        //System.out.println(a.toString());
    }
    
}    
    

