package parser.declaration;

import common.Const;
import common.Mode;
import common.NameTable;
import common.Util;
import common.Identifier;
import common.Kind;
import common.Type;

public class Specification extends Declaration
{ //public final static String SIMPLE_BY_NAME="ValueTypeByName_";
//Identifier identifier;  // Inherited
  Identifier parameterIdentifier; // Used by Coder when generating Java-code for parameters to a Class.
  Mode mode;
//  Type type;  // Inherited 
  Kind kind;

  public Specification(Identifier identifier)
  { this.identifier=identifier; }
  
  public Specification(Identifier identifier,Type type,Kind kind)
  { this(identifier); this.type=type; this.kind=kind; }
  
  public void setMode(Mode mode)
  {	if(this.mode!=null)
	Util.ERROR("Parameter "+identifier+" is already specified by"+this.mode);
	this.mode=mode;
  }

  public void setTypeAndKind(Type type,Kind kind)
  { this.type=type; this.kind=kind; }
  
  public Kind getKind() { return(kind); }
  public Mode getMode() { return(mode); }
  
  public Identifier createParameterIdentifier()
  { if(parameterIdentifier!=null) return(parameterIdentifier);
	String name="param_"+identifier.toString();
//	String name=identifier.toString();
	while(NameTable.lookupIndex(name)!=null) name="_"+name;
	parameterIdentifier=NameTable.defineName(name);
	return(parameterIdentifier);
  }
  
  public Identifier getParameterIdentifier()
  { if(parameterIdentifier!=null) return(parameterIdentifier);
	return(getIdentifier());
  }
	
  public enum ParameterKind
  { ValueType       // Simple Type Integer, Real, Character
  , ObjectReference // Simple ref(ClassIdentifier)
  , SimpleText      // Simple Text
  , ValueTypeArray  // Array of Type Integer, Real, Character
  , ReferenceTypeArray // ref(ClassIdentifier) Array
  
  , Procedure          // notype Procedure
  , TypeProcedure      // Type Procedure
  , Label
  , Switch
  }
  public ParameterKind getParameterKind()
  { switch(kind)
    { case Simple:
	    if(type==Type.Text) return(ParameterKind.SimpleText);
	    if(type.isReferenceType()) return(ParameterKind.ObjectReference);
	    return(ParameterKind.ValueType);
      case Procedure:
    	  if(type==null) return(ParameterKind.Procedure);
		  return(ParameterKind.TypeProcedure);
      case Array:
    	  if(type.isReferenceType()) return(ParameterKind.ReferenceTypeArray);
		  return(ParameterKind.ValueTypeArray);
      case Label: return(ParameterKind.Label);
      case Switch: return(ParameterKind.Switch);
      default: return(null);
    }
  }	

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
    Util.setLine(getLineNumber());
	//Util.BREAK("Specification("+this.toString()+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	// TODO: ??? hva checker vi ?
    SET_SEMANTICS_CHECKED();	  
  }

  public String edClassParamCoding()
  { 
//	String tp=type.toJavaType();
//    if(kind==Kind.Array) tp=tp+"[]";
//	return(tp + ' ' + getParameterIdentifier());
	
	return(toJavaCode());  // TODO: Check om dette stemmer !
  }
  
  public String toJavaCode()
  { ASSERT_SEMANTICS_CHECKED(this);
	if(type!=null)
    { if(mode==Mode.name)
      { if(kind==Kind.Simple)
        { //if(type.isReferenceType()) return("ObjectReferenceByName" + ' ' + identifier);
          //if(type==Type.Text) return("TextByName" + ' ' + identifier);
    	  return(Const.SIMPLE_BY_NAME+'<'+type.toJavaTypeClass()+">" + ' ' + getParameterIdentifier());
        }
        error(type.toJavaType()+' '+identifier+" by name is not supported - rewrite program");
      }
      if(kind==Kind.Array) return(type.toJavaType() + "[] " + getParameterIdentifier());
	  return(type.toJavaType() + ' ' + getParameterIdentifier());
    }
//    else return(identifier.toString());
    else return(getParameterIdentifier().toString());
  }

  public String toString()
  {	String s="";
    if(type!=null) s=s+type; else s="NOTYPE";
    if(mode!=null) s=""+mode+" "+type;
    if(kind==null) s=s+" NOKIND";
    else if(kind!=Kind.Simple) s=s+" "+kind;
  	return(s+' '+identifier);
  }

}
