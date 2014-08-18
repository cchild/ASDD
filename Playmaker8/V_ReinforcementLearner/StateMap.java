
package V_ReinforcementLearner;

import V_Sensors.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class StateMap {
    
    public ArrayList <ArrayList> list;
    
    
    public StateMap () {
    
        this.list = new ArrayList ();
    }
    
    

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
    
    
    public void increaseOccurenciesOfAction (int row, Token action) {
        
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
        
        int d = this.findReferenceIndex(row, g, ref);
        

        // REF DOESN'T ALREADY EXIST
        if (d == -1) {

            ArrayList e = new ArrayList ();
            e.add(ref);
            e.add(1);
            
            b.add(e);

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
                

                Sensor s = new Sensor(line,t);

                action_saved = new Token (t);
                
                action_saved.setPosition(s.size()-1);
                action_saved.setReference(s.getToken(s.size()-1).getReference());
                
                
                s.getToken(s.size()-1).setReference(0);
   
                int isInMap = this.findSensor(s);
                int isInMap2 = this.findSensor(s_previous);
                
                
                
                // s IS NOT IN MAP
                if (isInMap == -1) {
                    
                    this.addSensor(s, action_saved);
                    
                    this.addReference(isInMap2, this.size()-1, action);
                }
                
                // S IS IN MAP
                else {
                    
                    
                    ArrayList <Token> a = this.getActions(isInMap);

                    
                    boolean seen = false;
                    
                    
                    for (int j = 0; j < a.size(); j++) {

                        // S IS IN MAP WITH ITS ACTION

                            if (a.get(j).getReference() == action_saved.getReference()) {
                                seen = true;
                                this.increaseOccurenciesOfAction(isInMap, action_saved);
                                this.addReference(isInMap2, isInMap, action);
                            }


                    }

                    
                    if (!seen) {

                        // S IS IN MAP BUT NOT THE ACTION, ADDING ACTION
                        
                        this.addAction(isInMap, action_saved);
                        this.addReference(isInMap2, isInMap, action);
                    }
                    
                    
                    
                    
                }
                
                
                s_previous = s;
                action = action_saved.copy();

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
        
       
        ArrayList a = new ArrayList ();
        
        for (int i = 0; i < this.size(); i++) {
            
            if ( (this.getSensor(i).sensorMatch_exact(sen))) {

                // LOOKS FOR BOTH
                ArrayList b = this.getActions(i);
                
                for (int j = 0; j < b.size(); j++) {
                    if (this.getActions(i).get(j).getReference() == action.getReference()) {
                    
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
            
         
        }
        
        
        return -1;
    }
    

}
