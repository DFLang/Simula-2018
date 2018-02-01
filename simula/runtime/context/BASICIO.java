package simula.runtime.context;

public class BASICIO extends ENVIRONMENT {
	static final int INPUT_LINELENGTH_ = 80;
	static final int OUTPUT_LINELENGTH_ = 132;
	// Declare parameters as attributes
	// Declare locals as attributes
	InFile SYSIN_;

	public InFile sysin() {
		return (SYSIN_);
	}

	public PrintFile SYSOUT_;

	public PrintFile sysout() {
		return (SYSOUT_);
	}

	// Constructor
	public BASICIO() {
		// Parameter assignment to locals
		// Class Body
		SYSIN_ = new InFile(new TEXTREF("SYSIN"));
		SYSOUT_ = new PrintFile(new TEXTREF("SYSOUT"));
		SYSIN_.open(blanks(INPUT_LINELENGTH_));
		SYSOUT_.open(blanks(OUTPUT_LINELENGTH_));
	}

	protected void finalize() throws Throwable {
		SYSIN_.close();
		SYSOUT_.close();
	}

}