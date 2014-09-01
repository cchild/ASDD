
package V_Sensors;


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
        
        this.Sensors = new ArrayList <> ();
    }
    
    // Adds one Sensor
    public void addSensor (Sensor s) {
        
        this.Sensors.add(s);
        
    }
    
    // Adds all the Sensors of a SensorList to another one
    public void addSensorList (SensorList sList) {
        
        for (int i=0; i < sList.Sensors.size();i++) {
        
            this.Sensors.add(sList.getSensor(i+1));
        }
        
    }
     
    // Returns Sensor at position. 
    // Careful : Indexes begin at 1, not 0.
    public Sensor getSensor (int position) {
        if(position <= this.Sensors.size()){
        return this.Sensors.get(position-1);
        }
        return this.Sensors.get(1);
    }
    
    
    // Builds the database from INPUT_FILE.
    public int fromFile (TokenMap t) {
        
        String filePath = Logging.LogFiles.INPUT_FILE;
 
        try {
        
            Scanner scanner=new Scanner(new File(filePath));
            int i = 0;
            while (scanner.hasNextLine()) {
                i++;
                String line = scanner.nextLine();
                
                Sensor s = new Sensor(line,t, 2);

                this.addSensor(s);                
                
            }
            scanner.close();
            return 0;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return 1;
        
    }

    
    // Buils the impossible list level 2 
    // Using SensorMap to be quick
    // Impossible List is all the Sensors that can't be observed
    // Works with 2 non Wildcarded indexes
    public SensorList getImpossibleList (SensorMap sMap) {
        
        SensorList res = new SensorList();
        SensorList stock = new SensorList();
        SensorList stock2 = new SensorList();
        
        Sensor root = new Sensor(this.getSensor(1).tokenMap);
        
        // GENERATES ALL POSSIBLE SENSORS WITH 2 NON WILDCARD
        for (int i = 0; i < root.size()-1; i ++) {
            
            stock=root.expand(i);
            
                
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
    
    
    // Prints a SensorList
    public int printList (String str) {
        
        int n = this.Sensors.size();
        System.out.println("\nPRINTING " + str + " SENSORLIST (SIZE:" + n + ")");
        
        for (int i = 0; i < n; i++) {
            System.out.println("Sensor " + (i+1) + " " + this.Sensors.get(i).toString());
        }
        
        return 0;
    }

    
    public int size () {
        return this.Sensors.size();
    }
     
    
    // Returns the number of occurrencies of a Sensor
    public int findSensor (Sensor s) {
        
        int occurrences = Collections.frequency(this.Sensors, s);
        
        return occurrences;
    }
    
    
    // Another version used for the Impossible List
    public int findSensor2 (Sensor s) {
        
        int occurrencies = 0;
        for (int i = 0; i < this.size(); i++) {
            
            ArrayList a = s.detectCommonNonWilcardedIndexes(this.getSensor(i+1));
            if (this.getSensor(i+1).sensorMatch(s) && (a.size() > 1))
                    occurrencies++;
        }
        
        return occurrencies;
    }
    
    
    // First index of Sensor
    public int firstIndexOfSensor (Sensor s) {
        if (this.findSensor(s)==0)
            return -1;
        
        return this.Sensors.indexOf(s)+1;
    }
    
    
    
    // Returns a List with all the Sensor occurrencies indexes
    public ArrayList <Integer> indexesOfSensor (Sensor o) {
        
        ArrayList res = new ArrayList();
        
        for (int i = 1; i <= this.size(); i++) {
            if (this.getSensor(i).sensorMatch(o)) {
                res.add(i);
            }
                    
        }
        return res;
    }

    

    
    
    // Expands all Sensors at precised index
    // Used for RuleSets
    public void expandListAt (int index) {
        
        for (int i =0; i < this.size(); i ++) {
            
            this.addSensorList(this.getSensor(i+1).expand(index));
        }
    }
     
    
    // Old version to find Rule Occurencies
    // Not used anymore, see RuleMaps
    /*
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
    */
    
    
 }
     
     
     
    

