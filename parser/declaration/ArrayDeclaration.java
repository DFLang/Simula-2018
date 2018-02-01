package parser.declaration;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;
import parser.expression.Expression;
import parser.statement.ConnectionBlock;

/**
 * Array Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ArrayDeclaration = [ Type ] ARRAY ArraySegment { , ArraySegment }
 *	  ArraySegment = IdentifierList "[" BoundPairList "]"
 *
 *		IdentifierList = Identifier { , Identifier }
 *
 *		BoundPairList = BoundPair { , BoundPair }
 *		 BoundPair = ArithmeticExpression : ArithmeticExpression
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ArrayDeclaration extends Declaration
{ static boolean DEBUG = true;// false; //true;
  //Type type;
  Vector<BoundPair> boundPairList;
  public Vector<BoundPair> getBoundPairList() { return(boundPairList); }
  
  private ArrayDeclaration(Identifier identifier,Type type,Vector<BoundPair> boundPairList)
  { this.identifier=identifier; this.type=type; this.boundPairList=boundPairList;
	if(DEBUG) Util.log("END NEW ArrayDeclaration: "+toString());
  }
  
  public static void parse(Type type,DeclarationList declarationList)
  { if(DEBUG) Util.log("Parse ArrayDeclaration, type="+type+", current="+getCurrentToken());
    do { parseArraySegment(type,declarationList); } while(accept(KeyWord.COMMA));
  }
  
  private static void parseArraySegment(Type type,DeclarationList declarationList)
  { if(DEBUG) Util.log("Parse ArraySegment, current="+getCurrentToken()+", prev="+getPrevToken());
	//	IdentifierList = Identifier { , Identifier }
	Vector<Identifier> identList=new Vector<Identifier>();
    do { identList.add(expectIdentifier()); } while(accept(KeyWord.COMMA));  

	if(DEBUG) Util.log("Parse ArraySegment(2),identList="+identList+", current="+getCurrentToken()+", prev="+getPrevToken());
    expect(KeyWord.BEGBRACKET);
    // BoundPairList = BoundPair { , BoundPair }
    if(DEBUG) Util.log("Parse BoundPairList, current="+getCurrentToken()+", prev="+getPrevToken());
    Vector<BoundPair> boundPairList=new Vector<BoundPair>();
    do
    { Expression lhs=Expression.parseExpression();
      expect(KeyWord.COLON);
      Expression rhs=Expression.parseExpression();
      boundPairList.add(new BoundPair(lhs,rhs));
    } while(accept(KeyWord.COMMA)) ;   
    if(DEBUG) Util.log("Parse BoundPairList(2), boundPairList="+boundPairList);
	if(DEBUG) Util.log("Parse ArraySegment(3),identList="+identList+", current="+getCurrentToken()+", prev="+getPrevToken());
    expect(KeyWord.ENDBRACKET);
	for(Enumeration<Identifier> e=identList.elements(); e.hasMoreElements();)
	{ Identifier identifier=e.nextElement();
	  declarationList.add(new ArrayDeclaration(identifier,type,boundPairList));
	}
  }

  public static class BoundPair
  { // BoundPair = ArithmeticExpression : ArithmeticExpression
    Expression lhs,rhs;
    public Expression getLowerBound() { return(lhs); }
    BoundPair(Expression lhs,Expression rhs)
    { this.lhs=lhs; this.rhs=rhs; }
    public String toString() { return(""+lhs+':'+rhs); }
    public void doChecking()
    { // TODO: Hvis konstante grenser så eveluerer vi her !
      lhs.doChecking(Type.Integer); rhs.doChecking(Type.Integer);
      warning("Only Arrays with lower bound zero is fully supported");
      // TODO: Her mangler mye
    }
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
    if(type==null) type=Type.Real;
    for(Iterator<BoundPair> it=boundPairList.iterator();it.hasNext();)
    { it.next().doChecking(); }
    SET_SEMANTICS_CHECKED();
  }
  
  public String toJavaCode()
  {	ASSERT_SEMANTICS_CHECKED(this);
    String s1=this.type.toJavaType();
	String s2="new "+this.type.toJavaType();
    for(Iterator<BoundPair> it=boundPairList.iterator();it.hasNext();)
    { BoundPair boundPair=it.next();
      String n="["+boundPair.rhs+'-'+boundPair.lhs+"+1]";
      s1=s1+"[]";
      s2=s2+n;
    }
	Scope scope=getCurrentScope().getEnclosure();
	//Util.BREAK("ArrayDeclaration.toJavaCode: scope="+scope.getClass().getName());
	if(scope instanceof ClassDeclaration) s1="public "+s1;
	if(scope instanceof ConnectionBlock) s1="public "+s1;
	return(s1+' '+identifier+'='+s2+';');
  }

  public String toString()
  {	String s = "ARRAY "+identifier+boundPairList;
	if(type!=null) s=type.toString()+" "+s;
	return(s);
  }
}
