package parser.expression;

import common.Identifier;
import common.Util;
import common.KeyWord;
import common.Type;
import compiler.NonTerminal;


/**
 * Expression.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  Expression = DesignationalExpression | ReferenceExpression | ValueExpression
 *  
 * 		DesignationalExpression = SimpleDesignationalExpression
 *                              | Ifclause SimpleDesignationalExpression ELSE DesignationalExpression
 *			SimpleDesignationalExpression = Label | SwitchDesignator | "(" DesignationalExpression ")"
 *				SwitchDesignator = SwitchIdentifier [ ArithmeticExpression ]
 *
 *		ReferenceExpression = ObjectExpression | TextExpression
 *			ObjectExpression = SimpleObjectExpression
 *                           | Ifclause SimpleObjectExpression ELSE ObjectExpression
 *				SimpleObjectExpression = NONE | Variable | FunctionDesignator | ObjectGenerator
 *                                     | LocalObject | QualifiedObject | ( ObjectExpression)
 * 			TextExpression = SimpleTextExpression
 *                         | Ifclause SimpleTextExpression ELSE TextExpression
 *
 * 		ValueExpression = ArithmeticExpression | BooleanExpression | CharacterExpression | TextValueExpression
 *			CharacterExpression = SimpleCharacterExpression
 *                              | Ifclause SimpleCharacterExpression ELSE CharacterExpression
 * 			TextValueExpression = SimpleTextValue
 *                              | Ifclause SimpleTextValue ELSE TextValueExpression
 * 
 * 				Ifclause = IF BooleanExpression THEN
 *   
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public abstract class Expression extends NonTerminal
{ 

  public String toString() { return("NO EXPRESSION"); }
  
  public void doChecking(Type expectedType) { doChecking(); }
	
  public static Expression parseExpression()
  { if(DEBUG) Util.log("Expression.Parse ValueExpression, current="+getCurrentToken());
    if(accept(KeyWord.IF))
    { Expression condition=parseExpression();
      expect(KeyWord.THEN); Expression thenExpression=parseSimpleExpression();
      expect(KeyWord.ELSE); Expression elseExpression=parseExpression();
      Expression expr=new ConditionalExpression(Type.Boolean,condition,thenExpression,elseExpression);
      if(DEBUG) Util.log("Expression.Parse ValueExpression, result="+expr);
      return(expr);
    } else return(parseSimpleExpression());
  }

  protected static Expression parseSimpleExpression()
  { if(DEBUG) Util.log("Expression.Parse SimpleValueExpression, current="+getCurrentToken()+", prev="+getPrevToken());   
    return(parseBinaryOperation(12));
  }

  private static Expression parseUnaryOperation()
  { if(DEBUG) Util.log("Expression.Parse Operation, current="+getCurrentToken()+", prev="+getPrevToken());
    Expression expr=new UnaryOperation(getPrevToken(),parseValuePrimary());
    return(expr);
  }

  private static Expression parseBinaryOperation(int level)
  { if(DEBUG) Util.log("Expression.Parse Operation, level="+level+", current="+getCurrentToken()+", prev="+getPrevToken());
    Expression expr=(level>0)?parseBinaryOperation(level-1):parseValuePrimary();
    while(acceptBinaryOperator(level))
    { if(level==0) expr=new BinaryOperation(expr,getPrevToken(),parseValuePrimary());
      else expr=new BinaryOperation(expr,getPrevToken(),parseBinaryOperation(level-1)); }
    return(expr);
  }
  
  public static Expression parseValuePrimary()
  { // ValuePrimary =  ( Expression ) | FunctionDesignator | ObjectGenerator | Constant | Variable | LocalObject | QualifiedObject
	//		Constant = IntegerConstant | RealConstant | CharacterConstant | TextConstant | SymbolicValue  
	//			SymbolicValue = TRUE | FALSE | NONE | NOTEXT
	//		LocalObject = THIS ClassIdentifier
	//		QualifiedObject = SimpleObjectExpression QUA ClassIdentifier
    if(DEBUG) Util.log("Expression.Parse ValuePrimary, current="+getCurrentToken()+", prev="+getPrevToken());
    if (accept(KeyWord.BEGPAR)) { Expression expr=parseExpression(); expect(KeyWord.ENDPAR); return(expr); }
    else if(accept(KeyWord.INTEGERKONST)) return(new Constant(Type.Integer,getPrevToken()));
    else if(accept(KeyWord.REALKONST)) return(new Constant(Type.Real,getPrevToken()));
    else if(accept(KeyWord.BOOLEANKONST)) return(new Constant(Type.Boolean,getPrevToken()));
    else if(accept(KeyWord.CHARACTERKONST)) return(new Constant(Type.Character,getPrevToken()));
    else if(accept(KeyWord.TEXTKONST)) return(new Constant(Type.Text,getPrevToken()));
    else if(accept(KeyWord.NONE)) return(new Constant(Type.Ref,getPrevToken()));
    else if(accept(KeyWord.NOTEXT)) return(new Constant(Type.Text,getPrevToken()));
    else if(accept(KeyWord.NEW)) return(ObjectGenerator.parse()); // TODO 
    else if(accept(KeyWord.THIS)) return(LocalObject.acceptThisIdentifier()); // TODO 
    else if(accept(KeyWord.PLUS)) return(parseUnaryOperation());
	else if(accept(KeyWord.MINUS)) return(parseUnaryOperation());
	else if(accept(KeyWord.NOT)) return(parseUnaryOperation());
    else
    { Identifier ident=expectIdentifier();
	  if(accept(KeyWord.BEGBRACKET)) return(new SubscriptedVariable(ident));
      else if (accept(KeyWord.BEGPAR)) return(new FunctionDesignator(ident));
      else return(new Variable(ident));
    }
  }
  
}
