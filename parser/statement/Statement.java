package parser.statement;

import common.Util;
import common.Identifier;
import common.KeyWord;
import compiler.NonTerminal;
import parser.expression.Expression;
import parser.expression.ObjectGenerator;
import parser.expression.LocalObject;
import parser.expression.Variable;

/**
 * Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * Statement = LabelList Statement
 *           | ConditionalStatement
 *           | ConnectionStatement | <repetition statement> | <unconditional statement>
 *   	<label list> ::= {<label list>}<label>:
 *   		<label> ::= <identifier>
 *		<repetition statement> ::=<for statement> | <while statement>
 *    	<unconditional statement> ::= <basic statement> | {<block prefix>}<block> | {<block prefix>}<compound statement>
 *    	<basic statement> ::= <activation statement> | <assignment statement> | <dummy statement>
 *                            | <goto statement> | <object generator> | <procedure statement>
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public abstract class Statement extends NonTerminal
{ 
  public static Statement doParse()
  { if(DEBUG) Util.log("Parse Statement, current="+getCurrentToken()+", prev="+getPrevToken());
    Identifier ident=acceptIdentifier();
    if(ident!=null)
    { if(accept(KeyWord.COLON)) return(new LabelStatement(ident));
      if(DEBUG) Util.log("Parse Statement(2), current="+getCurrentToken()+", prev="+getPrevToken());
      Expression variable=Variable.parse(ident);
      if(accept(KeyWord.ASSIGN)) return(new AssignmentStatement(variable));
      else if(accept(KeyWord.BEGIN)) return(new Block(variable)); 
      else return(new StandaloneExpression(variable));
    }
    if(accept(KeyWord.BEGIN)) return(new Block(null)); 
    if(accept(KeyWord.IF)) return(new ConditionalStatement());
    if(accept(KeyWord.GOTO)) return(new GotoStatement());
    if(accept(KeyWord.GO))
    { if(accept(KeyWord.TO)) return(new GotoStatement());
      error("Missing 'TO' after 'GO'");
    }
    if(accept(KeyWord.NEW))
    { if(DEBUG) Util.log("Parse Statement(4), current="+getCurrentToken()+", prev="+getPrevToken());
      Expression variable=ObjectGenerator.parse();
      if(accept(KeyWord.ASSIGN)) return(new AssignmentStatement(variable));
      else return(new StandaloneExpression(variable));
    }
    if(accept(KeyWord.THIS))
    { if(DEBUG) Util.log("Parse Statement(4), current="+getCurrentToken()+", prev="+getPrevToken());
      Expression variable=LocalObject.acceptThisIdentifier();
      if(accept(KeyWord.ASSIGN)) return(new AssignmentStatement(variable));
      else return(new StandaloneExpression(variable));
    }
    if(accept(KeyWord.FOR)) return(new ForStatement());
    if(accept(KeyWord.WHILE)) return(new WhileStatement());
    if(accept(KeyWord.INSPECT)) return(new ConnectionStatement());
    if(accept(KeyWord.ACTIVATE)) return(new ActivationStatement());
    if(accept(KeyWord.REACTIVATE)) return(new ActivationStatement());
    if(accept(KeyWord.SEMICOLON)) return(new DummyStatement()); // Dummy Statement
    return(null);
  }

  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
	Util.code(indent+toJavaCode()+';');
  }

}
