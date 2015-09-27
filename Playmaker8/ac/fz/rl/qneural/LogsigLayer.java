package ac.fz.rl.qneural;

import ac.fz.matrix.FzMatrix;

public class LogsigLayer extends Layer {
	
	public LogsigLayer(String lid, int dimension){
		super(lid, dimension);
	}	
	@Override
	public void computeOutMessage(){
		state = input_message.logistic();
	}
	@Override
	public void activate(){
		
	}
	
	public FzMatrix grad(){
		try{
			FzMatrix ones = FzMatrix.create(state.getRowDimension(),state.getColumnDimension(),1);
			//grad = o(1-o)
			return state.arrayTimes(ones.minus(state));
		}catch(Exception e){
			return null;
		}
	}
	@Override
	public Layer copyStateLayer() {		
		Layer layer = new LogsigLayer(lid, dimension);
		layer.setState(state);
		return layer;	
	}
	
}
