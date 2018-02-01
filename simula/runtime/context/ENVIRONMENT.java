package simula.runtime.context;

public class ENVIRONMENT extends OBJECT {
	// Declare parameters as attributes
	// Declare locals as attributes
	public static int rank(char c) {
		return ((int) c);

	}

	public static char Char(int n) {
		return ((char) n);
	}

	public static boolean digit(char c) {
		return (Character.isDigit(c));
	}

	public static boolean letter(char c) {
		return (Character.isLetter(c));
	}

	public static TEXTREF blanks(int n) {
		//Util.BREAK("ENVIRONMENT.Blanks("+n+")");
		TEXTREF textRef=new TEXTREF();
		TEXTOBJ textObj=new TEXTOBJ(n,false);
		textObj.fill(' ');
		textRef.START = 0;
		textRef.LENGTH = n;
		textRef.POS = 1;
		textRef.OBJ=textObj;
		//Util.BREAK("ENVIRONMENT.Blanks("+n+") Result="+textRef);
		return(textRef);
	}

	public static TEXTREF copy(TEXTREF U) {
		TEXTREF T; T=blanks(U.LENGTH);
		T.ASSIGN(U); return(T);
	}

	public static boolean draw(float a, ValueTypeByName_<Integer> U) {
		return (false);
	}

	public static float randint(int a, float b, ValueTypeByName_<Integer> U) {
		return (0.0f);

	}

	public static float uniform(float a, float b, ValueTypeByName_<Integer> U) {
		return (0.0f);

	}

	public static float normal(float a, float b, ValueTypeByName_<Integer> U) {
		return (0.0f);

	}

	public static int discrete(float[] A, ValueTypeByName_<Integer> U) {
		return (0);

	}

	public static int histd(float[] A, ValueTypeByName_<Integer> U) {
		return (0);

	}

	public static void histo(float[] A, float[] B, float c, float d) {

	}

	public static void accum(ValueTypeByName_<Float> a, ValueTypeByName_<Float> b,
			ValueTypeByName_<Float> c, float d) {

	}

	public static void error(TEXTREF msg) {
			error(msg.toString());
	}

	public static double sqrt(double x) {
		return(Math.sqrt(x));
	}

	public static double arctg(double x, double y) {
		return(Math.atan2(x,y)); }

	// Constructor
	public ENVIRONMENT() {
		// Parameter assignment to locals
		// Class Body
	}

}
