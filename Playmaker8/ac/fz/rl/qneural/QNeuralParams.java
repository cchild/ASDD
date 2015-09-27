package ac.fz.rl.qneural;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.protobuf.TextFormat;

import fzdeepnet.GlobalVar;
import fzdeepnet.Setting;

/*
 * This includes neural system params
 */
public class QNeuralParams {
	// Are parameters initialized
	public static boolean PARAMS_INIT = false;
	// Model configuration file
	public static String MODEL_CONF_FILE=null;
	// Model's name
	public static String MODEL_NAME;
	// Matrix package
	public static String MTX_TYPE = "JAMA";
	
	// Layer
	public static List<Setting.Layer> LAYERS;
	// Depth of network
	public static int DEPTH;
	// State dimension
	public static int STATE_DIM;
	// Action dimension
	public static int ACTION_DIM;
	// Number of sample in one training batch
	public static int SNUM = 1;
	// Training rate
	public static double LEARNING_RATE = 0.1;
	// Momentum
	public static double INIT_MOMENTUM=0.0;
	// Weight decay
	public static double WEIGHT_DECAY = 0.0;
	
	/*
	 * Load params from protobufer
	 */
	public static void loadParams() throws Exception{
		if (QNeuralParams.MODEL_CONF_FILE !=null){
			FileInputStream fis = new FileInputStream(QNeuralParams.MODEL_CONF_FILE);
		    java.io.InputStreamReader reader = new java.io.InputStreamReader(fis);
		    Setting.Model.Builder modelBuilder= Setting.Model.newBuilder();   
			TextFormat.merge(reader, modelBuilder);
			Setting.Model mConf = modelBuilder.build();
			Setting.Trainer trnConf  = mConf.getDisFtune();			
			
			// Set Params
			MODEL_NAME = mConf.getModelName();
			// Layer configs
			LAYERS = mConf.getLayerList();
			// Get state,action dimensions, depth
			DEPTH = 0;
			for (Setting.Layer l:LAYERS) {
				if(l.getLid().equals("i1"))
					STATE_DIM = l.getDimensions();
				if(l.getLid().equals("i2"))
					ACTION_DIM = l.getDimensions();
				if(l.getLid().charAt(0)=='h'){
						DEPTH++;
				}
			}
			
			
			// Done, all parameters have been initialized
			PARAMS_INIT = true;
		}else{
			throw new Exception("No configuration file found!!");
		}
	}
}
