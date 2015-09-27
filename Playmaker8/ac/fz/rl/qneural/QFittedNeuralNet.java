package ac.fz.rl.qneural;

import java.util.HashSet;

import Jama.Matrix;
import fzdeepnet.FzMath;
import fzdeepnet.Setting;
import fzdeepnet.Setting.Model;

public class QFittedNeuralNet{ 
	/*extends QNeuralNet{    
	public QFittedNeuralNet()
			throws Exception {
		super();
				
	}

	@Override
	public void train(){
		try{
			System.out.println(nextState.getColumnDimension() + " x " + nextState.getColumnDimension());
			applyStateAction(nextState,bestAction);
			
			this.forwardMessage();
			Matrix target = getOutput();
			
			applyStateAction(currState,currAction);
			this.forwardMessage();
			Matrix y = getOutput();
			
			double error = FzMath.sum(FzMath.pow(y.minus(rewards).minus(target),2),true,true).get(0,0);
		    System.out.println("Error = " + error);
		    
			this.back_prop(y.minus(target.plus(rewards)));
			
			finalize_back_prop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void back_prop(Matrix err_as){
		update("o",err_as);
	}
	
	private void update(String lid,Matrix err_as){
		if(layers.get(lid).getType() == Setting.Layer.UType.INPUT){
			return;
		}
		Layer layer = layers.get(lid);		
		HashSet<String> dlayerIdx = layer.getDependencies();
		for(String s:dlayerIdx){
			String key = lid+":"+s;
			Matrix DW = DWs.get(key);
			Matrix bp_err_as;
			Matrix bp_err_axsx;	
			int sNum = layers.get(s).getState().getColumnDimension();
			Matrix tmp;
			err_as.arrayTimesEquals(layer.grad());
			
			if(directions.get(key)){				
			    // update the weight: lix.dimension x s.dimension							
				tmp = layers.get(s).getState().times(err_as.transpose());
				tmp.minusEquals(weights.get(key).times(trnConf.getWeightNorm()));							
				// back prop error
				bp_err_as = weights.get(key).times(err_as);
			}else{
			    // update the weight: s.dimension x lix.dimension - normally not happen in Neural Nets				
				tmp = err_as.times(layers.get(s).getState().transpose());
				tmp.minusEquals(weights.get(key).times(trnConf.getWeightNorm()));				
				// back prop error
				bp_err_as = weights.get(key).transpose().times(err_as);
			}			
			// TODO
			DWs.put(key,DW.times(trnConf.getInitialMomentum()).plus(tmp.times(trnConf.getLearningRate())));
			DBs.put(lid, FzMath.sum(err_as.times(trnConf.getLearningRate()),false,true).times(1/sNum));
			update(s,bp_err_as);
		}			
	}
	*/	
}
