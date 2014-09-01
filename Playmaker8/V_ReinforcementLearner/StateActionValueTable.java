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
public class StateActionValueTable {
    
    public ArrayList <ArrayList> list ;
    
    
    
    // State Action Value Tables are designed like this : 
    //
    // SENSOR 1 [W, E, E, W, E, *] Actions : [N, S, E, W] Values : [0.35, 0.35, 0.50, 0.33]
    //
    // Each Action has a Separate Value, Inside a State row
    public StateActionValueTable () {
        
        list = new ArrayList();
    }
    
    
    // Adds a Sensor, an action and its value
    public void addSensor (Sensor sen, Token action, double value) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        ArrayList actions = new ArrayList ();
        actions.add(action);
        
        ArrayList values = new ArrayList ();
        values.add(value);
        
        a.add(actions);
        a.add(values);
        
        this.list.add(a);
    }
    
    
    // Same than above for specified index
    public void addSensor (Sensor sen, Token action, double value, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        ArrayList actions = new ArrayList ();
        actions.add(action);
        
        ArrayList values = new ArrayList ();
        values.add(value);
        
        a.add(actions);
        a.add(values);
        
        this.list.add(index, a);
    }
    
    
    // Adds the whole lists
    public void addSensor (Sensor sen, ArrayList <Token> actions, ArrayList <Double> values, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(actions);
        
        a.add(values);
        
        this.list.add(index, a);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }
    
    
    // Adds a new action & value to an existing State
    public void addActionAndValue (int i, Token action, double value) {
        
        ArrayList b = this.getAction(i);
        b.add(action);
        
        ArrayList c = this.getValue(i);
        c.add(value);
        
        this.addSensor(this.getSensor(i), b, c, i);
        
        this.remove(i+1);
    }
    
    
    // Updates the Value of the State "i" and the action "ii"
    public void increaseValue (int i, int ii, double increase) {
        
        ArrayList res = new ArrayList ();
        ArrayList <Double> b = this.getValue(i);
        
        for (int t = 0; t < b.size(); t++) {
            
            if (t != ii) {
                res.add(b.get(t));
            }
            else {
                res.add(b.get(t)+increase);
            }
        }
        
        this.addSensor(this.getSensor(i), this.getAction(i), res, i);
        
        this.remove(i+1);
    }
        
        
    public void setValue (int i, int ii, double value) {
        
        this.addSensor(this.getSensor(i), this.getAction(i).get(ii), value, i);
        
        this.remove(i+1);
    }
    
    
    public void setAction (int i, int ii, Token action) {
        
        this.addSensor(this.getSensor(i), action, this.getValue(i).get(ii), i);
        
        this.remove(i+1);
    }
    
    
    
    public void printTable () {
        this.printTable("");
    }
    
    public void printTable (String str) {
        
        System.out.println("\nPRITING " + str + " State Action Value Table ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("SENSOR " + (i+1) + " " + this.getSensor(i) + " Actions : " + this.getAction(i)  + " Values : " + this.getValue(i) + " max : " + this.getAction(i).get(this.findMaxValueIndex(i)));
        }
        
    }
    
    
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.list.get(i).get(0);
    }
    
    public ArrayList <Token> getAction (int i) {
        
        return (ArrayList <Token>) this.list.get(i).get(1);
    } 
    
    public ArrayList <Double> getValue (int i) {
        
        return (ArrayList <Double>) this.list.get(i).get(2);
    }
    
    

    
    // Returns the indexesof State and Action if the Table contains State & Action
    //
    // INDEX 0 : Index of the State (if any)
    // INDEX 1 : Index of the Action for the State (if any)
    public ArrayList <Integer> containsStateAndAction (Sensor sen, Token action) {
        
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ( (this.getSensor(i).sensorMatch_exact(sen))) {

                // LOOKS FOR BOTH
                ArrayList b = this.getAction(i);
                
                for (int j = 0; j < b.size(); j++) {
                    if (this.getAction(i).get(j).getReference() == action.getReference()) {
                    
                    
                        a.add(i);
                        a.add(j);
                        
                        return a;
                    }
                }
                
                a.add(i);
                
                return a;
            }
        }
        
        
        return a;
    }
    
    
    
    public double findMaxActionValue (int i) {
        
        ArrayList a = this.getValue(i);
        double res = 0.0;
        
        for (int j = 0; j < a.size(); j++) {
            
            if ((double) a.get(j) > res) 
                res = (double) a.get(j);
        }
        
        return res;
    }
    
    
    public int findMaxValueIndex (int i) {
        
        ArrayList a = this.getValue(i);
        double max = 0.0;
        int res = 0;
        
        for (int j = 0; j < a.size(); j++) {
            
            if ((double) a.get(j) > max) {
                res = j;
                max = (double) a.get(j);
            }
        }
        
        return res;
    }
    

    
    
    public int findSensor (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        return -1;
    }
    
    
    public int findActionIndex (int row, Token action) {
        
        ArrayList <Token> a = this.getAction(row);
        
        
        for (int i = 0; i < a.size(); i++) {
            
            if (a.get(i).match_exact(action))
                return i;
        }
        
        return -1;
    }
    
    
    
 
    
    
    // Exports to StateTable.txt
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.println(this.getSensor(i).simple(), 6);
            
            for (int u = 0; u < this.getAction(i).size(); u++) {
            
                logFiles.print(this.getAction(i).get(u).toString() + " ", 6);
                
            }
            
            logFiles.println("", 6);
            for (int u = 0; u < this.getAction(i).size(); u++) {
            
                logFiles.print(this.getValue(i).get(u).toString() + " ", 6);
                
            }
            
            logFiles.println("", 6);
        }
    }
    
    
    
    // Init from StateMap with all values set to 0.0
    public StateActionValueTable fromStateMap (StateMap stMap) {
        
        
        StateActionValueTable actionValueTable = new StateActionValueTable ();
        
        for (int i = 0; i < stMap.size(); i++) {
            
            Sensor sen = stMap.getSensor(i);
            
            for (int j = 0; j < stMap.getActions(i).size(); j++) {
                
                Token action = stMap.getActions(i).get(j);
                
                
                if (j == 0)
                    actionValueTable.addSensor(sen, action, 0.0);
                
                else 
                    actionValueTable.addActionAndValue(i, action, 0.0);
            }
        }
        
        return actionValueTable;
    }
    
    
    
    
    
    
    
    // Imports from StateTable.txt
    //
    // Used in Predator APP
    public StateActionValueTable fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_6;
        
        StateActionValueTable sTable = new StateActionValueTable ();
        
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
         
            while (scanner.hasNextLine()) {

                
                String line = scanner.nextLine();
                
                Sensor s = new Sensor(line,t);
                
                String line2 = scanner.nextLine();
                
                String [] a = line2.split(" ");
                
                ArrayList actions = new ArrayList ();
                
                for (int j = 0; j < a.length; j++) {
                    
                    Token action = new Token (a[j], s.size()-1,  t);
                    
                    actions.add(action);
                }
                
                String line3 = scanner.nextLine();

                String [] b = line3.split(" ");
                
                ArrayList values = new ArrayList ();
                
                for (int j = 0; j < b.length; j++) {
                    
                    
                    values.add(Double.parseDouble(b[j]));
                }
                
                
                sTable.addSensor(s, actions, values, i);
                
                i++;
            }
            
            
            
            scanner.close();

        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return sTable;
        
    }    
    

    
    
}
