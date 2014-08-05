/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;


import V_RuleLearner.*;

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
        
        if ((position >= this.tokens.size() ) || (position <0) || this.tokens.get(position).isNotWildcard()) {
            return sMap;
        }
       
        int limit = this.tokenMap.getRefMax(position);
        

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

    
    public SensorList expandNonWildcard (int position) {
        
       SensorList sMap = new SensorList ();
        //sMap.addSensor(this);
        
        if ((position >= this.tokens.size() ) || (position <0) || this.tokens.get(position).isWildcard()) {
            return sMap;
        }
        
        
        
        int limit = this.tokenMap.getRefMax(position);

        
        for (int i = 0; i < limit; i++) {
            Sensor a = this.copy();
            Token tok = new Token(this.tokenMap.getToken(position, i),position,this.tokenMap);
            //System.out.println(this.tokenMap.getToken(position, i));
            //System.out.println("Copied Sensor : " + a.tokens);
            if (!tok.match_exact(this.getToken(position))){
                a.tokens.set(position, tok);
                sMap.addSensor(a);
            }
            //System.out.println("Adding " + a.getString());
            //System.out.println("Added >> " + a.tokens);
            
        }
        
        return sMap;
        
       
    }  
    
    
    public SensorList expandNonWildcards () {
        
        SensorList res = new SensorList ();
        
        for ( int i = 0; i < this.size(); i++) {
            
            if (this.getToken(i).isNotWildcard()) {
                
                res.addSensorList(this.expandNonWildcard(i));
            }
        }
        
        return res;
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
    
    
    public int numberOfWildcards () {
        
        int res = 0;
        for (int i=0; i<this.size(); i++) {
            
            if (this.getToken(i).isWildcard()) 
                res++;
        }
        
        return res;
    }
    
    public int numberOfNonWildcards () {
        
        int res = 0;
        for (int i=0; i<this.size(); i++) {
            
            if (this.getToken(i).isNotWildcard()) 
                res++;
        }
        
        return res;
    }
  
    
    public Sensor merge (Sensor sen) {
        
        Sensor res = new Sensor(this.tokenMap);
        
        for (int i = 0; i < sen.size(); i++) {
            
            if (sen.getToken(i).isNotWildcard()) {
                
                res.getToken(i).setReference(sen.getToken(i).reference);
            }
            
            if (this.getToken(i).isNotWildcard()) {
                
                res.getToken(i).setReference(this.getToken(i).reference);
            }
        }
        
        return res;
        
    }
    
    
    
    public ArrayList <Integer> getAllMatchingRuleSets (RuleSetList rsList) {
        
        ArrayList res = new ArrayList ();
        for (int i = 0; i < rsList.size(); i++) {
            
            if (this.sensorMatch(rsList.getRuleSet(i+1).getPrecursor())) {
                
                res.add(i+1);
            }
        }
        
        return res;
    }

    public int chooseNextRuleSet (RuleSetList rsList) {

            ArrayList a = new ArrayList();
            return chooseNextRuleSet(rsList,a);
    } 
        
    public int chooseNextRuleSet (RuleSetList rsList, ArrayList <Integer> a) {
        
        int score;
        ArrayList <Integer> all = getAllMatchingRuleSets(rsList);
        int chosen = -1;
        int best_score = 0;
        boolean isCandidate;
        
        if (all.isEmpty())
            return -1;
        
        if (rsList.size() == 0)
            return -1;
        
        for (int i = 0; i < all.size(); i++) {
            
            score = 0;
            isCandidate = true;
            
            for (int t = 0; t < a.size(); t ++) {
                
                if(rsList.getRuleSet(all.get(i)).getSuccessor().getToken(a.get(t)).isNotWildcard()) {
                    
                    isCandidate = false;
                    break;
                }
                    
            }
            
            if(isCandidate) {
                
                for (int j = 0; j < all.size(); j++ ) {

                    if ( (rsList.getRuleSet(all.get(i)).precedences.contains(all.get(j))) && (i != j) ) {

                        score++;
                    }
                    

                }
            
                // PRINTS OUT ALL CANDIDATES WITH SCORE - DEBUG
//            if(isCandidate) {
//                
//                System.out.println("Candidate : " + rsList.getRuleSet(all.get(i)).getRule(0) + " Score : " + score);
//
//            }    
                //System.out.println("SCORE OF " + all.get(i) + " IS " + score);

                if (score == best_score) {

                    chosen = all.get(i);
                    best_score = score;
                }
                if (score > best_score) {

                    chosen = all.get(i);
                    best_score = score;
                    //System.out.println("BEST SCORE SET TO : " + best_score + " by RS" + chosen);
                }
            }
        }
        
        return chosen;
    }    
    
    
    
    // OLD VERSION USING PRECURSOR OCCURENCIES
    public int getBestMatchingRuleSets (RuleSetList rsList) {
        
        int level = 100000; // TO START, BIG ENOUGH TO ENSURE WE FIND LESS
        int candidate_level;
        int chosen_ruleset = -1;
        
        
        // LOOKING FOR MATCHES AND GETTING THE ONE WITH LESS PREC. OCCURRENCIES
        for (int i = 0; i < rsList.size(); i++) {
            
            if (this.sensorMatch(rsList.getRuleSet(i+1).getPrecursor())) {
                
                candidate_level = rsList.getRuleSet(i+1).getPrecursorOccurrencies();
                
                if (candidate_level < level) {
                    
                    level = candidate_level;
                    chosen_ruleset = (i+1);
                }

            }
        }
        
        return chosen_ruleset;
    }
    



    public ArrayList detectCommonNonWilcardedIndexes (Sensor s2) {
        
        ArrayList res = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ((this.getToken(i).isNotWildcard()) && (s2.getToken(i).isNotWildcard()))
                res.add(i);
        }
        
        return res;
    }
    
    
    public String simple () {
        
        String str = new String();
        
        for (int i = 0; i < this.size(); i++) {
            
            str = str.concat(this.getToken(i).toString());
        }
        
        return str;
    }
    
    
    
    
    public boolean isRewarded () {
        
        if (this.getToken(this.size()-2).getReference() == 2)
            return true;
        
        else {
            return false;
        }
    }
    
}
