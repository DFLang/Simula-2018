package parser.declaration;

import common.Token;
import common.Util;
import common.KeyWord;
import common.Type;

/**
 * External Declaration.
 * <p>
 * An external declaration is a substitute for a complete introduction of the
 * corresponding source module referred to, including its external head. In the
 * case where multiple but identical external declarations occur as a
 * consequence of this rule, this declaration will be incorporated only once.
 * 
 * <pre>
 * 
 * Syntax:
 *   external-declaration = EXTERNAL [ type ] PROCEDURE external-list
 *                        | EXTERNAL CLASS external-list
 *    external list = external-item { , external-item }
 * 	  external-item = identifier [ "=" external-identifier ]
 *          external-identifier = text-constant   // E.g  a file-name
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ExternalDeclaration extends Declaration {
	static boolean DEBUG = true;// false; //true;
	/**
	 * The optional type of an external procedure. For external classes this
	 * field has no meaning.
	 */
	private Type type;
	/** Indicates Procedure or Class. */
	private Token kind;
	/** The Class or Procedure identifier */
//	Token identifier;
	/** The external identification. Normally a file-name. */
	Token externalIdentifier;

	/**
	 * Create and parse a new External Declaration.
	 *
	 */
	public ExternalDeclaration() {
		type = acceptType();
		kind = getCurrentToken();
		if (!(accept(KeyWord.CLASS) || accept(KeyWord.PROCEDURE)))
			error("parseExternalDeclaration: Expecting CLASS or PROCEDURE");
		identifier = expectIdentifier();
		externalIdentifier = null;
		if (accept(KeyWord.EQ))	{
			externalIdentifier = getCurrentToken();
			expect(KeyWord.TEXTKONST);
		}
		if (DEBUG)
			Util.log("END NEW ExternalDeclaration: " + toString());
	}

	/**
	 * Redefinition of the toString method.
	 * 
	 * @return The string representation of this External Declaration.
	 */
	public String toString() {
		return ("EXTERNAL " + type + ' ' + kind + ' ' + identifier + " = "
				+ externalIdentifier);
	}
}
