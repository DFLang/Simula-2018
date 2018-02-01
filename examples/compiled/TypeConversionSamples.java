package examples.compiled;

import simula.runtime.context.*;

public class TypeConversionSamples extends OBJECT {
	// Declare parameters as attributes
	// Declare locals as attributes
	int i;
	int j;
	float r;
	float q;
	TEXTREF U;
	boolean cond;
	public int[] AR = new int[9 - 1 + 1];

	public class A extends OBJECT {
		// Declare parameters as attributes
		public int n;

		// Declare locals as attributes
		// Constructor
		public A(int param_n) {
			super();
			// Parameter assignment to locals
			n = param_n;
			// Class Body
		}
	}

	public class B extends A {
		// Declare parameters as attributes
		public TEXTREF T;

		// Declare locals as attributes
		// Constructor
		public B(int param_n, TEXTREF param_T) {
			super(param_n);
			// Parameter assignment to locals
			T = param_T;
			// Class Body
		}
	}

	public class C extends B {
		// Declare parameters as attributes
		// Declare locals as attributes
		// Constructor
		public C(int param_n, TEXTREF param_T) {
			super(param_n, param_T);
			// Parameter assignment to locals
			// Class Body
		}
	}

	public class D extends B {
		// Declare parameters as attributes
		// Declare locals as attributes
		// Constructor
		public D(int param_n, TEXTREF param_T) {
			super(param_n, param_T);
			// Parameter assignment to locals
			// Class Body
		}
	}

	public void P(int i, B x, TEXTREF T) {
		{
		}
	}

	public A aa;
	public B bb;
	public C cc;
	public D dd;

	// Constructor
	public TypeConversionSamples() {
		super();
		// Parameter assignment to locals
		// Class Body
		r = ((float) (i));
		aa = ((A) (bb));
		bb = ((B) (aa));
		P(((int) (r)), ((B) (aa)), new TEXTREF("xxx"));
		new B(((int) (r)), new TEXTREF("abra"));
		AR[((int) (r)) - 1] = (bb.n);
		bb.T = U;
		bb.T.ASSIGN(U);
		i = ((int) (r = ((float) (j = ((int) (q))))));
		r = (i > 4) ? (((float) (i))) : (((float) (i + 5)));
	}
}