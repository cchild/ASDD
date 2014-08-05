/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_ReinforcementLearner;

import V_Sensors.Sensor;
import V_Sensors.SensorList;
import V_Sensors.Token;
import V_Sensors.TokenMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author virgile
 */
public class StateMap {
    
    public ArrayList <ArrayList> list;
    
    
    public StateMap () {
    
        this.list = new ArrayList ();
    }
    
    
    
//    public void addSensor (Sensor sen, Token action, int value) {
//        
//        
//        ArrayList a = new ArrayList ();
//        
//        a.add(sen);
//        
//        ArrayList actions = new ArrayList ();
//        actions.add(action);
//        
//        ArrayList references = new ArrayList ();
//        references.add(value);
//        
//        a.add(actions);
//        a.add(references);
//        
//        
//        ArrayList h = new ArrayList ();
//        
//        int occurencies = 1;
//        
//        h.add(occurencies);
//        
//        a.add(h);
//        
//        this.list.add(a);
//    }
    
    public void addSensor (Sensor sen, Token action) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        ArrayList actions = new ArrayList ();
        actions.add(action);
        
        ArrayList references = new ArrayList ();
        ArrayList refForAction = new ArrayList ();
        
        references.add(refForAction);
        
        a.add(actions);
        a.add(references);
        
        ArrayList h = new ArrayList ();
        
        int occurencies = 1;
        
        h.add(occurencies);
        
        a.add(h);
        
        this.list.add(a);
    }
    
    public void addSensor (Sensor sen, Token action, int ref, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        ArrayList actions = new ArrayList ();
        actions.add(action);
        
        ArrayList referencies = new ArrayList ();
        referencies.add(ref);
        
        a.add(actions);
        a.add(referencies);
        
        
        ArrayList h = new ArrayList ();
        
        int occurencies = 1;
        
        h.add(occurencies);
        
        a.add(h);
        
        this.list.add(index, a);
    }
    
    
    
    public void addSensor (Sensor sen, ArrayList <Token> actions, ArrayList <ArrayList <ArrayList <Integer> > > referencies, ArrayList <Integer> occ, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(actions);
        
        a.add(referencies);
        
        a.add(occ);
        
        this.list.add(index, a);
    }
    
    
    public ArrayList getOccurencies (int row) {
        
        return (ArrayList) this.list.get(row).get(3);
    }
    
    
    public void increaseOccurencies (int row, Token action) {
        
        ArrayList a = this.getOccurencies(row);
        
        int b = this.findActionIndex(row, action);
        
        a.set(b, (int) a.get(b)+1);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }
    
    
    public void addActionAndReference (int i, Token action, int ref) {
        
        ArrayList b = this.getActions(i);
        b.add(action);
        
        ArrayList c = this.getReferencies(i);
        c.add(ref);
        
        ArrayList d = new ArrayList ();
        d.add(1);
        this.addSensor(this.getSensor(i), b, c, d, i);
        
        this.remove(i+1);
    }
    
    
    public void addAction (int i, Token action) {
        
        ArrayList b = this.getActions(i);
        b.add(action);
        
        ArrayList c = this.getReferencies(i);
        //System.out.println(c.getClass());
        ArrayList d = new ArrayList();
        c.add(d);
        
        ArrayList f = (ArrayList) this.getOccurencies(i);
        
        f.add(1);
        
        
        this.addSensor(this.getSensor(i), b, c, f, i);
        
        this.remove(i+1);
    }    
    
    
   
        
    public void addReference (int row, int ref, Token action) {
        
        ArrayList <ArrayList <ArrayList <Integer> > > a = this.getReferencies(row);
        
        
        int g = this.findActionIndex(row, action);
        
        ArrayList b = a.get(g);
        //System.out.println("b : " + b);
        
        int d = this.findReferenceIndex(row, g, ref);
        

        // REF DOESN'T ALREADY EXIST
        if (d == -1) {

            ArrayList e = new ArrayList ();
            e.add(ref);
            e.add(1);
            
            b.add(e);
            
            //System.out.println("Added " + e + " to b : " + b);
            a.set(g,b);
            
            this.addSensor(this.getSensor(row), this.getActions(row), a, this.getOccurencies(row), row);

            this.remove(row+1);
        }
        
        // REF EXISTS, ADDING 1
        else {
            
            ArrayList k = (ArrayList) b.get(d);
            
            k.set(1, (int) k.get(1)+1);
        }
        
        
        
    }
    
    public int findReferenceIndex (int row, int place, int ref) {
        
        ArrayList <ArrayList <ArrayList <Integer> > >  b = this.getReferencies(row);
        
        //System.out.println ( " b is " + b);
        
        
        ArrayList a = b.get(place);
        
            //System.out.println(" a is : " + a);
            for (int i = 0; i < a.size(); i++) {
            
                ArrayList r = (ArrayList) a.get(i);

                if ( (int) r.get(0) == ref)
                    return i;
            }
        
        
        return -1;
    }
    
    public int findActionIndex (int row, Token action) {
        
        ArrayList  <Token> a = this.getActions(row);
        
        //System.out.print("Looking for " + action + " in " + a );
        for (int i = 0; i < a.size(); i++) {
                       
            if ( a.get(i).getReference() == action.getReference()) {
                //System.out.println("Result : " + i);
                return i;
            }
        }
        
        return -1;
    }    
////    public void setAction (int i, int ii, Token action) {
////        
////        this.addSensor(this.getSensor(i), action, this.getReferencies(i).get(ii), i);
////        
////        this.remove(i+1);
////    }
    
    
    
    public void printMap () {
        this.printMap("");
    }
    
    public void printMap (String str) {
        
        System.out.println("\nPRITING " + str + " STATEMAP ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("STATE " + (i) + " " + this.getSensor(i) + " Actions : " + this.getActions(i)  + " Occurencies : " + this.getOccurencies(i) + " Leading To : " + this.getReferencies(i));
        }
        
    }
    
    
    
    public void printMap_soft (String str) {
        
        System.out.println("\nPRITING " + str + " STATEMAP ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            for (int j = 0; j < this.getActions(i).size(); j++) {
                System.out.println("STATE " + (i) + " " + this.getSensor(i) + " Action : " + this.getActions(i).get(j)  + " Occurencies : " + this.getOccurencies(i).get(j) + " Leading To : " + this.getReferencies(i).get(j));
            }
            System.out.println("(///////////////////////)");
        }
    }
    
    
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.list.get(i).get(0);
    }
    
    public ArrayList <Token> getActions (int i) {
        
        return (ArrayList <Token>) this.list.get(i).get(1);
    } 
    
    public ArrayList getReferencies (int i) {
        
        return (ArrayList) this.list.get(i).get(2);
    }
    
    
//    public void fromSensorList (SensorList s) {
//        
//        for (int i = 0; i < s.size(); i++) {
//            
//            this.addSensor(s.getSensor(i+1), s.getSensor(i+1).getToken(s.getSensor(i+1).size()-1), 99);
//        }
//    }
    
    
    public int fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
        Token action;
        Token action_saved;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            
            String line_previous = scanner.nextLine();

            Sensor s_previous = new Sensor(line_previous,t);
            
            action = new Token (t);
            
            
            action.setPosition(s_previous.size()-1);
            action.setReference(s_previous.getToken(s_previous.size()-1).getReference());

            s_previous.getToken(s_previous.size()-1).setReference(0);
            
            this.addSensor(s_previous, action);
                
            while (scanner.hasNextLine() && i < 100000) {
                i++;
                String line = scanner.nextLine();
                
                //System.out.println("line " + i + " (" + line + ")");
                
//                for (int j = 0; j < (line.length()); j++) {
//                    setToken(String.valueOf(line.charAt(j)),j);
//                }
                Sensor s = new Sensor(line,t);

                action_saved = new Token (t);
                
                action_saved.setPosition(s.size()-1);
                action_saved.setReference(s.getToken(s.size()-1).getReference());
                
                //System.out.println("Action : " + action);
                s.getToken(s.size()-1).setReference(0);
                
                
                //System.out.println("State " + s_previous + " & " + action_saved + " led to : " + s);
                
                int isInMap = this.findSensor(s);
                int isInMap2 = this.findSensor(s_previous);
                
                //System.out.println("Index : " + isInMap2);
                
                // s IS NOT IN MAP
                if (isInMap == -1) {
                    //System.out.println("Adding Sensor " + s + " with action " + action_saved);
                    this.addSensor(s, action_saved);
                    //System.out.println(this.findActionIndex(isInMap2, action));
                    this.addReference(isInMap2, this.size()-1, action);
                }
                
                // S IS IN MAP
                else {
                    
                    
                    ArrayList <Token> a = this.getActions(isInMap);

                    //System.out.println("Sensor " + s + " found at index : " + isInMap + ", Actions : " + a);
                    
                    boolean seen = false;
                    
                    //System.out.println("Looking for : " + action_saved.getReference() + " in " + a);
                    
                    for (int j = 0; j < a.size(); j++) {

                        // S IS IN MAP WITH ITS ACTION

                            if (a.get(j).getReference() == action_saved.getReference()) {
                                //System.out.println("Found same ref (" + action_saved.getReference() + " at index " + j);
                                seen = true;
                                this.increaseOccurencies(isInMap, action_saved);
                                this.addReference(isInMap2, isInMap, action);
                            }


                    }

                    //System.out.println("bool is : " + seen);
                    
                    if (!seen) {

                        // S IS IN MAP BUT NOT THE ACTION, ADDING ACTION
                        
                        //System.out.println("Adding Action " + action_saved + " to Sensor " + s + " at index " + isInMap);
                        this.addAction(isInMap, action_saved);
                        this.addReference(isInMap2, isInMap, action);
                    }
                    
                    
                    
                    
                }
                
                
                s_previous = s;
                action = action_saved.copy();
                //action.setReference(action1.getReference());
                
                //System.out.println("Action " + i + " : " + s.getString().toString());
                
                
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }
    
    
    public ArrayList <Integer> containsStateAndAction (Sensor sen, Token action) {
        
        //System.out.println("Looking for : " + sen + " & " + action);
        
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ( (this.getSensor(i).sensorMatch_exact(sen))) {

                // LOOKS FOR BOTH
                ArrayList b = this.getActions(i);
                
                for (int j = 0; j < b.size(); j++) {
                    if (this.getActions(i).get(j).getReference() == action.getReference()) {
                    
                    //System.out.println("Found " + this.getSensor(i) + " & : " + this.getActions(i) + " returning true");
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
    
    
    public double findMaxValue (int i) {
        
        ArrayList a = this.getReferencies(i);
        double res = 0.0;
        
        for (int j = 0; j < a.size(); j++) {
            
            if ((double) a.get(j) > res) 
                res = (double) a.get(j);
        }
        
        return res;
    }
    
    
    public int findMaxValueIndex (int i) {
        
        ArrayList a = this.getReferencies(i);
        int res = 0;
        
        for (int j = 0; j < a.size(); j++) {
            
            if ((double) a.get(j) > res) 
                res = j;
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
    
    
    public int findReference (int row, int number) {
        
        ArrayList <ArrayList <ArrayList <Integer> > > b = this.getReferencies(row);
        
        for (int i = 0; i < b.size(); i++) {
            
////            if (b.get(i).get(0) == number)
////                return b.get(i).get(1);
                 
        }
        
        
        return -1;
    }
    
    

    
    
    
    
    
    
    
    
////////////    public void addNewState (Sensor sen, Token t) {
////////////        
////////////        ArrayList a = new ArrayList ();
////////////        
////////////        ArrayList actions = new ArrayList ();
////////////        
////////////        ArrayList leading_to = new ArrayList ();
////////////        
////////////        
////////////        a.add(sen);
////////////        
////////////        actions.add(t);
////////////        
////////////        a.add(actions);
////////////        
////////////        a.add(leading_to);
////////////        
////////////        
////////////        this.list.add(a);
////////////    }
////////////    
////////////    
////////////    public int findActionIndex (int a, Token action) {
////////////        
////////////        ArrayList <Token>  b = this.getActions(a);
////////////        
////////////        for (int j = 0; j < b.size(); j++) {
////////////            
////////////            if (b.get(j).getReference() == action.getReference())
////////////                return j;
////////////        }
////////////        
////////////        return -1;
////////////    }
////////////    
////////////    
////////////
////////////    public void addAction (int row, Token action) {
////////////        
////////////        ArrayList a = this.getActions(row);
////////////        
////////////        ArrayList b = new ArrayList ();
////////////        
////////////        for (int i = 0; i < a.size(); i++) {
////////////            
////////////            b.add(a.get(i));
////////////        }
////////////        
////////////        b.add(action);
////////////        
////////////        this.list.get(row).add(1, b);
////////////        
////////////        this.list.get(row).remove(2);
////////////        
////////////    }
////////////    
////////////    
////////////    public void addReference (int i, int ref) {
////////////        
////////////        ArrayList a = this.getLeadingList(i);
////////////        
////////////        boolean done = false;
////////////        
////////////        for (int j = 0; j < a.size(); j++) {
////////////            
////////////            ArrayList b = (ArrayList) a.get(j);
////////////            if (b.contains(ref)) {
////////////                
////////////                int zou = (int) b.get(1);
////////////                b.set(1, zou+1);
////////////                done = true;
////////////            }
////////////        }
////////////        
////////////        if (!done) {
////////////            ArrayList b = new ArrayList ();
////////////        
////////////            b.add(ref);
////////////            b.add(1);
////////////
////////////            a.add(b);
////////////        }
////////////        
////////////    }
////////////    
////////////    public Sensor getState (int i) {
////////////        
////////////        return (Sensor) this.list.get(i).get(0);
////////////    }
////////////    
////////////    public ArrayList getActions (int i) {
////////////        
////////////        return (ArrayList) this.list.get(i).get(1);
////////////    }
////////////        
////////////    public ArrayList getLeadingList (int i) {
////////////        
////////////        return (ArrayList) this.list.get(i).get(2);
////////////    }
////////////    
////////////    public int findState (Sensor sen) {
////////////        
////////////        for (int i = 0; i < this.size(); i++) {
////////////            
////////////            if (this.getState(i).sensorMatch_exact(sen))
////////////                return i;
////////////        }
////////////        
////////////        return -1;
////////////    }
////////////    
////////////    
////////////    public int size () {
////////////        
////////////        return this.list.size();
////////////    }
////////////    
////////////    
////////////    
////////////    public int fromFile (TokenMap t) {
////////////        
////////////        String filePath = Logging.LogFiles.INPUT_FILE;
////////////        Token action = new Token(t);
////////////
//////////// 
////////////        try {
////////////        
////////////            Scanner scanner=new Scanner(new File(filePath));
////////////            int i = 0;
////////////            
////////////////            String previous_line = scanner.nextLine();
////////////////            Sensor firstSensor = new Sensor(previous_line,t);
////////////////            action = firstSensor.getToken(firstSensor.size()-1);
////////////////            firstSensor.setToken("*", firstSensor.size()-1);
////////////////            this.addState(firstSensor, action);
////////////            
////////////            while (scanner.hasNextLine()) {
////////////                
////////////                String line = scanner.nextLine();
////////////                
////////////                Sensor s = new Sensor(line,t);
////////////
////////////                System.out.print("Sensor : " + s );
////////////                action.setPosition(s.size()-1);
////////////                
////////////                action.setReference(s.getToken(s.size()-1).getReference());
////////////                
////////////                System.out.print(" Action : " + action + " ref : " + action.getReference());
////////////                
////////////                s.getToken(s.size()-1).setReference(0);
////////////                 
////////////                System.out.println(" New Sensor : " + s );
////////////                int row = this.findState(s);
////////////                
////////////                if (row == -1) {
////////////                    
////////////                    i++;
////////////                    
////////////
////////////                    
////////////                    System.out.println(i);
////////////            
////////////                    this.addNewState(s, action);
////////////                }
////////////                
////////////                else {
////////////                    
////////////                    int u = this.findActionIndex(row, action);
////////////                    
////////////                    System.out.println("U : " + u);
////////////                    
////////////                    if (u == -1) {
////////////                        
////////////                        
////////////                        this.addAction(row, action);
////////////                    }
////////////                }
////////////////                    int hey = this.findState(firstSensor);
////////////////                    this.addReference(hey, i);
////////////////                    firstSensor = s.copy();
////////////////                }
////////////////                
////////////////                else {
////////////////                    i++;
////////////////                    //int hey = this.findState(firstSensor);
////////////////                    //this.addReference(hey, test);
////////////////                    //firstSensor = s.copy();
////////////////                }
////////////
////////////                
////////////            }
////////////            scanner.close();
////////////            return 0;
////////////        
//////////// 
////////////        }
////////////        catch (FileNotFoundException e) {
////////////            System.out.println("ERROR OPENING INPUT FILE");
////////////        }
////////////        
////////////        return 1;
////////////        
////////////    }
////////////    
////////////    
////////////    
////////////    public int printList (String str) {
////////////        
////////////        int n = this.size();
////////////        //int n2 = this.bigSize();      + " (GLOBAL SIZE : " + n2 + ")"
////////////        
////////////        System.out.println("\nPRINTING " + str + " STATEMAP (SIZE:" + n + ")" );
////////////        
////////////        for (int i = 0; i < n; i++) {
////////////            System.out.println("State " + (i) + " " + this.getState(i) + " Actions : " + this.getActions(i) + " References : " + this.getLeadingList(i));
////////////        }
////////////        
////////////        return 0;
////////////    }
////////////    
////////////    public int bigSize () {
////////////        
////////////        int a = 0;
////////////        
////////////        for (int i = 0; i < this.size(); i++) {
////////////            
////////////            a = a + this.getLeadingList(i).size();
////////////        }
////////////        
////////////        return a;
////////////    }
    
    
    
    
    
}
