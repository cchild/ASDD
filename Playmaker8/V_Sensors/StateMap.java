
package V_Sensors;

import V_Sensors.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class StateMap {
    
    public ArrayList <ArrayList> list;
    
    
    // STATEMAPS are designed like this : 
    //
    // STATE 6 [E, W, A, E, E, *] Actions : [N, S] Occurencies : [1, 3] Leading To : [[[7, 1]], [[12, 1], [5, 1], [35, 1]]]
    //
    // In other words [E, W, A, E, E, *] occurs 4 times, 
    //
    // 1 time was action N leading to State 7
    // 3 times were action S leading to States 12 (1 time), 5 (1 time) and 35 (1 time)
    public StateMap () {
    
        this.list = new ArrayList ();
    }
    
    
    // Adds a Sensor with an empty ref list
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
    
    
    // Adds a Sensor with one ref
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
    
    
    // Adds a Sensor and all of its ref
    public void addSensor (Sensor sen, ArrayList <Token> actions, ArrayList <ArrayList <ArrayList <Integer> > > referencies, ArrayList <Integer> occ, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(actions);
        
        a.add(referencies);
        
        a.add(occ);
        
        this.list.add(index, a);
    }
    
    
    // Returns the Occurrencies List
    public ArrayList getOccurencies (int row) {
        
        return (ArrayList) this.list.get(row).get(3);
    }
    
    
    // Increases the occurrencies of the specified action
    public void increaseOccurenciesOfAction (int row, Token action) {
        
        ArrayList a = this.getOccurencies(row);
        
        int b = this.findActionIndex(row, action);
        
        a.set(b, (int) a.get(b)+1);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }
    
    
    // Adds an action and the Reference to an existing State
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
    
    // Finds the index of "ref" in the Reference list for the action "place"
    public int findReferenceIndex (int row, int place, int ref) {
        
        ArrayList <ArrayList <ArrayList <Integer> > >  b = this.getReferencies(row);
        
        
        ArrayList a = b.get(place);
        
            
            for (int i = 0; i < a.size(); i++) {
            
                ArrayList r = (ArrayList) a.get(i);

                if ( (int) r.get(0) == ref)
                    return i;
            }
        
        
        return -1;
    }
    
    
    
    public int findActionIndex (int row, Token action) {
        
        ArrayList  <Token> a = this.getActions(row);
        
        for (int i = 0; i < a.size(); i++) {
                       
            if ( a.get(i).getReference() == action.getReference()) {
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
    
    

    // Builds the StateMap from the INPUT FILE
    public int fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
        Token action;
        Token action_saved;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            
            String line_previous = scanner.nextLine();

            Sensor s_previous = new Sensor(line_previous,t,2);
            
            action = new Token (t);
            
            
            action.setPosition(s_previous.size()-1);
            action.setReference(s_previous.getToken(s_previous.size()-1).getReference());

            s_previous.getToken(s_previous.size()-1).setReference(0);
            
            this.addSensor(s_previous, action);
                
            while (scanner.hasNextLine() && i < 100000) {
                i++;
                String line = scanner.nextLine();
                

                Sensor s = new Sensor(line,t,2);

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
    
    
    
    
    
    // Returns true if State and Action already are in the StateMap
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
    
    
    // Used to choose an action, and Optimal Future Estimation
    public double findMaxValue (int i) {
        
        ArrayList a = this.getReferencies(i);
        double res = 0.0;
        
        for (int j = 0; j < a.size(); j++) {
            
            if ((double) a.get(j) > res) 
                res = (double) a.get(j);
        }
        
        return res;
    }
    
    
    // Index of value above
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
    

    
    

}
