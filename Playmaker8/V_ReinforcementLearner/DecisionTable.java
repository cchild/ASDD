
package V_ReinforcementLearner;

import Logging.LogFiles;
import V_RuleLearner.RuleSetList;
import V_Sensors.*;
import V_Sensors.StateMap;
import V_StateGenerator.StateGenerator_Maps;
import java.io.*;
import java.util.*;

/**
 *
 * @author virgile
 */
public class DecisionTable {
    
    public ArrayList <ArrayList> list ;
    
    
    
    // A Decision Table looks like : 
    // STATE 1 [W, E, E, E, E, *] Action : S
    // STATE 2 [E, E, A, E, E, *] Action : S
    // STATE 3 [E, E, E, A, E, *] Action : W

    public DecisionTable () {
        
        list = new ArrayList();
    }
    
    
    public void addSensor (Sensor sen, Token action) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        

        a.add(action);
        
        
        this.list.add(a);
    }
    
    
    
    public void addSensor (Sensor sen, Token action, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        

        a.add(action);
        
        
        this.list.add(index, a);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }

        
        
    public void setAction (int row, Token action) {
        
        this.addSensor(this.getSensor(row), action, row);
        
        this.remove(row+1);
    }
    

    
    public void printTable (String str) {
        
        System.out.println("\nPRITING " + str + " DECISIONTABLE ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("STATE " + (i) + " " + this.getSensor(i) + " Action : " + this.getAction(i));
        }
        
    }
    
    
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int row) {
        
        return (Sensor) this.list.get(row).get(0);
    }

    
    public Token getAction (int row) {
        
        return (Token) this.list.get(row).get(1);
    }
    

    // Returns the index of a Sensor
    // If not found, returns -1
    public int findSensor (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        return -1;
    }
    

    
    // Converts an Action Value Table into a Decision Table
    public DecisionTable fromStateActionValueTable (StateActionValueTable sTab) {
       
       DecisionTable res = new DecisionTable ();
       
       
       
       for (int i = 0; i < sTab.size(); i++) {
           
           Sensor sen = sTab.getSensor(i).copy();
           
           int max_value_index = sTab.findMaxValueIndex(i);
           
           
           ArrayList actions = sTab.getAction(i);
           
           Token action = (Token) actions.get(max_value_index);
           
           
           res.addSensor(sen, action);
       }
       
       
       
       return res;
    }
   
   
   
    // Converts a Value Table into a Decision Table
    public DecisionTable fromStateValueTable (StateValueTable sTab, StateMap stMap) {

    
        DecisionTable res = new DecisionTable ();
       
        StateGenerator_Maps sGen = new StateGenerator_Maps ();
       
        for (int i = 0; i < sTab.size(); i++) {
           
           Sensor sen = sTab.getSensor(i).copy();
           
           Token action = sGen.generateActionFromStateValueTable(sen, stMap, sTab);
           
           
           res.addSensor(sen, action);
       }
       
       
       
       return res;
       
       
    }
   
   
   
   // Returns the action to take when seeing current_state
    public Token chooseAction (Sensor current_state) {
       
       int state_index = this.findSensor(current_state);
       
       // The State is in the Map, we return the Action
       if (state_index != -1)
        return this.getAction(state_index);
       
       
       // The State isn't in the Map, we select a Random Action
       else {
           int number_of_actions = current_state.tokenMap.getTokenList(current_state.size()-1).size();
           double rand = Math.random();
           
           double action_index = (double) number_of_actions;
           
           action_index = action_index * (rand - 0.001);
           
           int action_index_int = (int) action_index;
           
           String token_str =  (String) current_state.tokenMap.getTokenList(current_state.size()-1).get(action_index_int);
           
           Token action = new Token (token_str, current_state.size()-1, current_state.tokenMap);
           
           System.out.println("Rand action : " + action);
           return action;
       }
    }
   
   
   
       
    

    // Exports to StateTable.txt
    public void export () {
        
        LogFiles logFiles = LogFiles.getInstance();
        
        for (int i = 0; i < this.size(); i++) {
            
            logFiles.print(this.getSensor(i).simple() + " ", 6);
            


            logFiles.println(this.getAction(i) + "", 6);

            
        }
    }
    
    
    
    // Imports from StateTable.txt
    //
    // Used in Predator APP 
    public DecisionTable fromFile (TokenMap t) {
        
   
    
        String filePath = Logging.LogFiles.FILE_NAME_6;
        
        DecisionTable dTab = new DecisionTable ();
        
        try {
        
            Scanner scanner=new Scanner(new File(filePath));

            
            while (scanner.hasNextLine()) {

                
                String line = scanner.nextLine();
                
                
                String [] a = line.split(" ");
                
                Sensor s = new Sensor (a[0], t, 2);
                
                Token action = new Token (a[1], s.size()-1, t) ;
                

               dTab.addSensor(s, action);

            }
            
            
            
            scanner.close();

        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
        
        return dTab;
        
    }
    
    
    public DecisionTable from_stateMap (StateMap stMap) {
        
        DecisionTable dec = new DecisionTable ();
        
        
        for (int i = 0; i < stMap.size(); i++) {
            
            
            
            Token wildcard = new Token ("*", stMap.getSensor(0).size()-1, stMap.getSensor(0).tokenMap);
            
            dec.addSensor(stMap.getSensor(i), wildcard);
            
            
        }
        
        return dec;
    }
    
    
    // Converts an Action Value Table into a Decision Table
    public void RVLR (RuleSetList rsList, StateMap stMap) {
       
       
       for (int i = 0; i < stMap.size(); i++) {
        
            SensorList senList = stMap.getSensor(i).expand(stMap.getSensor(i).size()-1);
            
            double max_score = 0.0;
            int max_index = 0;
            
            System.out.println("State : " + stMap.getSensor(i));
            
            for (int j = 0; j < senList.size(); j++) {
                
                double score = rsList.get_RVLR_score(senList.getSensor(j+1));
                
                
                if (score >= max_score) {
                    max_score = score;
                    max_index = j;
                }
                
                System.out.println("Score for  " + senList.getSensor(j+1).getToken(senList.getSensor(j+1).size()-1) + " : " + score);
            }
            
            this.setAction(i, senList.getSensor(max_index+1).getToken(senList.getSensor(max_index+1).size()-1));
       
       }
       
       
    }
   
}
