package ac.fz.rl.qneural;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ac.fz.matrix.*;
import fzdeepnet.Setting;
import fzdeepnet.Setting.Trainer;

public abstract class QNeuralSystem extends NeuralSystem{
	int sfNum, afNum; // Dimension of state feature and action features
	
	FzMatrix currState;
	FzMatrix currAction;	
	FzMatrix nextState;
	FzMatrix bestAction;
	FzMatrix rewards;
	
	HashMap<String,Layer> clayers;
	
	// This index is for online accumulative batch
	int sinx = 0;
	public QNeuralSystem()throws Exception{
		
	}		
	
	@Override
	protected void buildNetwork() throws Exception{
		layers = new HashMap<String,Layer>();
		weights = new HashMap<String,FzMatrix>();
		biases = new HashMap<String,FzMatrix>();
		directions = new HashMap<String,Boolean>();
		
		// Initialize all layers
		for(Setting.Layer lconf:QNeuralParams.LAYERS){
			Layer l = null;			
			int dim = lconf.getDimensions();
			// WHY DO WE NEED THIS
			if(lconf.getLid().equals("i1")){
				dim = QNeuralParams.STATE_DIM;
			}else if(lconf.getLid().equals("i2")){
				dim = QNeuralParams.ACTION_DIM;
			}
						
			if(lconf.getUnit()==Setting.Layer.Unit.BINOMIAL){
					l = new BinomialLayer(lconf.getLid(),dim);						
			}else if(lconf.getUnit()==Setting.Layer.Unit.LOGSIG){
					l = new LogsigLayer(lconf.getLid(), dim);									
			}else if(lconf.getUnit()==Setting.Layer.Unit.SOFTMAX){
					l = new SoftmaxLayer(lconf.getLid(), dim);									
			}else{
					throw new Exception("Layer's type is not supported");
			}
			// Create bias & assign to layer
			FzMatrix bias = FzMatrix.create(dim,1);
			biases.put(lconf.getLid(), bias);
			l.setBias(bias);
			// Add layer to the list
			layers.put(lconf.getLid(), l);
		}
	
	}
	@Override
	protected void makeDependencies(){
		try{
			weights = new HashMap<String,FzMatrix>();
			directions = new HashMap<String,Boolean>();
						
			// Setting dependencies among layers
			for(Setting.Layer lconf:QNeuralParams.LAYERS){					
				Layer l = layers.get(lconf.getLid());
				for(Setting.Dependence dConf:lconf.getDependList()){
					String dlayerID = dConf.getLayerId();
					l.addDependency(dlayerID);
					/* NOTE:
					 * In this project convolution is not used, therefore do not
					 * need to define the convolution type
					 */				
					int n = l.getDimension();
					// WHY DO NEED THIS
					if(l.getLid().equals("i1")){
						n = QNeuralParams.ACTION_DIM;
					}else if(l.getLid().equals("i2")){
						n = QNeuralParams.STATE_DIM;
					}
					Layer ll = layers.get(dlayerID);
					int m = ll.getDimension();
					// WHY DO WE NEED THIS
					if(ll.getLid().equals("i1")){
						m = QNeuralParams.STATE_DIM;
					}else if(ll.getLid().equals("i2")){
						m = QNeuralParams.ACTION_DIM;
					}
					
					String key= l.getLid()+":"+dlayerID;
					String ikey = dlayerID +":"+ l.getLid();
					//System.out.println(key + m + " x " + n);
					if(!weights.containsKey(key)){
						if(dConf.getDirection() == Setting.Dependence.Direction.FORWARD || !weights.containsKey(ikey)){
							weights.put(key, FzMatrix.rand(m,n).timesEquals(0.0001));
							directions.put(key, true);
						}else{ // It is bidirectional connection and the weight has been defined in "ikey" 								
							//weights.put(key, weights.get(ikey));
							directions.put(key, false);
						}
						
					}
				}
				
				// Set reference to list of weights & directions
				l.setWeights(weights, directions);
				// Set reference to list of layers
				l.setLayers(layers);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/*
	 * Copy Layers
	 */
	protected void copyLayers(){
		Iterator it= layers.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, Layer> pair = (Map.Entry)it.next();			
			clayers.put(pair.getKey(), pair.getValue().copyStateLayer());
		}
	}
		
	/*
	 * Set state to state layer
	 */
	public void addStateActionRewards(double[] s,double[] a,double[] ns,double[] ba,double r){
		try{
			System.out.println(QNeuralParams.SNUM);
			if(currState==null){
				currState = FzMatrix.create(s);
				currAction = FzMatrix.create(a);
				nextState = FzMatrix.create(ns);
				bestAction = FzMatrix.create(ba);
				rewards = FzMatrix.create(1,1,r);
			}else if(currState.getColumnDimension()<QNeuralParams.SNUM){
				currState =  currState.concat(s,2);
				currAction = currAction.concat(a,2);
				nextState  = nextState.concat(ns,2);
				bestAction = bestAction.concat(ba,2);
				rewards = rewards.concat(r,2);
			}else{ // Full batch
				System.out.println("sinx " + sinx);
				int row = currState.getRowDimension();
				currState.setMatrix(0, row-1, sinx, sinx, s);
				currAction.setMatrix(0, row-1,sinx,sinx,a);
				nextState.setMatrix(0,row-1,sinx,sinx,ns);
				bestAction.setMatrix(0,row-1,sinx,sinx,ba);
				rewards.set(0, sinx, r);
				// set next index for replace, until all is done then repeat the process
				if(sinx<QNeuralParams.SNUM - 1){
					sinx++;
				}else{
					sinx = 0;
				}
			}
			}catch(Exception e){
				e.printStackTrace();
		}
	}
	
}
