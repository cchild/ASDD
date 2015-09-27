package ac.fz.matrix;



public abstract class FzMatrix {
	public enum MatrixType{
		JAMA,JBLAS
	}
	
	public static MatrixType type;
	// constructor
	//public FzMatrix(double[][] m){};
	//public FzMatrix(int row,int col){};
	//public FzMatrix(int row,int col,double value){};
	
	public abstract void set(int i,int j,double value);
	public abstract void setMatrix(int[] is,int[] js,FzMatrix m);
	public abstract void setMatrix(int i0,int i1,int j0,int j1,FzMatrix m);
	public abstract void setMatrix(int i0,int i1,int j0,int j1,double[] m) throws Exception;
	
	public abstract double get(int i,int j);
	public abstract FzMatrix getMatrix(int[] is,int[] js);
	public abstract FzMatrix getMatrix(int i0,int i1,int j0,int j1);
	
	public abstract int getRowDimension();
	public abstract int getColumnDimension();
	
	public abstract FzMatrix copy();
	
	/*********** BASIC OPERATION *********************************************/
	// Scalar multiply
	public abstract FzMatrix times(double vl);
	public abstract FzMatrix timesEquals(double vl);
	// Element wise multiply
	public abstract FzMatrix arrayTimes(FzMatrix m);
	public abstract FzMatrix arrayTimesEquals(FzMatrix m);
	// Scalar divide
	public abstract FzMatrix divide(double vl);
	public abstract FzMatrix divideEquals(double vl);
	// Element wise division
	public abstract FzMatrix arrayDivide(FzMatrix m);
	public abstract FzMatrix arrayDivideEquals(FzMatrix m);
	// Scalar addition
	public abstract FzMatrix plus(double vl);
	public abstract FzMatrix plusEquals(double vl);
	// Addition
	public abstract FzMatrix plus(FzMatrix m);
	// Add equal
	public abstract FzMatrix plusEquals(FzMatrix m);
	// Subtraction
	public abstract FzMatrix minus(FzMatrix m);
	// Subtract equal
	public abstract FzMatrix minusEquals(FzMatrix m);
	// Multiply
	public abstract FzMatrix times(FzMatrix m);
	// Transpose
	public abstract FzMatrix T();
	
	// Logistic
	public abstract FzMatrix logistic();
	public abstract FzMatrix logisticEquals();
	// Exponent
	public abstract FzMatrix exp();
	public abstract FzMatrix expEquals();
	// Logarithm
	public abstract FzMatrix log();
	public abstract FzMatrix logEquals();
	
	
	// Concatenate matrix
	public abstract FzMatrix concat(FzMatrix m,int direction) throws Exception;
	//concatenate vector
	public abstract FzMatrix concat(double[] m,int direction) throws Exception;
	
	public abstract FzMatrix concat(double m,int direction) throws Exception;
	// Repmat
	public abstract FzMatrix repmat(int row, int col) throws Exception;
	// Sum
	public abstract FzMatrix sum(int direction);
	// power
	public abstract FzMatrix pow(int base);
	// multiply row by row
	public abstract FzMatrix rowMultiply(FzMatrix m) throws Exception;
	// multiply column by column
	public abstract FzMatrix colMultiply(FzMatrix m) throws Exception;
	// trace
	public abstract double trace();
	
	public double logistic(double x){
		return 1/(1+Math.exp(-x));
	}
	
	public static FzMatrix create(int row,int col) throws Exception{
		if (type==MatrixType.JAMA){
			FzMatrix m = new JamaMatrix(row,col); 
			return m;
		}else{
			throw new Exception("Matrix is not set");
		}
	}
	
	public static FzMatrix create(int row, int col, double vl) throws Exception{
		if (type==MatrixType.JAMA){
			FzMatrix m = new JamaMatrix(row,col,vl); 
			return m;
		}else{
			throw new Exception("Matrix is not set");
		}
	}
	
	public static FzMatrix create(double[] vt) throws Exception{
		if(type==MatrixType.JAMA){
			FzMatrix m = new JamaMatrix(vt);
			return m;
		}else{
			throw new Exception("Matrix is not set");
		}
	}
	public static FzMatrix rand(int row,int col) throws Exception{
		if (type==MatrixType.JAMA){
			FzMatrix m = JamaMatrix.rand(row, col); 
			return m;
		}else{
			throw new Exception("Matrix type is not set");
		}
	}
}
