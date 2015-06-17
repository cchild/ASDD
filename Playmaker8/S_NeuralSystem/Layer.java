package S_NeuralSystem;

import java.util.ArrayList;
import Jama.Matrix;

public abstract class Layer {
	int dimesion;
	int sNum;
	Matrix state;
	Matrix input_message;
	ArrayList<Layer> dependentLayer;
	public Layer(int dimension,int sNum){
		this.dimesion = dimension;
		this.sNum = sNum;
		state = new Matrix(dimension,sNum);
		input_message = new Matrix(dimension,sNum);
	}
	
	private void getInputMessage(){
		
	}
	
	abstract void activation();
	abstract void sampling();
}
