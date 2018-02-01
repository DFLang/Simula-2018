package examples.compiled;

import simula.runtime.context.OBJECT;
import simula.runtime.context.TEXTREF;
import simula.test.context.Simset;

public class SubClassSample {
	   public class A extends OBJECT {
		      // Declare parameters as attributes
		      public int pa1;
		      public TEXTREF pa2;
		      // Declare locals as attributes
		      int a1;
		      int a2;
		      A Ax;
		      // Constructor
		      public A(int param_pa1,TEXTREF param_pa2) {
		         super();
		         // Parameter assignment to locals
		         pa1 = param_pa1;
		         pa2 = param_pa2;
		         // Class Body
		      }
		   }
		   public class B extends A {
		      // Declare parameters as attributes
		      public D pb1;
		      public int pb2;
		      // Declare locals as attributes
		      boolean test;
		      int b1;
		      int b2;
		      // Constructor
		      public B(int param_pa1,TEXTREF param_pa2,D param_pb1,int param_pb2) {
		         super(param_pa1,param_pa2);
		         // Parameter assignment to locals
		         pb1 = param_pb1;
		         pb2 = param_pb2;
		         // Class Body
		      }
		   }
		   public class C extends B {
		      // Declare parameters as attributes
		      public int pc1;
		      public char pc2;
		      // Declare locals as attributes
		      int c1;
		      int c2;
		      C z;
		      // Constructor
		      public C(int param_pa1,TEXTREF param_pa2,D param_pb1,int param_pb2,int param_pc1,char param_pc2) {
		         super(param_pa1,param_pa2,param_pb1,param_pb2);
		         // Parameter assignment to locals
		         pc1 = param_pc1;
		         pc2 = param_pc2;
		         // Class Body
		      }
		   }
		   public class D extends B {
		      // Declare parameters as attributes
		      public int pd1;
		      public int pd2;
		      // Declare locals as attributes
		      int d1;
		      int d2;
		      B yy;
		      C zz;
		      D ww;
		      // Constructor
		      public D(int param_pa1,TEXTREF param_pa2,D param_pb1,int param_pb2,int param_pd1,int param_pd2) {
		         super(param_pa1,param_pa2,param_pb1,param_pb2);
		         // Parameter assignment to locals
		         pd1 = param_pd1;
		         pd2 = param_pd2;
		         // Class Body
		      }
		   }
		   public class E extends A {
		      // Declare parameters as attributes
		      public TEXTREF pe1;
		      public float pe2;
		      // Declare locals as attributes
		      boolean test;
		      int b1;
		      int b2;
		      // Constructor
		      public E(int param_pa1,TEXTREF param_pa2,TEXTREF param_pe1,float param_pe2) {
		         super(param_pa1,param_pa2);
		         // Parameter assignment to locals
		         pe1 = param_pe1;
		         pe2 = param_pe2;
		         // Class Body
		      }
		   }
//		BREAK Block(Block46).doCodePrefixedBlock: prefix'name=parser.expression.FunctionDesignator: <

//		BREAK FunctionDesignator(C).toJavaCode() params=[1, NONE, NONE, 2, 3, 'f']: <

		   { new C(1,((TEXTREF)(null)),null,2,3,'f') { // Prefixed Block !
		        int c;
		        A aa;
		        B bb;
//		BREAK Block(Block77).doCodePrefixedBlock: prefix'name=parser.expression.Variable: <
		        C cc;
		        D dd;
		        public void Statements() {
		           bb=new B(4,new TEXTREF("abc"),null,4);
		           cc=new C(4,new TEXTREF("abc"),null,4,6,'k');
		           bb=((B)(cc));
		           c=a1;
		           c=Ax . a1;
		           Ax=((A)(bb));
		           aa=(bb instanceof A)?(((A)(bb))):(((A)(null)));
		        }
		     }.Statements();
		   }

		   { new Simset() { // Prefixed Block !
		        int i;
		        public void Statements() {
		           i=4;
		        }
		     }.Statements();
		   }
}
