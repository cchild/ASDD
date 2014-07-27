/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;

import V_RuleLearner.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class SensorMap {
    
    
    public ArrayList <ArrayList> map;
    private TokenMap tokenMap;
    
    
    
    
    
    public SensorMap (TokenMap t) {
        
        this.map = new ArrayList ();
        this.tokenMap = t;
    }
    
 
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.map.get(i).get(0);
    }
    
    
    public ArrayList <Integer> getIndexes (int i) {
        
        return (ArrayList <Integer>) this.map.get(i).get(1);
    } 
    
    
    public int getOccurrencies (int i) {
        
        ArrayList a = (ArrayList) this.map.get(i).get(1);
        return a.size();
    } 
    
    
    public void remove (int i) {
        
        this.map.remove(i);
    }
    
    
    public void addSensor (Sensor sen, ArrayList indexes, int insert_index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(indexes);
        
        
        this.map.add(insert_index, a);
    }
 
    
    public void addSensor (Sensor sen, ArrayList indexes) {
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(indexes);
        
        
        this.map.add(a);
    }
    
    public void addSensor (Sensor sen, int firstIndex) {
        
        ArrayList a = new ArrayList ();
        ArrayList b = new ArrayList ();
        
        b.add(firstIndex);
        a.add(sen);
        
        a.add(b);
        
        
        this.map.add(a);
    }    
    
    
    
    public void setSensor (Sensor sen, int index) {
        
        
        this.addSensor(sen, this.getIndexes(index), index+1);
        
        this.remove(index);
    }
    
    // REPLACE EXISTING INDEXLIST AT (INDEX) BY A
    public void setIndexes (ArrayList a, int index) {
        
        
        this.addSensor(this.getSensor(index), a, index+1);
        
        this.remove(index);
    }    
    
    
    public void increaseIndexes (int i, int newindex) {
        
        ArrayList a = (ArrayList) this.map.get(i).get(1);
        
        a.add(newindex);
        
        this.setIndexes(a, i);
    } 
    
    
    public int findSensor (Sensor sen) {
        
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        return -1;
    }
    
     
    public int size () {
        
        return this.map.size();
    }
    
    
    
    
    public int fromFile () {
        

            String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                

                Sensor s = new Sensor(line,tokenMap);

                int tip = this.findSensor(s);
                
                if (tip > -1) {
                    
                    this.increaseIndexes(tip, i);
                }
                
                else {
                    this.addSensor(s, i);
                }
               
                
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }
    
    
    
    public void printList (String str) {
        
        System.err.println("\nPRINTING " + str + " SENSORMAP (" + this.size() + " entries).");
        for (int i =0; i < this.size(); i++) {
            
            System.out.println("Sensor " + i + " " + this.getSensor(i) + " Occurrencies : " + this.getOccurrencies(i) );
        }
    }
    
    
    
    
    public int getMatchingOccurencies (Sensor sen) {
        
        int a = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch(sen))
                a = a + this.getOccurrencies(i);
        }
        
        
        
        return a;
    }
    
    
    public ArrayList <Integer> getMatchingIndexes (Sensor sen) {
        
        ArrayList <Integer> a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch(sen))
                a.addAll(this.getIndexes(i));
        }
        
        
        
        return a;
    }    
    
    
    public int getExactMatchingOccurencies (Sensor sen) {
        
        int a = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return this.getOccurrencies(i);
        }
        
        
        
        return a;
    }
    
    public ArrayList getExactMatchingIndexes (Sensor sen) {
        
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return this.getIndexes(i);
        }
        
        
        
        return a;
    }   
    
    
    public ArrayList <Integer> indexesOfRule (Rule rule) {
        
        ArrayList res = new ArrayList ();
        ArrayList <Integer> a = this.getMatchingIndexes(rule.getPrecondition());
        ArrayList b = this.getMatchingIndexes(rule.getPostcondition());
        
        
        for (int i = 0; i < a.size(); i++) {
            
            if (b.contains(a.get(i)+1))
                res.add(i);
        }
        
        return res;
    }
    
    
    
    public float Gstatistic(Rule rule1, Rule rule2) {
        //1.count d1 predecessor equals with the database
        int d1Precursorequals = this.getMatchingOccurencies(rule1.getPrecondition());
        //2. count d1 full equals with the database
        int d1Fullequals = rule1.occurrencies;
        
        //3. count d2 predecessor full equals with the database
        int d2Precursorequals = this.getMatchingOccurencies(rule2.getPrecondition());
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
    
    
    
}
