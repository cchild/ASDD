/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;


import Logging.LogFiles;
import V_RuleLearner.Rule;
import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class SensorList {
    
    public ArrayList <Sensor> Sensors;
    
    public SensorList()
    {
        //String [][] a = {{""}};
        
        this.Sensors = new ArrayList <> ();
    }
    
    
    public void addSensor (Sensor s) {
        
        this.Sensors.add(s);
        
    }
    
    public void addSensorList (SensorList sList) {
        
        for (int i=0; i < sList.Sensors.size();i++) {
        this.Sensors.add(sList.getSensor(i+1));
        }
        
    }
     
    
    public Sensor getSensor (int position) {
        if(position <= this.Sensors.size()){
        return this.Sensors.get(position-1);
        }
        return this.Sensors.get(1);
    }
    
    
    
    public int fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                //System.out.println("Creating Sensor from line " + i + " (" + line + ")");
                
//                for (int j = 0; j < (line.length()); j++) {
//                    setToken(String.valueOf(line.charAt(j)),j);
//                }
                Sensor s = new Sensor(line,t);

                this.addSensor(s);
                
                //System.out.println("Sensor " + i + " : " + s.getString().toString());
                
                
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }

//    public SensorList getImpossibleList () {
//        return getImpossibleList(this.size());
//    }
    // SHOULD BE STOCKED, TAKING A HUGE TIME IF DATABASE IS LONG
    public SensorList getImpossibleList (SensorMap sMap) {
        
        SensorList res = new SensorList();
        SensorList stock = new SensorList();
        SensorList stock2 = new SensorList();
        
        Sensor root = new Sensor(this.getSensor(1).tokenMap);
        
        // GENERATES ALL POSSIBLE SENSORS WITH 2 NON WILDCARD
        for (int i = 0; i < root.size()-1; i ++) {
            
            stock=root.expand(i);
            int size = stock.size();
                
            for (int j = i+1; j < root.size() -1; j ++) {
                

                for (int h = 0; h < root.size()-1; h++) {
                    
                    stock2.addSensorList(stock.getSensor(h+1).expand(j));
                }
            }
        }
        
        
        for (int y = 0; y < stock2.size(); y++) {
            
            if (sMap.getMatchingOccurencies(stock2.getSensor(y+1)) == (0))
                res.addSensor(stock2.getSensor(y+1));
        }
        
        return res;
        
        
    }
    
    
    public int printList (String str) {
        
        int n = this.Sensors.size();
        System.out.println("\nPRINTING " + str + " SENSORLIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            System.out.println("Sensor " + (i+1) + " " + this.Sensors.get(i).toString());
        }
        
        return 0;
    }

    public int printListWithOcc (SensorList sList) {
        
        int n = this.Sensors.size();
        System.out.println("\nPRINTING SENSORLIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            System.out.println("Sensor " + (i+1) + " " + this.Sensors.get(i) + " Occ :" + sList.findSensor(this.getSensor(i+1)));
        }
        
        return 0;
    }
    
    public int size () {
        return this.Sensors.size();
    }
     
    
    public int findSensor (Sensor s) {
        
        int occurrences = Collections.frequency(this.Sensors, s);
        
        return occurrences;
    }
    
    
    public int findSensor2 (Sensor s) {
        
        int occurrencies = 0;
        for (int i = 0; i < this.size(); i++) {
            
            ArrayList a = s.detectCommonNonWilcardedIndexes(this.getSensor(i+1));
            if (this.getSensor(i+1).sensorMatch(s) && (a.size() > 1))
                    occurrencies++;
        }
        
        return occurrencies;
    }
    
    public int findSensor (String str, TokenMap t) {
        
        Sensor a = new Sensor(str, t);
        
        int occurrences = Collections.frequency(this.Sensors, a);
        
        return occurrences;
    }
    
    public int firstIndexOfSensor (Sensor s) {
        if (this.findSensor(s)==0)
            return -1;
        
        return this.Sensors.indexOf(s)+1;
    }
    
    public ArrayList <Integer> indexesOfSensor (Sensor o) {
        
        ArrayList res = new ArrayList();
        
        for (int i = 1; i <= this.size(); i++) {
            if (this.getSensor(i).sensorMatch(o)) {
                res.add(i);
            }
                    
        }
        return res;
    }

    
    public SensorList clean (SensorList ref, Sensor precondition) {
        
        SensorList res = new SensorList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i+1).numberOfWildcards() == 0) {
                
                Rule r = new Rule (precondition, this.getSensor(i+1), ref);
                
                if (r.occurrencies > 0)
                
                    res.addSensor(this.getSensor(i+1));
//                if (ref.findSensor(this.getSensor(i+1)) > 0)
//                
//                    res.addSensor(this.getSensor(i+1));
            }
        }
        
        return res;
    }
    
    
    
    public SensorList clean (SensorList ref) {
        
        SensorList res = new SensorList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i+1).numberOfWildcards() == 0) {
                
                if (ref.findSensor(this.getSensor(i+1)) > 0)
                
                    res.addSensor(this.getSensor(i+1));
            }
        }
        
        return res;
    }   
    
    
    
    public void expandListAt (int number) {
        
        for (int i =0; i < this.size(); i ++) {
            
            this.addSensorList(this.getSensor(i+1).expand(number));
        }
    }
    
    public int containsRule_fast (Rule rule) {

        
        for (int i =0; i<this.size()-1; i++) {
            //System.out.println("TOUR " + i + "VAUT " + a.get(i));
            if (this.getSensor(i+1).sensorMatch_exact(rule.getPrecondition())) {
                if (this.getSensor(i+2).sensorMatch_exact(rule.getPostcondition())) {
                    
                    return i;
                }
            }
        }
        
        return -1;
        
    }
    
    
    
    public ArrayList <Integer> indexesOfRule (Rule rule) {
        rule.occurrencies = 0;
        ArrayList res = new ArrayList ();
        ArrayList <Integer> a = this.indexesOfSensor(rule.getPrecondition());
        rule.prec_occurrencies = a.size();
        //ArrayList <Integer> b = this.indexesOfSensor(rule.getPostcondition());
        
        if (a.isEmpty()) //|| (b.isEmpty()))
            return res;
        
        for (int i =0; i<a.size()-1; i++) {
            //System.out.println("TOUR " + i + "VAUT " + a.get(i));
            if (this.getSensor(a.get(i)+1).sensorMatch(rule.getPostcondition())) {
                res.add((a.get(i)));
                //rule.occurrencies++;
                rule.prec_occurrencies++;
            //System.out.println("ADDING AT " + i);
            }
        }
        
        return res;
        
    }
    
    
    public ArrayList <Integer> indexesOfRule2 (Rule rule) {
        rule.occurrencies = 0;
        ArrayList res = new ArrayList ();
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            boolean prec_match = false;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();

                Sensor s = new Sensor(line,rule.getPrecondition().tokenMap);
                
                
                if(prec_match) {
                    if (s.sensorMatch(rule.getPostcondition()))
                        res.add(i-1);
                }
                
                if (s.sensorMatch(rule.getPrecondition())) {
                    prec_match = true;
                }
                
                else {
                    prec_match = false;
                }

  
            }
            
            scanner.close();
            return res;
        
 
        }
        
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return res;    
    }
    
    
 
    
    public ArrayList <Integer> indexesOfRuleGivenPrec (Rule rule, ArrayList <Integer> prec) {
        rule.occurrencies = 0;
        ArrayList res = new ArrayList ();
        //ArrayList <Integer> a = this.indexesOfSensor(rule.getPrecondition());
        rule.prec_occurrencies = prec.size();
        //ArrayList <Integer> b = this.indexesOfSensor(rule.getPostcondition());
        
        if (prec.isEmpty()) //|| (b.isEmpty()))
            return res;
        
        for (int i =0; i<prec.size()-1; i++) {
            //System.out.println("TOUR " + i + "VAUT " + a.get(i));
            if (this.getSensor(prec.get(i)+1).sensorMatch(rule.getPostcondition())) {
                res.add((prec.get(i)));
                //rule.occurrencies++;
                rule.prec_occurrencies++;
            //System.out.println("ADDING AT " + i);
            }
        }
        
        return res;
        
    } 
    
    
    
      
    public float Gstatistic(Rule rule1, Rule rule2) {
        //1.count d1 predecessor equals with the database
        int d1Precursorequals = this.indexesOfSensor(rule1.getPrecondition()).size();
        //2. count d1 full equals with the database
        int d1Fullequals = rule1.occurrencies;
        
        //3. count d2 predecessor full equals with the database
        int d2Precursorequals = this.indexesOfSensor(rule2.getPrecondition()).size();
        //4. count d2 successor full equals with the database
        int d2Fullequals = rule2.occurrencies;
        
        //calculate numbers for the Gstatistic
        int n1 = d1Fullequals;
        int n2 = d1Precursorequals - d1Fullequals;
        int n3 = d2Fullequals;
        int n4 = d2Precursorequals -d2Fullequals;
        
        /*Ive put these bit in myself to cope with divide by zero, I think it gets shot of 
         *the right rules on the basis that both are match the same things but one subsumes the
         other*/
        if ((n1 == n3) && (n2 == n4))
            return 0;
        
        /*both these rules have 100% reliability,*/
        /*so we remove the specific one as it should be covered by the general one*/
        if ((n2 + n4) == 0)
            return 0;
        
        if (n4 == 0)
            return 100;
        
        float Gstat = GStatistic(n1, n2, n3, n4);
       
        
        return Gstat;
    }


    public float GStatistic(int n1, int n2, int n3, int n4) {
        long r1 = n1 + n2;
        long r2 = n3 + n4;
        long c1 = n1 + n3;
        long c2 = n2 + n4;
        long t = r1 + r2;

        if ((n1 == n3) && (n2 == n4))
            return 0;

        /*both these rules have 100% reliability,*/
        /*so we remove the specific one as it should be covered by the general one*/
        if ((n2 + n4) == 0)
            return 0;

        if (n4 == 0)
            return 100;
        
        return 2.0f * (float)(
            (float)n1*Math.log((double)(n1*t)/(double)(r1*c1)) +
            (float)n2*Math.log((double)(n2*t)/(double)(r1*c2)) +
            (float)n3*Math.log((double)(n3*t)/(double)(r2*c1)) +
            (float)n4*Math.log((double)(n4*t)/(double)(r2*c2)));
    }
    
    
    // COULD SPEED UP BY WORKING WITH SENSORMAPS INSTEAD OF SENSORLISTS, BUT MINOR THING
    public StateList getPossibleList (Sensor sen, SensorMap sMap) {
        
        SensorList res = new SensorList();
        
        //ArrayList <Integer> a = this.indexesOfSensor(sen);
        
        // USING SMAP
        ArrayList <Integer> a = sMap.getMatchingIndexes(sen);
        
        
        for (int i = 0; i < a.size(); i++) {
            
            res.addSensor(this.getSensor(a.get(i)+1));
        }
        
        for (int j = 0; j < res.size(); j++) {
            
            res.getSensor(j+1).getToken(res.getSensor(j+1).size()-1).setReference(0);
        }
        
        //res.printList();
        
        double hey = res.size();
        //System.out.println(hey + " occ");
        
        StateList states = new StateList ();
        ArrayList <Integer> probs = new ArrayList ();
        ArrayList <Integer> seen = new ArrayList ();
        for (int j = 1; j < res.size(); j++) {
            
            if (!seen.contains(j)) {
                
                double occ = res.indexesOfSensor(res.getSensor(j)).size();
                
                //System.out.println(res.getSensor(j) + " Occ : " + occ);
                
                double prob = occ / hey;
                prob = Math.round(prob * 1000);
                prob = prob/1000;
                
                if (!states.hasSensor(res.getSensor(j)))
                    states.addSensor(res.getSensor(j), prob);
            }
        }
        
        
        return states;
        
    }
    
    
    
    
    

    
 }
     
     
     
    

