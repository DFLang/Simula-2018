package parser.declaration;

import common.NameTable;
import common.Type;
import common.Util;

public class StandardProcedure extends ProcedureDeclaration
{
  public StandardProcedure(Scope enclosure,Type type, String ident)
  { this.enclosure=enclosure; this.type=type; this.identifier=NameTable.defineName(ident); }

  public StandardProcedure(Scope enclosure,Type type, String ident,Specification param)
  { this.enclosure=enclosure; this.type=type; this.identifier=NameTable.defineName(ident);
    getParameterList().add(param);
  }

  public StandardProcedure(Scope enclosure,Type type, String ident,Specification p1,Specification p2)
  { this.enclosure=enclosure; this.type=type; this.identifier=NameTable.defineName(ident);
    getParameterList().add(p1); getParameterList().add(p2);
  }

  public StandardProcedure(Scope enclosure,Type type, String ident,Specification p1,Specification p2,Specification p3)
  { this.enclosure=enclosure; this.type=type; this.identifier=NameTable.defineName(ident);
    getParameterList().add(p1); getParameterList().add(p2); getParameterList().add(p3);
  }

  public StandardProcedure(Scope enclosure,Type type, String ident,Specification p1,Specification p2,Specification p3,Specification p4)
  { this.enclosure=enclosure; this.type=type; this.identifier=NameTable.defineName(ident);
    getParameterList().add(p1); getParameterList().add(p2); getParameterList().add(p3); getParameterList().add(p4);
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
  	String name=this.getClass().getSimpleName();
	if(DEBUG) Util.log("BEGIN "+name+".doChecking");
	//super.doChecking();
	//procedureBody.doChecking();
	currentScope=enclosure;
	if(DEBUG) Util.log("END StandardProcedure("+toString()+").doChecking - Result type="+this.type);
    SET_SEMANTICS_CHECKED();
  }
  
  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    
	String modifier="public ";
	// Test om dette er en 'static' metode
	// TODO: Gjør dette litt mere elegant !
	StandardClass stdc=(StandardClass)this.getEnclosure();
	boolean isContext=stdc.getContextFlag();
	if(isContext) modifier="public static ";
	
    String line=modifier+((type==null)?"void":type.toJavaType());
	line=line+' '+getProcedureIdentifier()+' '+edFormalParameterList();
	Util.code(indent+line);
	//super.doCoding(indent);
	//procedureBody.doCoding(indent+"   ");
	//Util.code(indent+'}');
	currentScope=enclosure;
  }

  public String toString()
  { String pfx=""; if(type!=null) pfx=type.toString()+" ";
    return(pfx+"PROCEDURE "+identifier);//+super.toString());
  }

}
