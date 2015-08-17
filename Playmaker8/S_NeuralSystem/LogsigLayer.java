package S_NeuralSystem;

import Jama.Matrix;
import fzdeepnet.FzMath;

public class LogsigLayer extends Layer {
	
	public LogsigLayer(String lid, int dimension){
		super(lid, dimension);
	}	
	@Override
	public void computeOutMessage(){
		state = FzMath.logistic(input_message);
	}
	@Override
	public void activate(){
		
	}
	
	public Matrix grad(){
		Matrix ones = new Matrix(state.getRowDimension(),state.getColumnDimension(),1);
		//grad = o(1-o)
		return state.arrayTimes(ones.minus(state));
	}
	@Override
	public Layer copyStateLayer() {		
		Layer layer = new LogsigLayer(lid, dimension);
		layer.setState(state);
		return layer;	
	}
	
}
