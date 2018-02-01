package parser.statement;

import common.Mode;
import common.Type.ConversionKind;
import common.Token;
import common.Util;
import common.KeyWord;
import common.Type;
import compiler.NonTerminal;
import parser.declaration.Declaration;
import parser.declaration.Specification;
import parser.declaration.Specification.ParameterKind;
import parser.expression.Expression;
import parser.expression.TypeConversion;
import parser.expression.Variable;

/**
 * Assignment Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 *  AssignmentStatement = ValueAssignment | ReferenceAssignment
 *  
 *     ValueAssignment = ValueLeftPart  :=  ValueRightPart
 *  		ValueLeftPart = Variable | ProcedureIdentifier | SimpleTextExpression
 *  		ValueRightPart = ValueExpression | TextExpression | ValueAssignment
 *  
 *     ReferenceAssignment = ReferenceLeftPart  :-  ReferenceRightPart
 *  		ReferenceLeftPart = Variable | ProcedureIdentifier
 *  		ReferenceRightPart = ReferenceExpression> | ReferenceAssignment
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class AssignmentStatement extends Statement
{ //static boolean DEBUG=true; 
  private Expression leftPart;
  private Token assignmentOperator;
  private NonTerminal rightPart;  // Expression or AssignmentStatement
  private boolean textValueAssignment=false;

  public AssignmentStatement(Expression leftPart)
  { if(DEBUG) Util.log("Parse parseAssignmentStatement, current="+getCurrentToken()+", prev="+getPrevToken());
    this.leftPart=leftPart;
    //try
    { assignmentOperator=getPrevToken();
      rightPart=Expression.parseExpression();
      if(DEBUG) Util.log("Parse parseAssignmentStatement(2), current="+getCurrentToken()+", prev="+getPrevToken());
      if(accept(KeyWord.ASSIGN)) rightPart=new AssignmentStatement((Expression)rightPart);
    } //catch(Error e) { e.printStackTrace(); }
  }
	

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
   	String name=this.getClass().getSimpleName();
	if(DEBUG) Util.log("BEGIN "+name+".doChecking");
	leftPart.doChecking();
	if(rightPart instanceof Expression)
	{
		((Expression)rightPart).doChecking(leftPart.getType());  // TODO: ???  (leftPart.type)  ???
		
	} else rightPart.doChecking();  // TODO: ???  (leftPart.type)  ???
	Type type1=leftPart.getType();
	Type type2=rightPart.getType();
	if(DEBUG) Util.log("BEGIN AssignmentStatement("+toString()+").doChecking - ("+type1+assignmentOperator+type2+")");
	if(DEBUG) Util.log("AssignmentStatement, operator: "+assignmentOperator.getValue());
	if(assignmentOperator.getValue()==KeyWord.ASSIGNVALUE)
	{ if(DEBUG) Util.log("AssignmentStatement: Value Assignment, operator: "+assignmentOperator.getValue());
	  this.textValueAssignment=(type1==Type.Text);
	  
	  this.type=null;//checkDirectAssignableValueTypes(type1,type2);
	  ConversionKind conversionKind=type2.isConvertableTo(type1);
	  if(conversionKind==ConversionKind.DirectAssignable) this.type=type1;
	  else if(conversionKind==ConversionKind.ConvertValue)
	  { rightPart=new TypeConversion(type1,rightPart); this.type=type1; }
	  else error("AssignmentStatement("+this+"): Illegal types: "+type1+" := "+type2);
	}
	else if(assignmentOperator.getValue()==KeyWord.ASSIGNREF)
	{ if(DEBUG) Util.log("AssignmentStatement: Reference Assignment, operator: "+assignmentOperator.getValue());
	
	  this.type=null;//checkDirectAssignableReferenceTypes(type1,type2);
	  ConversionKind conversionKind=type2.isConvertableTo(type1);
	  if(conversionKind==ConversionKind.DirectAssignable) this.type=type1;
	  
	  if(this.type==null)
	  { rightPart=new TypeConversion(type1,rightPart); this.type=type1;
		//  error("AssignmentStatement("+this+"): Illegal types: "+type1+" :- "+type2);
	  }
	}
	if(DEBUG) Util.log("CHECKED AssignmentStatement("+toString()+").doChecking - Result type="+this.type+"("+type1+assignmentOperator+type2+")");
	if(this.type==null) error("AssignmentStatement: Illegal types: "+type1+" := "+type2);
    SET_SEMANTICS_CHECKED();
  }

  public String toJavaCode()
  {	ASSERT_SEMANTICS_CHECKED(this);
    StringBuilder s=new StringBuilder();
    boolean isProcedure=false; // Check assignment to return value
    // Check for return value  TODO: Dette bør gjøres på en annen måte - via en result-variabel og tilslutt  return(result)
//    if(leftPart instanceof Variable) isProcedure=((Variable)leftPart).isProcedure();
    if(leftPart instanceof Variable) isProcedure=Variable.isProcedure(((Variable)leftPart).getSemantic());
    if(isProcedure) { s.append("return(").append(rightPart.toJavaCode()+")"); }
    
    //else if(this.textValueAssignment) s.append("textValueAssignment(").append(leftPart.toJavaCode()).append(',').append(rightPart.toJavaCode());
    else if(this.textValueAssignment) s.append(leftPart.toJavaCode()).append(".ASSIGN(").append(rightPart.toJavaCode()).append(')');
    else s.append(leftPart.put(rightPart));
   
    return(s.toString());
  }

  public String toString()
  { StringBuilder s=new StringBuilder();
    s.append(leftPart).append(assignmentOperator).append(rightPart);
    return(s.toString());
  }

}
