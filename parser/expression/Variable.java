package parser.expression;

import parser.declaration.ConnectedDeclaration;
import parser.declaration.Declaration;
import parser.declaration.ProcedureDeclaration;
import parser.declaration.Specification;
import parser.declaration.Specification.ParameterKind;
import parser.declaration.StandardProcedure;
import common.Const;
import common.Mode;
import common.NameTable;
import common.Type;
import common.Util;
import common.Identifier;
import common.KeyWord;
import compiler.NonTerminal;

/**
 * Variable.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * Variable = SimpleObjectExpression . Variable | SimpleVariable | SubscriptedVariable
 * 
 * 	SimpleObjectExpression = NONE | Variable | FunctionDesignator | ObjectGenerator
 *                             | LocalObject | QualifiedObject | ( ObjectExpression)
 *                             
 * 	SimpleVariable = Identifier
 * 
 * 	SubscriptedVariable = ArrayIdentifier [ SubscriptList ]
 * 			SubscriptList ::= ArithmeticExpression { , ArithmeticExpression }
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class Variable extends Expression {
	// static boolean DEBUG=true;//false; //true;
	private Identifier identifier;
	protected Declaration semantic;
	private boolean remotelyAccessed;
	

	public Identifier getIdentifier() {
		return (identifier);
	}

	public void setRemotelyAccessed(Declaration semantic) {
		this.semantic = semantic;
		remotelyAccessed=true;
		this.doChecking();	
		SET_SEMANTICS_CHECKED(); // Checked as remote attribute
	}

	public Declaration getSemantic() {
		if (semantic == null)
			semantic = currentScope.findDefinition(identifier);
		return (semantic);
	}

	public Variable(Identifier identifier) {
		this.identifier = identifier;
		if (DEBUG)
			Util.log("NEW Variable: " + identifier);
		// Util.BREAK("NEW Variable: "+this);
	}

	public Variable(String ident) {
		this(NameTable.defineName(ident));
	}

	public static Expression parse(Identifier ident) {
		if (DEBUG)
			Util.log("Parse Variable, current=" + getCurrentToken() + ", prev="
					+ getPrevToken());
		Expression var = new Variable(ident);
		while (true) {
			if (accept(KeyWord.DOT))
				// var = new RemoteAccess(var, parseValuePrimary());
				var = new BinaryOperation(var, getPrevToken(),
						parseValuePrimary());
			else if (accept(KeyWord.QUA))
				// var = new QualifiedObject(var);
				var = new BinaryOperation(var, getPrevToken(),
						parseValuePrimary());
			else if (accept(KeyWord.BEGBRACKET))
				var = new SubscriptedVariable(((Variable) var).getIdentifier());
			else if (accept(KeyWord.BEGPAR))
				var = new FunctionDesignator(((Variable) var).getIdentifier());
			else
				return (var);
		}
	}

	public void doChecking() {
		if (IS_SEMANTICS_CHECKED())
			return;
		Util.setLine(getLineNumber());
		// Util.BREAK("BEGIN Variable("+identifier+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
		this.type = getSemantic().getType();
		Util.log("Variable(" + identifier + ").doChecking: type=" + type
				+ ", Declared as: " + semantic.getEnclosureName() + '.'
				+ semantic);
		SET_SEMANTICS_CHECKED();
	}

	public static boolean isProcedure(Declaration semantic) {
		return (semantic instanceof ProcedureDeclaration);
	}

	public static boolean isProcedureWithoutParameters(Declaration semantic) {
		if (isProcedure(semantic))
			return (((ProcedureDeclaration) semantic).hasNoParameter());
		else {
			// Util.NOT_IMPLEMENTED("Variable.hasNoParameter");
			return (false);
		}
	}

	public Mode getMode() {
		Declaration semantic = this.getSemantic();
		if (semantic instanceof Specification) {
			Specification specification = (Specification) semantic;
			return (specification.getMode());
		}
		return (null);
	}

	public ParameterKind getParameterKind() {
		Declaration semantic = this.getSemantic();
		if (semantic instanceof Specification) {
			Specification specification = (Specification) semantic;
			return (specification.getParameterKind());
		}
		return (null);
	}

	// Generate code for putting an value(expression) into this Variable
	public String put(NonTerminal rightPart) {
		String result = this.identifier.toString();
		// Util.BREAK("Variable("+result+").put("+rightPart+")");
		ASSERT_SEMANTICS_CHECKED(this);
		if (getMode() == Mode.name) {
			Specification.ParameterKind parameterKind = getParameterKind();
			// Util.BREAK("Variable("+result+").put("+rightPart+"), ParameterKind="+parameterKind);
			if (parameterKind == ParameterKind.ValueType)
				result = result + "." + Const.NAME_PUT + "("
						+ rightPart.get(this.getType()) + ')';
			else if (parameterKind == ParameterKind.SimpleText)
				result = result + "." + Const.NAME_PUT + "("
						+ rightPart.get(this.getType()) + ')';
			else if (parameterKind == ParameterKind.ObjectReference)
				result = result + "." + Const.NAME_PUT + "("
						+ rightPart.get(this.getType()) + ')';
			else { // TODO: Hva hvis andre Parameter Kinds by Name
				error("Parameter " + this
						+ " by Name is not Supported - Rewrite Program");
				result = result + '=' + rightPart.get(this.getType());
			}
		} else
			result = result + '=' + rightPart.get(this.getType());
		return (result);
	}

	// Generate code for getting the value of this Variable
	public String get(Type cast) {
		String result = this.identifier.toString();
		// Util.BREAK("Variable("+result+").get("+cast+") Semantic="+semantic);
		ASSERT_SEMANTICS_CHECKED(this);
		if (getMode() == Mode.name) {
			Specification.ParameterKind parameterKind = getParameterKind();
			if (parameterKind == ParameterKind.ValueType)
				result = result + "." + Const.NAME_GET + "()";
			else if (parameterKind == ParameterKind.SimpleText)
				result = result + "." + Const.NAME_GET + "()";
			else if (parameterKind == ParameterKind.ObjectReference)
				result = result + "." + Const.NAME_GET + "()";
			else { // TODO: Hva hvis andre Parameter Kinds by Name
				error("Parameter " + this
						+ " by Name is not Supported - Rewrite Program");
			}
		} else if (semantic instanceof ProcedureDeclaration)
			result = result + "()";
		// else if(semantic instanceof StandardProcedure) result=result+"()";
		if (cast != null && !cast.equals(this.type))
			result = "((" + cast.toJavaType() + ")(" + result + "))";
		return (result);
	}

	public String toJavaCode() {
		ASSERT_SEMANTICS_CHECKED(this);
		// Util.BREAK("Variable.toJavaCode: ident="+identifier+", decl="+semantic+", decl.enc="+semantic.getEnclosureName());
		if (semantic instanceof ConnectedDeclaration) {
			ConnectedDeclaration conn = (ConnectedDeclaration) semantic;
			String result = conn.edConnectedVariable() + '.' + identifier;
			if (isProcedureWithoutParameters(conn.getDeclaration()))
				return (result + "()");
			else
				return (result);
		} else
			return (get(null));
	}

	public String toString() {
		return ("" + identifier);
	}

}
