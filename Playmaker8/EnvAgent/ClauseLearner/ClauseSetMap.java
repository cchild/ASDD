/*
 * ruleSetList.java
 *
 * Created on 20 November 2002, 18:34
 */

package EnvAgent.ClauseLearner;

import EnvModel.*;

import EnvAgent.RuleLearner.*;

import java.util.*;
import java.io.*;
import Logging.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public class ClauseSetMap implements Serializable {

    private ArrayList clauseSetList;
    /** Creates new ruleSetList */
    public ClauseSetMap() {
        clauseSetList = new ArrayList();
    }
    
    public ArrayList getArrayList() {
        return clauseSetList;
    }
    
    public ClauseSet get(int which) {
        return (ClauseSet)clauseSetList.get(which);
    }
    
    public void set(int which, ClauseSet val) {
        clauseSetList.set(which, val);
    }
    
    public int size() {
        return clauseSetList.size();
    }
    
    public void remove(RuleSet node) {
        clauseSetList.remove(node);
    }
    
    public void remove(int which) {
        clauseSetList.remove(which);
    }
    
    public void add(ClauseSet node) {
        if (!contains(node))
            clauseSetList.add(node);
    }
    
    public void addAt(int pos, ClauseSet node) {
        clauseSetList.add(pos, node);
    }
    
    public void addAll(ClauseSetMap otherList) {
        clauseSetList.addAll(otherList.getArrayList());
    }
    
   
    /*Call standard write object on the ruleSetList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeObject(clauseSetList);
    }
  
    /*Call standard read object on the ruleSetList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        clauseSetList = (ArrayList)s.readObject();
    }
  
    public boolean contains(ClauseSet set) {
        for (int i = 0; i  < size(); i++) {
            if (get(i) == set)
                return true;
        }
        return false;
    }
    
    public ClauseSetMap getMatching(Percep percep, Action action) {
        ClauseSetMap matchingClauseSets = new ClauseSetMap();
        ClauseElements clausePercepAction = new ClauseElements(percep, action, true, null);
        for (int i = 0; i < size(); i++) {
            ClauseSet clauseSeti = null;
            clauseSeti = get(i);
            if (clauseSeti.getBody().bodyequals(clausePercepAction))
                matchingClauseSets.add(get(i));
        }
     
        return matchingClauseSets;
    }
    
    public ClauseSetMap getWinningMatching(Percep percep, Action action) {
        ClauseSetMap matching = getMatching(percep, action);
        matching.removeByPrecedence();
        matching.removeLessGeneral();
        matching.removeMoreSpecificOutcome();
        matching.setUsedWithSpremacy();
        return matching;
    }
    
    public void removeByPrecedence() {
        //System.out.print("\nSize: " + size() + " ");
        for (int i = 0; i < size(); i ++) {
            //System.out.print(" [i:" + i);
            for (int j = size() -1; j >= 0; j--) {
                //System.out.print(" j:" + j +"]");
                if (j != i) {
                    if (get(j).defersTo(get(i))) {
                        this.remove(j);
                        
                        //if j was less than i then we need to take one off i to keep it as the same rule
                        if (j < i)
                            i--;
                    }
                }
            }
        }
    }
    public void removeMoreSpecificOutcome() {
       for (int i = size() -1; i > 0 ; i--) {
            for (int j = i-1; j >= 0 ; j--) {
                ClauseSet seti = get(i);
                ClauseSet setj = get(j);

                //if (seti.getOutputVaribleName().equals(setj.getOutputVaribleName()))
                if (seti.getClauseHead().sameOutVariable(setj.getClauseHead()))
                {
                    if (seti.size() < setj.size()) {
                        remove(i);
                        j=0;
                    } else {
                        remove(j);
                        i--;
                    }

                }
            }
        }   
    }


    public void setUsedWithSpremacy() {
       for (int i = size() -1; i >= 0 ; i--) {
          ClauseSet seti = get(i);
          seti.setUsedWithSpremacy(true);
          if (LogFile.OUTPUT_LOG0) {
              if (!seti.getBody().containsAnAction()) {
                  LogFile log = new LogFile();
                  //log.print("\nError with rule supremacy: \n ");
                  //log.print(seti.toString());
                  log.close();
              }
          }
        }
    }
    
    public void removeLessGeneral() {
       for (int i = size() -1; i > 0 ; i--) {
            for (int j = i-1; j >= 0 ; j--) {
                ClauseSet seti = get(i);
                ClauseSet setj = get(j);

                //if (seti.getOutputVaribleName().equals(setj.getOutputVaribleName()))
                if (seti.getClauseHead().sameOutVariable(setj.getClauseHead()))
                {
                    if (seti.getBody().getBodySize() < setj.getBody().getBodySize()) {
                        remove(i);
                        j=0;
                    } else {
                        remove(j);
                        i--;
                    }

                }
            }
        }   
    }
    
    public double getAvgValue() {
        double totalValue = 0;
        for (int i =0; i< size(); i++) {
            ClauseSet ith = get(i);
            totalValue += ith.getUtility().getValue();
        }
        return totalValue/(double)size();
    }
    
    public double getWeightedAvgValue() {
    
        //find the rule set with the minimum error
        double weighting = 3.0f;
        double minError = 1000000;
        int minRule = 0;
        for (int i =0; i< size(); i++) {
            if (get(i).getError() < minError) {
                minRule = i;
                minError = get(i).getError();
            }
        }
        
        //we can do this more cleverly by taking the inverse proportion of the error to the total error
        //but for now we'll do it by weighting
        double totalValue = 0;
        //int weighting = 3;
        for (int i =0; i< size(); i++) {
            ClauseSet ith = get(i);
            if ((i == minRule) && (minError != 1000000))
                totalValue += ith.getUtility().getValue()*(double)weighting;
            else
                totalValue += ith.getUtility().getValue();
        }
        return totalValue/(double)(size()+ weighting -1);
    }

    public double getVairenceWeightedAvgValue()
    {

        double minVariance = 0.001f;
        //NOTE: There are four places that minDBMatcehs needs to be changed
        //if you want to change specificity rules

        //find the rule set with the minimum error
        double totalInverseVariancePlusBias = 0;

        //how about we weight heavily towards unusual rules because, as they are
        //unusual, they will not be updated as often

        //careful - if vareince is 0 we don't want to divide by zero. Use 1 as default

        for (int i =0; i< size(); i++) {
            //Sum the total inverse  variances (including bias)

            //I think this is the wrong kind of biad. It's just telling us how far
            //we are from the weighted avg, so pushing things to the whoever initially
            //got the vote as the best estimator.
            //If we take that bias out of the equation and just use varience, we are told
            //who gives us the most consistent estimates.
            if (get(i).getVariance().getValue() > minVariance) {
                //Notice the fiddle above to get over low variance
                totalInverseVariancePlusBias += 1/
                        (get(i).getVariance().getValue()
                        + get(i).getBias().getValue()* get(i).getBias().getValue());
            } else {
                //OK. Comment out this bit so that it will explore the new values.
                /*if (get(i).getVariance().getValue() == 0)
                {
                    totalInverseVariancePlusBias += 1
                            /
                        (2.0f + get(i).getBias().getValue()* get(i).getBias().getValue());
                } else {*/
                    totalInverseVariancePlusBias += 1
                        /
                    (minVariance + get(i).getBias().getValue()* get(i).getBias().getValue());
               // }

            }
        }
        //then find the inverse of that.
        totalInverseVariancePlusBias = 1/totalInverseVariancePlusBias;

        //we can do this more cleverly by taking the inverse proportion of the error to the total error
        //but for now we'll do it by weighting
        double totalValue = 0;


        //int ignoreBecauseNoConditions = 0;
        //int weighting = 3;
        for (int i =0; i< size(); i++) {
            ClauseSet ith = get(i);

            double ithVariancePlusBias = 0;
            
            //coommnet out the 0 so theat it will explore the new values.
            //if (ith.getVariance().getValue() == 0)
            //    ithVariancePlusBias = 2.0f;
            //else {
                if (ith.getVariance().getValue() > minVariance)
                    ithVariancePlusBias = ith.getVariance().getValue();
                else
                    ithVariancePlusBias = minVariance;
           // }
                
            ithVariancePlusBias += ith.getBias().getValue()*ith.getBias().getValue();

            double weighti = totalInverseVariancePlusBias/ithVariancePlusBias;
            totalValue += weighti*ith.getUtility().getValue();
        }

        return totalValue;
    }

    public double getTotalVairenceWeightedAvgValue()
    {

        double minVariance = 0.001f;
        //NOTE: There are four places that minDBMatcehs needs to be changed
        //if you want to change specificity rules

        //find the rule set with the minimum error
        double totalInverseVariancePlusBias = 0;

        //how about we weight heavily towards unusual rules because, as they are
        //unusual, they will not be updated as often

        //careful - if vareince is 0 we don't want to divide by zero. Use 1 as default

        for (int i =0; i< size(); i++) {
            //Sum the total inverse  variances (including bias)

            //I think this is the wrong kind of biad. It's just telling us how far
            //we are from the weighted avg, so pushing things to the whoever initially
            //got the vote as the best estimator.
            //If we take that bias out of the equation and just use varience, we are told
            //who gives us the most consistent estimates.
            if (get(i).getTotalVariance().getValue() > minVariance) {
                //Notice the fiddle above to get over low variance
                totalInverseVariancePlusBias += 1/
                        (get(i).getTotalVariance().getValue()
                        /*+ get(i).getBias().getValue()* get(i).getBias().getValue()*/);
            } else {
                //OK. Comment out this bit so that it will explore the new values.
                /*if (get(i).getVariance().getValue() == 0)
                {
                    totalInverseVariancePlusBias += 1
                            /
                        (2.0f + get(i).getBias().getValue()* get(i).getBias().getValue());
                } else {*/
                    totalInverseVariancePlusBias += 1
                        /
                    (minVariance/* + get(i).getBias().getValue()* get(i).getBias().getValue()*/);
               // }

            }
        }
        //then find the inverse of that.
        totalInverseVariancePlusBias = 1/totalInverseVariancePlusBias;

        //we can do this more cleverly by taking the inverse proportion of the error to the total error
        //but for now we'll do it by weighting
        double totalValue = 0;


        //int ignoreBecauseNoConditions = 0;
        //int weighting = 3;
        for (int i =0; i< size(); i++) {
            ClauseSet ith = get(i);

            double ithVariancePlusBias = 0;

            //coommnet out the 0 so theat it will explore the new values.
            //if (ith.getVariance().getValue() == 0)
            //    ithVariancePlusBias = 2.0f;
            //else {
                if (ith.getTotalVariance().getValue() > minVariance)
                    ithVariancePlusBias = ith.getTotalVariance().getValue();
                else
                    ithVariancePlusBias = minVariance;
           // }

            ithVariancePlusBias += 0;/*ith.getBias().getValue()*ith.getBias().getValue()*/;

            double weighti = totalInverseVariancePlusBias/ithVariancePlusBias;
            totalValue += weighti*ith.getUtility().getValue();
        }

        return totalValue;
    }
    
    public double getWeightedSpecificAvgValue(boolean USE_NONFRAME_RULES_ONLY,
                            boolean USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY,
                            boolean USE_WEIGHTING_OF_ONE) {
    
        //NOTE: There are four places that minDBMatcehs needs to be changed
        //if you want to change specificity rules

        //find the rule set with the minimum error
        double weighting;
        //how about we weight heavily towards unusual rules because, as they are
        //unusual, they will not be updated as often
        
        double maxSpecific = 0;
        int ignoreRules = 0;
        int minBodyMatches = 36000000;
        int maxRule = 0;
        for (int i =0; i< size(); i++) {
            //Was using max specificity for this, I think we should use least
            //database matches as that is the most unusual situation
            //if (get(i).get(0).clauseSize() > maxSpecific) {
            if ((get(i).getUtility().getValue() != 0) && (get(i).getUtility().getValue() != get(i).getOptimisticValue())
                    && (!(get(i).isFrameRule() && USE_NONFRAME_RULES_ONLY))
                    && (get(i).bodyContainsActionAndConditions() || !USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY))
            {
                if (get(i).get(0).getPrecursorequals() < minBodyMatches) {
                    //WE ALSO NEED TO ADD SOMETHING HERE TO IGNORE ACTIONLESS (ENVT) RULES
                    maxRule = i;
                    //maxSpecific = get(i).get(0).clauseSize();
                    minBodyMatches = get(i).get(0).getPrecursorequals();
                } else {
                        //if (get(i).isFrameRule())
                        //    get(i).isFrameRule();
                        /*if (!get(i).bodyContainsActionAndConditions()) {
                            int whatthefuck = 1;
                            boolean whywhy = get(i).bodyContainsActionAndConditions();
                            whywhy = whywhy;
                        }*/
                }
            } else {
                ignoreRules ++;
            }
        }
        
        //we can do this more cleverly by taking the inverse proportion of the error to the total error
        //but for now we'll do it by weighting
        double totalValue = 0;

        if (USE_WEIGHTING_OF_ONE)
            weighting = 1.0f;
        else
            weighting = /*1.0f;*/(double)(size()-ignoreRules)*3.0f;
        //int ignoreBecauseNoConditions = 0;
        //int weighting = 3;
        for (int i =0; i< size(); i++) {
            ClauseSet ith = get(i);
            if ((get(i).getUtility().getValue() != 0)
                    && (!(get(i).isFrameRule() && USE_NONFRAME_RULES_ONLY))
                    && (get(i).bodyContainsActionAndConditions() || !USE_RULES_WITH_ACTION_AND_CONDITIONS_ONLY))
            {
                //don't include this in the weighting if it hasn't been visited
                //or if it's a FRAME variable where head is same as body
                //if ((i == maxRule) && (maxSpecific != 0))
                if ((i == maxRule) && (minBodyMatches != 36000000))
                    totalValue += ith.getUtility().getValue()*weighting;
                else
                    totalValue += ith.getUtility().getValue();
            } else {
              
                
                //We'll do something with this to make give a deflaut action
                //value if nothing else matters (for the NEW tye action
                 //if (get(i).isFrameRule()) {
                 //   ignoreBecauseNoConditions++;
                 //   System.out.print("\nFrame rule: " + get(i).toString());
                 ///   if (get(i).isFrameRule()) {
                  //      System.out.print("\nFrame rule: " + get(i).toString());
                  //  }
                //}

                //if (!get(i).bodyContainsActionAndConditions()) {
                 //   ignoreBecauseNoConditions++;
                //    System.out.print("\nNo action and conditons: " + get(i).toString());
                //}
            }
        }

        //(maxSpecific != 0)
        if (ignoreRules != size()) { //test that whether all rules were zero
            if (minBodyMatches != 36000000) {
                return totalValue/(double)(size()+weighting -1-ignoreRules);
            }
            else
                return totalValue/(double)(size()-ignoreRules);
        } else {
            return 0;
        }
    }
    
    public String toString() {
        //we're starting a new rule set level so output the last one
        String theString = new String();
        for (int setCount = 0; setCount < size(); setCount ++) {
            ClauseSet clauseSet = (ClauseSet)get(setCount);
            theString += "\nClause Set: " + clauseSet.toString();

            if (clauseSet.getTotalProb() < 0.999f)
                theString += "WARNING Total prob: " + clauseSet.getTotalProb();

            theString += "  Prec. DB-Occ: " + clauseSet.get(0).getPrecursorequals();
        }
        
        return theString;
    }

    public String toStringOnlyWinning() {
        //we're starting a new rule set level so output the last one
        String theString = new String();
        for (int setCount = 0; setCount < size(); setCount ++) {
            ClauseSet clauseSet = (ClauseSet)get(setCount);
            if (clauseSet.getUsedWithSpremacy()) {
                theString += "\nClause Set: " + clauseSet.toString();

                if (clauseSet.getTotalProb() < 0.999f)
                    theString += "WARNING Total prob: " + clauseSet.getTotalProb();

                theString += "  Prec. DB-Occ: " + clauseSet.get(0).getPrecursorequals();
            }
        }

        return theString;
    }
}
