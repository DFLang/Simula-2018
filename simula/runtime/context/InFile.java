package simula.runtime.context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import common.Util;

public class InFile extends FILE_ {
	// Declare parameters as attributes
	// Declare locals as attributes
	boolean ENDFILE_;
	
	InputStream inputStream;
	
	// Constructor
    public InFile(TEXTREF param_FILENAME) {
        super(param_FILENAME);
        // Parameter assignment to locals
        // Class Body
        ENDFILE_=true;
     }

	public void open(TEXTREF IMAGE_) {
		// Util.BREAK("InFile.open("+IMAGE_+')');
		if (OPEN_)
			error("File already opened");
		OPEN_ = true;
		image = IMAGE_;
		ENDFILE_ = false;
		image.ASSIGN(null); // image := NOTEXT;
		setpos(length() + 1);
		
		Util.BREAK("InFile.open: Filename="+FILENAME_);
		if(FILENAME_.edText().equalsIgnoreCase("sysin")) inputStream=System.in;
		else
		{ try
		  { inputStream = new FileInputStream(FILENAME_.edText());
			System.out.println("Available bytes from the file :"+inputStream.available());
			// read a single byte
			int b = inputStream.read();
			System.out.println("Read byte : +"+b);
		  } catch (FileNotFoundException e) { e.printStackTrace();
		  } catch (IOException e1) { e1.printStackTrace(); }
		}
	}

	public void close() {
		OPEN_ = false;
		ENDFILE_ = true;
		image = null; // image :- NOTEXT;
	}

	public boolean endfile() {
		return (ENDFILE_);
	}

	public void inimage() {
		if (ENDFILE_) {
			error("Attempt to read past EOF");
		}
		//...  inputStream.read();
		setpos(1);
	}

	public char inchar() {
		if (!more()) {
			inimage();
			if (ENDFILE_) {
				error("Attempt to read past EOF");
			}
		}
		return (image.getchar());
	}

	public boolean lastitem() {
		while(!ENDFILE_)
		{ while(more()) if(inchar()!=' ') { setpos(pos()-1); return(false); }
		  inimage();
		}
		return(true);
    }

	public int inint() {
		TEXTREF T;
		if (lastitem()) {
			error("Attempt to read past EOF");
		}
		T = image.sub(pos(), length() - pos() + 1);
		int result = T.getint();
		setpos(pos() + T.pos() - 1);
		return (result);
	}

	public float inreal() {
		TEXTREF T;
		if (lastitem()) {
			error("Attempt to read past EOF");
		}
		T = image.sub(pos(), length() - pos() + 1);
		float result = ((float) (T.getreal()));
		setpos(pos() + T.pos() - 1);
		return (result);
	}

	public int infrac() {
		TEXTREF T;
		if (lastitem()) {
			error("Attempt to read past EOF");
		}
		T = image.sub(pos(), length() - pos() + 1);
		int result = T.getfrac();
		setpos(pos() + T.pos() - 1);
		return (result);
	}

	public TEXTREF intext(int w) {
		TEXTREF T;
		int m;
		T = ENVIRONMENT.blanks(w);
		for (m = 1; m <= w; m = m + 1) {
			T.putchar(inchar());
		}
		return (T);
	}
}
