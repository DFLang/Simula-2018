package parser.expression;

import parser.declaration.ClassDeclaration;
import parser.declaration.Declaration;
import parser.declaration.StandardClass;
import common.Token;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;

/**
 * Binary Operation.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *   BinaryOperation = Expression  operator  Expression
 *   
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class BinaryOperation extends Expression
{ // static boolean DEBUG=true;//false; //true; 
  private Expression lhs;
  private Token opr;
  private Expression rhs;
  private String useFuncEQ=null; // Set by doChecking
  private String useFuncNE=null; // Set by doChecking
  
  public BinaryOperation(Expression lhs,Token opr,Expression rhs)
  { this.lhs=lhs; this.opr=opr; this.rhs=rhs;
	if(DEBUG) Util.log("NEW BinaryOperation: "+toString());
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
	Util.log("BEGIN BinaryOperation"+toString()+".doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	//Util.BREAK("BEGIN BinaryOperation"+toString()+".doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	KeyWord keyWord=opr.getKeyWord();
	switch(keyWord)
	{ case PLUS: case MINUS: case MUL: case DIV:
	  { // ArithmeticExpression
		lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType(); Type type2=rhs.getType();
		this.type=Type.arithmeticTypeConversion(type1,type2);
		lhs=(Expression)TypeConversion.testAndCreate(this.type,lhs);
		rhs=(Expression)TypeConversion.testAndCreate(this.type,rhs);
		//Util.BREAK("BinaryOperation.doChecking: arithmeticTypeConversion("+type1+','+type2+") ==> "+this.type);
		if(this.type==null) error("Incompatible types in binary operation: "+toString());
		break;
	  }
	  case INTDIV:
	  {	lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType(); Type type2=rhs.getType();
		if(type1==Type.ShortInteger || type1==Type.Integer)
	    { if(type2==Type.ShortInteger || type2==Type.Integer)  this.type=Type.Integer; }
		if(this.type==null) error("Incompatible types in binary operation: "+toString());
		lhs=(Expression)TypeConversion.testAndCreate(this.type,lhs);
		rhs=(Expression)TypeConversion.testAndCreate(this.type,rhs);
	    break; 
	  }
	  case EXP:
	  {	lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType(); Type type2=rhs.getType();
        if(type1==Type.ShortInteger || type1==Type.Integer)
	    { if(type2==Type.ShortInteger || type2==Type.Integer)  this.type=Type.Integer;
	      else if(type2==Type.Real || type2==Type.LongReal)  this.type=type2;
	    }
	    else if(type1==Type.Real || type1==Type.LongReal )
	    { if(type2==Type.ShortInteger || type2==Type.Integer)  this.type=type1;
	      else if(type2==Type.Real || type2==Type.LongReal)  this.type=type2;
	    }
		lhs=(Expression)TypeConversion.testAndCreate(this.type,lhs);
		rhs=(Expression)TypeConversion.testAndCreate(this.type,rhs);
    	if(this.type==null) error("Incompatible types in binary operation: "+toString());
	    break; 
	  }
	  case LT: case LE: case EQ: case NE: case GE: case GT:
	  {	lhs.doChecking(); rhs.doChecking(); 
		Type type1=lhs.getType(); Type type2=rhs.getType();
		if(type1==Type.Text)
		{ if(type2==Type.Text)
		  { if(keyWord==KeyWord.EQ)
		    { // Text value relation
			  this.type=Type.Boolean; this.useFuncEQ="equals";
			  break;
		    }if(keyWord==KeyWord.NE)
		    { // Text value relation
			  this.type=Type.Boolean; this.useFuncNE="equals";
			  break;
		    }
		  }
		}
		if(type1==Type.Character&&type2==Type.Character) { this.type=Type.Boolean; break; }
		// Arithmetic Relation
		Type atype=Type.arithmeticTypeConversion(type1,type2);
		if(atype==null) error("Incompatible types in binary operation: "+toString());
		this.type=Type.Boolean;
		lhs=(Expression)TypeConversion.testAndCreate(atype,lhs);
		rhs=(Expression)TypeConversion.testAndCreate(atype,rhs);
		break;
	  }
	  case AND: case OR: case IMP: case EQV: case AND_THEN: case OR_ELSE:
	  { // Boolean operation
		lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType(); Type type2=rhs.getType();
		if( type1.equals(type2) & type1==Type.Boolean) this.type=Type.Boolean;
		if(this.type==null) error("Incompatible types in binary operation: "+toString());
		break;
	  }
	  case IS: case IN:
	  { // Object IS ClassIdentifier   |   Object IN ClassIdentifier
		lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType();
//		Type type2=rhs.getType();
//		Util.log("BinaryOperation.doChecking: getRefIdent="+type1.getRefIdent());
	    Identifier objIdentifier=type1.getRefIdent();
	    if(objIdentifier==null)
	    	error("BinaryOperation.doChecking: The Variable "+lhs+" is not ref() type");
	    
	    Declaration decl=this.getEnclosure().findDefinition(objIdentifier);
	    if(decl==null) error("BinaryOperation.doChecking: The Class "+objIdentifier+" is not visible");
	    
		this.type=Type.Boolean;
		//  Debug.BREAK("BinaryOperation.doChecking: "+oprCode);
		break;
	  }
	  case EQR: case NER:
	  { // Object =/= Object   or   Object == Object
		lhs.doChecking(); rhs.doChecking();
		Type type1=lhs.getType(); Type type2=rhs.getType();
		if((!type1.isReferenceType())||(!type2.isReferenceType()))
		error("BinaryOperation: Illegal types: "+type1+" "+opr+" "+type2);
		
		this.type=Type.Boolean;
		break;
	  }
	  case DOT:
	  { this.type=doRemoteChecking(lhs,rhs);
		break;  
	  }
	  case QUA:
	  { Identifier classIdentifier=((Variable)rhs).getIdentifier();
	    //Util.BREAK("BinaryOperation QUA, rhs="+rhs.getClass().getName());
	    this.type=doQuaChecking(lhs,classIdentifier);
		break;
	  }
	  default:
			Util.NOT_IMPLEMENTED("BinaryOperation -- error(Something went wrong) opr="+opr);
			this.type=rhs.getType();  // TODO  TEMP
	}
	Util.log("END BinaryOperation"+toString()+".doChecking - Result type="+this.type);
	SET_SEMANTICS_CHECKED();
  }

  private Type doRemoteChecking(Expression obj,Expression attr)
  {	Util.setLine(getLineNumber());
    Type result;
	//Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	obj.doChecking(); Type objType=obj.getType();
	if(DEBUG); Util.log("BEGIN doRemoteChecking("+toString()+").doChecking(2) - objType="+objType+", obj="+obj);
	if(objType==Type.Text) return(doRemoteTextChecking(obj,attr));
	Identifier classIdentifier=objType.getRefIdent();
	if(classIdentifier==null) error("doRemoteChecking: Object Expression is not a ref() type.");
    Declaration decl=currentScope.findDefinition(classIdentifier);
	//Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking(3) findDefinition("+classIdentifier+") ==> "+decl);
	//attr.doChecking(); // NOTE Should not be called for a remote attribute
	//Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking(4) - attr="+attr+", attr.Class="+attr.getClass().getName());
	if(attr instanceof Variable) // Covers FunctionDesignator and SubscriptedVariable since they are subclasses
	{ Variable var=(Variable)attr;
	  Identifier ident=var.getIdentifier();
	  //Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking(5a) findAttribute("+ident+")  Search in "+decl);
	  Declaration remote=((ClassDeclaration)decl).findAttribute(ident);
	  //Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking(5) findAttribute("+ident+")  ==> "+remote);
	  if(remote==null) error("BinaryOperation.doRemoteTextChecking: "+ident+" is not an attribute of "+classIdentifier);
	  var.setRemotelyAccessed(remote);
	  result=remote.getType();
	  //Util.BREAK("BinaryOperation.doRemoteChecking("+toString()+").doChecking(6) - attr="+attr+", attr.Type="+result);
	}
	else 
	{ Util.BREAK("BinaryOperation.doRemoteChecking -- Usikker på dette !!!");		
	  Util.BREAK("BinaryOperation.doRemoteChecking: attr="+attr.getClass().getName());
	  error("Illegal attribute("+attr+") in remote access");
	  result=attr.getType();
	}
	return(result);
  }	

  private Type doRemoteTextChecking(Expression obj,Expression attr)
  {	//Util.BREAK("BinaryOperation.doRemoteTextChecking("+toString()+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
    Type result;
    //Util.BREAK("BinaryOperation.doRemoteTextChecking("+toString()+").doChecking - attr'name="+attr.getClass().getName());
	if(attr instanceof Variable) // Covers FunctionDesignator and SubscriptedVariable since they are subclasses
	{ Variable var=(Variable)attr;
	  Identifier ident=var.getIdentifier();
	  Declaration remote=StandardClass.typeText.findAttribute(ident);
	  //Util.BREAK("BinaryOperation.doRemoteTextChecking: remote="+remote);
	  if(remote==null) error("BinaryOperation.doRemoteTextChecking: "+ident+" is not a Text attribute");
	  var.setRemotelyAccessed(remote);
	  //var.doChecking();
	  result=remote.getType();

	}
	else 
	{ Util.BREAK("BinaryOperation.doRemoteTextChecking -- Usikker på dette !!!");		
	  Util.BREAK("BinaryOperation.doRemoteTextChecking: attr="+attr.getClass().getName());
	  error("Illegal attribute("+attr+") in remote access");
	  result=attr.getType();
	}
	return(result);
  }
  
	public Type doQuaChecking(Expression simpleObjectExpression,Identifier classIdentifier) {
		Util.setLine(getLineNumber());
		simpleObjectExpression.doChecking();
		this.type = simpleObjectExpression.getType();
		Identifier refId = this.type.getRefIdent();
		Declaration objDecl = currentScope.findDefinition(refId);
		if (!(objDecl instanceof ClassDeclaration))
			error("Illegal ref(" + refId + ") -- " + refId + " is not a class");
		Declaration castDecl = currentScope.findDefinition(classIdentifier);
		if(castDecl!=objDecl)
		{ if (!(castDecl instanceof ClassDeclaration))
			error("Illegal QUA -- " + classIdentifier + " is not a class");
		  if (!((ClassDeclaration) castDecl)
				.isSubClassOf((ClassDeclaration) objDecl))
			error("Illegal QUA -- " + refId 
					+ " is not a subclass of " + classIdentifier);
		} else warning("Unneccessary QUA "+ classIdentifier);
		return(new Type(classIdentifier));
	}
  
  public String toJavaCode()
  { //Util.BREAK("BinaryOperation.toJavaCode: "+this);
	ASSERT_SEMANTICS_CHECKED(this);
	if(this.useFuncEQ!=null) return('('+lhs.get(null)+".equals("+rhs.get(null)+"))");
	if(this.useFuncNE!=null) return("(!"+lhs.get(null)+".equals("+rhs.get(null)+"))");
	if(opr.getKeyWord()==KeyWord.QUA) return ("((" + rhs + ")(" + lhs + "))");
	if(opr.getKeyWord()==KeyWord.IS) return(""+lhs+".getClass() == Class.forName(\"simula.test.context."+rhs+"\")");
    else return(lhs.get(null)+' '+opr.toJavaCode()+' '+rhs.get(null)); }
	  
  public String toString()
  { return("("+lhs+' '+opr+' '+rhs+")"); }

}
