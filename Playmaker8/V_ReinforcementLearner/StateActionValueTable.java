/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_ReinforcementLearner;

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
    
    
    
    
    public StateActionValueTable () {
        
        list = new ArrayList();
    }
    
    
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
    
    
    public void addActionAndValue (int i, Token action, double value) {
        
        ArrayList b = this.getAction(i);
        b.add(action);
        
        ArrayList c = this.getValue(i);
        c.add(value);
        
        this.addSensor(this.getSensor(i), b, c, i);
        
        this.remove(i+1);
    }
    
    
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
    
    
    public void fromSensorList (SensorList s) {
        
        for (int i = 0; i < s.size(); i++) {
            
            this.addSensor(s.getSensor(i+1), s.getSensor(i+1).getToken(s.getSensor(i+1).size()-1), 0.0);
        }
    }
    
    
    public ArrayList <Integer> containsStateAndAction (Sensor sen, Token action) {
        
        //System.out.println("Looking for : " + sen + " & " + action);
        
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ( (this.getSensor(i).sensorMatch_exact(sen))) {

                // LOOKS FOR BOTH
                ArrayList b = this.getAction(i);
                
                for (int j = 0; j < b.size(); j++) {
                    if (this.getAction(i).get(j).getReference() == action.getReference()) {
                    
                    //System.out.println("Found " + this.getSensor(i) + " & : " + this.getAction(i) + " returning true");
                        a.add(i);
                        a.add(j);
                        //System.out.println ( " returning " + a);
                        return a;
                    }
                }
                
                a.add(i);
                //System.out.println ( " returning " + a);
                return a;
            }
        }
        
        //System.out.println ( " returning " + a);
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
    
    
    public int findBestIndex (int i) {
        
        ArrayList a = this.getValue(i);
        double stack = 0.0;
        int res = 0;
        double rand = Math.random() * this.getSumOfRow(i);
        
        for (int j = 0; j < a.size(); j++) {
            
            stack = stack + (double) a.get(j);
            
            if (stack > rand) 
                return j;
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
    
    
    
    public StateActionValueTable sort () {
        
        StateActionValueTable res = new StateActionValueTable (); 
        
        ArrayList <Token> actionsOrder = new ArrayList ();
        
        
        
        this.printTable("BEFORE SORTING");
        
        for (int i = 0; i < this.size(); i++) {
            
            // GETTING SENSOR
            
            Sensor sen = this.getSensor(i);
            
            
            
            // GETTING AND SORTING ACTIONS
            
            ArrayList <Token> actions = this.getAction(i);
            
            ArrayList newActions = new ArrayList ();
            
            ArrayList values = this.getValue(i);
            
            ArrayList newValues = new ArrayList ();
            
            int number = actions.size();
            
                // COPYING ORDER FROM FIRST SENSOR
//                if (i == 0) {
//                
//                    for (int j = 0; j < number; j++) {
//                    
//                        actionsOrder.add(actions.get(j));
//                    }
//                    
//                    
//                    res.addSensor(sen, actions, values, i);
//                }
            
                if (i == 0) {
                
                    for (int j = 0; j < number; j++) {
                    
                        actionsOrder.add(actions.get(j));
                        actionsOrder.get(j).setReference(j+1);
                    }
                    
                    
                    res.addSensor(sen, actions, values, i);
                }
            
                // BUILDING NEWACTIONS, SORTED
                else {
                    
                    for (int u = 0; u < number; u++) {
                        
                        Token t = (Token) actionsOrder.get(u);

                        
                        int index = this.findActionIndex(i, t);
                        if (index != -1) {
                        
                            Token a = this.getAction(i).get(index);
                        
                            double d = this.getValue(i).get(index);
                        
                            newActions.add(a);
                        
                            newValues.add(d);
                        }
                    }
                    
                    
                    res.addSensor(sen, newActions, newValues, i);
                }
                
            
        }
        
        
        System.out.println("AFTER SORTING");
        
        return res;
    }
    
    
    
    
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
    
    
    
    public StateActionValueTable fromStateMap (StateMap stMap) {
        
        
        StateActionValueTable s33 = new StateActionValueTable ();
        
        for (int i = 0; i < stMap.size(); i++) {
            
            Sensor sen = stMap.getSensor(i);
            //System.out.println("State : " + sen);
            
            for (int j = 0; j < stMap.getActions(j).size(); j++) {
                
                Token action = stMap.getActions(i).get(j);
                
                //System.out.println("Action : " + action);
                
                
                if (j == 0)
                    s33.addSensor(sen, action, 0.0);
                
                else 
                    s33.addActionAndValue(i, action, 0.0);
            }
        }
        
        return s33;
    }
    
    
    
    
    
    
    

    public StateActionValueTable fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.FILE_NAME_6;
        
        StateActionValueTable sTable = new StateActionValueTable ();
        
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
         
            //System.out.println("Hello");
            
            while (scanner.hasNextLine()) {

                
                String line = scanner.nextLine();
                
                //System.out.println("Line i " + i + " " + line);
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
                
                
                //System.out.println("Sensor : " + s);
                //System.out.println("actions : " + actions);
                //System.out.println("values : " + values);
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
    
    
    
    public double getSumOfRow (int row) {
        
        double a = 0.0;
        
        for (int i = 0; i < this.getValue(row).size(); i++) {
            
            a = a + this.getValue(row).get(i);
        }
        
        
        return a;
    }
    
    
}
