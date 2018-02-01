package simula.test.context;

import simula.runtime.context.*;

public class VariableByName extends OBJECT {
	// Declare parameters as attributes
	// Declare locals as attributes
	float v;
	float x;
	TEXTREF tt;
	TEXTREF uu;
	TEXTREF zz;
	A aa;
	A bb;

	public class A extends OBJECT {
		// Declare parameters as attributes
		// Declare locals as attributes
		// Constructor
		public A() {
			// Parameter assignment to locals
			// Class Body
		}
	}

	public void F(ValueTypeByName_<Boolean> cond, ValueTypeByName_<Float> q) {
		{
			float j;
			j = q.get();
			q.put(9.7f);
			q.put(q.get() + 13.4f);
			q.put((cond.get()) ? (q.get()) : (q.get() + 67.9f));
		}
	}

	public void G (ValueTypeByName_<A> q,A r) {
	      {
	         q.put(r);
	      }
	   }

//	public void W(TEXTREF t, TextByName u, TEXTREF z) {
	public void W(TEXTREF t, ValueTypeByName_<TEXTREF> u, TEXTREF z) {
		{
		}
	}

	// Constructor
	public VariableByName() {
	      // Parameter assignment to locals
	      // Class Body
	      v=4.0f;
	      x=5.0f;
	      F(new ValueTypeByName_<Boolean>(){ public Boolean get() { return(true); } },new ValueTypeByName_<Float>(){ public Float get() { return(v); } public void put(Float x) { v=(float)x; } });
	      F(new ValueTypeByName_<Boolean>(){ public Boolean get() { return(x < v); } },new ValueTypeByName_<Float>(){ public Float get() { return(x + 4.5f); } });
	      G(new ValueTypeByName_<A>(){ public A get() { return(aa); } public void put(A x) { aa=(A)x; } },bb);
	      W(tt,new ValueTypeByName_<TEXTREF>(){ public TEXTREF get() { return(uu); } public void put(TEXTREF x) { uu=(TEXTREF)x; } },zz);

	   }
}