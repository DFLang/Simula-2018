package parser.declaration;

import java.util.Iterator;

import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.Type;
import parser.declaration.ClassDeclaration.ClassParameterIterator;
import parser.statement.Statement;

/**
 * Procedure Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ProcedureDeclaration
 *     = [ type ] PROCEDURE ProcedureIdentifier ProcedureHead ProcedureBody
 *     
 * ProcedureHead
 *     = [ FormalParameterPart ; [ ModePart ]
 *         specification-part  ] ;
 *         
 * ProcedureBody = Statement
 * ProcedureIdentifier = Identifier
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ProcedureDeclaration extends BlockHead
{ //static boolean DEBUG=true;//false; //true; 
  //private Type type;
  private Statement procedureBody;
  public Identifier getProcedureIdentifier() { return(identifier); }
  public DeclarationList getDeclarationList() { return(getParameterList()); }
  
  protected ProcedureDeclaration() {} // Used by StandardProcedure
  
  public ProcedureDeclaration(Type type)
  {	super(false);
    this.type=type;
    if(DEBUG) Util.log("Parse ProcedureDeclaration, type="+type+", current="+getCurrentToken()+", prev="+getPrevToken());
    procedureBody=Statement.doParse();
	if(DEBUG) Util.log("END ProcedureDeclaration: "+this.edScopeChain());
	//getSemanticMap().print("END ProcedureDeclaration: "+this.identifier);
	//Debug.BREAK("END ProcedureDeclaration: ");
	currentScope=getEnclosure();
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
  	String name=this.getClass().getSimpleName();
	if(DEBUG) Util.log("BEGIN "+name+".doChecking");
	super.doChecking();
	procedureBody.doChecking();
	currentScope=enclosure;
	if(DEBUG) Util.log("END ProcedureDeclaration("+toString()+").doChecking - Result type="+this.type);
    SET_SEMANTICS_CHECKED();
  }
  
  public Iterator<Declaration> parameterIterator()
  { return(getParameterList().iterator()); }
  
  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
    //Util.BREAK("ProcedureDeclaration.doCoding: "+getProcedureIdentifier());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    String line="public "+((type==null)?"void":type.toJavaType());
	line=line+' '+getProcedureIdentifier()+' '+edFormalParameterList();
	Util.code(indent+line);
	//super.doCoding(indent);
	procedureBody.doCoding(indent+"   ");
	Util.code(indent+'}');
	currentScope=enclosure;
  }

  public Declaration findDefinition(Identifier identifier)
  {	//Debug.log("BlockHead.findDefinition("+identifier+"): scope="+getScopeName());
	if(DEBUG) getParameterList().print("ProcedureDeclaration("+getScopeName()+").findDefinition("+identifier+") PARAMETER LIST","DEFINED: ");
	Declaration decl=getParameterList().lookup(identifier);
    if(decl==null)
	{ if(DEBUG) getVirtualMap().print("ProcedureDeclaration("+getScopeName()+").findDefinition("+identifier+") VIRTUAL LIST","DEFINED: ");
	  decl=getVirtualMap().lookup(identifier);
	  if(decl==null && enclosure!=null) decl=enclosure.findDefinition(identifier);
	  if(decl==null) error("Undefined variable: "+identifier);
	}
	Util.TRACE("ProcedureDeclaration("+getProcedureIdentifier()+").findDefinition("+identifier+"): result="+decl.getEnclosureName()+'.'+decl);  
    return(decl);
  }

  public void print(String indent,String tail)
  { StringBuilder s=new StringBuilder(indent);
    if(type!=null) s.append(type.toString()).append(' ');
    s.append("PROCEDURE ").append(identifier);
    s.append(editParameterList());
    System.out.println(s.toString());
    procedureBody.print(indent+"   ",tail);
  }
  
  public String toString()
  { String pfx=""; if(type!=null) pfx=type.toString()+" ";
    return(pfx+"PROCEDURE "+identifier+super.toString()); }
}
