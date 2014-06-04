package Model;

import EnvModel.PredatorModel.*;
import EnvModel.*;
public class ActionStateRecord implements Cloneable{

    private PredatorAgentPercep before;
    private Action action;
    private AfterPercepStore afterstore;
    private double utility;
    
    public ActionStateRecord(PredatorAgentPercep before, Action action, AfterPercepStore afterstore) {
        this.before = before;
        this.action = action;
        this.afterstore = (AfterPercepStore)afterstore.clone();
        
    }

    public Object clone() {
        try {
            ActionStateRecord actionstaterecord = (ActionStateRecord)super.clone(); // clone the record
            actionstaterecord.afterstore = (AfterPercepStore)afterstore.clone();
            return actionstaterecord;               // return the clone
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public PredatorAgentPercep getPercepBefore(){
        return before;
    }

    public AfterPercepStore getAfterPercepStore(){
        return afterstore;
    }

    public Action getAction(){
        return action;
    }

    public double getUtility(){
        return utility;
    }

    public void setUtility(double x){
        utility = x;
    }
}