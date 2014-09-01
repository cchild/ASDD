

package EnvModel.PaintModel;


import EnvAgent.PaintAgent.*;

import Logging.*;

public class PaintTester
{   
  
    PaintTester() {
        ;
    }
    
    public static void main (String[] args) {
        PaintEnvironment paintEnvironment = new PaintEnvironment();
        PaintAgent agent = (PaintAgent)paintEnvironment.addPaintAgent(0,0);
        paintEnvironment.addPaintBlock(0,0);
        int blocksPainted = 0;
        int blocksUnpainted = 0;
        int totalReward = 0;
        if (PaintAgentBody.useNotCleanWithPickup == true)
             System.out.print("\nWARNIG: *** Using not clean with pickup\n");
        
        
        
        ///////////////////////
        int NUM_MOVES = 100000;
        ///////////////////////
        
        
        
        
        for (int i = 0; i < NUM_MOVES; i++){
            paintEnvironment.updateEnvironment();
            if (agent.USE_REINFORCEMENT_POLICY)
                System.out.print("\nSTEP: " + i);
            int rewardVal = ((PaintAgentPercep)agent.getPaintAgentBody().getPercep()).getPercep(PaintAgentPercep.REWARD);

              if (Math.IEEEremainder(i, 1000000) == 0)
                {
                    System.out.print("\n" + i + " cleanup");
                    System.runFinalization();
                    System.gc();
                }
           
            switch (rewardVal) {
                case PaintRewardFluent.NO_REWARD:
                    break;
                case PaintRewardFluent.POSITIVE_REWARD:
                    if (agent.USE_REINFORCEMENT_POLICY)
                        System.out.print(" Block Painted");
                    blocksPainted ++;
                    break;
                case PaintRewardFluent.NEGATIVE_REWARD:
                    blocksUnpainted ++;
                    break;
                default:
                    System.out.print("ERROR WITH BLOCKS PAINTED IN MAIN LOOP");
            }

            totalReward += agent.getPercep().reward();
          
        }
        
        LogFiles logfile2 = LogFiles.getInstance(2);


       //logfile2.print("\n\nBlocks painted: " + blocksPainted,2);
       //logfile2.print("\nBlocks unpainted: " + blocksUnpainted,2);
       //logfile2.print("\nTotal reward: " + totalReward  + "\n",2);
       System.out.print("\n\nBlocks painted: " + blocksPainted);
       System.out.print("\nBlocks unpainted: " + blocksUnpainted);
       //logfile2.print("\nTotal reward: " + totalReward + "\n",2);
       
       
       //paintEnvironment.testAgentRecords();
       
       
        //System.out.println("OUTPUT PERCEPS & ACTIONS : ");
        //logfile2.println("OUTPUT PERCEPS & ACTIONS : ", 2);
         for (int h = 0; h < agent.getPercepRecord().size(); h++) {


            //System.out.println("ACTIONS : " + agent.getActionRecord().getActionString(h));
            //System.out.println("PERCEPS : " + agent.getPercepRecord().getPercep(h).translation());
             
            //logfile2.println("", 2);
            logfile2.println(agent.getPercepRecord().getPercep(h).translation() + "" + agent.getActionRecord().getAction(h).translation(), 2);
         }
       
       
       
       logfile2.closeall();
    }
}
      