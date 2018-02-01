package parser.statement;

import common.DeclarationList;
import common.Identifier;
import common.Util;
import parser.declaration.ClassDeclaration;
import parser.declaration.ConnectedDeclaration;
import parser.declaration.Declaration;
import parser.declaration.Scope;
import parser.expression.Expression;

public class ConnectionBlock extends Statement implements Scope
{ String blockName;
  ClassDeclaration classDeclaration;
  Expression objectExpression;
  public ClassDeclaration getClassDeclaration() { return(classDeclaration); } 
  public Expression getObjectExpression() { return(objectExpression); } 
  
  public ConnectionBlock(Expression objectExpression)
  { blockName="Block"+getLineNumber();
    enclosure=currentScope; currentScope=this;
    this.objectExpression=objectExpression;
	//Debug.BREAK("BEGIN ConnectionBlock: "+this.edScopeChain());
    if(DEBUG) Util.log("Parse ConnectionBlock, current="+getCurrentToken()+", prev="+getPrevToken());
  }
  
  private DeclarationList declarationList=new DeclarationList();
  public DeclarationList getDeclarationList() { return(declarationList); }
  public String getScopeName() { return(blockName); }
  
  public void end() 
  {	if(DEBUG) Util.log("END ConnectionBlock: "+this.edScopeChain());
	//declarationMap.print("END ConnectionBlock: "+blockName);
	//Debug.BREAK("END ConnectionBlock: "+this.edScopeChain());
	currentScope=enclosure; 
  }

  public void setClassDeclaration(ClassDeclaration classDeclaration)
  { this.classDeclaration=classDeclaration; }
  
  public Declaration findDefinition(Identifier identifier)
  {	//Util.BREAK("ConnectionBlock("+getScopeName()+").findDefinition("+identifier+"): scope="+getScopeName());
    Declaration result=classDeclaration.findAttribute(identifier);
    //Util.BREAK("ConnectionBlock("+getScopeName()+").findDefinition("+identifier+"): result="+result);
    if(result!=null) result=new ConnectedDeclaration(result,this);
	if(result==null && enclosure!=null) result=enclosure.findDefinition(identifier);
	else if(result==null) error("Undefined variable: "+identifier);
   	//Util.TRACE("ConnectionBlock("+getScopeName()+").findDefinition("+identifier+"): result="+result.getEnclosureName()+'.'+result);   
    return(result);
  }
  
  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
   	Util.setLine(getLineNumber());
	Util.BREAK("BEGIN ConnectionBlock"+toString()+".doChecking - Current Scope Chain: "+currentScope.edScopeChain());
	objectExpression.doChecking();
	SET_SEMANTICS_CHECKED();
  }
  
  public String toString() { return("Inspect("+objectExpression+")"); }
}
