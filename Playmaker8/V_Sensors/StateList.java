
package V_Sensors;

import V_RuleLearner.*;
import java.util.ArrayList;

/**
 *
 * @author virgile
 */
public class StateList {
    
    // A StateList is defined like this : 
    //
    // Sensor + Probability (double)
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
    
    
    public void remove (int index) {
        
        this.list.remove(index);
    }
    
    
    public void setProb (int index, double value) {
        
        this.addSensor(this.getSensor(index), value, index);
        
        this.remove(index+1);
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
    
    
    // See below
    public void init (RuleSet RS) {
        
        for (int i = 0; i < RS.size(); i++) {
            
            this.addSensor(RS.getSuccessor(i), RS.getRule(i).getProb());
        }
    }
        
        
    // Updates the StateList by adding the RuleSet information to it.
    //
    // The new RuleSet brings at least one new Non-Wildcarded index
    //
    // We merge all the existing Sensors with the ones found in the RuleSet
    //
    // Then we delete the old ones
    public void update (RuleSet RS, SensorList impList) {
        
        StateList problems = new StateList ();
        
        // If this is empty, creates an entry with RS (see init)
        if (this.size() == 0)
            this.init(RS);
        
        else {
            
            double prob;
            int size = this.size();
            
            // If the generated Sensor is Impossible, we report its probability 
            // to the other Sensors that could have fit, according to their
            // respective probs
            double lost_probability = 0;
            
            for (int i = 0; i < size; i ++) {

                for (int j = 0; j < RS.size(); j++) {
                    
                    // Merging
                    Sensor sensor = RS.getSuccessor(j).merge(this.getSensor(i));
                    
                    // Obtaining proba
                    prob = RS.getRule(j).getProb() * this.getProb(i);
                    
                    // Reports lost prob
                    if (lost_probability != 0.0) {
                        prob = prob + lost_probability;
                        lost_probability = 0.0;
                    }
                    
                    prob = Math.round(prob * 1000);
                    prob = prob/1000;

                    
                    // Checks if the Sensor is Impossible
                    if (impList.findSensor2(sensor) > 0) {

                            int toc = problems.findSensor_exact(RS.getSuccessor(j));
                            if (toc > -1) {
                                problems.setProb(toc, problems.getProb(toc)+prob);
                            }
                            else {
                            problems.addSensor(RS.getSuccessor(j), prob);
                            }
                            lost_probability = prob;
                            continue;
                    }

                    if ( (prob > 0) ) {
                        
                            this.addSensor(sensor, prob);
                            
                    }

                }
            }

            
            // REMOVING SENSORS USED TO BUILD NEW ONES
            for(int i = size-1; i >= 0; i--) {
                
               this.remove(i);
            } 
            
            

            // Determining which Sensors could have fit an Impossible one
            for (int h = 0; h < problems.size(); h++) {
                
                ArrayList <Integer> a = this.findSensor(problems.getSensor(h));
                
                double prob2 = problems.getProb(h)/a.size();
                
                for (int j = 0; j < a.size(); j++) {
                    
                    this.setProb(a.get(j), prob2+this.getProb(a.get(j)));
                    
                }
                
                
            }
            

            // If we didn't found any fitting Sensor, adding to the last generated one
            if ( (lost_probability != 0.0) && (Math.abs(this.getTotalProb()-1.0) < 0.999)) {
                
                double r = this.getProb(this.size()-1)+lost_probability;
                r = Math.round(r * 1000);
                r = r/1000;
                this.setProb(this.size()-1, r);
            }
            
            
        }
        
              
    }
    
    
    // Cleaning by deleting every Sensor Impossible or Not contained in the Possible List
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
    

    // Cleans using the Impossible List
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
    
    
    // Sums the Probs
    //
    // Used to see if we've got 1 for Total Prob
    public double getTotalProb () {
        
        double res = 0.0;
        for (int i = 0; i < this.size(); i++) {
            
            res = res + this.getProb(i);
        }
        
        res = Math.round(res * 1000);
        res = res/1000;
        return res;
    } 
    
    

    
    // Returns true if sen occurs in the StateList
    public boolean hasSensor (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return true;
        }
        
        
        return false;
    }
    
    
    // Returns the indexes of Sensors matching sen in the StateList
    public ArrayList <Integer> findSensor (Sensor sen) {
        
        ArrayList <Integer> res = new ArrayList <> ();
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch(sen))
                res.add(i);
        }
        
        
        return res;
    }
    
    
    // Returns the indexes of sen in the StateList
    public int findSensor_exact (Sensor sen) {
        
        for (int i = 0; i < this.size(); i++) {
            
            if (this.getSensor(i).sensorMatch_exact(sen))
                return i;
        }
        
        
        return -1;
    }
    

    
}
