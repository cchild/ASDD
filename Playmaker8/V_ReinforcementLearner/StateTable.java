/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_ReinforcementLearner;

import V_Sensors.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class StateTable {
    
    public ArrayList <ArrayList> list ;
    
    
    
    
    public StateTable () {
        
        list = new ArrayList();
    }
    
    
    public void addSensor (Sensor sen, Token action, double value) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(action);
        
        a.add(value);
        
        
        this.list.add(a);
    }
    
    
    public void addSensor (Sensor sen, Token action, double value, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(action);
        
        a.add(value);
        
        
        this.list.add(index, a);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }
    
    
    public void setValue (int i, double value) {
        
        this.addSensor(this.getSensor(i), this.getAction(i), value, i);
        
        this.remove(i+1);
    }
    
    
    public void setAction (int i, Token action) {
        
        this.addSensor(this.getSensor(i), action, this.getValue(i), i);
        
        this.remove(i+1);
    }
    
    
    
    public void printTable () {
        this.printTable("");
    }
    
    public void printTable (String str) {
        
        System.out.println("\nPRITING " + str + " STATETABLE ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("SENSOR " + (i+1) + " " + this.getSensor(i) + " Action : " + this.getAction(i) + " Value : " + this.getValue(i));
        }
        
    }
    
    
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.list.get(i).get(0);
    }
    
    public Token getAction (int i) {
        
        return (Token) this.list.get(i).get(1);
    } 
    
    public double getValue (int i) {
        
        return (double) this.list.get(i).get(2);
    }
    
    
    public void fromSensorList (SensorList s) {
        
        for (int i = 0; i < s.size(); i++) {
            
            this.addSensor(s.getSensor(i+1), s.getSensor(i+1).getToken(s.getSensor(i+1).size()-1), 0.0);
        }
    }
    
    
    public boolean containsStateAndAction (Sensor sen, Token action) {
        
        for (int i = 1; i < this.size(); i++) {
            
            if ( (this.getSensor(i).sensorMatch_exact(sen)) && (this.getAction(i).match(action)) )
                return true;
        }
        
        return false;
    }
    
    
}
