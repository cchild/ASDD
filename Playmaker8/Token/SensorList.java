/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Token;


import RuleLearner2.Rule;
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
    
    public void addSensorMapSensorListap (SensorList sMap) {
        
        for (int i=0; i < sMap.Sensors.size();i++) {
        this.Sensors.add(sMap.getSensor(i));
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
    
    
    public int printList () {
        
        int n = this.Sensors.size();
        System.out.println("\nPRINTING SENSORLIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            System.out.println("Sensor " + (i+1) + " " + this.Sensors.get(i).toString());
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
    
    
//    public ArrayList indexOfSensor (Sensor s) {
//        
//        ArrayList res = new ArrayList();
//        if (this.findSensor(s)==0)
//            return res;
//        
//        int occurrences = this.findSensor(s);
//        int a = 1;
//        for (int i = 1; i <= occurrences; i++) {
//            
//            int b = this.indexOf(s, a);
//            res.add(a);
//        }
//        
//        return res;
//    }
    
    
    
    
    public boolean containsRule (Rule rule) {
        
//        if ((this.findSensor(rule.getPrecondition()) <= 0) || (this.findSensor(rule.getPostcondition()) <= 0))
//            return false;
//        
//        int index = this.firstIndexOfSensor(rule.getPrecondition());
//        
//        int index2 = this.firstIndexOfSensor(rule.getPostcondition());
//        
//        if (index2 == (index+1))
//                return true;
        
        return (!this.indexOfRule(rule).isEmpty());
    }
    
    
    public ArrayList <Integer> indexOfRule (Rule rule) {
        rule.occurrencies = 0;
        ArrayList res = new ArrayList ();
        ArrayList <Integer> a = this.indexesOfSensor(rule.getPrecondition());
        //ArrayList <Integer> b = this.indexesOfSensor(rule.getPostcondition());
        
        if (a.isEmpty()) //|| (b.isEmpty()))
            return res;
        
        for (int i =0; i<a.size(); i++) {
            //System.out.println("TOUR " + i + "VAUT " + a.get(i));
            if (this.getSensor(a.get(i)+1).sensorMatch(rule.getPostcondition())) {
                res.add((a.get(i)));
                rule.occurrencies++;
            //System.out.println("ADDING AT " + i);
            }
        }
        
        return res;
        
    }
    
    
//     public int indexesOfRule (Rule rule) {
//        
//        if ((this.findSensor(rule.getPrecondition()) <= 0) || (this.findSensor(rule.getPostcondition()) <= 0))
//            return -1;
//        
//        int index = this.firstIndexOfSensor(rule.getPrecondition());
//        
//        int index2 = this.firstIndexOfSensor(rule.getPostcondition());
//        
//        if (index2 == (index+1))
//                return index;
//        
//        return -1;
//    }
    
 }
     
     
     
    

