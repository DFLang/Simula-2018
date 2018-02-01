package parser.declaration;

import common.Util;
import common.Identifier;
import common.Type;

/**
 * Type Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * TypeDeclaration = Type IdentifierList
 * 
 *   	Type ::= BOOLEAN | CHARACTER | INTEGER | REAL | REF(ClassIdentifier) | TEXT
 * </pre>
 * 
 * @see Type
 * @author Øystein Myhre Andersen
 */
public class TypeDeclaration extends Declaration {
	// Type type;
	// Category category; // Procedure Local, Class Attribute, ...
	// public enum Category
	// { ProcedureParameter, ClassParameter, ProcedureLocal, ClassAttribute
	//
	// }

	public TypeDeclaration(Type type, Identifier identifier) {
		this.type = type;
		this.identifier = identifier;
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
		Scope scope = getCurrentScope().getEnclosure();
		// Util.BREAK("TypeDeclaration.toJavaCode: scope="+scope.getClass().getName());
		String modifier = "";
		// if(scope instanceof ProcedureDeclaration) modifier="local ";
		// else
		if (scope instanceof ClassDeclaration)
			modifier = "public ";
		return (modifier + type.toJavaType() + ' ' + identifier + ';');
		// return(type.toJavaType() + ' ' + identifier+';');
	}

	public String toString() {
		String s = type.toString();
		return (s + ' ' + identifier);
	}
}
