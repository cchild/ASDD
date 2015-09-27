package ac.fz.rl.qneural;

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
public class QFittedRBM{ 
		/*extends QRBM{
										
			// This is used to interact with Chris' game 
			public QFittedRBM(Setting.Model mConf,int stateDim,int actionDim) throws Exception{	
				super(mConf,stateDim,actionDim);
			}
						
			
			//  Train QFittedRBM
			public void train(){	
				try{
					// Forward message for s,a & compute initial backprop update for this pair
					//System.out.println("Q-Learning with Energy based systems (RBMs) ...");
					applyStateAction(nextState,bestAction);
					this.forwardMessage();
					Matrix target = this.freeEnergy();
					
					sNum = currState.getColumnDimension();
					applyStateAction(currState,currAction);
					this.forwardMessage();
					Matrix currentFE = this.freeEnergy();
					
					Matrix TD = currentFE.minus(target).minus(rewards);
					
					// Print out error
					System.out.println("Error =" + FzMath.sum(FzMath.pow(TD,2),true,true).get(0,0));
					
					// Updating weights
					layers.get("h1").setState(FzMath.rowMultiply(layers.get("h1").getState(),TD));
					
					
					Iterator it= weights.entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<String, Matrix> pair = (Map.Entry)it.next();
						String wkey = pair.getKey();
						String[] lkeys  = wkey.split(":");
						Matrix weightDecay  = pair.getValue().times(trnConf.getWeightNorm());
						Matrix dwMomentum     = DWs.get(wkey).times(trnConf.getInitialMomentum());
						// Note that weight is lkeys[1] x lkeys[0]
						Matrix diff = layers.get(lkeys[1]).getState().times(layers.get(lkeys[0]).getState().transpose()).times(1.0f/sNum);
						
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
						DBs.put(key,FzMath.sum(layers.get(key).getState().times(trnConf.getLearningRate()),false,true)
								.times(1.0/sNum).plus(db));
						biases.put(key, biases.get(key).plus(DBs.get(key)));
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			*/
}
