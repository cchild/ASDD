/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;

import EnvModel.Percep;
import java.io.*;
import java.util.*;


/**
 *
 * @author virgile
 */
public class Sensor {
    
    public ArrayList <Token> tokens;
    
    public  TokenMap tokenMap = null;
    
    
    
    
    // CREATES AN EMPTY SENSOR OF THE SIZE OF TOKENMAP
    public Sensor(TokenMap t) {    
        
        
        this.tokenMap = t;
        this.tokens = new ArrayList ();
        
        int i = t.TokenTypes.size();
        
        for (int j=0; j<i ; j++) {
            Token empty = new Token(0,j,t);
            tokens.add(empty);
        }
        
        
    }
    
    public Sensor(String str, TokenMap t) {    
        
        
        this.tokenMap = t;
        this.tokens = new ArrayList ();
        
        
        
        int n = str.length();
        
        for (int i = 0; i < (n); i++)
        {
            if (String.valueOf(str.charAt(i)).compareTo("*")==0) {
                Token a = new Token(0,i,t);
                this.tokens.add(a);
                continue;
            }
            Token a = new Token(String.valueOf(str.charAt(i)), i, t);
            //System.out.println("Char "+ i + " : " + str.charAt(i));
            //System.out.println("Token "+ i + " : (text)" + a.toString() + " (Pos) : " + a.position +" (Ref) : " + a.reference );
            //System.out.println("Token Ref "+ i + " : " + a.reference);
            this.tokens.add(a);
        }
        
       
        
        
    }
    

    
    
    public int setToken (String str, int position) {
        
        int a = tokenMap.getReference(position, str);
        
        // IF TOKEN NOT IN THE TOKENMAP
        if ((a == -1) && (str != "*")) {      
            return 1;
        }
        
        this.tokens.get(position).setReference(a);
        return 0;
        
    }
    
    
//        public int setToken (int reference, int position) {
//        
//
//        Token a = this.tokenMap.getToken(position).get(reference);
//        
//        return 0;
//        
//    }
    
    
    public ArrayList getString () {
        
        return tokens;
        
    }
    
    public Sensor copy () {
        int n = this.tokens.size();
        
        Sensor a = new Sensor(this.tokenMap);
        for (int i = 0; i < n; i++) {
            a.tokens.set(i, this.getToken(i));
        }
        
        return a;
    }
    
    
    
    public Token getToken (int position) {
        
        return this.tokens.get(position);
    }
    
    public String getActionString() {
        return this.tokens.get(this.tokens.size()-1).toString();
    }
    
    
    public boolean equals (Sensor s2) {
        boolean res = false;
        
        int a = this.getString().toString().compareTo(s2.getString().toString());
        if (a==0){
            res = true;
        }
        return res;
        
        
    }
    
    
    public SensorList expand (int position) {
        
       SensorList sMap = new SensorList ();
        //sMap.addSensor(this);
        
        if ((position >= this.tokens.size() ) || (position <0) || this.tokens.get(position).isNotWildcard()) {
            return sMap;
        }
        
        
        
        int limit = this.tokenMap.getRefMax(position);
        
        //System.out.println("Expanding from " + position + " " + limit + " times.");
        
//        if (this.getToken(position).isWildcard()){
//            System.out.println("WILDCARD ERROR");
//            return sMap;
//        }
//        
//        else {
        for (int i = 0; i < limit; i++) {
            Sensor a = this.copy();
            Token tok = new Token(this.tokenMap.getToken(position, i),position,this.tokenMap);
            //System.out.println(this.tokenMap.getToken(position, i));
            //System.out.println("Copied Sensor : " + a.tokens);
            a.tokens.set(position, tok);
            sMap.addSensor(a);
            //System.out.println("Adding " + a.getString());
            //System.out.println("Added >> " + a.tokens);
            
        }
        
        return sMap;
        
       
    }
    
    
    
    @Override
    public String toString () {
        
        return this.tokens.toString();
    }
    
    
    public boolean sensorMatch (Sensor expression) {
        boolean res = true;
        
        if (this.tokens.size() != expression.tokens.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < this.tokens.size(); i++) {
            
            if(!this.getToken(i).match(expression.getToken(i))) {
                res = false;
                return res;
            }
        }
        return res;
    }
    
    
    public boolean sensorMatch_exact (Sensor expression) {
        boolean res = true;
        
        if (this.tokens.size() != expression.tokens.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < this.tokens.size(); i++) {
            
            if(!this.getToken(i).match_exact(expression.getToken(i))) {
                res = false;
                return res;
            }
        }
        return res;
    }
    
    
    
         @Override
    public boolean equals(Object obj){
         if(obj == null)
           return false;
         if(this==obj)
           return true;
         
         Sensor s = (Sensor) obj;
         
         return (this.sensorMatch(s));
    }
    
    
    public int size() {
        
        return this.tokens.size();
        
    }
    
    
    // RETURNS THE FIRST EXPANDABLE SPOT, OR -1
    public int isExpandable () {
        for (int i=0; i<this.size(); i++) {
            if (this.getToken(i).isWildcard())
                return i;
        }
        
        return -1;
    }
    
    
    
    
    
}
