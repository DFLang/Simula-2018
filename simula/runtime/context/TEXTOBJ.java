package simula.runtime.context;

public class TEXTOBJ extends OBJECT {
	// Declare parameters as attributes
	public int SIZE;
	public boolean CONST;
	// Declare locals as attributes
	char[] MAIN;
	
	public String toString()
	{ return("TEXTOBJ: SIZE="+SIZE+", CONST="+CONST+", MAIN="+edText()); }
	
	public String edText()
	{ StringBuilder s=new StringBuilder();
	  for (int i = 0; i < SIZE; i++) s.append(MAIN[i]);
	  return(s.toString());
	}
	

	// Constructor
	public TEXTOBJ(int param_SIZE, boolean param_CONST) {
		// Parameter assignment to locals
		SIZE = param_SIZE;
		CONST = param_CONST;
		// Class Body
		MAIN = new char[SIZE];
		fill(' ');
		//Util.BREAK("new "+this);
	}
	public TEXTOBJ(String s)
	{ CONST=true;
	  MAIN=s.toCharArray();
	  SIZE=MAIN.length;
	  //Util.BREAK("new "+this);
	}

	public void fill(char c) {
		//Util.BREAK("TEXTOBJ.fill(" + c + ')' + ", MAIN=" + MAIN.length);
		for (int i = 0; i < SIZE; i++) MAIN[i] = c;
	}
}
