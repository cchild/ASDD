package S_ReinforcementLearner;

import java.util.*;

import EnvModel.*;
import EnvModel.PaintModel.PaintAction;
import EnvModel.PaintModel.PaintAgentPercep;
import EnvModel.PredatorModel.PredatorAction;
import EnvModel.PredatorModel.PredatorAgentPercep;
import EnvModel.PredatorModel.PredatorFluent;

import java.io.*;

import com.google.protobuf.TextFormat;

import fzdeepnet.Setting;
import Jama.Matrix;
import Logging.*;
import ReinforcementLearner.StateActionValue;
import ac.fz.rl.qneural.*;
import ReinforcementLearner.StateActionValueMap;
/**
 *
 * @author  Son Tran
 * @version 
 */
/*QUESTION: HOW TO DECIDE ACTION*/
public class BaseStateActionValueMap extends ReinforcementLearner.BaseStateActionValueMap implements Serializable {
    
	 ArrayList stateActionValues;                    // This is not inherited from super class
	                                                 // Will remove it later on
     QNeuralSystem qNS;    
	 HashMap<String,Integer> actionInputMap;
	 HashMap<String,Action> allActions;
	 //Setting.Trainer trnConf;
	/** Creates new StateValueMap */
	public BaseStateActionValueMap() {
		super();
		System.out.println("Using Son's StateActionValueMap");
		try{
			//java.util.Scanner scanner = new java.util.Scanner(System.in);	
			//char c = scanner.next().charAt(0);	    	
		    stateActionValues = new ArrayList();
		    QNeuralParams.loadParamsFromConfFile();
			getAgentInfo(QNeuralParams.APP_NAME);			
			// initializing model ?? stateDim is empty
			qNS= (QNeuralSystem)NeuralSystem.initializeModel();	  
			qNS.print();
						
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Get all possible actions in the game
	 * and create Action-Input value mapping
	 */
	private void getAgentInfo(String appName){		
		Action a = null;
		Percep s = null;
		actionInputMap = new HashMap<String,Integer>();
		allActions = new HashMap<String,Action>();
		if(appName.equals("Predator")){
			a = new PredatorAction();
			QNeuralParams.STATE_DIM = 5*3;
			QNeuralParams.STATE_RANGES = new int[]{3,3,3,3,3};
		}else if(appName.equals("Paint")){
			a = new PaintAction();
			QNeuralParams.STATE_DIM = 5*3; // Need to check *****************
			QNeuralParams.STATE_RANGES = new int[]{3,3,3,3,3}; // Need to check **********
		}
		if(a !=null){
			int i=0;
			a.firstLegalAction();
			System.out.println(a.translation());
			actionInputMap.put(a.translation(),i);	
			allActions.put(a.translation(), (Action)a.clone());
			while(a.nextLegalAction()!=null){
				actionInputMap.put(a.translation(),i++);
				allActions.put(a.translation(), (Action)a.clone());
			}
			QNeuralParams.ACTION_DIM = actionInputMap.size();
		}		
	}
    
		
    /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();
        s.writeObject(qNS);
    }
     
    public void readFrom(String fileName) {
        try {   
            /* Open the file and set to read objects from it. */ 
            FileInputStream istream = new FileInputStream(fileName); 
            ObjectInputStream q = new ObjectInputStream(istream); 

            /* Read the learned rules object */

            BaseStateActionValueMap stateActionValueMap = (BaseStateActionValueMap)q.readObject();

            this.qNS = stateActionValueMap.getTDNeuralNet();
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        } 
    }
    
    /*
     * Return TDNeuralNet
     */
    public QNeuralSystem getTDNeuralNet(){
    	return qNS;
    }

    
    public StateActionValue getStateActionValue(Percep percep, Action action){        
    	try{
	    	String s_state = percep.translation();    	
	    	String s_action = action.translation();
	    	
	    	double[] mState = state2Double(percep);
	    	double[] mAction = action2Double(action);
	    	
	    	double sa_val = qNS.getOutput(mState,mAction);
			return new StateActionValue(percep,action,sa_val);
    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println("ERROR retrieveing state value");
    	}    	
        return null;
    }
    
    /*
     * Get Best Action
     */
    public Action getBestAction(Percep percep) {
    	
    	Action a = null;
    	/* THIS MUST BE CHANGED TO 
    	Iterator it = allActions.entrySet().iterator();
    	double vl = -Double.MAX_VALUE;
        while (it.hasNext()) {
        	Map.Entry pair = (Map.Entry)it.next();
        	Action tmpA = (Action)pair.getValue();
        	if(getStateActionValue(percep,tmpA).getValue()>vl){
        		a = (Action)tmpA.clone();
        	}
    	}
    	/*
        return a;
   }  
    
    /*
     * TODO WOKR HERE TO UPDATE NN 
     */
    
    public void setStateActionValue(Percep percep,Action action,double reward, Percep nextPercep,Action maxAction){
    	// Convert states & action to matrices & set to models   
    	qNS.addStateActionRewards(state2Double(percep),action2Double(action),state2Double(nextPercep),
    			action2Double(maxAction),reward);
    	// Do the training
    	qNS.train();
    }
    
    public void setStateActionValue(Percep percep, Action action, double value) {
    	/*** OLD CODE ***/
        // StateActionValue stateActionValue = getOrCreateStateActionValue(percep, action);
        // stateActionValue.setValue(value);
    	// double diff = oValue - value;
    	// qNS.train(diff);
     }
    /*
     * Print all possible action
     */
    public void printAllActions(){
    	Iterator it = actionInputMap.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry pair = (Map.Entry)it.next();
        	System.out.println(pair.getKey() + " = " + pair.getValue());
    	}
    }
    
    /*
     * Convert state to vector/matrix 
     */
    public double[] state2Double(Percep percep){
    	double[] m = new double[QNeuralParams.STATE_DIM];
    	int count = 0;
    	for (int i = 0; i < percep.getNumFluents(); i++) {
    		//System.out.println(count + " " + percep.getFluent(i).getValue() + " " + m.length);
             m[count+percep.getFluent(i).getValue()] =  1;
             count = count + QNeuralParams.STATE_RANGES[i];
        }
        return m;
    }
    
    /*
     * Convert action matrix
     */
    
    public double[] action2Double(Action action){
    	double[] m = new double[QNeuralParams.ACTION_DIM];
    	m[actionInputMap.get(action.translation())] =  1;
    	return m;
    }
    
    /*************************************************************/
    /********** OLD CODE *****************************************/
    /*************************************************************/
    
    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {
        
        s.defaultReadObject();
        qNS = (QNeuralSystem)s.readObject();
    }
    
    
    public void addStateAction(Percep percep, Action action) {
        if (!exists(percep, action)) {
            stateActionValues.add(new StateActionValue(percep, action));
        }
    }
    
    public void addPercepAction(Percep percep, Action action) {
        addStateAction(percep, action);
    }
    
  
    public boolean exists(Percep percep, Action action) {
        for (int i = 0; i < stateActionValues.size(); i++) {
            if (percep.isEqual(((StateActionValue)stateActionValues.get(i)).getState()))
                if (action.isEqual(((StateActionValue)stateActionValues.get(i)).getAction()))
                    return true;
        }
        return false;
    }
    
    public String toString() {
        String output = new String();
        for (int i = 0; i < stateActionValues.size(); i++) {
            output += ((StateActionValue)stateActionValues.get(i)).toString();   
        }
        return output;
    }
    
    public void output() {
        for (int i = 0; i < stateActionValues.size(); i++) {
            System.out.print(toString());   
        }
        System.out.flush();
    }
    
    public StateActionValue getOrCreateStateActionValue(Percep percep, Action action) {
       for (int i = 0; i < stateActionValues.size(); i++) {
            if (((StateActionValue)stateActionValues.get(i)).getPercep().isEqual(percep))
               if (((StateActionValue)stateActionValues.get(i)).getAction().isEqual(action))
                   return (StateActionValue)stateActionValues.get(i);
        }
        
       addStateAction(percep, action);
       return getStateActionValue(percep, action);
    }
    
    public ArrayList getStateActionValues() {
        return stateActionValues;
    }
    
    
    //Picks a random state which has already been visisted or gernerated
    public Percep pickRandomState() {
        if (stateActionValues.size() == 0)
            return null;
        
        int randomState = (int)(Math.random() * (double)stateActionValues.size());
        if (randomState == stateActionValues.size())
            randomState = stateActionValues.size() -1;
        StateActionValue stateActionValue = (StateActionValue)stateActionValues.get(randomState);
        return stateActionValue.getState();
    }
        
    
}
