

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
    
    // Counts the number of lines in a file
    public static int getLines () {
        String filePath = Logging.LogFiles.INPUT_FILE;
        int lines = 1;
         try {
        
            Scanner scanner=new Scanner(new File(filePath));
            
            
            while (scanner.hasNextLine()) {
                lines++;
                scanner.nextLine();
            }
            
            
            scanner.close();
            return lines;
        
 
        }
        catch (FileNotFoundException e) {
            System.out.println("ERROR OPENING INPUT FILE");
        }
         return 0;
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
        
        
        // READS THE INPUT FILE, LEARNS THE RULES FROM IT
        boolean changeFromInput = false;
        
        
        
        PredatorEnvironment predatorEnvironment = new PredatorEnvironment();
        PredatorAgent pred = (PredatorAgent)predatorEnvironment.addPredatorAgent(0,0,PredatorAgent.PREDATOR);
        PredatorAgent prey = (PredatorAgent)predatorEnvironment.addPredatorAgent(3,3,PredatorAgent.PREY);
        int stepsOntop = 0;
        int NUM_MOVES = 1000;
        if (changeFromInput) {
            NUM_MOVES = getLines();
        }
         //will be doubled for turn taking
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
        
        
        
        
        
        
        
        
        //// TARGET STRING /////
        
        //String str = "WEEAEN";
        
        ////////////   PERCEP PART   ////////////
        
        //System.out.println("\nPrecep before function : " + pred.getPercep().toString());
        //pred.getPercep().readFromString(str);
        //pred.getPercep().setPercep(0, 0);
        //System.out.println("Precep after function : " + pred.getPercep().toString());
        
        //System.out.println(pred.getActionRecord().getActionString(0));
        //System.out.println(pred.getActionRecord().getAction(0).getNumValues());
        //System.out.println(pred.getActionRecord().);
        
        
        
        ///////////    ACTION PART  ///////////
        
        //Action action = pred.getActionRecord().getAction(0);
  
        //System.out.println("\nAction before function : " + action.toString());
 
        //action.readFromString(str);
        
        //System.out.println("Action after function : " + action.toString());
        
        
        
        // READ THE TEXT FILE : LogFileInput.txt
        if (changeFromInput)
        {
        
            int lines = getLines();
            int i = 0;

             for (int h = 0; h < pred.getActionRecord().size(); h++) {

            //System.out.println("INITIAL PERCEPS : " + pred.getPercepRecord().getPercep(h));
            //System.out.println("INITIAL ACTIONS : " + pred.getActionRecord().getActionString(h));

             }


            String filePath = Logging.LogFiles.INPUT_FILE;

            
            //logfile2.println("OUTPUT PERCEPS : ", 4);
             try {

                Scanner scanner=new Scanner(new File(filePath));

                System.out.println();
                while (scanner.hasNextLine()) {
                // modifying manually the perceps & actions
                    String line = scanner.nextLine();
                    pred.getPercepRecord().getPercep(i).readFromString(line);
                    //logfile2.println(i + " : " + pred.getPercepRecord().getPercep(i).toString() , 4);
                    pred.getActionRecord().getAction(i).readFromChar(line.charAt(line.length()-1));
                    i++;
                }

                
                scanner.close();

            }
            catch (FileNotFoundException e) {
                System.out.println("ERROR OPENING INPUT FILE");
            }




              for (int h = 0; h < pred.getActionRecord().size(); h++) {


            //System.out.println("MODIFIED ACTIONS : " + pred.getActionRecord().getActionString(h));
            //System.out.println("MODIFIED PERCEPS : " + pred.getPercepRecord().getPercepString(h));
             }
              
              // Only does "lines" steps, instead of all the iterations
              predatorEnvironment.testAgentRecords2(lines);
        }
        
        
        else {
        predatorEnvironment.testAgentRecords();
        }
        
        logfile2.println("OUTPUT PERCEPS & ACTIONS : ", 4);
         for (int h = 0; h < pred.getActionRecord().size(); h++) {


            //System.out.println("MODIFIED ACTIONS : " + pred.getActionRecord().getActionString(h));
            //System.out.println("MODIFIED PERCEPS : " + pred.getPercepRecord().getPercepString(h));
             
            logfile2.println(h + " : " + pred.getPercepRecord().getPercep(h).toString() + pred.getActionRecord().getAction(h).toString(), 4);
         }
//        NodeList nodes = new NodeList();
//        RuleStateGenerator R = new RuleStateGenerator(nodes);
//        System.out.println("Percep : " + pred.getPercepRecord().getPercep(3) + " Action : " + pred.getActionRecord().getAction(3));
//        
//        String target = "WEEAEN";
//        
//        pred.getPercepRecord().getPercep(3).readFromString(target);
//                
//        
//        R.generateNextState(pred.getPercepRecord().getPercep(3),pred.getActionRecord().getAction(0));
        //System.out.println("BYBY" + R.generateNextState(pred.getPercepRecord().getPercep(3),pred.getActionRecord().getAction(3)));
        logfile2.closeall();
        
        
    }
}
      