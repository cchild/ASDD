package S_NeuralSystem;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import fzdeepnet.FzMath;
import fzdeepnet.Setting;
import fzdeepnet.Setting.Trainer;

public abstract class QNeuralSystem extends NeuralSystem{
	int sfNum, afNum; // Dimension of state feature and action features
	
	Matrix currState;
	Matrix currAction;	
	Matrix nextState;
	Matrix bestAction;
	Matrix rewards;
	
	HashMap<String,Layer> clayers;
	
	// This index is for online accumulative batch
	int sinx = 0;
	public QNeuralSystem(Setting.Model mConf)throws Exception{
		super(mConf);
	}		
	
	@Override
	protected void buildNetwork(List<Setting.Layer> layerConfs) throws Exception{
		layers = new HashMap<String,Layer>();
		weights = new HashMap<String,Matrix>();
		biases = new HashMap<String,Matrix>();
		directions = new HashMap<String,Boolean>();
		
		// Initialize all layers
		for(Setting.Layer lconf:layerConfs){
			Layer l = null;			
			int dim = lconf.getDimensions();
			if(lconf.getLid().equals("i1")){
				dim = sfNum;
			}else if(lconf.getLid().equals("i2")){
				dim = afNum;
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
			Matrix bias = new Matrix(dim,1);
			biases.put(lconf.getLid(), bias);
			l.setBias(bias);
			// Add layer to the list
			layers.put(lconf.getLid(), l);
		}
	
	}
	@Override
	protected void makeDependencies(List<Setting.Layer> layerConfs){
		weights = new HashMap<String,Matrix>();
		directions = new HashMap<String,Boolean>();
					
		// Setting dependencies among layers
		for(Setting.Layer lconf:layerConfs){					
			Layer l = layers.get(lconf.getLid());
			for(Setting.Dependence dConf:lconf.getDependList()){
				String dlayerID = dConf.getLayerId();
				l.addDependency(dlayerID);
				/* NOTE:
				 * In this project convolution is not used, therefore do not
				 * need to define the convolution type
				 */				
				int n = l.getDimension();
				if(l.getLid().equals("i1")){
					n = sfNum;
				}else if(l.getLid().equals("i2")){
					n = afNum;
				}
				Layer ll = layers.get(dlayerID);
				int m = ll.getDimension();
				if(ll.getLid().equals("i1")){
					m = sfNum;
				}else if(ll.getLid().equals("i2")){
					m = afNum;
				}
				
				String key= l.getLid()+":"+dlayerID;
				String ikey = dlayerID +":"+ l.getLid();
				//System.out.println(key + m + " x " + n);
				if(!weights.containsKey(key)){
					if(dConf.getDirection() == Setting.Dependence.Direction.FORWARD || !weights.containsKey(ikey)){
						weights.put(key, FzMath.initializeMatrix(m,n));
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
	 * Add rewards for each state-action-new state
	 */
	public void addReward(double r) {
		if(rewards==null){
			rewards = new Matrix(1,1,r);
		}else{
			Matrix rw = rewards;
			int currentSz = rw.getColumnDimension();
			rewards = new Matrix(1,currentSz+1);
			rewards.setMatrix(0,0, 0, currentSz-1,rw);
			rewards.set(1, currentSz, r);
		}
	}	
	/*
	 * Set state to state layer
	 */
	public void addStateActionRewards(Matrix s,Matrix a,Matrix ns,Matrix ba,double r){
		try{
			System.out.println(trnConf.getBatchSize());
			if(currState==null){
				currState = s;
				currAction = a;
				nextState = ns;
				bestAction = ba;
				rewards = new Matrix(1,1,r);
			}else if(currState.getColumnDimension()<trnConf.getBatchSize()){
				currState =  FzMath.concat(currState,s,2);
				currAction = FzMath.concat(currAction,a,2);
				nextState  = FzMath.concat(nextState,ns,2);
				bestAction = FzMath.concat(bestAction,ba,2);
				rewards = FzMath.concat(rewards, r);
			}else{
				System.out.println("sinx " + sinx);
				currState.setMatrix(0, currState.getRowDimension()-1, sinx, sinx, s);
				currAction.setMatrix(0, currAction.getRowDimension()-1,sinx,sinx,a);
				nextState.setMatrix(0,nextState.getRowDimension()-1,sinx,sinx,ns);
				bestAction.setMatrix(0,bestAction.getRowDimension()-1,sinx,sinx,ba);
				rewards.set(0, sinx, r);
				// set next index for replace, until all is done then repeat the process
				if(sinx<trnConf.getBatchSize()-1){
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
