package S_NeuralSystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import fzdeepnet.FzMath;
import fzdeepnet.Setting;

//Q-learning RBM (Sallans - JMLR 2004)
public class QRBM extends QNeuralSystem{
			HashMap<String,Matrix> DWs;
			HashMap<String,Matrix> DBs;
		    int depth =1;
		    int sNum;
			double reward;
									
			/* This is used to interact with Chris' game */
			public QRBM(Setting.Model mConf,int stateDim,int actionDim) throws Exception{	
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
			 * Copy Layers
			 */
			
			/*
			 *  Train QNeuralNet
			 */
			public void train(){	
				try{
					// Forward message for s,a & compute initial backprop update for this pair
					//System.out.println("Q-Learning with Energy based systems (RBMs) ...");
					sNum = currState.getColumnDimension();
					applyStateAction(currState,currAction);
					this.forwardMessage();
					Matrix currentFE = this.freeEnergy();
					
					copyLayers();
					
					applyStateAction(nextState,bestAction);
					this.forwardMessage();
					Matrix nextFE = this.freeEnergy();
					
					Matrix TD = currentFE.minus(nextFE).minus(rewards);
					
					// Print out error
					System.out.println("Error =" + FzMath.sum(FzMath.pow(TD,2),true,true).get(0,0));
					
					// Updating weights
					clayers.get("h1").setState(FzMath.rowMultiply(clayers.get("h1").getState(),TD));
					layers.get("h1").setState(FzMath.rowMultiply(layers.get("h1").getState(),TD));
					
					
					Iterator it= weights.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<String, Matrix> pair = (Map.Entry)it.next();
						String wkey = pair.getKey();
						String[] lkeys  = wkey.split(":");
						Matrix weightDecay  = pair.getValue().times(trnConf.getWeightNorm());
						Matrix dwMomentum     = DWs.get(wkey).times(trnConf.getInitialMomentum());
						// Note that weight is lkeys[1] x lkeys[0]
						Matrix diff = clayers.get(lkeys[1]).getState().times(clayers.get(lkeys[0]).getState().transpose())
								.minus(layers.get(lkeys[1]).getState().times( layers.get(lkeys[0]).getState().transpose())).times(1.0f/sNum);
						
						
						
						DWs.put(wkey,diff.minus(weightDecay).times(trnConf.getLearningRate()).plus(dwMomentum));
						
						//System.out.println(DWs.get(wkey).getRowDimension() + " x " + DWs.get(wkey).getColumnDimension());
						weights.get(wkey).minusEquals(DWs.get(wkey));
						
					}
					
					// Updating biases
					it = layers.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<String, Layer> pair = (Map.Entry)it.next();
						String key = pair.getKey();
						Matrix db = DBs.get(key).times(trnConf.getInitialMomentum());
						DBs.put(key,FzMath.sum(clayers.get(key).getState().minus(pair.getValue().getState())
								.times(trnConf.getLearningRate()),false,true).times(1.0/sNum).plus(db));
						
						biases.put(key, biases.get(key).plus(DBs.get(key)));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
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
				
				return freeEnergy();
				
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
			}
			
			/*
			 * compute free energy in log-scale
			 */
			protected Matrix freeEnergy(){
				Matrix free_en  = null;
				try{
					free_en = FzMath.sum(FzMath.colMultiply(layers.get("i1").getState(),biases.get("i1")),true,false);
					free_en.plusEquals(FzMath.sum(FzMath.colMultiply(layers.get("i2").getState(),biases.get("i2")),true,false));
					Matrix exphidmes = FzMath.exp(layers.get("h1").getInputMessage());
					free_en.plusEquals(FzMath.sum(FzMath.log(exphidmes.plus(new Matrix(exphidmes.getRowDimension(),exphidmes.getColumnDimension(),1))),true,false));
				}catch(Exception e){
					e.printStackTrace();
				}
				return free_en;
			}
			private Matrix getOutput(){
				return freeEnergy();
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
			 * Print the QRBM
			 */
			public void print(){
				System.out.println("Print QRBM .... ");
				Iterator it = layers.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry<String, Layer> pair = (Map.Entry)it.next();
					pair.getValue().print();
				}
			}

}
