package parser.statement;

import common.Type;
import common.Util;
import common.KeyWord;
import parser.expression.Expression;

/**
 * While Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   WhileStatement = WHILE BooleanExpression DO Statement
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class WhileStatement extends Statement { // static boolean
												// DEBUG=true;//false; //true;
	Expression condition;
	Statement doStatement;

	public WhileStatement() {
		if (DEBUG)
			Util.log("Parse WhileStatement, current=" + getCurrentToken());
		condition = Expression.parseExpression();
		expect(KeyWord.DO);
		doStatement = Statement.doParse();
		if (DEBUG)
			Util.log("NEW WhileStatement: " + toString());
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		// Util.BREAK("BEGIN WhileStatement("+condition+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
		condition.doChecking(Type.Boolean);
		if (condition.getType() != Type.Boolean)
			error("While condition is not Boolean");
		doStatement.doChecking();
		SET_SEMANTICS_CHECKED();
	}

	public void doCoding(String indent) {
		Util.setLine(getLineNumber());
		ASSERT_SEMANTICS_CHECKED(this);
		Util.code(indent + "while(" + condition.toJavaCode() + ") {");
		doStatement.doCoding(indent + "   ");
		Util.code(indent + '}');
	}

	public String toString() {
		return ("WHILE " + condition + " DO " + doStatement);
	}
}
