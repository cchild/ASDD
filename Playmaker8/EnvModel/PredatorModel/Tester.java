

package EnvModel.PredatorModel;


import V_Sensors.TokenMap;
import V_Sensors.SensorList;
import EnvAgent.PredatorAgent.*;
import EnvAgent.ClauseLearner.*;
import EnvModel.*;

import Logging.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



public class Tester
{   
  
    Tester() {
        ;
    }
    
    public static void main (String[] args) {

        
        
        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
        int stepsOntop = 0;
        int NUM_MOVES = 10; //will be doubled for turn taking
        double totalReward = 0;
        for (int i = 0; i < NUM_MOVES * 2; i++){
            predatorEnvironment.updateEnvironment();
              if (Math.IEEEremainder(i, 1000000) == 0)
                {
                    System.out.print("\n" + i + " cleanup");
                    System.runFinalization();
                    System.gc();
                }
            if (predatorEnvironment.getAgentsTurn(pred)) {
                if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                    System.out.print("\nSTEP: " + i/2);
                if (((PredatorAgentPercep)pred.getPercep()).isOntopOfAgent()) {
                    if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                        System.out.print("Ontop of prey");
                    stepsOntop ++;
                }

                totalReward += pred.getPercep().reward();
            }
        }



        LogFiles logfile2 = LogFiles.getInstance();
        System.out.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES);
        System.out.print("\n" + "Total reward: " + totalReward + "\n");
        logfile2.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES,2);
        logfile2.print("\n" + "Total reward: " + totalReward + "\n",2);
        
 
        //predatorEnvironment.testAgentRecords();
        
        
        
        //// TARGET STRING /////
        
        String str = "WEEAEN";
        
        ////////////   PERCEP PART   ////////////
        
        
        int n = Logging.LogFiles.getLines();
        for (int i=0;i<8;i++){
        	System.out.println(pred.getPercepRecord().getPercep(i));
        
        }

        
        
        ///////////    ACTION PART  ///////////
        
        Action action = pred.getActionRecord().getAction(0);
  

        action.readFromString(str);
        

        
        TokenMap t = new TokenMap();
        SensorList s = new SensorList ();
        
        t.fromFile();
        s.fromFile(t);
        
        
        
        
        logfile2.closeall();
        
        
    }
}