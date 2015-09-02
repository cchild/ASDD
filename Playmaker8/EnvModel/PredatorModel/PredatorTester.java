

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

import fzdeepnet.GlobalVar;


public class PredatorTester
{   
  
    PredatorTester() {
        ;
    }
    
    public static void main (String[] args) {
    	// Set configuration file
    	GlobalVar.model_conf_file = args[0];
    	// Set data 
    	if(args.length<2) // If data dir is not in the arguments
    		GlobalVar.dat_dir = System.getProperty("user.dir") + System.getProperty("file.separator");
    	else
    		GlobalVar.dat_dir = args[1]+System.getProperty("file.separator");
    	
    	System.out.println(GlobalVar.dat_dir);
    	// Here is old code
        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
        int stepsOntop = 0;

        int NUM_MOVES = 100000; //will be doubled for turn taking
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

        

        //LogFile logfile1 = new LogFile(1);
        LogFiles logfile1 = LogFiles.getInstance(2);
        System.out.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES);
        System.out.print("\n" + "Total reward: " + totalReward + "\n");
        logfile1.println("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES,2);
        logfile1.println("\n" + "Total reward: " + totalReward + "\n",2);
        logfile1.closeall();        
        predatorEnvironment.testAgentRecords();
    }
}
      