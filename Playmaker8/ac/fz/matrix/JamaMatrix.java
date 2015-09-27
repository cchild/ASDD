package ac.fz.matrix;
import Jama.Matrix;
/*
 * Wrapper of Jama matrix
 */

public class JamaMatrix extends FzMatrix{
	Matrix mtx;
	public JamaMatrix(double[][] m) {
		super();
		mtx = new Matrix(m);
	}
	
	public JamaMatrix(double[] m){
		super();
		mtx = new Matrix(m.length,1);
		for(int i=0;i<m.length;i++){
			mtx.set(i,0, m[i]);
		}
	}
	public JamaMatrix(int row,int col){
		super();
		mtx = new Matrix(row,col);
	}
	public JamaMatrix(int row,int col,double value){
		super();
		mtx = new Matrix(row,col,value);
	}
	
	public JamaMatrix(Matrix m){
		super();
		mtx = m.copy();
	}
	
	@Override
	public void set(int i, int j, double value) {
		mtx.set(i, j, value);
		
	}

	@Override
	public void setMatrix(int[] is, int[] js, FzMatrix m) {
		mtx.setMatrix(is, js, ((JamaMatrix)m).getMatrix());
	}

	@Override
	public void setMatrix(int i0, int i1, int j0, int j1, FzMatrix m) {
		mtx.setMatrix(i0, i1, j0,j1,((JamaMatrix)m).getMatrix());
	}
	
	@Override
	public void setMatrix(int i0, int i1, int j0, int j1, double[] m) throws Exception {
		if(i0==i1){// set vector to row
			for(int i=j0;i<=j1;i++)
				mtx.set(i0, i, m[i]);
		}else if(j0==j1){// set vector to column
			for(int i=i0;i<=i1;i++)
				mtx.set(i,j0,m[i]);
		}else{
			throw new Exception("Only one row or one collum is set");
		}
	}
	
	public Matrix getMatrix(){
		return mtx;
	}
	@Override
	public double get(int i, int j) {
		return mtx.get(i,j);
	}

	
	@Override
	public FzMatrix getMatrix(int[] is, int[] js) {
		FzMatrix m = new JamaMatrix(mtx.getMatrix(is, js));
		return m;
	}

	@Override
	public FzMatrix getMatrix(int i0, int i1, int j0, int j1) {
		FzMatrix m = new JamaMatrix(mtx.getMatrix(i0,i1,j0,j1));
		return m;
	}

	@Override
	public int getRowDimension() {
		return mtx.getRowDimension();
	}

	@Override
	public int getColumnDimension() {
		return mtx.getColumnDimension();
	}

	@Override
	public FzMatrix copy() {
		return new JamaMatrix(mtx.copy());
	}

	@Override
	public FzMatrix arrayTimes(FzMatrix m) {
		return new JamaMatrix(mtx.arrayTimes(((JamaMatrix)m).getMatrix()));
	}

	@Override
	public FzMatrix arrayTimesEquals(FzMatrix m) {
		mtx.arrayTimesEquals(((JamaMatrix)m).getMatrix());
		return this;
	}
	
	@Override
	public FzMatrix arrayDivide(FzMatrix m) {
		return new JamaMatrix(mtx.arrayRightDivide(((JamaMatrix)m).getMatrix()));
	}

	@Override
	public FzMatrix arrayDivideEquals(FzMatrix m) {
		mtx.arrayRightDivideEquals(((JamaMatrix)m).getMatrix());
		return this;
	}
	
	@Override
	public FzMatrix plus(FzMatrix m) {
		return new JamaMatrix(mtx.plus(((JamaMatrix)m).getMatrix()));
	}

	@Override
	public FzMatrix plusEquals(FzMatrix m) {
		mtx.plusEquals(((JamaMatrix)m).getMatrix());
		return this;
	}

	@Override
	public FzMatrix minus(FzMatrix m) {
		return new JamaMatrix(mtx.minus(((JamaMatrix)m).getMatrix()));
	}

	@Override
	public FzMatrix minusEquals(FzMatrix m) {
		mtx.minusEquals(((JamaMatrix)m).getMatrix());
		return this;
	}

	@Override
	public FzMatrix times(FzMatrix m) {
		return new JamaMatrix(mtx.times(((JamaMatrix)m).getMatrix()));
	}
	
	@Override
	public FzMatrix T(){
		return new JamaMatrix(mtx.transpose());
	}
	
	/*@Override
	public FzMatrix mergeRow(FzMatrix m) throws Exception {
		int crow = getRowDimension();
		int ccol = getColumnDimension();
		int nrow = m.getRowDimension();
		int ncol = m.getColumnDimension();
		
		if(ccol != ncol)
			throw new Exception("Two matrix must have the same columns!!!");
		
		JamaMatrix mm = new JamaMatrix(crow+nrow,ccol);
		mm.setMatrix(0, crow-1,0,ccol-1,new JamaMatrix(mtx));
		mm.setMatrix(crow,crow+nrow-1, 0,ccol-1,m);
		return mm;
	}

	@Override
	public FzMatrix mergeCol(FzMatrix m) throws Exception {
		int crow = getRowDimension();
		int ccol = getColumnDimension();
		int nrow = m.getRowDimension();
		int ncol = m.getColumnDimension();
		
		if(crow != nrow)
			throw new Exception("Two matrix must have the same rows!!!");
		
		JamaMatrix mm = new JamaMatrix(crow,ccol+ncol);
		mm.setMatrix(0, crow-1,0,ccol-1,new JamaMatrix(mtx));
		mm.setMatrix(0,crow-1, ccol,ccol+ncol-1,m);
		return mm;
	}
	*/
	@Override
	public FzMatrix logistic() {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		FzMatrix m = new JamaMatrix(row,col);
		for(int i=0;i<row;i++){
			for(int j=0;j>col;j++){
				m.set(i, j, logistic(mtx.get(i, j)));
			}
		}
		return m;
	}

	@Override
	public FzMatrix logisticEquals() {
		for(int i=0;i<mtx.getRowDimension();i++){
			for(int j=0;j>mtx.getColumnDimension();j++){
				mtx.set(i, j, logistic(mtx.get(i, j)));
			}
		}
		return this;
	}

	@Override
	public FzMatrix exp() {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		FzMatrix m = new JamaMatrix(row,col);
		for(int i=0;i<row;i++){
			for(int j=0;j>col;j++){
				m.set(i, j, Math.exp(mtx.get(i, j)));
			}
		}
		return m;
	}

	@Override
	public FzMatrix expEquals() {
		for(int i=0;i<mtx.getRowDimension();i++){
			for(int j=0;j>mtx.getColumnDimension();j++){
				mtx.set(i, j, Math.exp(mtx.get(i, j)));
			}
		}
		
		return this;
	}

	@Override
	public FzMatrix log() {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		FzMatrix m = new JamaMatrix(row,col);
		for(int i=0;i<row;i++){
			for(int j=0;j>col;j++){
				m.set(i, j, Math.log(mtx.get(i, j)));
			}
		}
		return m;
	}

	@Override
	public FzMatrix logEquals() {
		for(int i=0;i<mtx.getRowDimension();i++){
			for(int j=0;j>mtx.getColumnDimension();j++){
				mtx.set(i, j, Math.log(mtx.get(i, j)));
			}
		}
		return this;
	}

	@Override
	public FzMatrix concat(FzMatrix m, int direction) throws Exception {
		FzMatrix  rs = null;
		int crow = mtx.getRowDimension();
		int ccol = mtx.getColumnDimension();
		int nrow = m.getRowDimension();
		int ncol = m.getColumnDimension();
		if(direction==1 && ccol == ncol){ // concat row by row
			rs = new JamaMatrix(crow+nrow,ccol);
			rs.setMatrix(0, crow-1, 0,ccol-1,new JamaMatrix(mtx));
			rs.setMatrix(crow,crow+nrow-1, 0,ccol-1,m);
		}else if(direction==2 && crow == nrow){// concate column by column
			rs = new JamaMatrix(crow,ccol + ncol);
			rs.setMatrix(0, crow-1, 0, ccol-1,new JamaMatrix(mtx));
			rs.setMatrix(0, crow-1, ccol,ccol+ncol-1,m);
		}else{
			throw new Exception("Dimensions mismatch!!");
		}
		return rs;
	}

	@Override
	public FzMatrix concat(double[] m,int direction) throws Exception {
		Matrix  rs = null;
		int crow = mtx.getRowDimension();
		int ccol = mtx.getColumnDimension();
		if(direction==2 && m.length==crow){ // concat col by col
			rs = new Matrix(crow,ccol+1);
			rs.setMatrix(0, crow, 0,ccol-2,mtx);
			for(int i=0;i<crow;i++){
				rs.set(i, ccol-1, m[i]);
			}
		}else if(direction==1 && m.length==ccol){// concat row by row
			rs = new Matrix(crow+1,ccol);
			rs.setMatrix(0, crow-2, 0,ccol,mtx);
			for(int i=0;i<ccol;i++){
				rs.set(crow-1, i,m[i]);
			}
		}else{
			throw new Exception("Dimension mismatch!");
		}
		return new JamaMatrix(rs);
	}
	
	@Override
	public FzMatrix concat(double m,int direction) throws Exception {
		FzMatrix  rs = null;
		int crow = mtx.getRowDimension();
		int ccol = mtx.getColumnDimension();
		if(direction==2){ // concat row vector
			rs = new JamaMatrix(1,ccol+1);
			rs.setMatrix(0, 0, 0,ccol-2,new JamaMatrix(mtx));
			rs.set(0,ccol-1,m);
		}else if(direction==1){// concate column vector
			rs = new JamaMatrix(crow+1,1);
			rs.setMatrix(0, crow-2, 0,0,new JamaMatrix(mtx));
			rs.set(crow-1, 0,m);
		}else{
			throw new Exception("x must be vector");
		}
		return rs;
	}

	@Override
	public FzMatrix repmat(int rtimes, int ctimes) throws Exception {
		int crow = mtx.getRowDimension();
		int ccol = mtx.getColumnDimension();
		Matrix m;
		if((rtimes>1 && ctimes>0) || (rtimes>1 && ctimes>0)){
			m = new Matrix(crow*rtimes,ccol*ctimes);
			for(int i=0;i<rtimes;i++){
				for(int j=0;j<ctimes;j++){ 
					m.setMatrix(i*crow, (i+1)*crow-1,j*ccol,(j+1)*ccol-1,mtx);
				}
			}
		}else{
			m= mtx.copy();
		}
		return new JamaMatrix(m);
	}

	@Override
	public FzMatrix sum(int direction) {
		Matrix res = null;
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		if(direction==1){
			res = new Matrix(1,col);
			for(int i=0;i<col;i++){
				double tmp = 0;
				for(int j=0;j<row;j++){
					tmp += mtx.get(j, i);
				}
				res.set(0,i, tmp);
			}
		}else if(direction==2){
			res = new Matrix(row,1);
			for(int i=0;i<row;i++){
				double tmp = 0;
				for(int j=0;j<col;j++){
					tmp += mtx.get(i, j);
				}
				res.set(i,0, tmp);
			}

		}
		return new JamaMatrix(res);
	}

	@Override
	public FzMatrix pow(int base) {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		Matrix rs = new Matrix(row,col);
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				rs.set(i,j, Math.pow(mtx.get(i, j), base));
			}
		}
		return new JamaMatrix(rs);
	}

	@Override
	public FzMatrix rowMultiply(FzMatrix m) throws Exception {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension(); 
		Matrix rs = new Matrix(row,col);
		if(m.getRowDimension()!=1 || m.getColumnDimension() != col){
			throw new Exception("Dimensions do not match");
		}
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				rs.set(i, j, mtx.get(i,j)*m.get(0, j));
			}
		}
		
		return new JamaMatrix(rs);
	}

	@Override
	public FzMatrix colMultiply(FzMatrix m) throws Exception {
		int row = mtx.getRowDimension();
		int col = mtx.getColumnDimension();
		Matrix rs = new Matrix(row,col);
		if(m.getColumnDimension()!=1 || m.getRowDimension() != row){
			throw new Exception("Dimensions do not match");
		}
		for(int i=0;i<col;i++){
			for(int j=0;j<row;j++){
				rs.set(j, i, mtx.get(j,i)*m.get(j, 0));
			}
		}
		return new JamaMatrix(rs);
	}


	public static FzMatrix rand(int row, int col) {
		JamaMatrix mtx =  new JamaMatrix(Matrix.random(row,col));
		//mtx.print(1, 5);
		return mtx;
	}

	@Override
	public FzMatrix times(double vl) {
		FzMatrix m = new JamaMatrix(mtx.times(vl));
		return m;
	}

	@Override
	public FzMatrix timesEquals(double vl) {
		mtx.timesEquals(vl);
		return this;
	}


	@Override
	public FzMatrix divide(double vl) {
		Matrix m = mtx.copy();
		for(int row=0;row<m.getRowDimension();row++){
			for(int col=0;col<m.getColumnDimension();col++){
				m.set(row,col, m.get(row,col)/vl);
			}
		}
		return new JamaMatrix(m);
	}

	@Override
	public FzMatrix divideEquals(double vl) {
		for(int row=0;row<mtx.getRowDimension();row++){
			for(int col=0;col<mtx.getColumnDimension();col++){
				mtx.set(row,col, mtx.get(row,col)/vl);
			}
		}
		return this;
	}


	@Override
	public FzMatrix plus(double vl) {
		Matrix m = mtx.copy();
		for(int row=0;row<m.getRowDimension();row++){
			for(int col=0;col<m.getColumnDimension();col++){
				m.set(row, col, m.get(row,col)+vl);
			}
		}
		return new JamaMatrix(m);
	}

	@Override
	public FzMatrix plusEquals(double vl) {
		for(int row=0;row<mtx.getRowDimension();row++){
			for(int col=0;col<mtx.getColumnDimension();col++){
				mtx.set(row, col, mtx.get(row,col)+vl);
			}
		}
		return this;
	}

	@Override
	public double trace() {
		return mtx.trace();
	}
}
