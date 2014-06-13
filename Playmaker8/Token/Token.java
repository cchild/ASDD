/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;


import java.io.*;
import java.util.*;
import EnvAgent.RuleLearner.RuleLearnerMSDD_Sensor;
/**
 *
 * @author virgile
 */




public class Token {
    
    
    
    public static final int
	TEST_TOKEN = 1;
    
    
    int reference;
    int position;
    static TokenMap tokenMap = null;
    
    
    public Token(String str, int position, TokenMap t)
    {
        this.tokenMap = t;
        this.position = position;
        this.reference = t.getReference(position, str);
    }
    
    
    
//    public Token(String str, Sensor S)
//    {
//        this.reference = str;
//        this.position = ;
//    }
    
    public String toString () {
        
        if (reference == -1)
        {
            return "*";
        }
        Object res = tokenMap.getToken(position).get(reference-1);
        return String.valueOf(res);
    }
    
    public int getReference () {
        
        return reference;
    }
    
    public int getPosition () {
        
        return position;
    }
    
    
}    
    

