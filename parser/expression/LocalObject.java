package parser.expression;

import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;
import compiler.NonTerminal;
import parser.declaration.ClassDeclaration;
import parser.declaration.Declaration;
import parser.expression.Expression;

/**
 * LocalObject i.e. This class expression.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * LocalObject = THIS ClassIdentifier
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class LocalObject extends Expression {
	private Identifier classIdentifier;

	private LocalObject(Identifier classIdentifier) {
		this.classIdentifier = classIdentifier;
		if (DEBUG)
			Util.log("NEW ThisObjectExpression: " + toString());
	}

	public static Expression acceptThisIdentifier() {
		if (DEBUG)
			Util.log("Parse ThisObjectExpression, current=" + getCurrentToken());
		Identifier classIdentifier = expectIdentifier();
		Expression expr = new LocalObject(classIdentifier);
		while (true) {
			if (accept(KeyWord.DOT)) {
				expr = new BinaryOperation(expr, getPrevToken(),
						parseValuePrimary());
			} else
				return (expr);
		}
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		if (DEBUG)
			Util.log("BEGIN LocalObject(" + toString()
					+ ").doChecking - Current Scope Chain: "
					+ currentScope.edScopeChain());
		Declaration decl = NonTerminal.currentScope
				.findDefinition(classIdentifier);
		if (decl instanceof ClassDeclaration)
			this.type = new Type(classIdentifier);
		else
			error("LocalObject(" + this + ") " + classIdentifier
					+ " is not a class");
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		return ("(" + classIdentifier + ")this");
	}

	public String toString() {
		return ("THIS " + classIdentifier);
	}

}
