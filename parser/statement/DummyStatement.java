package parser.statement;

/**
 * Dummy Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * DummyStatement = Empty
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class DummyStatement extends Statement {
	public DummyStatement() {
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		// No Checking ?
		SET_SEMANTICS_CHECKED();
	}

	public void doCoding(String indent) { /* No Coding */
		ASSERT_SEMANTICS_CHECKED(this);
	}

	public void print(String indent, String tail) {
	}

	public String toString() {
		return ("DUMMY");
	}

}
