/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package V_Sensors;

import V_RuleLearner.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class StateList {
    
    public ArrayList <ArrayList> list ;
    
    
    
    
    public StateList () {
        
        list = new ArrayList();
    }
    
    
    public void addSensor (Sensor sen, double prob) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(prob);
        
        
        this.list.add(a);
    }
    
    
    public void addSensor (Sensor sen, double prob, int index) {
        
        
        ArrayList a = new ArrayList ();
        
        a.add(sen);
        
        a.add(prob);
        
        
        this.list.add(index, a);
    }
    
    
    public void remove (int i) {
        
        this.list.remove(i);
    }
    
    public void setProb (int i, double value) {
        
        this.addSensor(this.getSensor(i), value, i);
        
        this.remove(i+1);
    }
    
    public void printList () {
        this.printList("");
    }
    
    public void printList (String str) {
        
        System.out.println("\nPRITING " + str + " STATELIST ("+ this.size() + " entries.)");
        
        for (int i = 0; i < this.size(); i ++) {
            
            System.out.println("SENSOR " + (i+1) + " " + this.list.get(i).get(0) + " Prob : " + this.list.get(i).get(1));
        }
        
        System.out.println("Total Prob : "+ this.getTotalProb());
    }
    
    
    
    public void update (RuleSet RS, SensorList impList) {
        
        StateList problems = new StateList ();
        
        if (this.size() == 0)
            this.init(RS);
        
        else {
            double prob;
            int size = this.size();
            double remove = 0;
            for (int i = 0; i < size; i ++) {

                for (int j = 0; j < RS.size(); j++) {
                    
                    
                    Sensor a = RS.getSuccessor(j).merge(this.getSensor(i));
                    
                    //System.out.println("Merging " + RS.getSuccessor(j) + " with " + this.getSensor(i) + " into " + a);
                    //if (impList.findSensor(a) == 0) {
                    prob = RS.getRule(j).getProb() * this.getProb(i);
                    if (remove != 0.0) {
                        prob = prob + remove;
                        remove = 0.0;
                    }
                    prob = Math.round(prob * 1000);
                    prob = prob/1000;

      
                      if (impList.findSensor2(a) > 0) {
                            //System.out.println("Impossible State : " + a + " prob : " + prob);
                            int toc = problems.findSensor_exact(RS.getSuccessor(j));
                            if (toc > -1) {
                                problems.setProb(toc, problems.getProb(toc)+prob);
                            }
                            else {
                            problems.addSensor(RS.getSuccessor(j), prob);
                            }
                            remove = prob;
                            continue;
                      }
                      
                      if ( (prob > 0) ) {
                            this.addSensor(a, prob);
                            //System.out.println("Adding : " + a);
                      }
                      
                      
                    //}
                }
            }

            
            // REMOVING SENSORS USED TO BUILD NEW ONES
            for(int i = size-1; i >= 0; i--) {
                
               //System.out.println("Removing " + i + this.getSensor(i)) ;
               this.remove(i);
            } 
            
            

            //problems.printList("PROBLEMS :");            
            
            
            // FIXING PROBLEMS
            for (int h = 0; h < problems.size(); h++) {
                
                ArrayList <Integer> a = this.findSensor(problems.getSensor(h));
                double prob2 = problems.getProb(h)/a.size();
                
                for (int j = 0; j < a.size(); j++) {
                    
                    this.setProb(a.get(j), prob2+this.getProb(a.get(j)));
                    //System.out.println("PROB : Adding " + prob2 + " from " + problems.getSensor(h) + " to " + this.getSensor(a.get(j)));
                }
                
                
            }
            
            
            
            
            
            
            

            
            
            if ( (remove != 0.0) && (Math.abs(this.getTotalProb()-1.0) < 0.999)) {
                
                double r = this.getProb(this.size()-1)+remove;
                r = Math.round(r * 1000);
                r = r/1000;
                this.setProb(this.size()-1, r);
            }
            
            
        }
        
              
    }
    
    public void clean_hard (SensorList impList, StateList posList) {
        
        
        for (int u = this.size()-1; u >= 0; u--) {
                
                int a = impList.findSensor(this.getSensor(u));
                if ( a > 0) {
                    this.remove(u); 
                    continue;
                }
                
                if (!posList.hasSensor(this.getSensor(u)))
                    this.remove(u);    
        }
        
        double lastProb = this.getTotalProb();
        for (int i = 0; i  < this.size(); i++) {
            
            double b =  this.getProb(i) / lastProb;            
            double a = Math.round(b * 1000);
            a= a/1000;
            this.setProb(i, a);
        }
        
    }
    

    
public void clean (SensorList impList) {
        
        
        for (int u = this.size()-1; u >= 0; u--) {
                
                int a = impList.findSensor(this.getSensor(u));
                if ( a > 0) {
                    this.remove(u); 
                    continue;
                }
   
        }
        
        double lastProb = this.getTotalProb();
        for (int i = 0; i  < this.size(); i++) {
            
            double b =  this.getProb(i) / lastProb;            
            double a = Math.round(b * 1000);
            a= a/1000;
            this.setProb(i, a);
        }
        
    }
        
    
    public int size () {
        
        return this.list.size();
    }
    
    
    public Sensor getSensor (int i) {
        
        return (Sensor) this.list.get(i).get(0);
    }
    
    public double getProb (int i) {
        
        return (Double) this.list.get(i).get(1);
    } 
    
    
    public double getTotalProb () {
        
        double res = 0.0;
        for (int i = 0; i < this.size(); i++) {
            
            res = res + this.getProb(i);
        }
        
        res = Math.round(res * 1000);
        res = res/1000;
        return res;
    } 
    
    public void init (RuleSet RS) {
        
        for (int i = 0; i < RS.size(); i++) {
            
            this.addSensor(RS.getSuccessor(i), RS.getRule(i).getProb());
        }
    }
    
    
    public boolean hasSensor (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return true;
        }
        
        
        return false;
    }
    
    
    
    public ArrayList <Integer> findSensor (Sensor sen) {
        
        ArrayList <Integer> res = new ArrayList <> ();
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch(sen))
                res.add(i);
        }
        
        
        return res;
    }
    
    
    
    public int findSensor_exact (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        
        return -1;
    }
    
    
    
    
    
    public StateList generateStates (SensorList numbers, Sensor s1, RuleSetList rulesetlist, int i, SensorList impossibleList, StateList possibleList, boolean hard_clean_statelist) {
        
        ArrayList <Integer> aList = new ArrayList();
        ArrayList <Integer> chosenRuleSets = new ArrayList();
           
        
            
            while ( (aList.size()+1)!= s1.size()) {

                //System.out.println("\nCHOOSING NEXT RULESET...");
                int chosen = numbers.getSensor(i+1).chooseNextRuleSet(rulesetlist, aList);
                //System.out.println("CHOSEN RULESET : " + chosen);
                //rulesetlist.getRuleSet(chosen).print();

                aList.addAll(rulesetlist.getRuleSet(chosen).detectNonWildcardedSpots());

                chosenRuleSets.add(chosen);
                //System.out.println("CHOSEN RULESET LIST IS : " + chosenRuleSets);

                //System.out.println("UPDATING STATELIST... ");
                this.update(rulesetlist.getRuleSet(chosen), impossibleList);

            }

            if (hard_clean_statelist) {
                //System.out.println("\nHARD CLEANING STATELIST ");
                this.clean_hard(impossibleList, possibleList);
            }
            else {
                //System.out.println("\nCLEANING STATELIST ");
                this.clean(impossibleList);

            }
    
            
            return this;
            
            
    }
    
    
    
    
    public double getScore () {
        
        double a = 0.0;
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).isRewarded())
                a = a + this.getProb(i);
        }
        
        return a;
    }
    
}
