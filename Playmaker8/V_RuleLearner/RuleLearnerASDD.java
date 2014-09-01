

package V_RuleLearner;


import V_Sensors.*;
import java.util.ArrayList;



/**
 *
 * @author virgile
 */
public class RuleLearnerASDD {
    
    public TokenMap tokenMap;
    public SensorList sensorList;
    
    static boolean silent_mode = false;
   
    
    public RuleLearnerASDD(TokenMap t, SensorList s) {
        this.tokenMap = t;
        this.sensorList = s;
    }
    
    
public static ArrayList learnRulesASDD(TokenMap t, SensorList sList, RuleMap rMap, SensorMap sMap)
    {
        
        boolean consolidate_rulesets = true;

        
        ArrayList res = new ArrayList ();

        RuleList closedList = new RuleList ();
        RuleList ASDD_list = new RuleList ();
        RuleList candidates;
        
        
        System.out.print("\nGenerating Level 1 Rules");
            closedList = closedList.generate_level1_rules(t, rMap, sMap);
            ASDD_list.addRuleList(closedList);
        System.out.println(" OK");
            
            
            
        for (int i = 2; i < t.TokenList.size()+2; i++) {
            
            
            System.out.print("\nGenerating Level " + i + " candidates");
            candidates = closedList.apriori_gen(rMap, sMap, i);
            System.out.println(" OK");

            System.out.println("Candidates : " + candidates.size());
            closedList = candidates.ASDD_pruning_occurrencies(rMap);
            ASDD_list.addRuleList(closedList);

            System.out.println("Candidates after pruning : " + closedList.size());

        }
            


        // Filtering
        System.out.println("ASDD_List size before filtering : "  + ASDD_list.size());
        
        ASDD_list = ASDD_list.removeWildcardSuccessors();
        
        
        
        System.out.println("\nFILTERING FREELOADERS...");
        
        ArrayList freeloaders = ASDD_list.findFreeloaders(sMap);

        
        ASDD_list = ASDD_list.removeFreeloaders_ASDD(freeloaders);
        
        System.out.println("ASDD_List size is now : " + ASDD_list.size());
        
        
        
        

        
        

        
        
        
        //////////////////////////////////
        ///////// RULESETLIST ////////////
        //////////////////////////////////
        
        
        System.out.print("\nBUILDING RULESETLIST FROM ASDD_LIST...");

        RuleSetList rulesetlist = new RuleSetList(ASDD_list, sList);
        
        rulesetlist.buildFromRuleList();

        System.out.println(" OK");

        System.out.println(rulesetlist.size() + " RULESETS HAVE BEEN GENERATED.");
        
        
        
        
        if (consolidate_rulesets) {

            System.out.println("\nCONSOLIDATING RULESETS...");
            
            int counter = rulesetlist.consolidate(sList, ASDD_list, rMap, silent_mode);
            
            System.out.println(counter + " RULES SUCCESFULLY ADDED");
            System.out.println("ASDD_LIST HAS NOW " + ASDD_list.size() + " ENTRIES.");
                 
        }

        System.out.println(rulesetlist.size() + " RULESETS HAVE BEEN GENERATED.");
        

        
        RuleList closedList2 = ASDD_list.copy();
             

        
        
        if (!silent_mode)
            System.out.print("\nINDENTIFYING CONFLICTS & ESTABLISHING PRECEDENCES BETWEEN RULESETS...");
        
        ArrayList <ArrayList> a = rulesetlist.getConflicts(sList, sMap, rMap, silent_mode);
        
        if (!silent_mode) {
            System.out.println(" OK");
        
            System.out.println(a.get(0).get(0) + " CONFLICTS IDENTIFIED");
            System.out.println(a.get(0).get(1) + " PRECEDENCES SET");
        }
        

        
        
        
        // OUTPUT
        
       res.add(closedList2);
       
       res.add(rulesetlist);

        
       return res;
       
       

         
    }



    



}