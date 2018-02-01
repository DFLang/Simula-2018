package parser.statement;

import parser.expression.Expression;

/**
 * Goto Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  GotoStatement = GOTO DesignationalExpression
 *                | GO TO DesignationalExpression
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class GotoStatement extends Statement {
	private Expression label;

	public GotoStatement() {
		label = Expression.parseExpression();
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
		return ("GOTO " + label);
	}

}
