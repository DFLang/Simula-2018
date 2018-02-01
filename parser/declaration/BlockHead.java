package parser.declaration;

import java.util.Iterator;

import parser.declaration.ClassDeclaration.ClassParameterIterator;
import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Kind;
import common.Type;
import common.Mode;

/**
 * Common Head for Class and Procedure Declarations.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * ProcedureHead = [ FormalParameterPart ; [ ModePart ] ParameterPart  ] ;
 *
 * ClassHead = [ FormalParameterPart ; [ ValuePart ] ParameterPart ] ;
 *			 [ ProtectionPart ; ] [ VirtualPart ]
 *
 *	FormalParameterPart = "(" FormalParameter { , FormalParameter } ")"
 *		FormalParameter = Identifier
 *	
 *	ModePart = ValuePart [ NamePart ] | NamePart [ ValuePart ]
 *		ValuePart = VALUE IdentifierList ;
 *		NamePart  = NAME  IdentifierList ;
 *	
 *	ParameterPart = Parameter ; { Parameter ; }
 *		Parameter = Specifier IdentifierList
 *			Specifier = Type [ ARRAY | PROCEDURE ] | LABEL | SWITCH
 *
 *	ProtectionPart = ProtectionParameter { ; ProtectionParameter }
 *		ProtectionParameter = HIDDEN IdentifierList | HIDDEN PROTECTED IdentifierList
 *								| PROTECTED IdentifierList | PROTECTED HIDDEN IdentifierList
 *
 *	VirtualPart = VIRTUAL: virtual-specification-part
 *		VirtualParameterPart = VirtualParameter ; { VirtualParameter ; }
 *			VirtualParameter = VirtualSpecifier IdentifierList
 *				VirtualSpecifier = [ type ] PROCEDURE | LABEL | SWITCH
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public abstract class BlockHead extends Declaration implements Scope
{ private DeclarationList parameterList=new DeclarationList();
  private DeclarationList virtualList=new DeclarationList();
  
  public abstract Iterator<Declaration> parameterIterator(); // Iterator over parameters through prefix-chain.
  public Iterator<Declaration> localParameterIterator() { return(parameterList.iterator()); }
  
  public DeclarationList getParameterList() { return(parameterList); }
  public boolean hasNoParameter() { return(parameterList.isEmpty()); }
  public DeclarationList getVirtualMap() { return(virtualList); }
  public String getScopeName() { return(this.identifier.toString()); }
  public Scope getEnclosure() { return(enclosure); }
  
  protected BlockHead() {} // Used by TypeText
 
  public BlockHead(boolean inClass)
  { enclosure=currentScope; currentScope=this;
    this.identifier=expectIdentifier();
	//Debug.BREAK("BEGIN BlockHead: "+this.edScopeChain());
    if(DEBUG) Util.log("Begin CommonHead, current="+getCurrentToken()+", prev="+getPrevToken());
	if(accept(KeyWord.BEGPAR))
	{ if(accept(KeyWord.ENDPAR)) { expect(KeyWord.ENDPAR); return; }
	  do //ParameterPart = Parameter ; { Parameter ; }
	  { parameterList.add(new Specification(expectIdentifier()));
	  } while(accept(KeyWord.COMMA));  
	  expect(KeyWord.ENDPAR); expect(KeyWord.SEMICOLON);
	  // ModePart = ValuePart [ NamePart ] | NamePart [ ValuePart ]
	  //   ValuePart = VALUE IdentifierList ;
	  //   NamePart  = NAME  IdentifierList ;
      if(accept(KeyWord.VALUE)) { expectModeList(Mode.value); expect(KeyWord.SEMICOLON); }
      if(!inClass) // Parameter by NAME is not defined for Classes
      { if(accept(KeyWord.NAME))  { expectModeList(Mode.name); expect(KeyWord.SEMICOLON); }
        if(accept(KeyWord.VALUE)) { expectModeList(Mode.value); expect(KeyWord.SEMICOLON); }
      }
      // ParameterPart = Parameter ; { Parameter ; }
      //	Parameter = Specifier IdentifierList
      //		Specifier = Type [ ARRAY | PROCEDURE ] | LABEL | SWITCH
      while(acceptParameterSpecifications()) { expect(KeyWord.SEMICOLON); } 
    } else expect(KeyWord.SEMICOLON);
	  
    if(inClass) // HIDDEN, PROTECTED and VIRTUAL is not defined for Procedures
    { // ProtectionPart = ProtectionParameter { ; ProtectionParameter }
      //	ProtectionParameter = HIDDEN IdentifierList | HIDDEN PROTECTED IdentifierList
      //							| PROTECTED IdentifierList | PROTECTED HIDDEN IdentifierList
      if(accept(KeyWord.HIDDEN)||accept(KeyWord.PROTECTED))
      { Util.NOT_IMPLEMENTED("Protection-part");	}

      // VirtualPart = VIRTUAL: virtual-specification-part
      //	VirtualParameterPart = VirtualParameter ; { VirtualParameter ; }
      //		VirtualParameter = VirtualSpecifier IdentifierList
      //			VirtualSpecifier = [ type ] PROCEDURE | LABEL | SWITCH
      if(accept(KeyWord.VIRTUAL))
      { expect(KeyWord.COLON);
        // VirtualParameterPart
    	//     = VirtualParameter ; { VirtualParameter ; }
        while(acceptVirtualSpecifications()) { expect(KeyWord.SEMICOLON); } 
      }
    }
  }
  
  private void expectModeList(Mode mode)
  { do
	{ Identifier identifier=expectIdentifier();
	  Specification parameter=(Specification)parameterList.lookupParameter(identifier);
	  parameter.setMode(mode);
	} while(accept(KeyWord.COMMA));  
  }
  
  private boolean acceptParameterSpecifications()
  { // Parameter = Specifier IdentifierList
	// Specifier = Type [ ARRAY | PROCEDURE ] | LABEL | SWITCH
	if(DEBUG) Util.log("Parse ParameterSpecifications, current="+getCurrentToken()+", prev="+getPrevToken());
	Type type;
	Kind kind=Kind.Simple;
    if(accept(KeyWord.SWITCH))  type=Type.Switch; 
    else if(accept(KeyWord.LABEL)) type=Type.Label; 
    else if(accept(KeyWord.PROCEDURE)) { type=null; kind=Kind.Procedure; }
    else
    { type=acceptType(); if(type==null) return(false);    	
      if(accept(KeyWord.ARRAY)) kind=Kind.Array; 
      else if(accept(KeyWord.PROCEDURE)) kind=Kind.Procedure;
    }
    do
    { Identifier identifier=expectIdentifier();
	  Specification parameter=(Specification)parameterList.lookupParameter(identifier);
	  parameter.setTypeAndKind(type,kind);
    } while(accept(KeyWord.COMMA));  
    return(true);
  }
  
  private boolean acceptVirtualSpecifications()
  {	// VirtualParameter = VirtualSpecifier IdentifierList
	// VirtualSpecifier = [ Type ] PROCEDURE | LABEL | SWITCH
	if(DEBUG) Util.log("Parse VirtualParameter, current="+getCurrentToken()+", prev="+getPrevToken());
	Type type;
	Kind kind=Kind.Simple;
    if(accept(KeyWord.SWITCH))  type=Type.Switch; 
    else if(accept(KeyWord.LABEL)) type=Type.Label; 
    else
    { type=acceptType();
      if(accept(KeyWord.PROCEDURE)) kind=Kind.Procedure;
      else return(false);
    }
    do
    { Identifier identifier=expectIdentifier();
      getVirtualMap().add(new Specification(identifier,type,kind));
    } while(accept(KeyWord.COMMA));  
    return(true);
  }
    
  protected String editParameterList()
  { StringBuilder s=new StringBuilder(); s.append('(');
    for(Iterator<Declaration> it=localParameterIterator();it.hasNext();)
    { s.append(it.next()); if(it.hasNext()) s.append(','); }
    s.append(')');
    if(!getVirtualMap().isEmpty())
    { s.append(" VIRTUAL: ");
      for(Iterator<Declaration> it=getVirtualMap().iterator();it.hasNext();)
      { s.append(it.next()); if(it.hasNext()) s.append(','); }
    }
    s.append(';');
    return(s.toString());
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
	currentScope=this;
//    for(Iterator<Declaration> it=localParameterIterator();it.hasNext();)
    for(Iterator<Declaration> it=parameterIterator();it.hasNext();)
    { Specification parameter=(Specification)it.next();
      parameter.doChecking();
      if(parameter.getType()==null && parameter.getKind()!=Kind.Procedure)
    	  error("Missing specification of parameter: "+parameter.identifier);
    }
    SET_SEMANTICS_CHECKED();
  }
  
//  public void doCoding(String indent)
//  {	Util.setCodeLinePrefix(getLineNumber());
//    
//  }

  protected String edFormalParameterList()   // Accumulates through prefix-chain when class
  { StringBuilder s=new StringBuilder(); s.append('(');
    for(Iterator<Declaration> it=parameterIterator();it.hasNext();)
    { s.append(it.next().toJavaCode()); if(it.hasNext()) s.append(','); }
    s.append(") {");
    return(s.toString());
  }
  
  public String toString()
  { return(editParameterList()); }

}

