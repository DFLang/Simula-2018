package parser.statement;

import common.Util;
import common.KeyWord;
import common.Type;
import parser.expression.Expression;

/**
 * Conditional Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   ConditionalStatement = Ifclause { Label : } ForStatement
 *                        | Ifclause { Label : } UnconditionalStatement  [ ELSE Statement ]
 *     Ifclause = IF BooleanExpression THEN
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ConditionalStatement extends Statement {
	// static boolean DEBUG=true;//false; //true;
	Expression condition;
	Statement thenStatement;
	Statement elseStatement = null;

	public ConditionalStatement() {
		condition = Expression.parseExpression();
		expect(KeyWord.THEN);
		thenStatement = Statement.doParse();
		if (accept(KeyWord.ELSE))
			elseStatement = Statement.doParse();
	}

	public void print(String indent, String tail) {
		StringBuilder s = new StringBuilder(indent);
		s.append("IF ").append(condition);
		System.out.println(s.toString());
		System.out.println(indent + "THEN ");
		if (elseStatement != null) {
			thenStatement.print(indent + "    ", "");
			System.out.println(indent + "ELSE ");
			elseStatement.print(indent + "     ", ";");
		} else
			thenStatement.print(indent + "    ", ";");
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		condition.doChecking(Type.Boolean);
		if (!condition.getType().equals(Type.Boolean))
			error("ConditionalStatement.doChecking: Condition is not of Type Boolean, but: "
					+ condition.getType());
		thenStatement.doChecking();
		if (elseStatement != null)
			elseStatement.doChecking();
		SET_SEMANTICS_CHECKED();
	}

	public void doCoding(String indent) {
		Util.setLine(getLineNumber());
		ASSERT_SEMANTICS_CHECKED(this);
		Util.code(indent + "if(" + condition.toJavaCode() + ") {");
		thenStatement.doCoding(indent + "   ");
		if (elseStatement != null) {
			Util.code(indent + "} else");
			elseStatement.doCoding(indent + "   ");
		} else
			Util.code(indent + "}");
	}

	public String toString() {
		return ("IF " + condition + " THEN " + thenStatement + " ELSE "
				+ elseStatement + ';');
	}
}
