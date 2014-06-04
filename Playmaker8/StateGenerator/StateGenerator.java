/*
 * StateGenerator.java
 *
 * Created on 20 November 2002, 13:59
 */

package StateGenerator;

import EnvModel.*;

import java.util.*;

import Logging.*;

import EnvAgent.*;
import EnvAgent.RuleLearner.*;

/**
 *
 * @author  Chris Child
 * @version 
 */
public abstract class StateGenerator {

          
    public abstract ArrayList generateNextStates(  Percep percep,   Action action);
    
    public Percep generateNextState(Percep percep, Action action) {
        ArrayList statesAndProbs = generateNextStates(percep, action);
        if (statesAndProbs != null)
            return rouletteStatesAndProbs(statesAndProbs);
        else {
            System.out.print("\n WARNING: No following state defined for: ");
            System.out.print(percep.toString() + " " + action.toString());
            System.out.print("\n WARNING: Returning original percep " + percep.toString());
            return percep;
        }
    }
    
    protected void normaliseProbabilitiesOfGeneratedStates(ArrayList statesAndProbs) {
        //Print out the states after we filter
        double totalProbability = 0.0f;
        for (int i = 0; i < statesAndProbs.size(); i++) {    
            totalProbability += ((StateAndProb)statesAndProbs.get(i)).getProbability();
        }
        
        for (int i = 0; i < statesAndProbs.size(); i++) {
            ((StateAndProb)statesAndProbs.get(i)).setProbability(
            ((StateAndProb)statesAndProbs.get(i)).getProbability()/totalProbability);
        }
    }
        
    public RuleElements convertToRuleElements(Percep percep, Action action) {
        //Create a rule elements object we can use to fire the state generator
        RuleElements preconditions = new RuleElements();
        preconditions.add(action);
        for (int i = 0; i < percep.getNumFluents(); i++) {
            Fluent f = percep.getFluent(i);
            preconditions.add(f);
        }
        
        return preconditions;
    }
 
    
    public Percep rouletteStatesAndProbs(ArrayList statesAndProbs) {
        float totalProb = 0.0f;
        
        if (statesAndProbs == null || statesAndProbs.size() == 0)
            return null;
        
        //note. These should sum to 1.0. IF they always do then we can save this step
        for (int i = 0; i < statesAndProbs.size(); i++) {
            totalProb += ((StateAndProb)statesAndProbs.get(i)).getProbability();
        }
        
        float rNum = (float)Math.random() * totalProb;
        
        int fluentValue = -1;
        while ((rNum > 0.0f) && (fluentValue < statesAndProbs.size())) {
            fluentValue ++;
            if (fluentValue < statesAndProbs.size())
                rNum -= ((StateAndProb)statesAndProbs.get(fluentValue)).getProbability();
            else
                System.out.print("Roulette has generated a number out of range in StateGenerator");
        }
        
        return ((StateAndProb)statesAndProbs.get(fluentValue)).getPercep();
    }

      public Percep randomState(ArrayList statesAndProbs) {
        float totalProb = 0.0f;

        if (statesAndProbs == null || statesAndProbs.size() == 0)
            return null;

        //note. These should sum to 1.0. IF they always do then we can save this step
        for (int i = 0; i < statesAndProbs.size(); i++) {
            totalProb += 1.0f;
        }

        float rNum = (float)Math.random() * totalProb;

        int fluentValue = -1;
        while ((rNum > 0.0f) && (fluentValue < statesAndProbs.size())) {
            fluentValue ++;
            if (fluentValue < statesAndProbs.size())
                rNum -= 1.0f;
            else
                System.out.print("Roulette has generated a number out of range in StateGenerator");
        }

        return ((StateAndProb)statesAndProbs.get(fluentValue)).getPercep();
    }
 
    protected int roulette(float probabilities[]){
        float totalProb = 0.0f;
        
        //note. These should sum to 1.0. IF they always do then we can save this step
        for (int i = 0; i < probabilities.length; i++) {
            totalProb += probabilities[i];
        }
        
        float rNum = (float)Math.random() * totalProb;
        
        int fluentValue = -1;
        while ((rNum > 0.0f) && (fluentValue < probabilities.length)) {
            fluentValue ++;
            if (fluentValue < probabilities.length)
                rNum -= probabilities[fluentValue];
            else
                System.out.print("Roulette has generated a number out of range in StateGenerator");
        }
        
        return fluentValue;
    }
    
   
}
