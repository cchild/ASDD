package ac.fz.rl.qneural;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fzdeepnet.FzMath;
import fzdeepnet.Setting;
import Jama.Matrix;
import ac.fz.rl.qneural.Layer;

public interface NeuralNet{		
	abstract void back_prop();
	abstract void forwardMessage();	
}
