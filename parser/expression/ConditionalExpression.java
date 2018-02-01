package parser.expression;

import common.Util;
import common.Type;

/**
 * Conditional Expression.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   ConditionalExpression
 *       = IF BooleanExpression THEN SimpleExpression ELSE Expression
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ConditionalExpression extends Expression {
	Expression condition;
	Expression thenExpression;
	Expression elseExpression;

	public ConditionalExpression(Type type, Expression condition,
			Expression thenExpression, Expression elseExpression) {
		this.condition = condition;
		this.thenExpression = thenExpression;
		this.elseExpression = elseExpression;
		if (DEBUG)
			Util.log("NEW ConditionalExpression: " + toString());
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		error("ConditionalExpression in this Context is not Supported - Rewrite program");
	}

	public void doChecking(Type expectedType) {
		Util.setLine(getLineNumber());
		// Util.BREAK("ConditionalExpression.doChecking("+expectedType+"): Condition: "+condition);
		// Util.BREAK("ConditionalExpression.doChecking("+expectedType+"): Then: "+thenExpression);
		// Util.BREAK("ConditionalExpression.doChecking("+expectedType+"): Else: "+elseExpression);
		condition.doChecking(Type.Boolean);
		thenExpression.doChecking(expectedType);
		elseExpression.doChecking(expectedType);
		Type cType = condition.getType();
		if (cType != Type.Boolean)
			error("ConditionalExpression: Condition is not a boolean (rather "
					+ cType + ")");
		thenExpression = (Expression) TypeConversion.testAndCreate(
				expectedType, thenExpression);
		elseExpression = (Expression) TypeConversion.testAndCreate(
				expectedType, elseExpression);
		this.type = expectedType;
		// Util.BREAK("END ConditionalExpression" + toString() +
		// ".doChecking - Result type=" + this.type);
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return ("(" + condition.get(Type.Boolean) + ")?("
				+ thenExpression.get(this.type) + "):("
				+ elseExpression.get(this.type) + ')');
	}

	public String toString() {
		return ("(IF " + condition + " THEN " + thenExpression + " ELSE "
				+ elseExpression + ')');
	}
}
