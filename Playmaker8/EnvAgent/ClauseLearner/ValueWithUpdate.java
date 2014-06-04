/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package EnvAgent.ClauseLearner;

import java.io.*;

/**
 *
 * @author Chris
 */
public class ValueWithUpdate implements  Serializable {

    protected double value;
    protected double alpha;
    protected double initialAlpha;
    protected double finalAlpha;
    protected int updateCount;
    boolean mcClainUpdate;
    
      /** Creates new RuleSet */
    public ValueWithUpdate() {
        resetDefaults();
    }
          /*Call standard write object on the nodeList.
     *Must ensure that the nodes also have customizable write and read functions*/
    public void writeObject(ObjectOutputStream s) throws IOException {
        // Call even if there is no default serializable fields.
        s.defaultWriteObject();

        s.writeDouble(value);
        s.writeDouble(alpha);
        s.writeDouble(initialAlpha);
        s.writeDouble(finalAlpha);
        s.writeInt(updateCount);
        s.writeBoolean(mcClainUpdate);

    }

    public final void resetDefaults() {
        value = 0;
        alpha = 1.0;
        initialAlpha = 1.0;
        finalAlpha = 0.1;
        updateCount = 0;
        mcClainUpdate = false;
    }

    /*Call standard read object on the nodeList.
     *Must ensure that the nodes also have readObject functions*/
    public void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException  {

        s.defaultReadObject();

        value = s.readDouble();
        alpha = s.readDouble();
        initialAlpha = s.readDouble();
        finalAlpha = s.readDouble();
        updateCount =  s.readInt();
        mcClainUpdate = s.readBoolean();
    }

    @Override
    public String toString() {
       String str = new String();
       str += "val: " + value
               + " Alp: " + alpha
               + " n: " + updateCount;
       return str;
    }

    public double updateValue(double newSample) {
        value = value + alpha*(newSample - value);

        if (mcClainUpdate) {
            alpha = alpha/(1 + alpha - finalAlpha);
        } else {

            if (updateCount > 100) {
                alpha = finalAlpha;
            } else {
               alpha = initialAlpha;
            }
        }
        updateCount ++;

        return value;
    }

     public double lambdaUpdateValue(double alpha) {
        //This is slightly odd, but we seem to need an update before one has happened
        if (updateCount == 0 || updateCount == 1) {
            value = (alpha)*(alpha);
        }  else {
            double squ = (1-alpha)*(1-alpha);
            value = squ*value+alpha*alpha;
        }
        updateCount ++;

        return value;
    }

   public double getValue() {
        return value;
    }

    public void setValue(double newVal) {
        value = newVal;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getInitialAlpha() {
        return initialAlpha;
    }

    public void setInitialAlpha(double initAlph){
        initialAlpha = initAlph;
    }

    public void setFinalAlpha(double finalAlph) {
        finalAlpha = finalAlph;
    }
    public int getUpdateCount() {
        return updateCount;
    }

    public void setMcClainUpdate(boolean newVal) {
         mcClainUpdate = newVal;
    }

    public boolean getMcClainUpdate() {
        return mcClainUpdate;
    }
}
