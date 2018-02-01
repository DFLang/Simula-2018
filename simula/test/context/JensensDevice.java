package simula.test.context;

import simula.runtime.context.*;

public class JensensDevice extends BASICIO {
	   // Declare parameters as attributes
	   // Declare locals as attributes
	   public double Sum (ValueTypeByName_<Integer> k,int lower,int upper,ValueTypeByName_<Double> ak) {
	      {
	         double s;
	         s=((double)(0.0));
	         k.put(lower);
	         while(k.get() <= upper) {
	            {
	               s=s + ak.get();
	               k.put(k.get() + 1);
	            }
	         }
	         return(s);
	      }
	   }
	   public int i;
	   public int j;
	   public int m;
	   public int n;
	   public double[] A=new double[99-0+1];
	   public double result;
	   // Constructor
	   public JensensDevice() {
	      super();
	      // Parameter assignment to locals
	      // Class Body
	      result=Sum(new ValueTypeByName_<Integer>(){ public Integer get() { return(i); } public void put(Integer x) { i=(int)x; } },10,60,new ValueTypeByName_<Double>(){ public Double get() { return(A[i-0]); } public void put(Double x) { A[i-0]=(double)x; } });
	      ((PrintFile)sysout()).outtext(new TEXTREF("JensensDevice Sum="));
	      ((PrintFile)sysout()).outchar('A');
	      ((PrintFile)sysout()).outint(((int)(result)),4);
	      ((PrintFile)sysout()).outchar('B');
	      ((PrintFile)sysout()).outreal(3.14f,2,4);
	      ((PrintFile)sysout()).outchar('C');
	      ((PrintFile)sysout()).outint(((int)(1233445)),6);
	      ((PrintFile)sysout()).outimage();
	      result=Sum(new ValueTypeByName_<Integer>(){ public Integer get() { return(j + m * n); } },10,20,new ValueTypeByName_<Double>(){ public Double get() { return(A[i-0]); } public void put(Double x) { A[i-0]=(double)x; } });
	   }
	}
