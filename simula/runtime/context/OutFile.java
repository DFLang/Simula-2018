package simula.runtime.context;

import common.Util;

public class OutFile extends FILE_ {
	// Declare parameters as attributes
	// Declare locals as attributes
	boolean ENDFILE_;

	public void open(TEXTREF IMAGE_) {
		if (OPEN_)
			error("File already opened");
		OPEN_ = true;
		image = IMAGE_;
		ENDFILE_ = false;
		IMAGE_.ASSIGN(null); // image := NOTEXT;
		setpos(length() + 1);
	}

	public void close() {
		OPEN_ = false;
		ENDFILE_ = true;
        if(pos() != 1) outimage();
		image = null; // image :- NOTEXT;
	}

	public void outimage() {
		Util.BREAK("OutFile.outimage(), image=" + image);
		if (!OPEN_)
			error("File not opened");
		// Write image to file
		System.out.println(image.edText());
		image.ASSIGN(null); // image:=NOTEXT;
		setpos(1);
	}

	public void outchar(char c) {
		if (!more())
			outimage();
		image.putchar(c);
	}

	public TEXTREF FIELD_(int w) {
		//Util.BREAK("BEGIN OutFile.FIELD("+w+") image="+image);
		if (w <= 0 || w > length())
			error("Illegal field width in output operation");
		if (pos() + w - 1 > length())
			outimage();
		TEXTREF result = image.sub(pos(), w);
		setpos(pos() + w);
		//Util.BREAK("END OutFile.FIELD("+w+") image="+image);
		//Util.BREAK("END OutFile.FIELD("+w+") result="+result);
		return (result);

	}

	public void outint(int i, int w) {
		FIELD_(w).putint(i);
	}

	public void outfix(float r, int n, int w) {
		FIELD_(w).putfix(r, n);
	}

	public void outreal(float r, int n, int w) {
		FIELD_(w).putreal(r, n);
	}

	public void outfrac(int i, int n, int w) {
		FIELD_(w).putfrac(i, n);
	}

	public void outtext(TEXTREF T) {
		// Util.BREAK("OutFile.outtext("+T+')');
		FIELD_(T.LENGTH).ASSIGN(T); // FIELD(T.length) := T;
	}

	// Constructor
	public OutFile(TEXTREF param_FILE_NAME) {
		// Parameter assignment to locals
		super(param_FILE_NAME);
		// Parameter assignment to locals
		// Class Body
		ENDFILE_ = true;
	}
}