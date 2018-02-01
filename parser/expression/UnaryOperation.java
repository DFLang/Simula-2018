package parser.expression;

import common.KeyWord;
import common.Token;
import common.Type;
import common.Util;

/**
 * Unary Operation.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   UnaryOperation =  operation  Expression
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class UnaryOperation extends Expression
{ // static boolean DEBUG=true;//false; //true; 
  private Token oprator;
  private Expression operand;
  
  public UnaryOperation(Token oprator,Expression operand)
  { this.oprator=oprator; this.operand=operand;
	if(DEBUG) Util.log("NEW UnaryOperation: "+toString());
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
	if(DEBUG) Util.log("BEGIN BinaryOperation"+toString()+".doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	//Util.BREAK("BEGIN BinaryOperation"+toString()+".doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	operand.doChecking();
	if(oprator.getKeyWord()==KeyWord.NOT) this.type=Type.Boolean;
	else if(oprator.getKeyWord()==KeyWord.PLUS || oprator.getKeyWord()==KeyWord.MINUS)
	{ this.type=operand.getType(); }
    SET_SEMANTICS_CHECKED();
  }
  
  public String toJavaCode()// { return(toString()); }
  { ASSERT_SEMANTICS_CHECKED(this);
    return("("+oprator.toJavaCode()+' '+operand.toJavaCode()+")"); }
  
  public String toString()
  { return("("+oprator+' '+operand+")"); }

}
