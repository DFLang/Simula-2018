package simula.runtime.context;

import common.Util;

public class TEXTREF extends OBJECT {
	TEXTOBJ OBJ;
	int START; // Start index of OBJ.MAIN[], counting from zero. Note this
				// differ from Simula Definition.
	int LENGTH;
	int POS;

	public String toString() {
		return ("TEXTREF: START=" + START + ", LENGTH=" + LENGTH + ", POS="
				+ POS + ", OBJ=" + OBJ);
	}

	public String edText() {
		return (OBJ.edText());
	}

	public boolean constant() {
		return (OBJ == null || OBJ.CONST);
	}

	public void ASSIGN(TEXTREF U) {
		int fromLength = 0;
		if (U != null)
			fromLength = U.LENGTH;
		for (int i = 0; i < fromLength; i++)
			OBJ.MAIN[i] = U.OBJ.MAIN[i];
		for (int i = fromLength; i < this.LENGTH; i++)
			OBJ.MAIN[i] = ' ';
	}

	public int start() {
		return (START + 1);
	}

	public int length() {
		return (LENGTH);
	}

	public TEXTREF main() {
		if (OBJ != null) {
			TEXTREF T;
			T = new TEXTREF();
			T.OBJ = OBJ;
			T.START = 0;
			T.LENGTH = OBJ.SIZE;
			T.POS = 1;
			return (T);
		}
		return (null);
	}

	public int pos() {
		return (POS);
	}

	public void setpos(int i) {
		POS = (i < 1 | i > LENGTH + 1) ? (LENGTH + 1) : (i);
	}

	public boolean more() {
		return (POS <= LENGTH);
	}

	public char getchar() {
		if (POS > LENGTH) {
			error("");
			return (0);
		} else {
			POS = POS + 1;
			return (OBJ.MAIN[START + POS - 1]);
		}
	}

	public void putchar(char c) {
		if (OBJ == null || OBJ.CONST || POS > LENGTH)
			error("");
		OBJ.MAIN[START + POS - 1] = c;
		POS = POS + 1;
	}

	public TEXTREF sub(int i, int n) {
		if (i < 0 || n < 0 || i + n > LENGTH + 1)
			error(" ! Sub out of frame");
		else if (n > 0) {
			TEXTREF T;
			T = new TEXTREF();
			T.OBJ = OBJ;
			T.START = START + i - 1;
			T.LENGTH = n;
			T.POS = 1;
			return (T);
		}
		return (null);
	}

	public TEXTREF strip() {
		return (null);
	}

	public int getint() {
		return (0);
	}

	public float getreal() {
		return (0.0f);
	}

	public int getfrac() {
		return (0);
	}

	private void putResult(String s) {
		char[] c = s.toCharArray();
		if (c.length > LENGTH) { warning("Edit Overflow Occured");
			for (int j = 0; j < LENGTH; j = j + 1)
				OBJ.MAIN[this.START + j] = '#';
		} else {
			for (int j = 0; j < LENGTH; j = j + 1)
				OBJ.MAIN[this.START + j] = (j < c.length) ? c[j] : ' ';
		}
		POS = LENGTH + 1;
	}

	public void putint(int i) {
		putResult("" + i);
	}

	public void putfix(float r, int n) {
		putResult("" + r);
	}

	public void putreal(float r, int n) {
		putResult("" + r);
	}

	public void putfrac(int i, int n) {
		putResult("" + i);
	}

	// Constructor
	public TEXTREF() {
	}

	public TEXTREF(String s) {
		OBJ = new TEXTOBJ(s);
		START = 0;
		LENGTH = OBJ.SIZE;
		POS = 1;
	}
}
