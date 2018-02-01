package parser.expression;

import common.Token;
import common.Util;
import common.Type;

/**
 * Constant.
 * <p>
 * All constants are treated by the Lexicographical Scanner.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   Constant =
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 * @see compiler.SimulaScanner
 */
public class Constant extends Expression {
	private Token token;

	public Constant(Type tpe, Token token) {
		this.type = tpe;
		this.token = token;
		if (DEBUG)
			Util.log("NEW Constant: type=" + type + ", token=" + token);
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		// TODO: ??? ???
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return (token.toJavaCode());
	}

	public String toString() {
		return ("" + token);
	}

}
