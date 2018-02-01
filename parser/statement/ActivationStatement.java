package parser.statement;

import common.Token;
import common.Util;
import common.KeyWord;
import parser.expression.Expression;

/**
 * Activation Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ActivationStatement = Activator  ObjectExpression [ SchedulingPart ]
 * 
 *		Activator = ACTIVATE | REACTIVATE
 *
 *		SchedulingPart = AT ArithmeticExpression [ PRIOR ]
 *                     | DELAY ArithmeticExpression [ PRIOR ]
 *                     | BEFORE ObjectExpression
 *                     | AFTER ObjectExpression
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ActivationStatement extends Statement
{ //  static boolean DEBUG=true;//false; //true; 
  Token activator; 
  Expression object;
  Token sched;
  Expression time=null;
  Boolean prior=false;

  public ActivationStatement()
  { activator=getPrevToken();
	if(DEBUG) Util.log("Parse ActivationStatement, current="+getCurrentToken()+", prev="+getPrevToken());
    object=Expression.parseExpression();
    sched=getCurrentToken();
    if((accept(KeyWord.AT)||accept(KeyWord.DELAY)||accept(KeyWord.BEFORE)||accept(KeyWord.AFTER)))
    { time=Expression.parseExpression();
      if(accept(KeyWord.PRIOR)) prior=true;
    }
    if(DEBUG) Util.log("END NEW ActivationStatement: "+toString());
  }
		  
  public String toString()
  { String pri=""; if(prior) pri=" PRIOR";
	  return(""+activator+' '+object+' '+sched+' '+time+pri); }

}
