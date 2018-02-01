package parser.expression;

import java.util.Iterator;
import java.util.Vector;

import parser.declaration.ArrayDeclaration;
import parser.declaration.ArrayDeclaration.BoundPair;
import parser.declaration.ConnectedDeclaration;
import parser.declaration.Declaration;
import parser.declaration.Specification;
import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Kind;
import common.Type;
import compiler.NonTerminal;

/**
 * Subscripted Variable.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *	SubscriptedVariable = ArrayIdentifier [ SubscriptList ]
 *		SubscriptList ::= ArithmeticExpression { , ArithmeticExpression }
 *   
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class SubscriptedVariable extends Variable
{ //static boolean DEBUG=true; 
  Vector<Expression> index=new Vector<Expression>();
  Vector<NonTerminal> checkedParams = new Vector<NonTerminal>();

  public SubscriptedVariable(Identifier arrayIdentifier)
  { super(arrayIdentifier);
    if(DEBUG) Util.log("Parse SubsciptedVariable, current="+getCurrentToken()+", prev="+getPrevToken());
    do { index.add(parseSimpleExpression()); } while( accept(KeyWord.COMMA) );  
    expect(KeyWord.ENDBRACKET);
	if(DEBUG) Util.log("NEW SubscriptedVariable: "+toString());
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
    if(DEBUG) Util.log("BEGIN SubscriptedVariable("+getIdentifier()+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
    DeclarationList declarationList=currentScope.getDeclarationList();
	super.doChecking();
	if(DEBUG) declarationList.print("SubscriptedVariable("+getIdentifier()+").doChecking() PARAMETER LIST","DEFINED: ");
	Declaration match=getSemantic();
	//Util.BREAK("SubscriptedVariable.doChecking("+ident+") match is "+match.getClass().getName());
	if(match instanceof ArrayDeclaration) // Declared Array
	{ ArrayDeclaration array=(ArrayDeclaration)match;
	  this.type=array.getType();
	  // Check parameters
	  Iterator<ArrayDeclaration.BoundPair> formalIterator=array.getBoundPairList().iterator();
	  Iterator<Expression> actualIterator=index.iterator();
	  while(actualIterator.hasNext())
	  { if(!formalIterator.hasNext()) error("Wrong number of indices to "+array);
	    ArrayDeclaration.BoundPair formalParameter=formalIterator.next();
	    Type formalType=Type.Integer;
	    if(DEBUG) Util.log("Formal Parameter: "+formalParameter+", Formal Type="+formalType);
	    Expression actualParameter=actualIterator.next();
	    actualParameter.doChecking();
		checkedParams.add(TypeConversion.testAndCreate(formalType,actualParameter));
	  }
	  if(formalIterator.hasNext()) error("Wrong number of indices to "+array);
	}
	else if(match instanceof Specification) // Parameter Array
	{ Specification spec=(Specification)match;
	  this.type=spec.getType();
	  Kind kind=spec.getKind();
	  if(kind!=Kind.Array) error("SubscriptedVariable("+getIdentifier()+") is matched to a parameter "+kind);
	  Util.WARNING("SubscriptedVariable("+getIdentifier()+") - Parameter Checking is postponed to Runtime");
	  Iterator<Expression> actualIterator=index.iterator();
	  while(actualIterator.hasNext())
	  { Expression actualParameter=actualIterator.next();
	    actualParameter.doChecking();
		checkedParams.add(actualParameter);
	  }
	}
	if(DEBUG) Util.log("END SubscriptedVariable("+getIdentifier()+").doChecking: type="+type);
	//Util.BREAK("END SubscriptedVariable("+variable+").doChecking: type="+type);
    SET_SEMANTICS_CHECKED();
  }

	
	// Generate code for putting an value(expression) into this SubscriptedVariable
	public String put(NonTerminal rightPart)
	{ ASSERT_SEMANTICS_CHECKED(this);
      //String result=this.getIdentifier().toString();
      String result=this.toJavaCode()+"="+rightPart;
	  //Util.BREAK("Variable("+result+").put("+rightPart+")");
	      //error("Can't assign to "+this.toJavaCode()+" - Rewrite Program");  // TODO: Når gjelder dette - name ?
	  return(result);
	}

	public String get(Type cast) {
		ASSERT_SEMANTICS_CHECKED(this);
		StringBuilder s = new StringBuilder();
		Declaration decl=getSemantic();
		//Util.BREAK("SubscriptedVariable("+getIdentifier()+").get("+cast+") Semantic="+semantic);
		if(decl instanceof ConnectedDeclaration)
		{ s.append(((ConnectedDeclaration)decl).edConnectedVariable()+'.');
		  decl=((ConnectedDeclaration) decl).getDeclaration();
		}
		s.append(getIdentifier());
		if(decl instanceof ArrayDeclaration) // Declared Array
		{ ArrayDeclaration array=(ArrayDeclaration)decl;
		  //Util.BREAK("SubscriptedVariable("+getIdentifier()+").get("+cast+") ArrayDeclaration="+array);
		  Iterator<BoundPair> ab=array.getBoundPairList().iterator();
		  for (Iterator<NonTerminal> it = checkedParams.iterator(); it.hasNext();)
		  { Expression lb=ab.next().getLowerBound();
			NonTerminal par = it.next();
			s.append("[").append(par.toJavaCode()+'-'+lb).append("]");
		  }
		}
		else if(decl instanceof Specification) // Parameter Array
		{ //Specification spec=(Specification)decl;
		  //Util.BREAK("SubscriptedVariable("+getIdentifier()+").get("+cast+") Specification="+spec);
		  warning("Parameter Array is only supported with one index counting from zero - Check program");
		  //Iterator<BoundPair> ab=array.getBoundPairList().iterator();
		  for (Iterator<NonTerminal> it = checkedParams.iterator(); it.hasNext();)
		  { //Expression lb=ab.next().getLowerBound();
			NonTerminal par = it.next();
			//s.append("[").append(par.toJavaCode()+'-'+lb).append("]");
			s.append("[").append(par.toJavaCode()+'-'+"lowerBound").append("]");
		  }
		}
		return (s.toString());
	}

	public String toJavaCode()
	{ return(this.get(null)); }
  
  public String toString()
  { return(""+getIdentifier()+index); }

}
