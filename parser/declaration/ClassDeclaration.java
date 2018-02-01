package parser.declaration;

import java.util.Iterator;

import common.DeclarationList;
import common.Kind;
import common.Token;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;
import parser.statement.Block;
import parser.statement.Statement;

/**
 * Class Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ClassDeclaration = [ Prefix ] MainPart
 * 
 *	Prefix = ClassIdentifier
 *
 *    MainPart = CLASS ClassIdentifier  ClassHead  ClassBody
 *		ClassIdentifier = Identifier
 *
 *		ClassHead = [ FormalParameterPart ; [ ValuePart ] SpecificationPart ] ;
 *					 [ ProtectionPart ; ] [ VirtualPart ]
 *
 *			FormalParameterPart = "(" FormalParameter { , FormalParameter ")"
 *				FormalParameter = Identifier
 *			ValuePart = VALUE IdentifierList
 *			SpecificationPart = Specifier  IdentifierList ; { Specifier  IdentifierList ; }
 *				Specifier = Type [ ARRAY | PROCEDURE ] | LABEL | SWITCH
 *
 *			ProtectionPart = ProtectionSpecification { ; ProtectionSpecification }
 *				ProtectionSpecification = HIDDEN IdentifierList | HIDDEN PROTECTED IdentifierList
 *										| PROTECTED IdentifierList | PROTECTED HIDDEN IdentifierList
 *
 *			VirtualPart = VIRTUAL: virtual-specification-part
 *				VirtualSpecificationPart = VirtualSpecification ; { VirtualSpecification ; }
 *					VirtualSpecification = VirtualSpecifier IdentifierList
 *						VirtualSpecifier = [ type ] PROCEDURE | LABEL | SWITCH
 *
 *		ClassBody = SplitBody | Statement
 *			SplitBody = BEGIN [ { Declaration ; } ]  [ { Statement ; } ] InnerPart  [ { Statement ; } ] 
 *				InnerPart = [ Label : ] INNER ;
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ClassDeclaration extends BlockHead
{ 
  protected Identifier prefix;
  private Statement classBody;
  public DeclarationList getDeclarationList() { return(getParameterList()); }
  
  protected ClassDeclaration() {} // Used by StandardClass

  public ClassDeclaration(Identifier prefix)
  { super(true);
	this.prefix=prefix;
	if(this.prefix==null) this.prefix=StandardClass.OBJECT.getIdentifier();
	this.type=Type.Ref(new Token(KeyWord.IDENTIFIER,identifier));
    if(DEBUG) Util.log("Parse ClassDeclaration, current="+getCurrentToken()+", prev="+getPrevToken());
    classBody=Statement.doParse();
	if(DEBUG) Util.log("END ClassDeclaration: "+this.edScopeChain());
	//getSemanticMap().print("END ClassDeclaration: "+this.identifier);
	//Debug.BREAK("END ClassDeclaration: ");
	currentScope=getEnclosure();
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
	String name=this.getClass().getSimpleName();
	if(DEBUG) Util.log("BEGIN "+name+".doChecking");
	super.doChecking();
    for(Iterator<Declaration> it=localParameterIterator();it.hasNext();)
    { Specification spec=(Specification)it.next();
      spec.createParameterIdentifier();
    }
	classBody.doChecking();
	currentScope=enclosure;
	if(DEBUG) Util.log("END ClassDeclaration("+toString()+").doChecking - Result type="+this.type);
    SET_SEMANTICS_CHECKED();
  }

  private String edSuperParameterList()
  { StringBuilder s=new StringBuilder(); s.append('(');
    for(Iterator<Declaration> it=parameterIterator();it.hasNext();) // Iterates through prefix-chain
    { Specification spec=(Specification)it.next();
      s.append(spec.getParameterIdentifier());
      if(it.hasNext()) s.append(',');
    }
    s.append(");");
    return(s.toString());
  }

  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    String line="public class "+getIdentifier();
	if(prefix!=null) line=line+" extends "+prefix;
	Util.code(indent+line+" {");
	Util.code(indent+"   // Declare parameters as attributes");
	for(Iterator<Declaration> it=localParameterIterator();it.hasNext();)
	{ Specification par=(Specification)it.next();
	  String tp=par.getType().toJavaType();
	  if(par.getKind()==Kind.Array) tp=tp+"[]";
	  Util.code(indent+"   public "+tp+' '+par.getIdentifier()+';');
	}
	Util.code(indent+"   // Declare locals as attributes");
	if(classBody instanceof Block)
	{ Block block=(Block)classBody;
	  for(Iterator<Declaration> it=block.getDeclarationList().iterator();it.hasNext();)
		  it.next().doCoding(indent+"   ");
	}
	
	//super.doCoding(indent);
	Util.code(indent+"   // Constructor");
	line="public "+getIdentifier()+edFormalParameterList();
	Util.code(indent+"   "+line);
	if(prefix!=null) 
	{ ClassDeclaration prefixClass=this.getPrefix();
		Util.code(indent+"      "+"super"+prefixClass.edSuperParameterList());
	}
	Util.code(indent+"      // Parameter assignment to locals");
	for(Iterator<Declaration> it=localParameterIterator();it.hasNext();)
	{ Specification par=(Specification)it.next();
	  Util.code(indent+"      "+par.getIdentifier()+" = "+par.getParameterIdentifier()+';');
	}
	Util.code(indent+"      // Class Body");
	if(classBody instanceof Block) 
	{ Block block=(Block)classBody;
	  block.doCodeStatements(indent+"      ");
	} else classBody.doCoding(indent+"      ");
	Util.code(indent+"   "+'}'); // End of Constructor
	
	Util.code(indent+'}'); // End of Class
	currentScope=enclosure;
  }

  /**
   * Consider the class definitions:
   * <pre> 
   *      Class A ......;
   *    A Class B ......;
   *    B Class C ......;
   * </pre>
   * Then Class B is a subclass of Class A, While Class C is subclass of both B and A.
   * @param other
   * @return Boolean true iff this class is a subclass of the 'other' class.
   */
  public boolean isSubClassOf(ClassDeclaration other)
  { ClassDeclaration prefixClass=getPrefix();
    if(DEBUG) Util.log("ClassDeclaration: ("+this+").isSubClassOf("+other+')');
    if(prefixClass!=null) do
    { //Util.BREAK("ClassDeclaration.isSubClassOf: prefix="+prefixClass);
      if(other==prefixClass)
      { //Util.BREAK("ClassDeclaration: ("+this.getIdentifier()+").isSubClassOf("+other.getIdentifier()+") Returns TRUE");
    	  return(true);
      }
    } while((prefixClass=prefixClass.getPrefix())!=null);
    //Util.BREAK("ClassDeclaration: ("+this.getIdentifier()+").isSubClassOf("+other.getIdentifier()+") Returns FALSE");
    return(false);
  }
  
  public Declaration findDefinition(Identifier identifier)
  {	//Util.BREAK("ClassDeclaration("+getIdentifier()+").findDefinition("+identifier+"): scope="+getScopeName());
	if(DEBUG) getParameterList().print("ClassDeclaration("+getScopeName()+").findDefinition("+identifier+") PARAMETER LIST","DEFINED: ");
	Declaration decl=findAttribute(identifier);
	if(decl==null && enclosure!=null) decl=enclosure.findDefinition(identifier);
	if(decl==null) error("Undefined variable: "+identifier);
	//Util.BREAK("ClassDeclaration("+getIdentifier()+").findDefinition("+identifier+"): result="+decl.getEnclosureName()+'.'+decl);
    return(decl);
  }
  
  public Declaration findAttribute(Identifier ident)
  { Declaration result=null;
   	//Util.BREAK("ClassDeclaration("+getIdentifier()+").findAttribute("+ident+"): scope="+getScopeName());
    if(classBody instanceof Block)
    { Block classBlock=(Block)classBody;
	  result=classBlock.getDeclarationList().lookup(ident);
    }
    if(result==null) result=this.getDeclarationList().lookup(ident);
    if(result==null) result=this.getVirtualMap().lookup(ident);
    
    if(result==null)
    { //Util.BREAK("TODO: ClassDeclaration.findAttribute("+ident+"): Search in prefix(chain)");
      ClassDeclaration prfx=getPrefix();
      //Util.BREAK("ClassDeclaration.findAttribute: Class "+identifier+" has prefix "+prfx);
      if(prfx!=null) result=prfx.findAttribute(ident);
//      if(prfx==null) result=StandardClass.OBJECT.findAttribute(ident);  // AD.HOC !!!
    }
   	if(result!=null) Util.log("END ClassDeclaration("+getIdentifier()+").findAttribute("+ident+"): result="+result.getEnclosureName()+'.'+result);   
    return(result);
  }
  
  public ClassDeclaration getPrefix()
  { //Util.BREAK("ClassDeclaration.getPrefix: "+prefix);
	if(prefix==null) return(null);
//    Declaration decl=getEnclosure().findDefinition(prefix);
    Scope encl=getEnclosure();
    //Util.BREAK("ClassDeclaration.getPrefix: "+prefix+", Search in "+encl.getScopeName());
    Declaration decl=encl.findDefinition(prefix);
    if(decl==null) error("Undefined prefix: "+prefix);
    if(decl==this) error("Class prefix chain loops");
	if(decl instanceof ClassDeclaration) return((ClassDeclaration)decl); 
	if(decl instanceof StandardClass) return((ClassDeclaration)decl); 
	error("Prefix "+prefix+" is not a Class, but "+decl.getClass().getSimpleName());
	return(null);
  }
  
  class ClassParameterIterator implements Iterator<Declaration>
  { Iterator<Declaration> prefixIterator;
    Iterator<Declaration> localIterator;
    public ClassParameterIterator()
    { ClassDeclaration prefix=getPrefix();
      if(prefix!=null) prefixIterator=prefix.parameterIterator();
	  localIterator = getDeclarationList().iterator();
    	
    }
	public boolean hasNext()
	{ if(prefixIterator!=null)
	  { if(prefixIterator.hasNext()) return(true);
	    prefixIterator=null;
	  }
	  return(localIterator.hasNext());
	}
	public Declaration next()
	{ if(!hasNext()) return(null);
	  if(prefixIterator!=null) return(prefixIterator.next());
	  return(localIterator.next());
	}  
  }
  
  public Iterator<Declaration> parameterIterator()
  { return(new ClassParameterIterator()); }
  
  public void print(String indent,String tail)
  { StringBuilder s=new StringBuilder(indent);
    if(prefix!=null) s.append(prefix).append(' ');
    s.append("CLASS ").append(identifier);
    s.append(editParameterList());
    System.out.println(s.toString());
    classBody.print(indent,tail);
  }

  public String toString()
  { String pfx=""; if(prefix!=null) pfx=prefix.toString()+" ";
	  return(pfx+"CLASS "+identifier+super.toString());
  }
}
