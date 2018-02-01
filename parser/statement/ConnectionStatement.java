package parser.statement;

import java.util.Enumeration;
import java.util.Vector;

import common.Type;
import common.Util;
import common.Identifier;
import common.KeyWord;
import compiler.NonTerminal;
import parser.declaration.ClassDeclaration;
import parser.declaration.Declaration;
import parser.expression.Expression;

/**
 * Connection Statement.
 * 
 * <pre>
 * 
 * Syntax:
 * 
 * connection-statement = INSPECT object-expression  connection-part [ OTHERWISE statement ]
 *		connection-part = DO statement | selective-part
 *			selective-part = { WHEN <class identifier> DO <statement> }
 *
 * </pre>
 * 
 * @author Øystein Myhre Andersen
 */
public class ConnectionStatement extends Statement
{ //static boolean DEBUG=true;//false; //true; 
  Expression objectExpression;
  Vector<DoPart> connectionPart=new Vector<DoPart>();
  Statement otherwise;
  boolean hasWhenPart;

  public ConnectionStatement()
  { if(DEBUG) Util.log("Parse ConnectionStatement, current="+getCurrentToken()+", prev="+getPrevToken());
    objectExpression=Expression.parseExpression();
    if(accept(KeyWord.DO))
    { ConnectionBlock connectionBlock=new ConnectionBlock(objectExpression);
      Statement statement=Statement.doParse();
      connectionPart.add(new DoPart(connectionBlock,statement));
      connectionBlock.end();
    }
    else
    { while(accept(KeyWord.WHEN))
      { Identifier classIdentifier=expectIdentifier();
        expect(KeyWord.DO);
        ConnectionBlock connectionBlock=new ConnectionBlock(objectExpression);
        hasWhenPart=true;
        Statement statement=Statement.doParse();
        connectionPart.add(new WhenPart(classIdentifier,connectionBlock,statement));
        connectionBlock.end();
      }
    	
    }
    otherwise=null;
    if(accept(KeyWord.OTHERWISE)) otherwise=Statement.doParse();
    if(DEBUG) Util.log("END NEW ConnectionStatement: "+toString());
  }

  class DoPart
  { Identifier refIdentifier;
    ConnectionBlock connectionBlock;
    Statement statement;
	public DoPart(ConnectionBlock connectionBlock,Statement statement)
	{ this.connectionBlock=connectionBlock; this.statement=statement;
	  if(DEBUG) Util.log("NEW DoPart: " + toString());
	}

	public void doChecking()
	{ Type type=objectExpression.getType();
	  refIdentifier=type.getRefIdent();
	  
      //Util.BREAK("ConnectionStatement.DoPart.doChecking: ");
	  if(refIdentifier!=null)
	  {	Declaration decl = NonTerminal.currentScope.findDefinition(refIdentifier);
		if(decl instanceof ClassDeclaration) connectionBlock.setClassDeclaration((ClassDeclaration)decl);
		else error("ConnectionStatement("+this+") "+refIdentifier+" is not a class");  
	  }
	  currentScope=connectionBlock;	
      Util.log("Begin Checking of "+connectionBlock.getScopeName()+" - Current Scope Chain: "+connectionBlock.edScopeChain());
	  statement.doChecking();
	  currentScope=enclosure;
	  SET_SEMANTICS_CHECKED();
	}
	
	public void doCoding(String indent,boolean first)
	{ //Util.BREAK("ConnectionStatement.DoPart.doCoding: statement="+statement.getClass().getName());
	  ASSERT_SEMANTICS_CHECKED(this);
	  statement.doCoding(indent+"   ");  
	}
	
	public String toString() { return("DO "+statement); }
  }

  class WhenPart extends DoPart
  { Identifier classIdentifier;
	public WhenPart(Identifier classIdentifier,ConnectionBlock connectionBlock,Statement statement)
	{ super(connectionBlock,statement);
	  this.classIdentifier=classIdentifier;
	  if(DEBUG) Util.log("NEW DoPart: " + toString());
	}

	public void doChecking()
	{ if(classIdentifier==null && objectExpression!=null)
	  { Type type=objectExpression.getType();
	    classIdentifier=type.getRefIdent();
	  }
      //Util.BREAK("ConnectionStatement.DoPart.doChecking: ");
	  if(classIdentifier!=null)
	  {	Declaration decl = NonTerminal.currentScope.findDefinition(classIdentifier);
		if(decl instanceof ClassDeclaration) connectionBlock.setClassDeclaration((ClassDeclaration)decl);
		else error("ConnectionStatement("+this+") "+classIdentifier+" is not a class");  
	  }
	  currentScope=connectionBlock;	
      //Util.log("Begin Checking of "+connectionBlock.getScopeName()+" - Current Scope Chain: "+connectionBlock.edScopeChain());
	  statement.doChecking();
      currentScope=enclosure;
	}
	
	public void doCoding(String indent,boolean first)
	{ //Util.BREAK("ConnectionStatement.DoPart.doCoding: statement="+statement.getClass().getName());
	  ASSERT_SEMANTICS_CHECKED(this);
	  String prfx=(first)?indent:indent+"else ";
	  Util.code(prfx+"if("+objectExpression+" instanceof "+classIdentifier+") {"+"// WHEN "+classIdentifier+" DO ");
	  statement.doCoding(indent+"   ");  
	}
	
	public String toString()
	{ return("WHEN "+classIdentifier+" DO "+statement);	}
  }

  public void doChecking()
  { if(IS_SEMANTICS_CHECKED()) return;
    Util.setLine(getLineNumber());
    //Util.BREAK("ConnectionStatement.doChecking: ");
    if(DEBUG); Util.log("BEGIN LocalObject("+toString()+").doChecking - Current Scope Chain: "+currentScope.edScopeChain());
    objectExpression.doChecking();
    for(Enumeration<DoPart> e=connectionPart.elements(); e.hasMoreElements();)
    { e.nextElement().doChecking(); }
    if(otherwise!=null) otherwise.doChecking();
	SET_SEMANTICS_CHECKED();
  }
  
  public void doCoding(String indent)
  {	Util.setLine(getLineNumber());
	ASSERT_SEMANTICS_CHECKED(this);
    if(hasWhenPart) Util.code(indent+"//"+"INSPECT "+objectExpression);
    else Util.code(indent+"if("+objectExpression+"!=null) { //"+"INSPECT "+objectExpression);
    boolean first=true;
    for(Enumeration<DoPart> e=connectionPart.elements(); e.hasMoreElements();)
    { e.nextElement().doCoding(indent,first); first=false; }
    if(otherwise!=null)
    { Util.code(indent+"   } else {  // OTHERWISE ");
      otherwise.doCoding(indent+"      ");
      Util.code(indent+"   } // END OTHERWISE ");
    }
    if(hasWhenPart) Util.code(indent+"// END INSPECTION ");
    else Util.code(indent+"} // END INSPECTION ");
  }
  
  public void print(String indent)
  { StringBuilder s=new StringBuilder(indent);
    s.append("INSPECT ").append(objectExpression);
    System.out.println(indent+"INSPECT "+objectExpression);
    for(Enumeration<DoPart> e=connectionPart.elements(); e.hasMoreElements();)
    { //System.out.println(indent+"   "+e.nextElement());
      String ss=indent+"   "+e.nextElement();
      if((otherwise==null) && (!(e.hasMoreElements()))) ss=ss+";";
      System.out.println(ss);
    }
    if(otherwise!=null)
    { System.out.println(indent+"   OTHERWISE "+otherwise+';'); }
  }

  public String toString()
  { String otherwisePart="";
    if(otherwise!=null) otherwisePart=" OTHERWISE "+otherwise;
	  return("INSPECT "+objectExpression+" "+connectionPart+otherwisePart); }
}
