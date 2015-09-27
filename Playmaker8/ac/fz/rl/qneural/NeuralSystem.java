package ac.fz.rl.qneural;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.protobuf.TextFormat;

import ac.fz.matrix.*;
import fzdeepnet.GlobalVar;
import fzdeepnet.Setting;


public abstract class NeuralSystem {
	//Setting.Model mConf;
	//Setting.Trainer trnConf;
	HashMap<String,Layer> layers;
	HashMap<String,FzMatrix> weights;
	HashMap<String,FzMatrix> biases;
	HashMap<String,Boolean> directions;	
	BufferedReader episodeR;
	//HashMap valueMap;	
	
	public NeuralSystem(){};
	
	//public NeuralSystem(Setting.Model mConf) throws Exception{
	//	FzMatrix.type = FzMatrix.MatrixType.valueOf(QNeuralParams.MTX_TYPE);
	//	this.mConf = mConf;
	//}
	
	public NeuralSystem(boolean autobuild) throws Exception{
		if(autobuild){
			FzMatrix.type = FzMatrix.MatrixType.valueOf(QNeuralParams.MTX_TYPE);
		layers = new HashMap<String,Layer>();
		weights = new HashMap<String,FzMatrix>();
		biases = new HashMap<String,FzMatrix>();
		directions = new HashMap<String,Boolean>();
		
		// Initialize all layers
				for(Setting.Layer lconf:QNeuralParams.LAYERS){
					Layer l = null;				
					if(lconf.getUnit()==Setting.Layer.Unit.BINOMIAL){
						l = new BinomialLayer(lconf.getLid(),lconf.getDimensions());						
					}else if(lconf.getUnit()==Setting.Layer.Unit.LOGSIG){
						l = new LogsigLayer(lconf.getLid(), lconf.getDimensions());									
					}else if(lconf.getUnit()==Setting.Layer.Unit.SOFTMAX){
						l = new SoftmaxLayer(lconf.getLid(), lconf.getDimensions());									
					}else{
						throw new Exception("Layer's type is not supported");
					}
					// Create bias & assign to layer
					FzMatrix bias = FzMatrix.create(lconf.getDimensions(),1);
					biases.put(lconf.getLid(), bias);
					l.setBias(bias);
					// Add layer to the list
					layers.put(lconf.getLid(), l);
				}
				/* debug
				Iterator it = layers.entrySet().iterator();
			    while (it.hasNext()) {
			        Map.Entry pair = (Map.Entry)it.next();
			        System.out.println(pair.getKey() + " = " + pair.getValue());			        
			    }				  
				 end debug*/
				// Initialize an empty set of weights
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
					
						int m = layers.get(dlayerID).getDimension();
						String key= l.getLid()+":"+dlayerID;
						//System.out.println(m + " x " + n);
						String ikey = dlayerID +":"+ l.getLid();	
						if(!weights.containsKey(key)){
							//System.out.println(weights.containsKey(ikey));
							//System.out.println(dConf.getDirection());
							if(dConf.getDirection() == Setting.Dependence.Direction.FORWARD || !weights.containsKey(ikey)){
								weights.put(key, FzMatrix.rand(m,n));
								//System.out.println(weights.get(key).getRowDimension() + " x " + weights.get(key).getColumnDimension());
								directions.put(key, true);
							}else{ // It is bidirectional connection and the weight has been defined in "ikey" 								
								//weights.put(key, weights.get(ikey));
								directions.put(key, false);
								//System.out.println(weights.get(key).getRowDimension() + " x " + weights.get(key).getColumnDimension());
							}
							
						}
					}
					
					// Set reference to list of weights & directions
					l.setWeights(weights, directions);
					// Set reference to list of layers
					l.setLayers(layers);
				}
		}
				
	}	
	/*
	public static NeuralSystem initializeModel(Setting.Model mConf) throws Exception{
		String modelName = mConf.getModelName();
		if(modelName.equals("Q Neural Net")){
			return new QNeuralNet(mConf);
		}
		return null;
	}
	*/
	/* This is for pump-priming project */
	public static NeuralSystem initializeModel() throws Exception{
		if(QNeuralParams.MODEL_NAME.equals("Q Neural Net")){
			System.out.println("Creating Q Neural Net model ......");
			return new QNeuralNet();
		/*
		}else if(QNeuralParams.MODEL_NAME.equals("Q Fitted Neural Net")){
			System.out.println("Creating Q fitted Neural Net model ......");
			return new QFittedNeuralNet();
		}else if(QNeuralParams.MODEL_NAME.equals("Q RBM")){
			System.out.println("Creating Q RBM model ......");
			return new QRBM();
		}else if(QNeuralParams.MODEL_NAME.equals("Q Fitted RBM")){
			System.out.println("Creating Q fitted RBM model ...");
			return new QFittedRBM();
		}else if(QNeuralParams.MODEL_NAME.equals("QMixtureRBM")){
			// Not implemented
			//return new QMixtureRBM(NeuralSystemParams.STATE_DIM,NeuralSystemParams.ACTION_DIM);
		}else if(QNeuralParams.MODEL_NAME.equals("QFittedMixtureRBM")){
			// Not implemented
			//return new QFittedMixtureRBM(mConf,stateDim,actionDim);	
		*/
		}else{
			throw new Exception("No model has been defined");
		}
	}
    
	
	
	// Initialize model
	protected abstract void buildNetwork() throws Exception;
	// Create dependencies
	protected abstract void makeDependencies();
	 // Train the model
	public abstract void train();
	
	// Print the model
	public abstract void print();
	
	// Get output from multiple-input layers in matrices format
	public abstract FzMatrix getOutput(FzMatrix ... inputs);		
}
