package simula.test.context;

import simula.runtime.context.BASICIO;
import simula.runtime.context.OBJECT;
import simula.runtime.context.PrintFile;
import simula.runtime.context.TEXTREF;

public class HelloWord extends BASICIO {
	// Declare parameters as attributes
	// Declare locals as attributes
	// Constructor
	public HelloWord() {
		super();
		// Parameter assignment to locals
		// Class Body
		((PrintFile) sysout()).outtext(new TEXTREF("Hello Word!"));
		((PrintFile) sysout()).outimage();
	}
}
