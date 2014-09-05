

package EnvModel.PredatorModel;


import EnvAgent.PredatorAgent.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.RuleLearner.NodeList;
import EnvModel.*;
import StateGenerator.*;

import Logging.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;



public class PredatorTester
{   
  
    PredatorTester() {
        ;
    }
    
  
    
    public static void main (String[] args) {

        
        
        // READS THE INPUT FILE, LEARNS THE RULES FROM IT
        
        
        
        
        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
        int stepsOntop = 0;
        
        
        
        ///////////////////////
        int NUM_MOVES = 100000;
        ///////////////////////
        
        
        
        
        // READ FROM INPUT >> NUMBER OF LINES IN INPUT
        
            
        
        
         // NUM_MOVES doubled for turn taking
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



        LogFiles logfile2 = LogFiles.getInstance(2);
        
        System.out.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES);
        System.out.print("\n" + "Total reward: " + totalReward + "\n");


        
        
      

                
             
                ////////////////////////////////////////////
                ///// UNCOMMENT THIS FOR OLD BEHAVIOUR /////
                ////////////////////////////////////////////
             
                //predatorEnvironment.testAgentRecords();
        

        
        
        
        
        

        // Exports to LogFileResults.txt
         for (int h = 0; h < pred.getPercepRecord().size(); h++) {


            logfile2.println(pred.getPercepRecord().getPercep(h).translation() + pred.getActionRecord().getAction(h).translation(), 2);
         }

         
        logfile2.closeall();
        
        
        System.out.println(pred.getPercepRecord().size());
        
        
    }
}
      