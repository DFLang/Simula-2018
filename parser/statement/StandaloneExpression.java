package parser.statement;

import common.Util;
import parser.expression.Expression;

/**
 * Standalone Expression Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   Statement = Expression
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class StandaloneExpression extends Statement {
	// static boolean DEBUG=true;//false; //true;
	private Expression expression;

	public StandaloneExpression(Expression expression) {
		this.expression = expression;
		if (DEBUG)
			Util.log("NEW StandaloneExpression: " + toString());
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		if (DEBUG)
			Util.BREAK("BEGIN StandaloneExpression(" + expression
					+ ").doChecking - Current Scope Chain: "
					+ currentScope.edScopeChain());
		expression.doChecking();
		if (DEBUG)
			Util.log("END StandaloneExpression(" + expression
					+ ").doChecking: type=" + type);
		// Debug.BREAK("END StandaloneExpression");
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return (expression.toJavaCode());
	}

	public void print(String indent, String tail) {
		expression.print(indent, tail);
	}

	public String toString() {
		return ("STANDALONE " + expression);
	}

}
