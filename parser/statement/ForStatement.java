package parser.statement;

import java.util.Iterator;
import java.util.Vector;

import common.Identifier;
import common.Mode;
import common.Token;
import common.Util;
import common.KeyWord;
import common.Type;
import parser.declaration.Declaration;
import parser.declaration.Specification;
import parser.expression.BinaryOperation;
import parser.expression.Expression;
import parser.expression.Variable;


/**
 * For Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  ForStatement = FOR Variable :- ReferenceList DO Statement
 *               | FOR Variable := ValueList DO Statement
 *               
 * 		ReferenceList = ReferenceListElement { , ReferenceListElement }
 * 			ReferenceListElement = ReferenceExpression [ WHILE BooleanExpression ]
 * 
 * 		ValueList = ValueListElement { , ValueListElement }
 * 			ValueListElement = ValueExpression [ WHILE BooleanExpression ]
 *                           | ArithmeticExpression STEP ArithmeticExpression UNTIL ArithmeticExpression
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ForStatement extends Statement
{ // static boolean DEBUG=true;//false; //true; 
  Variable variable;
//  Identifier identifier;
  Token assignmentOperator; //  :=   eller   :-
  Vector<ForListElement> forList=new Vector<ForListElement>();
  Statement doStatement;
  
  public ForStatement()
  { if(DEBUG) Util.log("Parse ForStatement, current="+getCurrentToken()+", prev="+getPrevToken());
    variable=new Variable(expectIdentifier());
    expect(KeyWord.ASSIGN); assignmentOperator=getPrevToken();
    do { forList.add(parseForListElement()); } while(accept(KeyWord.COMMA));
    expect(KeyWord.DO); doStatement=Statement.doParse();
    if(doStatement==null) { error("No statement following DO in For statement"); doStatement=new DummyStatement(); }
    if(DEBUG) Util.log("NEW ForStatement: "+toString());
  }
    
  private ForListElement parseForListElement()
  { if(DEBUG) Util.log("Parse ForListElement, current="+getCurrentToken()+", prev="+getPrevToken());
	Expression expr1=Expression.parseExpression();
	if(accept(KeyWord.WHILE)) return(new WhileElement(expr1,Expression.parseExpression()));
	if(accept(KeyWord.STEP))
	{ Expression expr2=Expression.parseExpression();
	  expect(KeyWord.UNTIL);
	  return(new StepUntilElement(expr1,expr2,Expression.parseExpression()));
	} else return(new ForListElement(expr1));	  
  }
 
  
  private class ForListElement
  { Expression expr1;
    Type type;
	public ForListElement(Expression expr1)
	{ this.expr1=expr1;
	  if(DEBUG) Util.log("NEW ForListElement: "+toString());
	}  
	public void doChecking()
	{ Util.log("BEGIN ForListElement("+this+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	  expr1.doChecking(); this.type=expr1.getType();
	  // Type checking  //  TODO:
	  //Debug.BREAK("END ForListElement("+this+").doChecking: type="+type);
	}
	public String edCode()
	{ Util.NOT_IMPLEMENTED("ForStatement: Single expression for list element");
      return(""+expr1.toJavaCode());
	}
	public String toString() { return(""+expr1); }
  }
  
  private void sampleForStatements()
  { int i,k=8;
	for(i=34,i=37;i<78;) k=k+1;  // For i:=34,37 While i<78 do k=k+1;
	for(i=34;i<78;)      k=k+1;  // For i:=34 While i<78 do k=k+1;
	for(i=34;i<=78;i=i+4) k=k+1;  // For i:=34 Step 4 until 78 do k=k+1;
  }
  private class WhileElement extends ForListElement
  { Expression expr2;
	public WhileElement(Expression expr1,Expression expr2) { super(expr1); this.expr2=expr2; }  
	public void doChecking()
	{ Util.log("BEGIN WhileElement("+this+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	  expr1.doChecking(); this.type=expr1.getType();
	  expr2.doChecking(); if(expr2.getType()!=Type.Boolean) error("While "+expr2+" is not of type Boolean");
	  // Type checking  //  TODO:
	  //Debug.BREAK("END WhileElement("+this+").doChecking: type="+type);
	}
	public String edCode()
	{ return(""+expr1.toJavaCode()+';'+expr2.toJavaCode()+';');
		
	}
	public String toString() { return(""+expr1+" while "+expr2); }
  }
  
  private class StepUntilElement extends ForListElement
  { Expression expr2;
    Expression expr3;
	public StepUntilElement(Expression expr1,Expression expr2,Expression expr3)
	{ super(expr1); this.expr2=expr2; this.expr3=expr3; }  
	public void doChecking()
	{ //Util.log("BEGIN StepUntilElement("+this+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	  expr1.doChecking();
	  expr2.doChecking();
	  expr3.doChecking();
	  this.type=Type.arithmeticTypeConversion(expr1.getType(),expr2.getType());
	  // Type checking: expr3 compatible   //  TODO:
	  //Debug.BREAK("END StepUntilElement("+this+").doChecking: type="+type);
	}
	public String edCode()
	{ // for(i=34;i<=78;i=i+4) k=k+1;  // For i:=34 Step 4 until 78 do k=k+1;
      String s=""+expr1.toJavaCode()+';'
    		     +variable+"<="+expr3.toJavaCode()+';'
    		     +variable+'='+variable+'+'+expr2.toJavaCode();
      return(s);
	}
	public String toString() { return(""+expr1+" step "+expr2+ " until "+expr3); }
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
    Util.log("BEGIN ForStatement("+variable+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
    variable.doChecking();
    type=variable.getType(); // Type of control variable
    if(variable.getMode()==Mode.name)
    	  error("For-Statement's Controled Variable("+variable+") cannot be a formal parameter called by Name");
    Iterator<ForListElement> iterator=forList.iterator();
    while(iterator.hasNext()) { iterator.next().doChecking(); }
    doStatement.doChecking();
	//Debug.BREAK("END ForStatement("+variable+").doChecking: type="+type);
	SET_SEMANTICS_CHECKED();
  }
  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    if(forList.size()>1) Util.NOT_IMPLEMENTED("ForStatement: Multiple("+forList.size()+") for list elements - ");
    ForListElement elt=forList.firstElement();
    Util.code(indent+"for("+variable+"="+elt.edCode()+") {");
    doStatement.doCoding(indent+"   ");
    Util.code(indent+'}');
  }  
  public void print(String indent)
  { String fl=forList.toString().replace('[',' ').replace(']',' ');
    System.out.println(indent+"FOR "+variable+" "+assignmentOperator+fl+"DO");
    if(doStatement!=null) doStatement.print(indent,";"); }
  
  public String toString()
  { String fl=forList.toString().replace('[',' ').replace(']',' ');
	return("FOR "+variable+" "+assignmentOperator+fl+" DO "+doStatement);
  }
}
