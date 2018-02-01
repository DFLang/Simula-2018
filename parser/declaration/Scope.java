package parser.declaration;

import common.DeclarationList;
import common.Identifier;

public interface Scope
{
  public Scope getEnclosure();
  public String getScopeName();
  public DeclarationList getDeclarationList();
  public Declaration findDefinition(Identifier identifier);
  
  default public String edScopeChain()
  { if(getEnclosure()==null) return(getScopeName());
    String encName=getEnclosure().edScopeChain();
	return(getScopeName()+'.'+encName);
  }

}
