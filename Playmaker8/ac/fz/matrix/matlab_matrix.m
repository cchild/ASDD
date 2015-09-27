function matlab_matrix()
EXP_DIR = '';
x1 = rand(3,4);
y1 = rand(4,2);
x2 = rand(3,4);

write_mtx_2_file(strcat(EXP_DIR,'x1.txt'),x1);
write_mtx_2_file(strcat(EXP_DIR,'x2.txt'),x2);
write_mtx_2_file(strcat(EXP_DIR,'y1.txt'),y);

%% Operations
	% FzMatrix arrayTimes(FzMatrix m)
	atimes = x1.*x2;
	write_mtx_2_file(strcat(EXP_DIR,'atimes.txt'),atimes);	
	% FzMatrix arrayDivide(FzMatrix m);
	adivide = x1./x2;
	write_mtx_2_file(strcat(EXP_DIR,'adivide.txt'),adivide);
	// FzMatrix plus(FzMatrix m); & plusEquals(FzMatrix m);
	mplus = x1+x2;
	write_mtx_2_file(strcat(EXP_DIR,'mplus.txt'),mplus);
	// minus(FzMatrix m) & minusEquals(FzMatrix m);
	mminus= x1-x2;
	write_mtx_2_file(strcat(EXP_DIR,'mminus.txt'),mminus);
	// times(FzMatrix m);
	mtxtime = x1*y;
	write_mtx_2_file(strcat(EXP_DIR,'mtimes.txt'),mtxtime);
	// T();
	mtrans = x1';
	write_mtx_2_file(strcat(EXP_DIR,'mplus.txt'),mplus);
	// logistic() & logisticEquals();
	mlogistic = 1/(1+exp(-x1));
	write_mtx_2_file(strcat(EXP_DIR,'mlogistic.txt'),mlogistic);
	// exp() & expEquals();
	mexp = exp(x1);
	write_mtx_2_file(strcat(EXP_DIR,'mexp.txt'),mexp);
	// log() & logEquals();
	mlog = log(x1);
	write_mtx_2_file(strcat(EXP_DIR,'mlog.txt'),mlog);
	// concat(FzMatrix m,int direction) throws Exception;
	catrow = [x1;x2];
	write_mtx_2_file(strcat(EXP_DIR,'catrow.txt'),catrow);
	catcol = [x1 x2];
	write_mtx_2_file(strcat(EXP_DIR,'catcol.txt'),catcol);
	//concat(double m) throws Exception;
	catrow1x1 = [x1(1,:) 5];
	write_mtx_2_file(strcat(EXP_DIR,'catrow1x1.txt'),catrow1x1);
	catcol1x1 = [x1(:,1);5];
	write_mtx_2_file(strcat(EXP_DIR,'catcol1x1.txt'),catcol1x1);
	// repmat(int row, int col) throws Exception;
	rmat1 = repmat(x1,2,1);
	write_mtx_2_file(strcat(EXP_DIR,'rmat1.txt'),rmat1);
	rmat2 = repmat(x1,1,2);
	write_mtx_2_file(strcat(EXP_DIR,'rmat2.txt'),rmat2);
	rmat3 = repmat(x1,3,4);
	write_mtx_2_file(strcat(EXP_DIR,'rmat3.txt'),rmat3);
	// sum(int direction);
	msum1 = sum(x1,1);
	write_mtx_2_file(strcat(EXP_DIR,'msum1.txt'),msum1);
	msum2 = sum(x1,2);
	write_mtx_2_file(strcat(EXP_DIR,'msum2.txt'),msum2);
	// pow(int base);
	mpower2 = x1.*x1;
	write_mtx_2_file(strcat(EXP_DIR,'mpower2.txt'),mpower2);
	mpower3 = mpower2.*x1;
	write_mtx_2_file(strcat(EXP_DIR,'mpower3.txt'),mpower3);
	//rowMultiply(FzMatrix m) throws Exception;
	mrowmul = bsxfun(@times,x1,x1(1,:));
	write_mtx_2_file(strcat(EXP_DIR,'mrowmul.txt'),mrowmul);
	//colMultiply(FzMatrix m) throws Exception;
	mcolmul = bsxfun(@times,x1,x1(:,1));
	write_mtx_2_file(strcat(EXP_DIR,'mcolmul.txt'),mcolmul);
	// rowSum(FzMatrix m) throws Exception;
	mrowsum = bsxfun(@plus,x1,x1(1,:));
	write_mtx_2_file(strcat(EXP_DIR,'mcolmul.txt'),mcolmul);
	// colSum(FzMatrix m) throws Exception;
	mcolsum = bsxfun(@plus,x1,x1(:,1));
	write_mtx_2_file(strcat(EXP_DIR,'mcolmul.txt'),mcolmul);
end

function write_mtx_2_file(fname,mtx)
fid = fopen(fname,'w');
for i=1:size(mtx,1)
  for j=1:size(mtx,2)
  	fprintf(fid,num2str(mtx(i,j));
  	fprintf(fid," ");
  end
end
fclose(fid);
end