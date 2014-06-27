

package RuleLearner2;


import Logging.*;
import Token.*;
import java.util.ArrayList;
import java.util.Collections;


/**
 *
 * @author virgile
 */
public class RuleLearnerMSDD_Sensor {
    
    public TokenMap tokenMap;
    public SensorList sensorMap;
   
    
    public RuleLearnerMSDD_Sensor(TokenMap t, SensorList s) {
        this.tokenMap = t;
        this.sensorMap = s;
    }
    
    
public static int learnRulesMSDD()
    {
        
         // INIT TOKENMAP
         TokenMap t = new TokenMap();
         t.fromFile();
         
         // INIT SENSORLIST
         SensorList sList = new SensorList();
         sList.fromFile(t);

         
         // INIT MAXNODES & PRUNING 
         int maxnodes = 8000;
         int totalnodes = 1;
         boolean pruning = true;

         
         // INIT CLOSELIST & OPENLIST
         RuleList closedList = new RuleList();
         RuleList openList = new RuleList();
         
         
         // INIT ROOTRULE
         String str_pre = "******";
         String str_post = "******";
         Sensor pre = new Sensor(str_pre,t);
         Sensor post = new Sensor(str_post,t);
         Rule rootRule = new Rule(pre,post);
         rootRule.occurrencies = sList.size();

         openList.addRule(rootRule);


         System.out.println("\nMAXNODES SET TO " + maxnodes);
         
         // STOPS IN 2 CASES : MAXNODES REACHED OR OPENLIST EMPTY
         while ((totalnodes < maxnodes) && (!openList.rulelist.isEmpty())) {

             openList = openList.sort();
             
             // THE CHOSEN RULE IS openList.getRule(0));
             // CREATING THE CHILDREN, AND ADDING THEM TO OPENLIST
             RuleList children = openList.getRule(0).getChildren(sList);
             int number_added = 0;
             
             //PRUNING
             if (pruning) 
                number_added = openList.addWithCheck(children,sList);

             if(!pruning)
                 number_added = openList.add(children);
             
             // ADDING THE SOURCE RULE TO CLOSEDLIST
             closedList.addRule(openList.getRule(0));

             // number_added children were added
             totalnodes = totalnodes + number_added; 

             // REMOVING THE SOURCE RULE FROM OPENLIST
             openList.remove(); 
             
             
             if ((totalnodes % 50) == 0) {
                 System.out.println("RULES : " + totalnodes);
             }

         } // END WHILE
         
         
         // IF MAXNODES HAS BEEN REACHED, ADDING REMAINING RULES FROM OPENLIST
         if (totalnodes >= maxnodes) {
             System.out.println("\nMAXNODES REACHED ( " + maxnodes + " )");
             openList = openList.sort();

             while (closedList.size() < maxnodes)  {

                 closedList.addRule(openList.getRule(0));
                 openList.rulelist.remove(0);

             } // END WHILE

         }

         // [*, *, *, *, *, *][*, *, *, *, E, *]
         String str3 = "****E*";
         Sensor s1= new Sensor(str3,t);
         Rule rule = new Rule(pre, s1);
         
         //System.out.println(sList.indexOfRule(rule).size());
         
         
         openList.sort().printList("OPEN LIST");
         closedList.printListByWithProb("CLOSED LIST",sList,1);

         //System.out.println("MSDD Rules (" + totalnodes + " entries) learned. ");

        
 

        
        return 0;
    }





public static void main (String[] args) {


    
    learnRulesMSDD();

     
     
    
}  


}

  


