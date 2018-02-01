package parser.declaration;

import common.DeclarationList;
import common.Util;
import common.Identifier;
import common.KeyWord;
import common.Type;
import compiler.NonTerminal;
import compiler.SimulaParser;

/**
 * Declaration.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * Declaration = TypeDeclaration | ArrayDeclaration | SwitchDeclaration
 *             | ProcedureDeclaration | ClassDeclaration | ExternalDeclaration
 *             
 *		TypeDeclaration = Type IdentifierList
 *
 *   	Type ::= BOOLEAN | CHARACTER | INTEGER | REAL | REF(ClassIdentifier) | TEXT
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public abstract class Declaration extends NonTerminal
{ //static public boolean DEBUG=true;//false; //true; 
  protected Identifier identifier; 
  public Identifier getIdentifier()
  { Util.ASSERT(identifier!=null,this.getClass().getName()+" has Identifier==null");
	  return(identifier);
  }
  
  public static boolean parseDeclaration(DeclarationList declarationList)
  { if(DEBUG) Util.log("Parse Declaration, current="+getCurrentToken()+", prev="+getPrevToken());
    Identifier prefix=acceptIdentifier();
    if(prefix!=null)
    { if(accept(KeyWord.CLASS)) declarationList.add(new ClassDeclaration(prefix)); 
      else
      { SimulaParser.saveCurrentToken(); // Identifier is NOT a class prefix.
        return(false);
      }
    }  
    else if(accept(KeyWord.ARRAY)) ArrayDeclaration.parse(Type.Real,declarationList);  // Default type real for arrays
    else if(accept(KeyWord.PROCEDURE)) declarationList.add(new ProcedureDeclaration(null));
    else if(accept(KeyWord.CLASS)) declarationList.add(new ClassDeclaration(null));
    else if(accept(KeyWord.SWITCH)) declarationList.add(new SwitchDeclaration());
    else if(accept(KeyWord.EXTERNAL)) declarationList.add(new ExternalDeclaration());
    else
    { Type type=acceptType(); if(type==null) return(false);
      parseTypeIdentifierList(type,declarationList);
      if(DEBUG) Util.log("Parse Declaration(2), current="+getCurrentToken()+", prev="+getPrevToken());
    }
    return(true);
  }
   
  private static void parseTypeIdentifierList(Type type,DeclarationList declarationList)
  { // identifier-list = identifier { , identifier }
    if(DEBUG) Util.log("Parse IdentifierList, current="+getCurrentToken()+", prev="+getPrevToken());
    if(accept(KeyWord.PROCEDURE)) declarationList.add(new ProcedureDeclaration(type));
    else if(accept(KeyWord.ARRAY)) ArrayDeclaration.parse(type,declarationList);
    else
    { do
      { Identifier ident=expectIdentifier();
        declarationList.add(new TypeDeclaration(type,ident));
      } while(accept(KeyWord.COMMA)); 
    }
  } 
  
}
