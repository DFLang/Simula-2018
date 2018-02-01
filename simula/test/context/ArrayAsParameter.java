package simula.test.context;

public class ArrayAsParameter {
	   // Declare parameters as attributes
	   // Declare locals as attributes
	   public int[] A=new int[89-7+1];
	   public class CC {
	      // Declare parameters as attributes
	      public int[] AAA;
	      // Declare locals as attributes
	      int m;
	      // Constructor
	      public CC(int[] param_AAA) {
	         // Parameter assignment to locals
	         AAA = param_AAA;
	         // Class Body
//	LINE 8: WARNING: Parameter Array is only supported for one index counting from zero - Check program
	         m=AAA[74];
	      }
	   }
	   public void P (int[] AA) {
	      {
	         int k;
//	LINE 14: WARNING: Parameter Array is only supported for one index counting from zero - Check program
	         k=AA[9];
	      }
	   }
	   // Constructor
	   public ArrayAsParameter() {
	      // Parameter assignment to locals
	      // Class Body
	      P(A);
	      new CC(A);
	   }
	}