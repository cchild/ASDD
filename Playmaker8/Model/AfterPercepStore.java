package Model;
import EnvModel.PredatorModel.*;
import EnvModel.*;
import java.util.*;
/**
 * This calss stores a list of all transiations staes that are possible as the result of a single.
 * action tken in a state
 *
 * @ Rima Varsy
 * @date 27-09-2002
 */

public class AfterPercepStore implements Cloneable{
    private int afterCounts;
    private ArrayList afterperceps;
    
    public Object clone() {
        try {
            AfterPercepStore a = (AfterPercepStore)super.clone();   // clone the record
            a.afterperceps = (ArrayList)afterperceps.clone();
            return a;               // return the clone
        } catch (CloneNotSupportedException e) {
           throw new InternalError();
        }
    }

    public AfterPercepStore(AfterPercepRecord afterrecord){
        afterCounts = 0;
        afterperceps = new ArrayList();
        AfterPercepRecord afterperceprecord = (AfterPercepRecord)afterrecord.clone();
        afterperceps.add(afterCounts, afterperceprecord); 
        afterCounts++;
    }
       
    
    public void addAfterPercepRecord(AfterPercepRecord afterrecord){
        boolean add = true; int i = 0;
        AfterPercepRecord afterperceprecord = (AfterPercepRecord)afterrecord.clone();       
        while (add != false && i < afterperceps.size()){ 
                      
             if (samePercep((getAfterPercepRecord(i)).getPercepAfter(), afterperceprecord.getPercepAfter())){
                 add = false; 
                 (getAfterPercepRecord(i)).setFrequency(1);
                        
             }else {  add = true;}
             i++;
        }
        if (add == true){
            afterperceps.add(afterCounts, afterperceprecord ); 
            afterCounts++;
        } 
    }
    
    public int getFrequencySum(){
        int frequencySum = 0;
        for (int index = 0; index < afterperceps.size(); index++)
            frequencySum = frequencySum + getAfterPercepRecord(index).getFrequency();
        return frequencySum;
    }

    public boolean samePercep(PredatorAgentPercep before, PredatorAgentPercep after){
        boolean samepercep = true; int index = 0;
        while (samepercep != false && index < 5){
            if (before.getPercep(index) == after.getPercep(index)){
                samepercep = true;
                index++;
            } else{
                samepercep = false;
            }
        }
        return samepercep;
    }

    public AfterPercepRecord getAfterPercepRecord(int which){
        return (AfterPercepRecord)afterperceps.get(which);
    }

    public int getSize(){
        return afterperceps.size();
    }
}
