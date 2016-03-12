

package EnvModel.PredatorModel;


import EnvAgent.PredatorAgent.*;
import EnvAgent.ClauseLearner.*;
import EnvAgent.RuleLearner.NodeList;
import EnvModel.*;
import StateGenerator.*;
import ac.fz.rl.qneural.QNeuralParams;
import fzdeepnet.Setting;
import Logging.*;
import S_ReinforcementLearner.PREDATORParams;

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
    	// Set configuration file
    	try{
    		QNeuralParams.loadParamsFromArguments(args);
    		System.out.println(QNeuralParams.MODEL_NAME);
    		System.in.read();	
    		//QNeuralParams.MODEL_NAME = "Q Fitted Neural Net";//QLearning";
    		//System.out.println(PREDATORParams.DAT_DIR);
    		//return;
     	}catch(Exception e){e.printStackTrace();}
    	
    	for(int trial=0;trial<PREDATORParams.TRIAL;trial++){
	    	// Here is old code
	        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
	        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
	        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
	        int stepsOntop = 0;
	        int lockedNum = 0;
	        
	        int NUM_MOVES = 100000; //will be doubled for turn taking
	        double totalReward = 0;
	        int lock_count=0;
	        boolean locked = false;
	        for (int i = 0; i < NUM_MOVES * 2; i++){
	            predatorEnvironment.updateEnvironment();
	            if (Math.IEEEremainder(i, 1000000) == 0){
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
	                    lock_count++;
	                }else{
	                	lock_count=0;
	                }
	                
	                if(lock_count==10){
	                	lockedNum ++;
	                	//System.out.println("locked");
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
	        saveResults(trial,stepsOntop,totalReward,lockedNum);
	      
    	}
        pwriter.flush();
        pwriter.close();
        
    }
    protected static PrintWriter pwriter = null;
    public static void saveResults(int trial, int catchNum,double totalReward,int lockedNum){
    	try{
	    	if(pwriter==null){
		    	String filename = PREDATORParams.EXP_DIR + QNeuralParams.SP + QNeuralParams.MODEL_NAME;
		    	File expdir = new File(filename);
		    	if(!expdir.exists()){
		    		expdir.mkdirs();
		    	}
		    	String hNums = "";
		    	for(Setting.Layer lconf:QNeuralParams.LAYERS){
		    		if(lconf.getLid().charAt(0)=='h')
		    			hNums = hNums + String.valueOf(lconf.getDimensions()) + "_";
		    	}
		    	filename = filename + QNeuralParams.SP + "rs_h" +hNums +  
		    			"lr"+QNeuralParams.LEARNING_RATE + "_mm" + QNeuralParams.INIT_MOMENTUM + 
		    			"_cst"+QNeuralParams.WEIGHT_DECAY + ".txt";
		    	pwriter = new PrintWriter(filename,"UTF-8");
		    	//pwriter.println("hello ");
	    	}
	    	pwriter.println(String.valueOf(trial) + " " + String.valueOf(catchNum) + " " + String.valueOf(totalReward) +  " " + String.valueOf(lockedNum));
	    	
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}