/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;

import java.util.*;
import java.io.*;


/**
 *
 * @author virgile
 */
public class Sensor {
    
    protected ArrayList <Token> tokens;
    protected int line;
    static TokenMap tokenMap = null;
    
    
    
    public Sensor(String str, TokenMap t) {    
        
        this.tokenMap = t;
        this.tokens = new ArrayList ();
        
        
        
        int n = str.length();
        
        for (int i = 0; i < (n-1); i++)
        {
            Token a = new Token(String.valueOf(str.charAt(i)), i, t);
            //System.out.println("Char "+ i + " : " + str.charAt(i));
            //System.out.println("Token "+ i + " : (text)" + a.toString() + " (Pos) : " + a.position +" (Ref) : " + a.reference );
            //System.out.println("Token Ref "+ i + " : " + a.reference);
            this.tokens.add(a);
        }
        
       
        
        
    }
    
    
    public ArrayList getString () {
        
        return tokens;
        
    }
    
    
    
    public Token getToken (int position) {
        
        return this.tokens.get(position);
    }
    
    
}
