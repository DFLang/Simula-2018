package simula.runtime.context;

import common.Util;

public class PrintFile extends OutFile {
	// Declare parameters as attributes
	// Declare locals as attributes
	int LINES_PER_PAGE_;
	int SPACING_;
	int LINE_;

	public int line() {
		return (LINE_);
	}

	public void lines_per_page(int n) {
		LINES_PER_PAGE_ = n;
	}

	public void spacing(int n) {
		SPACING_ = n;
	}

	public void eject(int n) {
		if (!OPEN_)
			error("File is not opened");
		if (n > LINES_PER_PAGE_)
			n = 1;
		LINE_ = n;
	}

	public void open(TEXTREF IMAGE_) {
		if (OPEN_)
			error("File is already opened");
		OPEN_ = true;
		image = IMAGE_;
		ENDFILE_ = false;
		IMAGE_.ASSIGN(null); // image := NOTEXT;
		setpos(1);
		eject(1);
	}

	public void close() {
		OPEN_ = false;
		ENDFILE_ = true;
		image = null; // image :- NOTEXT;
		SPACING_ = 1;
		eject(LINES_PER_PAGE_);
		LINES_PER_PAGE_ = 0;
		LINE_ = 0;
	}

	public void outimage() {
        //Util.BREAK("PrintFile.outimage(), image="+image);
		if ((!OPEN_) | image == null)
			error("File is not ready for outimage");
		if (LINE_ > LINES_PER_PAGE_)
			eject(1);
        System.out.println(image.edText());

		LINE_ = LINE_ + SPACING_;
		image.ASSIGN(null); // image := NOTEXT;
		setpos(1);

	}

	// Constructor
	public PrintFile(TEXTREF param_FILE_NAME) {
		super(param_FILE_NAME);
		// Parameter assignment to locals
		// Class Body
		LINES_PER_PAGE_ = 132;
		SPACING_ = 1;
	}
}