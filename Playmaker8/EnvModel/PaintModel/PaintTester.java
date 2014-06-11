

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

        for (int i = 0; i < 200; i++){
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
        
        LogFiles logfile2 = LogFiles.getInstance();


       logfile2.print("\n\nBlocks painted: " + blocksPainted,2);
       logfile2.print("\nBlocks unpainted: " + blocksUnpainted,2);
       logfile2.print("\nTotal reward: " + totalReward  + "\n",2);
       System.out.print("\n\nBlocks painted: " + blocksPainted);
       System.out.print("\nBlocks unpainted: " + blocksUnpainted);
       logfile2.print("\nTotal reward: " + totalReward + "\n",2);
       
       
       paintEnvironment.testAgentRecords();
       
       
       
       
       
       logfile2.closeall();
    }
}
      