

package EnvModel.KillPredatorModel;


import EnvAgent.KillPredatorAgent.*;
import EnvModel.Action;
import Logging.*;

public class KillPredatorTester
{   
  
    KillPredatorTester() {
        ;
    }
    
    public static void main (String[] args) {
        KillPredatorEnvironment killPredatorEnvironment = new KillPredatorEnvironment();
        KillPredatorAgent pred = (KillPredatorAgent)killPredatorEnvironment.addKillPredatorAgent(0,0, KillPredatorAgent.PREDATOR);
        KillPredatorAgent prey = (KillPredatorAgent)killPredatorEnvironment.addKillPredatorAgent(3,3, KillPredatorAgent.PREY);
        int stepsOntop = 0;
        int kills = 0;
        int antiKill = 0;
        int NUM_MOVES = 20; //will be doubled for turn taking
        double totalReward = 0;
        for (int i = 0; i < NUM_MOVES * 2; i++){
            killPredatorEnvironment.updateEnvironment();
              if (Math.IEEEremainder(i, 1000000) == 0)
                {
                    System.out.print("\n" + i + " cleanup");
                    System.runFinalization();
                    System.gc();
                }
            if (killPredatorEnvironment.getAgentsTurn(pred)) {
                if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                    System.out.print("\nSTEP: " + i/2);
                if (((KillPredatorAgentPercep)pred.getPercep()).isOntopOfAgent()) {
                    if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                        System.out.print("Ontop of prey");
                    stepsOntop ++;
                }
                String fluent = pred.getPercep().getFluent(5).toString();
                if (fluent.equals("PO_RE")) {
                    if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                        System.out.print(" Kill");
                    kills ++;
                }

                fluent = pred.getPercep().getFluent(5).toString();
                if (fluent.equals("NE_RE")) {
                    if (pred.LEARN_CLAUSES_ASDD || pred.LEARN_RULES_MSDD)
                        System.out.print(" Kill");
                    antiKill ++;
                }
               
                
                totalReward += pred.getPercep().reward();
            }
        }

        LogFiles logfile2 = LogFiles.getInstance();
        System.out.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES);
        System.out.print("\nKills:" + kills );
        System.out.print("\nAnti Kills:" + antiKill );
        System.out.print("\n" + "Total reward: " + totalReward + "\n");
        logfile2.print("\n\n/STEPS Ontop: " + stepsOntop + "out of " + NUM_MOVES,2);
        logfile2.print("\nKills:" + kills,2);
        logfile2.print("\nAnti-Kills:" + antiKill,2);
        logfile2.print("\n" + "Total reward: " + totalReward + "\n",2);
        
        killPredatorEnvironment.testAgentRecords();
        
        //// TARGET STRING /////
        
        String str = "WEEAE+E";
        
        
        
        ////////////   PERCEP PART   ////////////       
        
        System.out.println("\nPrecep before function : " + pred.getPercep().toString());
        pred.getPercep().readFromString(str);
        //pred.getPercep().setPercep(0, 0);
        
        System.out.println("Precep after function : " + pred.getPercep().toString());
        //System.out.println(pred.getPercep().getFluent(5).getNumValues());
        
        
        
        ///////////    ACTION PART  ///////////
        
        Action action = pred.getActionRecord().getAction(0);
  
        System.out.println("\nAction before function : " + action.toString());
        
        
        //action.setByValue(9);
        action.readFromString(str);
        
        System.out.println("Action after function : " + action.toString());
        
        logfile2.closeall();
    }
}
      