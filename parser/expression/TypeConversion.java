package parser.expression;

import common.Type;
import common.Util;
import common.Type.ConversionKind;
import compiler.NonTerminal;

public class TypeConversion extends Expression
{ //Type type; // Inherited
	NonTerminal expression;
  
  public TypeConversion(Type type,NonTerminal expression)
  { this.type=type; this.expression=expression;
    this.doChecking();
  }
  
  // Test if a TypeConversion is necessary and then create it.
  public static NonTerminal testAndCreate(Type type,NonTerminal expression)
  { //Util.BREAK("TypeConversion.testAndCreate("+type+','+expression+')');
    if(type==null) return(expression);
	if(expression.getType()==null && type.isReferenceType())
		  return(expression); // NONE is directly assignable to any ref.
	ConversionKind conversionKind=expression.getType().isConvertableTo(type);
	if(conversionKind==ConversionKind.DirectAssignable) return(expression);
	return(new TypeConversion(type,expression));
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
    //Util.BREAK("TypeConversion.doChecking(): "+this);
    expression.doChecking();
    Type type=expression.getType();
    //Util.BREAK("TypeConversion.doChecking(): Cast="+this.getType()+", expression.type="+type.toJavaType());
    if(type.isConvertableTo(this.type)==Type.ConversionKind.Illegal)
    	error("Illegal Type Conversion "+type+" ==> "+this.getType());
	SET_SEMANTICS_CHECKED();
  }
  
  public String toJavaCode()
  { ASSERT_SEMANTICS_CHECKED(this);
    if(type!=null)
    { if(expression.getType().isConvertableTo(this.type)==Type.ConversionKind.DirectAssignable)
    	   return("("+expression.toJavaCode()+")");
      else return("(("+type.toJavaType()+")("+expression.toJavaCode()+"))");
    } else return("("+expression.toJavaCode()+")");
  }
  
  public String toString()
  { return("(("+type+")("+expression+"))"); }

}
