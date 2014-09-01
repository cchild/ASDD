
package V_Sensors;


import V_RuleLearner.*;

import java.util.*;


/**
 *
 * @author virgile
 */
public class Sensor {
    
    public ArrayList <Token> tokenList;
    
    public  TokenMap tokenMap = null;

    
    
    // CREATES AN EMPTY SENSOR OF TOKENMAP'S SIZE
    // ALL TOKENS ARE WILDCARDS
    public Sensor(TokenMap t) {    
        
        
        this.tokenMap = t;
        this.tokenList = new ArrayList ();
        
        int i = t.TokenList.size();
        
        for (int j=0; j<i ; j++) {
            
            Token empty = new Token(0,j,t);
            tokenList.add(empty);
        }
        
        
    }
    
    
    // Creates a sensor from "str", using the TokenMap to define Tokens
    public Sensor(String str, TokenMap t) {    
        
        
        this.tokenMap = t;
        this.tokenList = new ArrayList ();
        
        
        
        int n = str.length();
        
        for (int i = 0; i < (n); i++)
        {
            if (String.valueOf(str.charAt(i)).compareTo("*") == 0) {
                
                Token a = new Token(0,i,t);
                this.tokenList.add(a);
                continue;
            }
            
            Token a = new Token(String.valueOf(str.charAt(i)), i, t);

            this.tokenList.add(a);
        }
        
       
        
        
    }
    

    
    // Creates a sensor from "str", using coma version
    // Use any int, makes no difference
    public Sensor(String str, TokenMap t, int version) {    
        
        
        this.tokenMap = t;
        this.tokenList = new ArrayList ();
        
        

        
        String [] a = str.split(",");
        
        for (int i = 0; i < a.length; i++)
        {
            if (a[i].compareTo("*") == 0) {
                
                Token b = new Token(0,i,t);
                this.tokenList.add(b);
                continue;
            }
            
            Token b = new Token(a[i], i, t);

            this.tokenList.add(b);
        }
        
       
        
        
    }
    
    
    // Sets a Token inside a Sensor, at "position"
    public int setToken (String str, int position) {
        
        int a = tokenMap.getReference(position, str);
        
        // IF TOKEN NOT IN THE TOKENMAP
        if ((a == -1) && (str != "*")) {      
            return 1;
        }
        
        this.tokenList.get(position).setReference(a);
        return 0;
        
    }
        

    // Returns a copy of the Sensor
    public Sensor copy () {
        
        int n = this.tokenList.size();
        
        Sensor a = new Sensor(this.tokenMap);
        for (int i = 0; i < n; i++) {
            a.tokenList.set(i, this.getToken(i));
        }
        
        return a;
    }
    
    
    
    public Token getToken (int position) {
        
        return this.tokenList.get(position);
    }
    

    
    
    // Expands the index "position"
    //
    // If the Token at position is Not a Wildcard, returns an empty List
    // If it is, returns a list with all possible Sensors according to the tokenMap
    public SensorList expand (int position) {
        
       SensorList sList = new SensorList ();
        
        if ((position >= this.tokenList.size() ) || (position <0) || this.tokenList.get(position).isNotWildcard()) {
            return sList;
        }
       
        int limit = this.tokenMap.getRefMax(position);
        

        for (int i = 0; i < limit; i++) {
            Sensor a = this.copy();
            Token tok = new Token(this.tokenMap.getToken(position, i),position,this.tokenMap);

            a.tokenList.set(position, tok);
            
            sList.addSensor(a);
            
        }
        
        return sList;
        
       
    }

    // Expands the position and returns a List of all possible Sensors, except the one already in place
    // In other words, returns all the similar Sensors with another Token at "position"
    // See function below for uses
    public SensorList expandNonWildcard (int position) {
        
       SensorList sensorList = new SensorList ();
        
        if ((position >= this.tokenList.size() ) || (position <0) || this.tokenList.get(position).isWildcard()) {
            return sensorList;
        }
        
        
        
        int limit = this.tokenMap.getRefMax(position);

        
        for (int i = 0; i < limit; i++) {
            Sensor a = this.copy();
            Token tok = new Token(this.tokenMap.getToken(position, i),position,this.tokenMap);

            if (!tok.match_exact(this.getToken(position))){
                a.tokenList.set(position, tok);
                sensorList.addSensor(a);
            }
            
        }
        
        return sensorList;
        
       
    }  
    
    
    // Expand all non-wildcarded indexes
    // Used to find same Ruleset Rules
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
        
        return this.tokenList.toString();
    }
    
    
    // Returns true if both Sensors match
    public boolean sensorMatch (Sensor expression) {
        
        boolean res = true;
        
        if (this.tokenList.size() != expression.tokenList.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < this.tokenList.size(); i++) {
            
            if(!this.getToken(i).match(expression.getToken(i))) {
                res = false;
                return res;
            }
        }
        return res;
    }
    
    
    // Returns true is both Sensors are the same, e.g. they exaclty have same tokens
    public boolean sensorMatch_exact (Sensor expression) {
        
        boolean res = true;
        
        if (this.tokenList.size() != expression.tokenList.size()) {
            res = false;
            return res;
        }
        
        for (int i = 0; i < this.tokenList.size(); i++) {
            
            if(!this.getToken(i).match_exact(expression.getToken(i))) {
                res = false;
                return res;
            }
        }
        return res;
    }
    
    
    
    
    public int size() {
        
        return this.tokenList.size();
        
    }
    
    
    // RETURNS THE FIRST EXPANDABLE SPOT, OR -1 IF THEY AREN'T ANY
    public int isExpandable () {
        
        for (int i=0; i<this.size(); i++) {
            if (this.getToken(i).isWildcard())
                return i;
        }
        
        return -1;
    }
    
    
    // Returns the number of Wildcards
    public int numberOfWildcards () {
        
        int res = 0;
        for (int i=0; i<this.size(); i++) {
            
            if (this.getToken(i).isWildcard()) 
                res++;
        }
        
        return res;
    }
    
    // Returns the number of Non - Wildcards
    public int numberOfNonWildcards () {
        
        int res = 0;
        for (int i=0; i<this.size(); i++) {
            
            if (this.getToken(i).isNotWildcard()) 
                res++;
        }
        
        return res;
    }
  
    
    // Merges two Sensors (By replacing Wildcards from 1 to Tokens from the other
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
    
    
    // Returns the list of all RuleSets that match the Sensor
    public ArrayList <Integer> getAllMatchingRuleSets (RuleSetList rsList) {
        
        ArrayList res = new ArrayList ();
        for (int i = 0; i < rsList.size(); i++) {
            
            if (this.sensorMatch(rsList.getRuleSet(i+1).getPrecursorWithID())) {
                
                res.add(i+1);
            }
        }
        
        return res;
    }

    
    // See below
    public int chooseNextRuleSet (RuleSetList rsList) {

            ArrayList a = new ArrayList();
            return chooseNextRuleSet(rsList,a);
    } 
        
    
    // Chooses the Next RuleSet using precedences.
    // The one preceding the most over the other candidates is selected
    // This is represented by "score"
    //
    // A candidate is a matching RuleSet that contains Wildcards at the indexes in the List "a"
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
        //System.out.print("\nCandidates : ");
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

                if (score >= best_score) {

                    chosen = all.get(i);
                    best_score = score;
                    
                    //System.out.println("New Best Score : " + chosen + " > " + score);
                    
                }
            }
        }
        //System.out.print("\nPicked RuleSet : " + chosen);
        return chosen;
    }    
    

 
    
    // OLD VERSION USING PRECURSOR OCCURENCIES
    // NOT USED SINCE WE CHOSE PRECEDENCES
    /*
    public int getBestMatchingRuleSets (RuleSetList rsList) {
        
        int level = 100000; // TO START, BIG ENOUGH TO ENSURE WE FIND LESS
        int candidate_level;
        int chosen_ruleset = -1;
        
        
        // LOOKING FOR MATCHES AND GETTING THE ONE WITH LESS PREC. OCCURRENCIES
        for (int i = 0; i < rsList.size(); i++) {
            
            if (this.sensorMatch(rsList.getRuleSet(i+1).getPrecursorWithID())) {
                
                candidate_level = rsList.getRuleSet(i+1).getPrecursorOccurrencies();
                
                if (candidate_level < level) {
                    
                    level = candidate_level;
                    chosen_ruleset = (i+1);
                }

            }
        }
        
        return chosen_ruleset;
    }
    */
    


    // Returns a List of Common Non-Wildcarded Indexes
    public ArrayList detectCommonNonWilcardedIndexes (Sensor s2) {
        
        ArrayList res = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ((this.getToken(i).isNotWildcard()) && (s2.getToken(i).isNotWildcard()))
                res.add(i);
        }
        
        return res;
    }
    
    
    // Simple string representation with coma
    public String simple () {
        
        String str = new String();
        
        for (int i = 0; i < this.size(); i++) {
            if (i != this.size()-1)
                str = str.concat(this.getToken(i).toString() + ",");
            else
                str = str.concat(this.getToken(i).toString());
        }
        
        return str;
    }
    
    
    
    // isRewarded now works using strings, because TokenMap referencies are risky
    // MUST BE CAREFUL not to use A and + for the same model etc..
    public boolean isRewarded () {
        
        // Predator Agent
        if (this.getToken(this.size()-2).toString().equals("A"))
            return true;
        
        // Paint Agent
        if (this.getToken(this.size()-2).toString().equals("+"))
            return true;
        
        
        return false;
        
    }
    
  
    // For Paint Agent
    public boolean is_negative_rewarded () {
        

        // Paint Agent
        if (this.getToken(this.size()-2).toString().equals("-"))
            return true;
        
        
        return false;
        
    }
    

    
    public void print () {
        
        System.out.println(this.toString());
    }
    
 
    
    public int detect_level () {
        
        return this.numberOfNonWildcards();
    }
    
    
    // Returns an ArrayList with the indexes of non_wildcarded indexes
    public ArrayList get_nonWildcarded_indexes () {
        
        ArrayList res = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getToken(i).isNotWildcard())
                res.add(i);
        }
        
        return res;
    }
    
    
    
    // Returns true if a Sensor is has an effect (non-root Sensor) 
    public boolean has_effect () {
        
        return (this.numberOfNonWildcards() > 0);
    }
    
    
    
    
}
