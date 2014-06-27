

package EnvModel.PredatorModel;


import EnvAgent.PredatorAgent.*;
import EnvAgent.ClauseLearner.*;
import EnvModel.*;

import Logging.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import Token.*;



public class Tester
{   
  
    Tester() {
        ;
    }
    
    public static void main (String[] args) {
//        PrintWriter outfile3 = null;
//        FileWriter w = null;
//        try {
//        
//        w = new FileWriter("c:\\JAVA Projects\\playmaker8\\Hello.txt", true);
//       
//        } catch (IOException e) {
//                System.out.print("\nCANNOT OPEN LOGFILES");
//            }
//        
//        outfile3 = new PrintWriter (w);
//        outfile3.print("Hello");
        
        
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
        
        //logfile2.print("Hello",4);
        
        
        
        //System.out.println(str.charAt(0));
        //predatorEnvironment.testAgentRecords();
        
        
        
        //// TARGET STRING /////
        
        String str = "WEEAEN";
        
        ////////////   PERCEP PART   ////////////
        
        //System.out.println("\nPrecep before function : " + pred.getPercep().toString());
        int n = Logging.LogFiles.getLines();
        for (int i=0;i<8;i++){
        System.out.println(pred.getPercepRecord().getPercep(i));
        
        }
        
        
        //pred.getPercep().readFromString(str);
        //pred.getPercep().setPercep(0, 0);
        //System.out.println("Precep after function : " + pred.getPercep().toString());
        
        //System.out.println(pred.getActionRecord().getActionString(0));
        //System.out.println(pred.getActionRecord().getAction(0).getNumValues());
        //System.out.println(pred.getActionRecord().);
        
        
        
        ///////////    ACTION PART  ///////////
        
        Action action = pred.getActionRecord().getAction(0);
  
        //System.out.println("\nAction before function : " + action.toString());
 
        action.readFromString(str);
        
        //System.out.println("Action after function : " + action.toString());
        
        //pred.getPercep().readFile(pred);
        
        TokenMap t = new TokenMap();
        SensorList s = new SensorList ();
        
        t.fromFile();
        s.fromFile(t);
        
        
        System.out.println(s.getSensor(3).getString());
        
        logfile2.closeall();
        
        
    }
}