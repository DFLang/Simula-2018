package parser.declaration;

import common.Type;
import parser.statement.ConnectionBlock;

/**
 * Wrapper to hold a semantic match of a declaration found through a connection
 * block.
 * 
 * @author Øystein
 *
 */
public class ConnectedDeclaration extends Declaration {
	Declaration declaration;
	ConnectionBlock connectionBlock;
	public Declaration getDeclaration() { return(declaration); }

	public ConnectedDeclaration(Declaration declaration,ConnectionBlock connectionBlock) {
		this.declaration = declaration;
		this.connectionBlock = connectionBlock;
		//Util.BREAK("new ConnectedDeclaration: decl="+this.declaration+", connectionBlock="+this.connectionBlock);
	}

	public String edConnectedVariable() {
		String cast = connectionBlock.getClassDeclaration().getIdentifier().toString();
		return ("((" + cast + ')'
				+ connectionBlock.getObjectExpression().toJavaCode() + ')');
	}

	public Type getType() {
		return (declaration.getType());
	}

	public String toString() {
		return ("CONNECTED " + declaration.toString());
	}
}
