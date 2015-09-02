package S_NeuralSystem;
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
import Jama.Matrix;

public class QNeuralNet extends QNeuralSystem implements NeuralNet{
	HashMap<String,Matrix> DWs;
	HashMap<String,Matrix> DBs;
	
	int depth;	
		
	
	/* This constructor has not been used for the project*/
	public QNeuralNet(Setting.Model mConf) throws Exception{		
		super(mConf);
		trnConf = mConf.getDisFtune();
		// Get depth
		depth = 0;
		Iterator it = layers.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String key = pair.getKey().toString();	        
	        if(key.charAt(0)=='h'){
	        	depth++;
	        }
	    }
	    
		sfNum = layers.get("i1").getDimension();    // State layer
		afNum = layers.get("i2").getDimension();    // Action layer
		clayers = new HashMap<String,Layer>();
	}
	
	/* This is used to interact with Chris' game */
	public QNeuralNet(Setting.Model mConf,int stateDim,int actionDim) throws Exception{	
		super(mConf);
		sfNum = stateDim;
		afNum = actionDim;					
		this.mConf = mConf;
		trnConf = mConf.getDisFtune();
		List<Setting.Layer> layerConfs = mConf.getLayerList();
		
		// Initialize network's layers
		buildNetwork(layerConfs);
		
		// Make dependencies
		makeDependencies(layerConfs);
		
		// Get the network's depth
		getDepth();
		
		// Initialize weights update for learning
		DWs = new HashMap<String,Matrix>();
		Iterator it = weights.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String,Matrix> pair = (Map.Entry)it.next();
			String key = pair.getKey();
			Matrix weight = pair.getValue();
			DWs.put(key, new Matrix(weight.getRowDimension(),weight.getColumnDimension()));
		}
		// Initialize biases update for learning
		DBs = new HashMap<String,Matrix>();
		it = biases.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Matrix> pair = (Map.Entry)it.next();
			String key = pair.getKey();
			Matrix bias = pair.getValue();
			layers.get(key).setBias(bias);
			DBs.put(key, new Matrix(bias.getRowDimension(),bias.getColumnDimension()));
		}
		// Initialize an copy of model's layers
		clayers = new HashMap<String,Layer>();
	}
	
	
	/*
	 * Get depth
	 */
	private void getDepth(){
		// Get depth
		depth = 0;
		Iterator it = layers.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			String key = pair.getKey().toString();	        
			if(key.charAt(0)=='h'){
				depth++;
			}
		}
	}
	/*
	 *  Train QNeuralNet
	 */
	public void train(){	
		// Forward message for s,a & compute initial backprop update for this pair
		//System.out.println("Train ...");
		applyStateAction(currState,currAction);
		this.forwardMessage();
		Matrix y = getOutput();
		// Copy states of the layer
		copyLayers();
		// Forward message for s' and argmax_a'Q(s',a)
	    applyStateAction(nextState,bestAction);
	    this.forwardMessage();
	    Matrix yt = getOutput();
	    
	    double error = FzMath.sum(FzMath.pow(y.minus(rewards).minus(yt),2),true,true).get(0,0);
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
	public Matrix getOutput(Matrix... inputs) {
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
	protected Matrix getOutput(){
		return layers.get("o").getState();
	}
	
	/*
	 * Forward message
	 */
	public void forwardMessage(){		
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
		
	public void back_prop(Matrix err_as){			
		// Back_prop
		update("o",err_as,err_as.copy());		
	}
	
	/*
	 * Recursive function to update parameters
	 */
	private void update(String lid,Matrix err_as,Matrix err_axsx){
		if(layers.get(lid).getType() == Setting.Layer.UType.INPUT){
			return;
		}
		Layer layer = layers.get(lid);
		Layer clayer = clayers.get(lid);		
		HashSet<String> dlayerIdx = layer.getDependencies();
		for(String s:dlayerIdx){
			String key = lid+":"+s;
			Matrix DW = DWs.get(key);
			Matrix bp_err_as;
			Matrix bp_err_axsx;	
			int sNum = layers.get(s).getState().getColumnDimension();
			Matrix tmp;
			err_as.arrayTimesEquals(layer.grad());
			err_axsx = err_axsx.arrayTimesEquals(clayer.grad());
			if(directions.get(key)){				
			    // update the weight: lix.dimension x s.dimension							
				tmp = clayers.get(s).getState().times(err_as.transpose());
				tmp = tmp.minus(layers.get(s).getState().times(err_axsx.transpose())).times(1/sNum);
				
				tmp.minusEquals(weights.get(key).times(trnConf.getWeightNorm()));							
				// back prop error
				bp_err_as = weights.get(key).times(err_as);
				bp_err_axsx = weights.get(key).times(err_axsx);
			}else{
			    // update the weight: s.dimension x lix.dimension - normally not happen in Neural Nets				
				tmp = err_as.times(clayers.get(s).getState().transpose());
				tmp = tmp.minus(err_axsx.times(layers.get(s).getState().trace())).times(1/sNum);
				tmp.minusEquals(weights.get(key).times(trnConf.getWeightNorm()));				
				// back prop error
				bp_err_as = weights.get(key).transpose().times(err_as);
				bp_err_axsx = weights.get(key).transpose().times(err_axsx);
			}			
			// TODO
			DWs.put(key,DW.times(trnConf.getInitialMomentum()).plus(tmp.times(trnConf.getLearningRate())));
			DBs.put(lid, FzMath.sum(err_as.times(trnConf.getLearningRate()),false,true).times(1/sNum));
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
	
	
	public void applyStateAction(Matrix s,Matrix a){
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
