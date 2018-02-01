package parser.statement;

/**
 * Error Statement.
 * 
 * @author �ystein Myhre Andersen
 */
public class ErrorStatement extends Statement {
	Throwable exception;

	public ErrorStatement(Throwable exception) {
		this.exception = exception;
	}

	public String toString() {
		return ("ERROR " + exception);
	}
}
