package ac.fz.rl.qneural;
/*
 * Son Tran
 * QNeuralNet: Neural Net Q-Learning
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fzdeepnet.FzMath;
import fzdeepnet.Setting;
import ac.fz.matrix.*;

public class QNeuralNet extends QNeuralSystem implements NeuralNet{
	HashMap<String,FzMatrix> DWs;
	HashMap<String,FzMatrix> DBs;
	
	int depth;	
		
	public QNeuralNet() throws Exception{
		if (!QNeuralParams.PARAMS_INIT)
			throw new Exception("Model's parameters have not been initialized!! Check setting file!!");
		// Initialize network's layers
		buildNetwork();
		// Make dependencies
		makeDependencies();
		
		// Initialize weights update for learning
		DWs = new HashMap<String,FzMatrix>();
		Iterator it = weights.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,FzMatrix> pair = (Map.Entry)it.next();
			String key = pair.getKey();
			FzMatrix weight = pair.getValue();
			DWs.put(key, FzMatrix.create(weight.getRowDimension(),weight.getColumnDimension()));
		}
		// Initialize biases update for learning
		DBs = new HashMap<String,FzMatrix>();
		it = biases.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, FzMatrix> pair = (Map.Entry)it.next();
			String key = pair.getKey();
			FzMatrix bias = pair.getValue();
			layers.get(key).setBias(bias);
			DBs.put(key, FzMatrix.create(bias.getRowDimension(),bias.getColumnDimension()));
		}
		// Initialize an copy of model's layers
		clayers = new HashMap<String,Layer>();
	}
	
	
	/*
	 *  Train QNeuralNet
	 */
	public void train(){	
		// Forward message for s,a & compute initial backprop update for this pair
		//System.out.println("Train ...");
		applyStateAction(currState,currAction);
		this.forwardMessage();
		FzMatrix y = getOutput();
		// Copy states of the layer
		copyLayers();
		// Forward message for s' and argmax_a'Q(s',a)
	    applyStateAction(nextState,bestAction);
	    this.forwardMessage();
	    FzMatrix yt = getOutput();
	    
	    double error = y.minus(rewards).minus(yt).pow(2).sum(1).sum(2).get(0,0);
	    System.out.println("Error = " + error);
	    //System.out.println("Back prop ...");
	    back_prop(y.minus(yt.plus(rewards)));
		// Finalizing Back-prop to update the params
		finalize_back_prop();
	}
	
	/*
	 * Get output given all inputs
	 */
	@Override
	public FzMatrix getOutput(FzMatrix... inputs) {
		// TODO Auto-generated method stub		
		int i=1;
		while(layers.containsKey("i"+String.valueOf(i))){
			layers.get("i"+String.valueOf(i)).setState(inputs[i-1]);
			//System.out.println(layers.get("i"+String.valueOf(i)).lid);
			//FzMath.printMatrixShape(layers.get("i"+String.valueOf(i)).getState());
			i++;
		}
		forwardMessage();
		return layers.get("o").getState();
		
	}
	protected FzMatrix getOutput(){
		return layers.get("o").getState();
	}
	
	/*
	 * Forward message
	 */
	public void forwardMessage(){		
		try{
			for(int i=0;i<this.depth;i++){ // forward message over all hidden layer
				Layer l = layers.get("h"+String.valueOf(i+1));
				//debug/*					
				//enddebug*/
				l.collectInpMessage();
				l.computeOutMessage();
				//l.activate();			
			}
			// Get the output		
			layers.get("o").collectInpMessage();
			layers.get("o").computeOutMessage();
			//layers.get("o").activate();
			//layers.get("o").getState().print(1, 5);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*
	 * @ override
	 */
	public void back_prop(){
		// EMPTY
	}
	
	/*
	 * Back-propagate the update with unit error in top layer
	 * The true update will be multiplied with actual error in top layer in finalize_back_prop
	 */
		
	public void back_prop(FzMatrix err_as){			
		// Back_prop
		update("o",err_as,err_as.copy());		
	}
	
	/*
	 * Recursive function to update parameters
	 */
	private void update(String lid,FzMatrix err_as,FzMatrix err_axsx){
		if(layers.get(lid).getType() == Setting.Layer.UType.INPUT){
			return;
		}
		Layer layer = layers.get(lid);
		Layer clayer = clayers.get(lid);		
		HashSet<String> dlayerIdx = layer.getDependencies();
		for(String s:dlayerIdx){
			String key = lid+":"+s;
			FzMatrix DW = DWs.get(key);
			FzMatrix bp_err_as;
			FzMatrix bp_err_axsx;	
			int sNum = layers.get(s).getState().getColumnDimension();
			FzMatrix tmp;
			err_as.arrayTimesEquals(layer.grad());
			err_axsx = err_axsx.arrayTimesEquals(clayer.grad());
			if(directions.get(key)){				
			    // update the weight: lix.dimension x s.dimension							
				tmp = clayers.get(s).getState().times(err_as.T());
				tmp = tmp.minus(layers.get(s).getState().times(err_axsx.T())).times(1/sNum);
				
				tmp.minusEquals(weights.get(key).times(QNeuralParams.WEIGHT_DECAY));							
				// back prop error
				bp_err_as = weights.get(key).times(err_as);
				bp_err_axsx = weights.get(key).times(err_axsx);
			}else{
			    // update the weight: s.dimension x lix.dimension - normally not happen in Neural Nets
				// If this is used, need to check
				tmp = err_as.times(clayers.get(s).getState().T());
				tmp = tmp.minus(err_axsx.times(layers.get(s).getState().trace())).times(1/sNum);
				tmp.minusEquals(weights.get(key).times(QNeuralParams.WEIGHT_DECAY));				
				// back prop error
				bp_err_as = weights.get(key).T().times(err_as);
				bp_err_axsx = weights.get(key).T().times(err_axsx);
			}			
			// TODO
			DWs.put(key,DW.times(QNeuralParams.INIT_MOMENTUM).plus(tmp.times(QNeuralParams.LEARNING_RATE)));
			DBs.put(lid, err_as.times(QNeuralParams.LEARNING_RATE).sum(2).times(1/sNum));
			update(s,bp_err_as,bp_err_axsx);
		}				
	}
	
	/*
	 * Finalize back prop to update the params
	 */
	protected void finalize_back_prop(){
		Iterator<String> it = weights.keySet().iterator();
		while(it.hasNext()){			
			String key = it.next();
			// weights.get(key).print(1, arg1);
			weights.get(key).minusEquals(DWs.get(key));
		}
		it = biases.keySet().iterator();
		while(it.hasNext()){			
			String key = it.next();			
			biases.get(key).minusEquals(DBs.get(key));
		}
	}
	
	
	public void applyStateAction(FzMatrix s,FzMatrix a){
		layers.get("i1").setState(s);
		layers.get("i2").setState(a);
	}
	
	
	/*
	 * Load mapping between states and neural input/output values
	 */	
	private HashMap loadValueMap(String filename) throws IOException{
		BufferedReader rder = new BufferedReader(new FileReader(filename));
		String line;
		HashMap map = new HashMap();
		int count = 0;
		String[] strs;		
		while(count<sfNum && (line=rder.readLine())!=null){
			count++;
			strs = line.split(",");
			for(int i=1;i<strs.length;i++){
				int inx  = strs[i].indexOf(":");
				map.put("i1:f"+strs[0]+":" + strs[i].substring(0,inx), Double.valueOf(strs[i].substring(inx+1,strs[i].length())));								
			}			
		}
				
		line=rder.readLine();
		strs = line.split(",");	
		for(int i=1;i<strs.length;i++){
			int inx  = strs[i].indexOf(":");
			map.put("i2:f"+strs[0]+ ":" + strs[i].substring(0,inx), Integer.valueOf(strs[i].substring(inx+1,strs[i].length())));								
		}	
		
		/* debug		
		Iterator it = map.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        System.out.println(pair.getKey() + " = " + pair.getValue());
	        it.remove(); // avoids a ConcurrentModificationException
	    }
		 java.util.Scanner scanner = new java.util.Scanner(System.in);		
		char c = scanner.next().charAt(0);		
		end debug */
		return map;
	}
	/*
	 * Print the QNeuralNet
	 */
	public void print(){
		System.out.println("Print QNeural Net .... ");
		System.out.print("("+sfNum+"+"+afNum+") --> " );
		for(int i=0;i<depth;i++){
			System.out.print(layers.get("h"+String.valueOf(i+1)).getDimension() + "-->");
		}
		System.out.println("1");
	}

	
}
