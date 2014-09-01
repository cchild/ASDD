/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_ReinforcementLearner;

import V_Sensors.StateMap;
import Logging.LogFiles;
import V_Sensors.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class StateValueTable {
    
    public ArrayList <ArrayList> list ;
    
    
    
    // A State Value Table looks like : 
    //
    // STATE 1 [E, W, W, E, A, *] Value : 1.71
    // STATE 2 [E, E, W, W, A, *] Value : 1.66
    // STATE 3 [E, W, A, E, E, *] Value : 1.64

    public StateValueTable () {
        
        list = new ArrayList();
    }
    
    
    public void addSensor (Sensor sen, double value) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        

        a.add(value);
        
        
        this.list.add(a);
    }
    
    
    public void addSensor (Sensor sen, double value, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);

        a.add(value);
        
        this.list.add(index, a);
    }
    
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }

    
    
    public void increaseValue (int i, double increase) {
        

        double b = this.getValue(i);
        

        
        this.addSensor(this.getSensor(i), (b+increase), i);
        
        this.remove(i+1);
    }
        
        
    public void setValue (int i, double value) {
        
        this.addSensor(this.getSensor(i), value, i);
        
        this.remove(i+1);
    }
    

    
    
    
    public void printTable () {
        this.printTable("");
    }
    
    public void printTable (String str) {
        
        System.out.println("\nPRITING " + str + " STATEVALUETABLE ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("STATE " + (i+1) + " " + this.getSensor(i) + " Value : " + this.getValue(i));
        }
        
    }
    
    
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.list.get(i).get(0);
    }

    public double getValue (int i) {
        
        return (double) this.list.get(i).get(1);
    }
    

 
    
    public int findSensor (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        return -1;
    }
    
    
    

    // Exports to StateTable.txt
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.print(this.getSensor(i).simple() + " ", 6);
            


            logFiles.println(this.getValue(i) + "", 6);

            
        }
    }
    
    
    // Inits with all values to 0 from the StateMap
    public StateValueTable fromStateMap (StateMap stMap) {
        
        StateValueTable res = new StateValueTable ();
        
        
        for (int i = 0; i < stMap.size(); i++) {
            
            res.addSensor(stMap.getSensor(i), 0.0);
        }
        
        
        return res;
    }
    
    
    
    // Returns the index of the maximum value
    public int findMaxValueIndex () {
        
        double max = 0.0;
        int index = 0;
        
        for (int i = 0; i < this.size(); i++) {
            
            
            
            if (this.getValue(i) > max) {
                index = i;
                max = this.getValue(i);
            }
        }
        
        
        return index;
    }
    
    
    // Sorts by decreasing Value
    public StateValueTable sort () {
        
        StateValueTable res = new StateValueTable ();
        
        int sep = this.size();
        
        for (int i = 0; i < sep; i++) {
            
            int a = this.findMaxValueIndex();
            
            res.addSensor(this.getSensor(a), this.getValue(a));
            
            this.remove(a);
        }
        
        return res;
    }
    
    
    // Imports from StateTable.txt
    // 
    // Used in Predator APP
    public StateValueTable fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_6;
        
        StateValueTable sTable = new StateValueTable ();
        
        try {
        
            Scanner scanner=new Scanner(new File(filePath));

            
            while (scanner.hasNextLine()) {

                
                String line = scanner.nextLine();
                
                
                String [] a = line.split(" ");
                
                Sensor s = new Sensor (a[0], t);
                
                double value = Double.parseDouble(a[1]);
                

                sTable.addSensor(s, value);

            }
            
            
            
            scanner.close();

        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return sTable;
        
    }   
    
    
   

    
    
}
