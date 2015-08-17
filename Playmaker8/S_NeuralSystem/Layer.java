package S_NeuralSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import fzdeepnet.FzMath;
import fzdeepnet.Setting;
import Jama.Matrix;

public abstract class Layer {
	int dimension;                                  // Dimension of the layer
	int sNum;                                       // Number of samples in one batch
	String lid;                                     // Layer id
	Setting.Layer.UType type;                       // Type of units in this layer
	Matrix state;                                   // State of the layer	 
	Matrix input_message;                           // Input message to the layer
	Matrix bias;                                    // Bias
	HashSet<String> dLayers;                        // List of the layers this layer depends on
	HashMap<String,Layer> layers;                   // Reference to all layers
	HashMap<String,Matrix> weights;                 // Reference to all weights
	HashMap<String,Boolean> directions;             // Reference to weight directions
	public Layer(String lid, int dimension){
		this.lid = lid;
		this.dimension = dimension;
		dLayers = new HashSet<String>();
	}	
	public void addDependency(String lid){
		dLayers.add(lid);
	}
	public HashSet<String> getDependencies(){
		return dLayers;
	}
	public void setLayers(HashMap<String,Layer> layers){
		this.layers = layers;
	}	
	public void setWeights(HashMap<String,Matrix> weights, HashMap<String,Boolean> directions){
		this.weights = weights;
		this.directions = directions;
	}
	
	public void setBias(Matrix bias){
		this.bias = bias;
	}
	public String getLid(){
		return lid;
	}
	public void setType(Setting.Layer.UType type){
		this.type = type;
	}
	public Setting.Layer.UType getType(){
		return type;
	}
	public int getDimension(){
		return dimension;
	}
	public void setState(Matrix state){		
		this.state = state.copy();
	}
	public Matrix getState(){
		return state;
	}
	public Matrix getInputMessage(){ // return input message to caller (different from getMessage)
		return input_message;
	}
	public Matrix copyState(){
		return this.state.copy();
	}
	private Matrix copyInputMessage(){
		return this.input_message.copy();
	}	
	protected void collectInpMessage(){ // get incoming message from all layers it depends on
		if(dLayers.size()>=1){
			String wkey;
			Matrix message;
			String dlid[] = new String[dLayers.size()];
			dlid = dLayers.toArray(dlid);					
			for(int i=0;i<dlid.length;i++){
				wkey = lid+":"+dlid[i];
				//System.out.println(wkey);
				//FzMath.printMatrixShape(weights.get(wkey));
				//FzMath.printMatrixShape(layers.get(dlid[i]).getState());
				if(directions.get(wkey)){
					message =  weights.get(wkey).transpose().times(layers.get(dlid[i]).getState());	
				}else{
					String iwkey = dlid[i]  + ":" + lid;
					message = weights.get(iwkey).times(layers.get(dlid[i]).getState());
				}
				
				if(i==0){
					input_message = message;					
				}
				
				input_message.plusEquals(message);
			}
			//bias.print(1, 1);
			//FzMath.repmat(bias,1,input_message.getColumnDimension()).print(1, 1);
			input_message.plusEquals(FzMath.repmat(bias,1,input_message.getColumnDimension()));
		}
	}
	

	public void print(){
		Iterator it = dLayers.iterator();
		//System.out.println(lid + " " + dimension + this.toString());
	    while (it.hasNext()) {
	    	String dlid = it.next().toString();
	    	String key = lid + ":" + dlid;
	    	System.out.print(key);
	        System.out.print(" " + directions.get(key).toString() + " ");
	        if(directions.get(key)){
	        	System.out.println(weights.get(key).getRowDimension() + " x " + weights.get(key).getColumnDimension());			        
	        }else{
	        	String ikey = dlid + ":"  + lid;
	        	System.out.println(weights.get(ikey).getRowDimension() + " x " + weights.get(ikey).getColumnDimension());
	        }
	    }
	}
	
	
	abstract void computeOutMessage(); // Result after input messages have been aggregated
	abstract void activate(); // Sampling or activate	
	abstract Matrix grad();    // Compute the gradient of output function
	abstract Layer copyStateLayer(); // Copy the state to new layer
}
