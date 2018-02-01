package parser.statement;

import common.Identifier;

/**
 * Label Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * LabelStatement = Label  :
 * 
 * 	Label = Identifier
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class LabelStatement extends Statement {
	private Identifier label;

	public LabelStatement(Identifier label) {
		this.label = label;
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		/* No Checking */
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return (toString()); // TODO: ???
	}

	public String toString() {
		return ("" + label + ':');
	}

}
