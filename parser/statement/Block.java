package parser.statement;

import java.util.Iterator;
import java.util.Vector;

import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;
import examples.compiled.SubClassSample.C;
import parser.declaration.ClassDeclaration;
import parser.declaration.Declaration;
import parser.declaration.Scope;
import parser.expression.Expression;
import parser.expression.Variable;

/**
 * Block.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * Block = CompoundStatement | SplitBody | [ Prefix ] BlockBody
 * 
 * 	 SplitBody = BEGIN [ { Declaration ; } ]  [ { Statement ; } ] InnerPart  [ { Statement ; } ] 
 *	 BlockBody = BEGIN [ { Declaration ; } ]  [ { Statement ; } ] END 
 *	 CompoundStatement = BEGIN [ { Statement ; } ] END
 *		InnerPart = [ Label : ] INNER ;
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class Block extends Statement implements Scope
{ // static boolean DEBUG=true;//false; //true; 
  Expression prefix;
  String blockName;
  private Vector<Statement> statements=new Vector<Statement>();
  private Vector<Statement> finalStatements=new Vector<Statement>(); // Statements after INNER
 
  private Scope enclosure;
  public Scope getEnclosure() { return(enclosure); }
  public String getScopeName() { return(blockName); }
  
  private DeclarationList declarationList=new DeclarationList();
  public DeclarationList getDeclarationList() { return(declarationList); }
  
  public Block(Expression prefix)
  { this.prefix=prefix;
    blockName="Block"+getLineNumber();
	Statement stm;
	enclosure=currentScope; currentScope=this;
	//Debug.BREAK("BEGIN Block: "+this.edScopeChain());
    if(DEBUG) Util.log("Parse Block, current="+getCurrentToken()+", prev="+getPrevToken());
    while(Declaration.parseDeclaration(declarationList))
    { expect(KeyWord.SEMICOLON); }
    while((stm=Statement.doParse())!=null) statements.add(stm);
    if(accept(KeyWord.INNER))
    { while((stm=Statement.doParse())!=null) finalStatements.add(stm); }
    if(!expect(KeyWord.END)) error("parseBlock - Expecting END");
	//declarationMap.print("END Block: "+blockName);
	//Debug.BREAK("END Block: "+this.edScopeChain());
	currentScope=enclosure;
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
    currentScope=this;
    Util.log("Begin Checking of "+getScopeName()+" - Current Scope Chain: "+edScopeChain());
    if(prefix!=null) prefix.doChecking();
    
    for(Iterator<Declaration> it=declarationList.iterator();it.hasNext();) it.next().doChecking();
    for(Iterator<Statement> it=statements.iterator();it.hasNext();) it.next().doChecking();
    if(!finalStatements.isEmpty())
	{ for(Iterator<Statement> it=finalStatements.iterator();it.hasNext();) it.next().doChecking(); }
	currentScope=enclosure;
    SET_SEMANTICS_CHECKED();
  }
  
  public void doCoding(String indent)
  { if(prefix!=null) doCodePrefixedBlock(indent);
    else
    { Util.code(indent+'{');
      doCodingWithoutBrackets(indent+"   ");
      Util.code(indent+'}');
    }
  }
  
  public void doCodingWithoutBrackets(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    if(prefix!=null) doCodePrefixedBlock(indent);
    else
    {
//    Util.log("Begin Coding of "+getScopeName()+" - Current Scope Chain: "+edScopeChain());
//    Util.code(indent+'{');
      for(Iterator<Declaration> it=declarationList.iterator();it.hasNext();) it.next().doCoding(indent);
      doCodeStatements(indent);
//    Util.code(indent+'}');
	  currentScope=enclosure;
	}
  }
  
  private void doCodePrefixedBlock(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    //Util.BREAK("Block("+blockName+").doCodePrefixedBlock: prefix'name="+prefix.getClass().getName());
    String pre=prefix.toJavaCode();
    if(!pre.endsWith(")")) pre=pre+"()";
    Util.code(indent+"{ new "+pre+" { // Prefixed Block !"); // NB: parametere
    for(Iterator<Declaration> it=declarationList.iterator();it.hasNext();) it.next().doCoding(indent+"     ");
    Util.code(indent+"     public void Statements() {");	  
    doCodeStatements(indent+"        ");
    Util.code(indent+"     }");
    Util.code(indent+"  }.Statements();");
    Util.code(indent+"}");
  }
  
  public void doCodeStatements(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    currentScope=this;
    Iterator<Statement> iterator2=statements.iterator();
    while(iterator2.hasNext()) iterator2.next().doCoding(indent);
	
    if(!finalStatements.isEmpty())
	{ Iterator<Statement> iterator3=finalStatements.iterator();
	  while(iterator3.hasNext()) iterator3.next().doCoding(indent);
	}
  }

  public Declaration findDefinition(Identifier identifier)
  {	//Util.BREAK("Block("+getScopeName()+").findDefinition("+identifier+"): scope="+getScopeName());
	if(DEBUG) declarationList.print("Block("+getScopeName()+").findDefinition("+identifier+") LOCAL DECLARATION LIST","DEFINED: ");
	Declaration result=declarationList.lookup(identifier);
	if(result!=null) return(result);
    //Debug.BREAK("TODO: Block("+getScopeName()+").findDefinition("+identifier+"): Search in prefix(chain)");
    
    ClassDeclaration prfx=getPrefix();
    //Debug.BREAK("Block.findDefinition: Block has prefix "+prfx+", enclosure="+enclosure);
    if(prfx!=null) result=prfx.findAttribute(identifier);
    //Debug.BREAK("Block.findDefinition: Block has prefix "+prfx+", result="+result);
    
	if(result==null && enclosure!=null) result=enclosure.findDefinition(identifier);
	else if(result==null) error("Undefined variable: "+identifier);
   	//Util.TRACE("END Block("+getScopeName()+").findDefinition("+identifier+"): result="+result.getEnclosureName()+'.'+result);   
    return(result);
  }
  
  public ClassDeclaration getPrefix()
  { if(prefix==null) return(null);
    //Util.BREAK("Block.getPrefix("+prefix+"), Class="+prefix.getClass().getName());
    currentScope=getEnclosure();
    prefix.doChecking(); 
    currentScope=this;
    Type prefixType=prefix.getType();
    Variable varPrfx=(Variable)prefix;
    Identifier prefixIdentifier=varPrfx.getIdentifier();
    //Util.BREAK("Block.getPrefix("+prefix+") Type="+prefixType+", prefixIdentifier="+prefixIdentifier);
    Declaration decl=getEnclosure().findDefinition(prefixIdentifier);
    if(decl==null) error("Undefined prefix: "+prefix);
	if(decl instanceof ClassDeclaration) return((ClassDeclaration)decl); 
	error("Prefix "+prefix+" is not a Class");
	return(null);
  }
  
  public void print(String indent,String tail)
  { String beg="begin["+edScopeChain()+']'; if(prefix!=null) beg=prefix+" "+beg;
    System.out.println(indent+beg); 
    for(Iterator<Declaration> it=declarationList.iterator();it.hasNext();) it.next().print(indent+"   ",";");
    for(Iterator<Statement> it=statements.iterator();it.hasNext();) it.next().print(indent+"   ",";");
	if(!finalStatements.isEmpty())
	{ System.out.println(indent+"   "+"inner"); 
	  for(Iterator<Statement> it=finalStatements.iterator();it.hasNext();) it.next().print(indent+"   ",";");		
	}
	System.out.println(indent+"end["+edScopeChain()+']'+tail); 
  }
  
  public String toString()
  { return(""+prefix+" BEGIN "+declarationList+' '+statements+" END"); }

}
