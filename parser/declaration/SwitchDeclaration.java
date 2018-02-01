package parser.declaration;

import java.util.Vector;

import common.Util;
import common.KeyWord;

import parser.expression.Expression;

/**
 * Switch Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  SwitchDeclaration
 *     = SWITCH SwitchIdentifier := DesignationalExpression { , DesignationalExpression }
 * </pre>
 *
 * @see parser.expression.ConditionalExpression
 * @author Øystein Myhre Andersen
 */
public class SwitchDeclaration extends Declaration
{ //static boolean DEBUG = true;// false; //true;
//  Token switchIdentifier;
  Vector<Expression> switchList=new Vector<Expression>();

  public SwitchDeclaration()
  { if(DEBUG) Util.log("Parse SwitchDeclaration, current="+getCurrentToken()+", prev="+getPrevToken());
	identifier=expectIdentifier();
	expect(KeyWord.ASSIGN);
	do { switchList.add(Expression.parseExpression()); } while( accept(KeyWord.COMMA) );      
	if(DEBUG) Util.log("Parse SwitchDeclaration(3), switchList="+switchList+", current="+getCurrentToken()+", prev="+getPrevToken());
  }

  public String toString()
  {	return ("SWITCH " + identifier + " := " + switchList); }
}
