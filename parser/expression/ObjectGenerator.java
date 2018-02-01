package parser.expression;

import java.util.Iterator;
import java.util.Vector;

import common.Token;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Kind;
import common.Type;
import compiler.NonTerminal;
import parser.declaration.ClassDeclaration;
import parser.declaration.Declaration;
import parser.declaration.Specification;
import parser.expression.Expression;

/**
 * ObjectGenerator i.e. new Object expression.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ObjectGenerator = NEW ClassIdentifier [ ( ActualParameterList ) ]
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ObjectGenerator extends Expression {
	private Identifier classIdentifier;
	private Vector<Expression> params;
	private Vector<NonTerminal> checkedParams = new Vector<NonTerminal>();

	public ObjectGenerator(Identifier classIdentifier, Vector<Expression> params) {
		this.classIdentifier = classIdentifier;
		this.params = params;
		if (DEBUG)
			Util.log("NEW ObjectGenerator: " + toString());
	}

	public static Expression parse() {
		if (DEBUG)
			Util.log("Parse ObjectGenerator, current=" + getCurrentToken());
		Identifier classIdentifier = expectIdentifier();
		Vector<Expression> params = new Vector<Expression>();
		if (accept(KeyWord.BEGPAR)) {
			if (!accept(KeyWord.ENDPAR)) {
				do {
					params.add(parseSimpleExpression());
				} while (accept(KeyWord.COMMA));
				expect(KeyWord.ENDPAR);
			}
		}
		Expression expr = new ObjectGenerator(classIdentifier, params);
		while (true) {
			if (accept(KeyWord.DOT)) {
				expr = new BinaryOperation(expr, new Token(KeyWord.DOT),
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
			Util.log("BEGIN ObjectGenerator(" + classIdentifier
					+ ").doChecking - Current Scope Chain: "
					+ currentScope.edScopeChain());
		Declaration match = currentScope.findDefinition(classIdentifier);
		if (match == null)
			error("Undefined variable: " + classIdentifier);
		// String name=match.getClass().getName();
		// Debug.BREAK("ObjectGenerator.doChecking("+classIdentifier+") match is "+name);
		if (match instanceof ClassDeclaration) // Declared Procedure
		{
			ClassDeclaration cls = (ClassDeclaration) match;
			this.type = cls.getType();
			// Debug.BREAK("ObjectGenerator.doChecking: type="+this.type);

			// Check parameters
			Iterator<Declaration> formalIterator = cls.parameterIterator();
			Iterator<Expression> actualIterator = params.iterator();
			while (actualIterator.hasNext()) {
				if (!formalIterator.hasNext())
					error("Wrong number of parameters to " + cls);
				Declaration formalParameter = formalIterator.next();
				Type formalType = formalParameter.getType();
				if (DEBUG)
					Util.log("Formal Parameter: " + formalParameter
							+ ", Formal Type=" + formalType);
				Expression actualParameter = actualIterator.next();
				actualParameter.doChecking(formalType);
				// Util.BREAK("ObjectGenerator.doChecking Parameter  "+formalParameter+" := "+actualParameter);

				Type actualType = actualParameter.getType();
				if (DEBUG)
					Util.log("Actual Parameter: " + actualType + " "
							+ actualParameter + ", Actual Type=" + actualType);
				checkedParams.add(TypeConversion.testAndCreate(formalType,
						actualParameter));
			}
			if (formalIterator.hasNext())
				error("Wrong number of parameters to " + cls);
		} else if (match instanceof Specification) // Parameter Procedure
		{
			Specification spec = (Specification) match;
			this.type = spec.getType();
			Kind kind = spec.getKind();
			if (kind != Kind.Procedure)
				error("ObjectGenerator(" + classIdentifier
						+ ") is matched to a parameter " + kind);
			Util.WARNING("ObjectGenerator(" + classIdentifier
					+ ") - Parameter Checking is postponed to Runtime");
		}
		if (DEBUG)
			Util.log("END ObjectGenerator(" + classIdentifier
					+ ").doChecking: type=" + type);
		// Debug.BREAK("END ObjectGenerator");
		SET_SEMANTICS_CHECKED();
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		StringBuilder s = new StringBuilder();
		s.append("new ").append(classIdentifier).append('(');
		for (Iterator<NonTerminal> it = checkedParams.iterator(); it.hasNext();) {
			NonTerminal par = it.next();
			s.append(par.toJavaCode());
			if (it.hasNext())
				s.append(',');
		}
		s.append(')');
		return (s.toString());
	}

	public String toString() {
		return (("NEW " + classIdentifier + checkedParams).replace('[', '(')
				.replace(']', ')'));
	}

}
