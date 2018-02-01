package parser.expression;

import java.util.Iterator;
import java.util.Vector;

import parser.declaration.BlockHead;
import parser.declaration.ClassDeclaration;
import parser.declaration.ConnectedDeclaration;
import parser.declaration.Declaration;
import parser.declaration.ProcedureDeclaration;
import parser.declaration.Scope;
import parser.declaration.Specification;
import parser.declaration.Specification.ParameterKind;
import parser.declaration.StandardClass;
import parser.declaration.StandardProcedure;
import common.Const;
import common.Mode;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Kind;
import common.Type;
import compiler.NonTerminal;

/**
 * Function Designator.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  FunctionDesignator = ProcedureIdentifier ( [ ActualParameterPart ] )
 *  
 * 	ActualParameterPart = ActualParameter { , ActualParameter }
 * 		ActualParameter = Expression | ArrayIdentifier1 | SwitchIdentifier1 | ProcedureIdentifier1
 * 			Identifier1 = Identifier | RemoteIdentifier
 * 				RemoteIdentifier = SimpleObjectExpression . AttributeIdentifier
 * 								 | TextPrimary . AttributeIdentifier
 * 
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class FunctionDesignator extends Variable {
	//private Identifier identifier; // functionIdentifier;
	Vector<Expression> params = new Vector<Expression>();
	Vector<NonTerminal> checkedParams; // Set by doChecking

	public FunctionDesignator(String functionIdentifier)
	{ super(functionIdentifier); } // Used by ProgramModule

	public FunctionDesignator(Identifier functionIdentifier) {
		super(functionIdentifier);
		if (DEBUG)
			Util.log("Parse FunctionDesignator, current=" + getCurrentToken()
					+ ", prev=" + getPrevToken());
		if (!accept(KeyWord.ENDPAR)) {
			do {
				params.add(parseSimpleExpression());
			} while (accept(KeyWord.COMMA));
			expect(KeyWord.ENDPAR);
		}
		if (DEBUG)
			Util.BREAK("NEW FunctionDesignator: " + toString());
	}

	public void doChecking()
    { if(IS_SEMANTICS_CHECKED()) return;
		Util.setLine(getLineNumber());
		if (DEBUG)
			Util.BREAK("BEGIN FunctionDesignator(" + getIdentifier()
					+ ").doChecking - Current Scope Chain: "
					+ currentScope.edScopeChain());
		super.doChecking();
		Declaration decl=getSemantic();
		if(decl instanceof ConnectedDeclaration)
		{ decl=((ConnectedDeclaration)decl).getDeclaration(); }
		if(decl instanceof BlockHead) // Declared Procedure or Prefix class 
		{	BlockHead blockHead = (BlockHead) decl;
			//Util.BREAK("FunctionDesignator("+getIdentifier()+") blockHead="+blockHead);
			this.type = blockHead.getType();
			// Check parameters
			checkedParams = new Vector<NonTerminal>();
			Iterator<Declaration> formalIterator = blockHead.parameterIterator();
			Iterator<Expression> actualIterator = params.iterator();
			//Util.BREAK("FunctionDesignator("+getIdentifier()+").doChecking: Params="+params);
			while (actualIterator.hasNext()) {
				if (!formalIterator.hasNext()) error("Wrong number of parameters to " + blockHead);
				Specification formalParameter = (Specification)formalIterator.next();
				Type formalType = formalParameter.getType();
				//Util.BREAK("Formal Parameter: " + formalParameter + ", Formal Type=" + formalType);
				Expression actualParameter = actualIterator.next();
				//Util.BREAK("Actual Parameter: " + actualParameter);
				checkTransmitionMode(formalParameter,actualParameter);
				actualParameter.doChecking(formalType);
				//Util.BREAK("Actual Parameter: " + actualType + " "	+ actualParameter + ", Actual Type=" + actualType);
				checkedParams.add(TypeConversion.testAndCreate(formalType,actualParameter));
				//Util.BREAK("FunctionDesignator("+getIdentifier()+").doChecking().addCheckedParam: "+checkedParams);
			}
			if (formalIterator.hasNext()) error("Wrong number of parameters to " + blockHead);
		} else if (decl instanceof ClassDeclaration) // Prefixed Block ...
		{ 
			Util.NOT_IMPLEMENTED("FunctionDesignator(" + getIdentifier()+").doChecking() - ClassDeclaration");
    	} else if (decl instanceof Specification) // Parameter Procedure
		{
			Specification spec = (Specification) decl;
			this.type = spec.getType();
			Kind kind = spec.getKind();
			if (kind != Kind.Procedure)	error("FunctionDesignator(" + getIdentifier() + ") is matched to a parameter " + kind);
			Util.WARNING("FunctionDesignator(" + getIdentifier()
					+ ") - Parameter Checking is postponed to Runtime");
		}
  	    else
  	    { // TODO: Vet ikke om det er mulig å komme hit.
  		  Util.NOT_IMPLEMENTED("FunctionDesignator("+getIdentifier()+").doChecking() decl'name="+decl.getClass().getName());
  	    }
		if (DEBUG)
			Util.log("END FunctionDesignator(" + getIdentifier()
					+ ").doChecking: type=" + type);
	    //Util.BREAK("END FunctionDesignator("+getIdentifier()+").doChecking: type=" + type+", checkedParams="+checkedParams);
		SET_SEMANTICS_CHECKED();
	}
	
	private void checkTransmitionMode(Specification formalParameter,Expression actualParameter)
	{ Type type=formalParameter.getType();
	  Kind kind=formalParameter.getKind();  
	  Mode mode=formalParameter.getMode();
	  //Util.BREAK("Parameter: "+kind+' '+type+' '+formalParameter.getIdentifier()+" by "+((mode!=null)?mode:"default"));
	  Specification.ParameterKind parameterKind=formalParameter.getParameterKind();
	  //Util.BREAK("FunctionDesignator.Parameter: "+parameterKind+' '+kind+' '+((type!=null)?type:"")+' '+formalParameter.getIdentifier()+" by "+((mode!=null)?mode:"default"));
      boolean illegal=false;
	  switch(parameterKind)
	  { case ValueType:       // Simple Type Integer, Real, Character
		    if(mode==null) mode=Mode.value;
		    break;
	    case SimpleText:      // Simple Text	    	
	    	break;
	    case ValueTypeArray:  // Array of Type Integer, Real, Character
	    	break;
	    case ObjectReference: // Simple ref(ClassIdentifier)
	    case ReferenceTypeArray: // ref(ClassIdentifier) Array
	    case Procedure:          // notype Procedure
	    case TypeProcedure:      // Type Procedure
	    case Label:
	    case Switch:
	    	if(mode==Mode.value) illegal=true;
	    	break;
	    default:	
	  }
	  if(illegal) error("Illegal transmission mode: "+parameterKind+' '
			  +formalParameter.getIdentifier()+" by "+((mode!=null)?mode:"default")+" is not allowed");
	}
	
	// Generate code for putting an value(expression) into this FunctionDesignator
	public String put(NonTerminal rightPart)
	{ ASSERT_SEMANTICS_CHECKED(this);
	  String result=this.getIdentifier().toString();
	  //Util.BREAK("Variable("+result+").put("+rightPart+")");
	      error("Can't assign to "+this+" - Rewrite Program"); 
	  return(result);
	}

	// Generate code for getting the value of this Variable
	public String get(Type cast)
	{ //Util.BREAK("FunctionDesignator("+getIdentifier()+").get("+cast+") Semantic="+semantic);
	  //Util.BREAK("FunctionDesignator2("+getIdentifier()+").get("+cast+") params="+params);
	  //Util.BREAK("FunctionDesignator3("+getIdentifier()+").get("+cast+") checkedParams="+checkedParams);
	  ASSERT_SEMANTICS_CHECKED(this);
	  StringBuilder s = new StringBuilder();
	  //Util.BREAK("FunctionDesignator("+getIdentifier()+").get("+cast+") Semantic="+semantic);
      Declaration decl=getSemantic();
	  if(decl instanceof ConnectedDeclaration)
	  { s.append(((ConnectedDeclaration)decl).edConnectedVariable()+'.');
	    decl=((ConnectedDeclaration)decl).getDeclaration();
	  }

	  
	  String methodIdent=getIdentifier().toString();
	  // Test om dette er en 'static' metode
	  // TODO: Gjør dette litt mere elegant !
	  if(decl instanceof ProcedureDeclaration)
	  { ProcedureDeclaration proc=(ProcedureDeclaration)decl;
	    Scope encl=proc.getEnclosure();
	    if(encl instanceof StandardClass)
	    { StandardClass stdc=(StandardClass)encl;
	      boolean isContext=stdc.getContextFlag();
	      if(isContext)
	      { String contextId=stdc.getIdentifier().toString();
	      methodIdent=contextId+'.'+methodIdent;
	      }
	    }
	  }
	  
	  s.append(methodIdent).append('(');
	  if(decl instanceof BlockHead) // Declared Procedure, Standard Procedure or Prefix class of a Block
	  {	BlockHead blockHead = (BlockHead) decl;
		// Generate Parameter Transmission
		Iterator<Declaration> formalIterator = blockHead.parameterIterator(); // If class also over prefix-chain
		Iterator<NonTerminal> actualIterator = checkedParams.iterator();
		while(actualIterator.hasNext())
		{ NonTerminal actualParameter = actualIterator.next();
		  //Util.BREAK("FunctionDesignator3("+getIdentifier()+").get("+cast+") Actual Parameter: " + actualParameter);
		  Specification formalParameter = (Specification)formalIterator.next();
		  //Util.BREAK("FunctionDesignator4("+getIdentifier()+").get("+cast+") Formal Parameter: " + formalParameter);
		  Type formalType = formalParameter.getType();
		  doParameterTransmition(s,formalParameter,actualParameter);
		  if(actualIterator.hasNext()) s.append(',');
		}
	  }
	  else if (decl instanceof Specification) // Parameter Procedure
	  {	Specification.ParameterKind parameterKind=getParameterKind();
		if(getMode()==Mode.name)
	    { 
//	  	  if(parameterKind==ParameterKind.Procedure) {}//  TODO: ???
//	 	  else if(parameterKind==ParameterKind.TypeProcedure) {}//  TODO: ???
//		  else
		  error("Parameter "+this+" by Name is not Supported - Rewrite Program"); 
	    }
		else if(getMode()==Mode.value)
	    { error("Parameter "+this+" by Value is not allowed - Rewrite Program"); }
		else // By Reference
	    { 
//	  	  if(parameterKind==ParameterKind.Procedure) {}//  TODO: ???
//	 	  else if(parameterKind==ParameterKind.TypeProcedure) {}//  TODO: ???
//		  else
		    error("Parameter "+this+" by Name is not(yet) Supported - Rewrite Program"); 
	    }
	  }
	  else
	  { // TODO: Vet ikke om det er mulig å komme hit.
		Util.NOT_IMPLEMENTED("FunctionDesignator("+getIdentifier()+").get("+cast+") decl'name="+decl.getClass().getName());
	  }
	  s.append(')');
	  String result=s.toString();
	  if(cast!=null && !cast.equals(this.type))
		  result="(("+cast.toJavaType()+")("+result+"))";
	  return(result);
	  
	}

	public String toJavaCode()
	{ return(this.get(null)); }
	
	private void doParameterTransmition(StringBuilder s,Specification formalParameter,NonTerminal actualParameter)
	{ Type type=formalParameter.getType();
	  Kind kind=formalParameter.getKind();  
	  Mode mode=formalParameter.getMode();
	  //Util.BREAK("FunctionDesignator.doParameterTransmition: "+kind+' '+type+' '+formalParameter.getIdentifier()+" by "+((mode!=null)?mode:"default"));
	  Specification.ParameterKind parameterKind=formalParameter.getParameterKind();
	  //Util.BREAK("FunctionDesignator.doParameterTransmition: "+parameterKind+' '+kind+' '+((type!=null)?type:"")+' '+formalParameter.getIdentifier()+" by "+((mode!=null)?mode:"default"));
	  
	  switch(parameterKind)
	  { case ValueType:       // Simple Type Integer, Real, Character
	    case ObjectReference: // Simple ref(ClassIdentifier)
	    case SimpleText:      // Simple Text
		    //if(mode==null) mode=Mode.value;
		    if(mode==Mode.value)
		    { if(parameterKind==ParameterKind.SimpleText)
		    	   s.append("copy(").append(actualParameter.toJavaCode()).append(')');
		      else s.append(actualParameter.toJavaCode());
		    }
		    else // Mode Name
		    { String javaTypeClass=type.toJavaTypeClass();
		      if(actualParameter instanceof Variable)
		      {	s.append("new "+Const.SIMPLE_BY_NAME+"<"+javaTypeClass+">()");
		    	s.append("{ public "+javaTypeClass+" "+Const.NAME_GET+"() { return("+actualParameter.toJavaCode()+"); }");
		    	s.append(" public void "+Const.NAME_PUT+"("+javaTypeClass+" x) { "+actualParameter.toJavaCode()+"=("+actualParameter.getType().toJavaType()+")x; } }");
		      }
		      else
		      {	s.append("new "+Const.SIMPLE_BY_NAME+"<"+javaTypeClass+">()");
		    	s.append("{ public "+javaTypeClass+" "+Const.NAME_GET+"() { return("+actualParameter.toJavaCode()+"); }");
		    	//s.append(" public void "+Const.NAME_PUT+"("+javaTypeClass+" x) { error(\"can't assign to ("+actualParameter.toJavaCode()+") ...\"); }");
		    	s.append(" }");
		      }
		    }
		    break;

	    	//break;
	    case ValueTypeArray:  // Array of Type Integer, Real, Character
	    	//break;
	    case ReferenceTypeArray: // ref(ClassIdentifier) Array
	    case Procedure:          // notype Procedure
	    	
	    case TypeProcedure:      // Type Procedure
	    case Label:
	    case Switch:
	    	s.append(actualParameter);
	    	break;
	    default:	
	  }
	}

	public String toString() {
		return (("" + getIdentifier() + params).replace('[', '(').replace(']', ')'));
	}
	
}
