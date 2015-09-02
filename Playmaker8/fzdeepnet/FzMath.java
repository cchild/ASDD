package fzdeepnet;

import Jama.Matrix;

public class FzMath {

	public static Matrix log(Matrix m){
		Matrix x = m.copy();
		int col = m.getColumnDimension();
		int row = m.getRowDimension();
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				x.set(i, j, Math.log(m.get(i,j)));
			}
		}
		return x;
	}
	
	public static Matrix exp(Matrix m){
		Matrix x = m.copy();
		int col = m.getColumnDimension();
		int row = m.getRowDimension();
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				x.set(i, j, Math.exp(m.get(i,j)));
			}
		}
		return x;
	}
	
	public static Matrix logistic(Matrix m){
		Matrix x = m.copy();
		int col = m.getColumnDimension();
		int row = m.getRowDimension();
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				x.set(i, j, 1/(1+Math.exp(-m.get(i,j))));
			}
		}
		return x;
	}
	
	public static void printMatrixShape(Matrix m){
		int col = m.getColumnDimension();
		int row = m.getRowDimension();
		System.out.println(row + " x " + col);
	}
	
	public static Matrix initializeMatrix(int m,int n){		
		Matrix mtx =  Matrix.random(m,n).times(0.001);
		//mtx.print(1, 5);
		return mtx;
	}
	public static Matrix repmat(Matrix x,int m,int n){
		// Need to double check
		// Replicate matrix to m row and n column
		int row = x.getRowDimension();
		int col = x.getColumnDimension();
		Matrix mtx = new Matrix(row*m,col*n);
		if((m>1 && n>0) || (n>1 && m>0)){
			for(int i=0;i<m;i++){
				for(int j=0;j<n;j++){ 
					mtx.setMatrix(i*row, (i+1)*row-1,j*col,(j+1)*col-1,x);
				}
			}
		}else{
			mtx= x.copy();
		}
		return mtx;
	}
	
	// Sum matrix in row/column direction
	public static Matrix sum(Matrix x, boolean row,boolean col){
		Matrix res = null;
		if(row){
			res = new Matrix(1,x.getColumnDimension());
			for(int i=0;i<x.getColumnDimension();i++){
				double tmp = 0;
				for(int j=0;j<x.getRowDimension();j++){
					tmp += x.get(j, i);
				}
				res.set(0,i, tmp);
			}
			x = res;
		}
		if(col){
			res = new Matrix(x.getRowDimension(),1);
			for(int i=0;i<x.getRowDimension();i++){
				double tmp = 0;
				for(int j=0;j<x.getColumnDimension();j++){
					tmp += x.get(i, j);
				}
				res.set(i,0, tmp);
			}
			x = res;
		}
		return res;
	}
	
	// Pow
	public static Matrix pow(Matrix x,int base){
		Matrix rs = new Matrix(x.getRowDimension(),x.getColumnDimension());
		for(int i=0;i<x.getRowDimension();i++){
			for(int j=0;j<x.getColumnDimension();j++){
				rs.set(i,j, Math.pow(x.get(i, j), base));
			}
		}
		return rs;
	}
	
	// Row multiply
	public static Matrix rowMultiply(Matrix x,Matrix y) throws Exception{
		Matrix rs = new Matrix(x.getRowDimension(),x.getColumnDimension());
		if(y.getRowDimension()!=1 || y.getColumnDimension() != x.getColumnDimension()){
			throw new Exception("Dimensions do not match");
		}
		for(int i=0;i<x.getRowDimension();i++){
			for(int j=0;j<x.getColumnDimension();j++){
				rs.set(i, j, x.get(i,j)*y.get(0, j));
			}
		}
		
		return rs;
	}
	// Col multiply
	public static Matrix colMultiply(Matrix x,Matrix y) throws Exception{
		Matrix rs = new Matrix(x.getRowDimension(),x.getColumnDimension());
		//System.out.println(y.getRowDimension() + " x " + y.getColumnDimension() );
		//System.out.println(x.getRowDimension() + " x " + x.getColumnDimension() );
		if(y.getColumnDimension()!=1 || y.getRowDimension() != x.getRowDimension()){
			throw new Exception("Dimensions do not match");
		}
		for(int i=0;i<x.getColumnDimension();i++){
			for(int j=0;j<x.getRowDimension();j++){
				rs.set(j, i, x.get(j,i)*y.get(j, 0));
			}
		}
		return rs;
	}
	
	// Matrix concatenation
	public static Matrix concat(Matrix x,Matrix y,int direction){
		Matrix  rs = null;
		if(direction==1){ // concat row by row
			rs = new Matrix(x.getRowDimension()+y.getRowDimension(),x.getColumnDimension());
			rs.setMatrix(0, x.getRowDimension()-1, 0,rs.getColumnDimension()-1,x);
			rs.setMatrix(x.getRowDimension(),rs.getRowDimension()-1, 0,rs.getColumnDimension()-1,y);
		}else if(direction==2){// concate column by column
			rs = new Matrix(x.getRowDimension(),x.getColumnDimension()+y.getColumnDimension());
			rs.setMatrix(0, rs.getRowDimension()-1, 0,x.getColumnDimension()-1,x);
			rs.setMatrix(0, rs.getRowDimension()-1, x.getColumnDimension(),rs.getColumnDimension()-1,y);
		}
		return rs;
	}
	//concatenate vector
	public static Matrix concat(Matrix x,double y) throws Exception{
		Matrix  rs = null;
		if(x.getRowDimension()==1){ // concat row vector
			rs = new Matrix(1,x.getColumnDimension()+1);
			rs.setMatrix(0, 0, 0,x.getColumnDimension()-1,x);
			rs.set(0,x.getColumnDimension(),y);
		}else if(x.getColumnDimension()==1){// concate column vector
			rs = new Matrix(x.getRowDimension()+1,1);
			rs.setMatrix(0, rs.getRowDimension()-1, 0,0,x);
			rs.set(x.getRowDimension(), 0,y);
		}else{
			throw new Exception("x must be vector");
		}
		return rs;
	}
}

