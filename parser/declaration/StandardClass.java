package parser.declaration;

import common.DeclarationList;
import common.Identifier;
import common.Kind;
import common.Mode;
import common.NameTable;
import common.Type;
import common.Util;

public class StandardClass extends ClassDeclaration
{ boolean contextFlag; // If true; all member methods are declared static
  public boolean getContextFlag() { return(contextFlag); }
  void setContextFlag(boolean flag) { contextFlag=flag; }
  
  // ******************************************************************
  // *** The Type TEXT
  // ******************************************************************
  public static StandardClass typeText=new StandardClass("TEXT");
  static // Define attribute procedures to Type TEXT
  {	typeText.addStandardProcedure(Type.Boolean,"constant");  
  	typeText.addStandardProcedure(Type.Integer,"start");  
  	typeText.addStandardProcedure(Type.Integer,"length");  
  	typeText.addStandardProcedure(Type.Text,"main");  
  	typeText.addStandardProcedure(Type.Integer,"pos");  
  	typeText.addStandardProcedure(null,"setpos",parameter("i",Type.Integer));  
  	typeText.addStandardProcedure(Type.Boolean,"more");  
  	typeText.addStandardProcedure(Type.Character,"getchar");  
  	typeText.addStandardProcedure(null,"putchar",parameter("c",Type.Character));  
  	typeText.addStandardProcedure(Type.Text,"sub",parameter("i",Type.Integer),parameter("n",Type.Integer));  
  	typeText.addStandardProcedure(Type.Text,"strip");  
  	typeText.addStandardProcedure(Type.Integer,"getint");  
  	typeText.addStandardProcedure(Type.LongReal,"getreal");  
  	typeText.addStandardProcedure(Type.Integer,"getfrac");  
  	typeText.addStandardProcedure(null,"putint",parameter("i",Type.Integer));  
  	typeText.addStandardProcedure(null,"putfix",parameter("r",Type.LongReal),parameter("n",Type.Integer));  
  	typeText.addStandardProcedure(null,"putreal",parameter("r",Type.LongReal),parameter("n",Type.Integer));  
  	typeText.addStandardProcedure(null,"putfrac",parameter("i",Type.Integer),parameter("n",Type.Integer));  
  }
  
  // ******************************************************************
  // *** The Standard Class UNIVERSE
  // ******************************************************************
  public static StandardClass UNIVERSE=new StandardClass("UNIVERSE");
  static
  { 
	  
  }
  
  // ******************************************************************
  // *** The Standard Class OBJECT - Prefix to all classes
  // ******************************************************************
  public static StandardClass OBJECT=new StandardClass("OBJECT");
  static
  { UNIVERSE.addStandardClass(OBJECT);
    OBJECT.addStandardProcedure(null,"detach");
    OBJECT.addStandardProcedure(null,"resume",parameter("obj",Type.Ref("OBJECT")));
  }
  
  // ******************************************************************
  // *** The Standard Class ENVIRONMENT
  // ******************************************************************
  // TODO: Rettes slik at alle genererte metoder blir static.
  //       Ta med et flag (enten i klassen eller i alle procedurene
  //  
  public static StandardClass ENVIRONMENT=new StandardClass("OBJECT","ENVIRONMENT");
  static
  { UNIVERSE.addStandardClass(ENVIRONMENT);
    ENVIRONMENT.setContextFlag(true); // This class is a Context i.e. all members are static
    ENVIRONMENT.addStandardProcedure(Type.Integer,"rank",parameter("c",Type.Character));
    ENVIRONMENT.addStandardProcedure(Type.Character,"char",parameter("n",Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Boolean,"digit",parameter("c",Type.Character));
    ENVIRONMENT.addStandardProcedure(Type.Boolean,"letter",parameter("c",Type.Character));
    ENVIRONMENT.addStandardProcedure(Type.Text,"blanks",parameter("n",Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Text,"copy",parameter("T",Mode.value,Type.Text));
    ENVIRONMENT.addStandardProcedure(Type.Boolean,"draw",parameter("a",Type.Real),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Integer,"randint",parameter("a",Type.Integer),parameter("b",Type.Integer),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Real,"uniform",parameter("a",Type.Real),parameter("b",Type.Real),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Real,"normal",parameter("a",Type.Real),parameter("b",Type.Real),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Integer,"discrete",parameter("A",Type.Real,Kind.Array),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(Type.Integer,"histd",parameter("A",Type.Real,Kind.Array),parameter("U",Mode.name,Type.Integer));
    ENVIRONMENT.addStandardProcedure(null,"histo",parameter("A",Type.Real,Kind.Array),parameter("B",Type.Real,Kind.Array)
    		                                     ,parameter("c",Type.Real),parameter("d",Type.Real));
    ENVIRONMENT.addStandardProcedure(null,"accum",parameter("a",Mode.name,Type.Real),parameter("b",Mode.name,Type.Real)
                                                 ,parameter("c",Mode.name,Type.Real),parameter("d",Type.Real));
    ENVIRONMENT.addStandardProcedure(null,"error",parameter("msg",Type.Text));
    ENVIRONMENT.addStandardProcedure(Type.LongReal,"sqrt",parameter("x",Type.LongReal));
    ENVIRONMENT.addStandardProcedure(Type.LongReal,"arctg",parameter("x",Type.LongReal),parameter("y",Type.LongReal));
  }
  
  // ******************************************************************
  // *** The Standard Linkage Class BASICIO
  // ******************************************************************
  public static StandardClass BASICIO=new StandardClass("ENVIRONMENT","BASICIO");
  static
  { UNIVERSE.addStandardClass(BASICIO);
    BASICIO.addStandardProcedure(Type.Ref("InFile"),"sysin");  
	BASICIO.addStandardProcedure(Type.Ref("PrintFile"),"sysout");  
	BASICIO.addStandardProcedure(null,"terminate_program");  
  }
  
  // ******************************************************************
  // *** The Standard Class FILE
  // ******************************************************************
  //  class FILE_(FILENAME_); value FILENAME_; text FILENAME_;
  //  virtual: procedure open, close;
  //  begin text image;
  //     Boolean OPEN_;
  //     text procedure filename; filename:=copy(FILENAME_);
  //     Boolean procedure isopen; isopem:=OPEN_;
  //     procedure setpos(i); integer i; image.setpos(i);
  //     integer procedure pos; pos:=image.pos;
  //     Boolean procedure more; more:=image.more;
  //     integer procedure length; length:=image.length;
  //  
  //     if FILENAME_ = notext then error("Illegal File Name");
  //  end FILE_;      
  public static StandardClass FILE_=new StandardClass("OBJECT","FILE_",parameter("FILENAME",Type.Text));
  static
  { BASICIO.addStandardClass(FILE_);
    FILE_.addStandardAttribute(Type.Boolean,"OPEN_");  
    FILE_.addStandardAttribute(Type.Text,"image");  
    FILE_.addStandardProcedure(Type.Text,"filename");
    FILE_.addStandardProcedure(Type.Boolean,"isopen");
//    FILE_.addStandardProcedure(null,"open");  
//    FILE_.addStandardProcedure(null,"close");  
    FILE_.addStandardProcedure(Type.Integer,"length");   
    FILE_.addStandardProcedure(Type.Integer,"pos");  
    FILE_.addStandardProcedure(null,"setpos",parameter("i",Type.Integer));  
    FILE_.addStandardProcedure(Type.Boolean,"more");  
  }  
  // ******************************************************************
  // *** The Standard File Class InFile
  // ******************************************************************
  public static StandardClass InFile=new StandardClass("FILE_","InFile");
  static
  { BASICIO.addStandardClass(InFile);
    InFile.addStandardProcedure(Type.Boolean,"open",parameter("fileimage",Type.Text));  
    InFile.addStandardProcedure(Type.Boolean,"close");  
    InFile.addStandardAttribute(Type.Boolean,"ENDFILE_");  
    InFile.addStandardProcedure(Type.Boolean,"endfile");  
    InFile.addStandardProcedure(null,"inimage");  
//    InFile.addStandardProcedure(Type.Boolean,"inrecord");  
    InFile.addStandardProcedure(Type.Character,"inchar");  
    InFile.addStandardProcedure(Type.Boolean,"lastitem");  
    InFile.addStandardProcedure(Type.Integer,"inint");  
    InFile.addStandardProcedure(Type.LongReal,"inreal");  
    InFile.addStandardProcedure(Type.Integer,"infrac");  
    InFile.addStandardProcedure(Type.Text,"intext",parameter("w",Type.Integer));  
  }  
  // ******************************************************************
  // *** The Standard ImageFile Class OutFile
  // ******************************************************************
  public static StandardClass OutFile=new StandardClass("FILE_","OutFile");
  static
  { BASICIO.addStandardClass(OutFile);
    OutFile.addStandardProcedure(Type.Boolean,"open",parameter("fileimage",Type.Text));  
    OutFile.addStandardProcedure(Type.Boolean,"close");  
    OutFile.addStandardProcedure(null,"outimage");  
    OutFile.addStandardProcedure(null,"outrecord");  
    OutFile.addStandardProcedure(null,"breakoutimage");  
    OutFile.addStandardProcedure(Type.Boolean,"checkpoint");  
    OutFile.addStandardProcedure(null,"outchar",parameter("c",Type.Character));  
    OutFile.addStandardProcedure(null,"outtext",parameter("t",Type.Text));  
    OutFile.addStandardProcedure(Type.Text,"FIELD_",parameter("w",Type.Integer));  
    OutFile.addStandardProcedure(null,"outint",parameter("i",Type.Integer),parameter("w",Type.Integer));  
    OutFile.addStandardProcedure(null,"outfix",parameter("r",Type.LongReal),parameter("n",Type.Integer),parameter("w",Type.Integer));  
    OutFile.addStandardProcedure(null,"outreal",parameter("r",Type.LongReal),parameter("n",Type.Integer),parameter("w",Type.Integer));  
    OutFile.addStandardProcedure(null,"outfrac",parameter("i",Type.Integer),parameter("n",Type.Integer),parameter("w",Type.Integer));  
  }  
  
  // ******************************************************************
  // *** The Standard ImageFile Class DirectFile
  // ******************************************************************
  public static StandardClass DirectFile=new StandardClass("FILE_","DirectFile");
  static
  { BASICIO.addStandardClass(DirectFile);
    DirectFile.addStandardAttribute(Type.Integer,"LOC_");  
    DirectFile.addStandardProcedure(Type.Integer,"location");  
    DirectFile.addStandardProcedure(null,"locate",parameter("i",Type.Integer));  
    DirectFile.addStandardProcedure(Type.Boolean,"open",parameter("fileimage",Type.Text));  
    DirectFile.addStandardProcedure(Type.Boolean,"close");      
    DirectFile.addStandardProcedure(Type.Boolean,"endfile");  
    DirectFile.addStandardProcedure(null,"inimage");  
    DirectFile.addStandardProcedure(null,"outimage");  
//    DirectFile.addStandardProcedure(Type.Boolean,"checkpoint");  
//    DirectFile.addStandardProcedure(Type.Integer,"lock",parameter("t",Type.Real),parameter("i",Type.Integer),parameter("j",Type.Integer));  
//    DirectFile.addStandardProcedure(Type.Boolean,"unlock");  
//    DirectFile.addStandardProcedure(Type.Boolean,"locked");  
//    DirectFile.addStandardProcedure(Type.Integer,"lastloc");  
//    DirectFile.addStandardProcedure(Type.Boolean,"maxloc");  
//    DirectFile.addStandardProcedure(Type.Boolean,"deleteimage");  
    DirectFile.addStandardProcedure(Type.Character,"inchar");  
    DirectFile.addStandardProcedure(Type.Boolean,"lastitem");  
    DirectFile.addStandardProcedure(Type.Integer,"inint");  
    DirectFile.addStandardProcedure(Type.LongReal,"inreal");  
    DirectFile.addStandardProcedure(Type.Integer,"infrac");  
    DirectFile.addStandardProcedure(Type.Text,"intext",parameter("w",Type.Integer));  
    DirectFile.addStandardProcedure(null,"outchar",parameter("c",Type.Character));  
    DirectFile.addStandardProcedure(null,"outtext",parameter("t",Type.Text));   
    DirectFile.addStandardProcedure(null,"outint",parameter("i",Type.Integer),parameter("w",Type.Integer));  
    DirectFile.addStandardProcedure(null,"outfix",parameter("r",Type.LongReal),parameter("n",Type.Integer),parameter("w",Type.Integer));  
    DirectFile.addStandardProcedure(null,"outreal",parameter("r",Type.LongReal),parameter("n",Type.Integer),parameter("w",Type.Integer));  
    DirectFile.addStandardProcedure(null,"outfrac",parameter("i",Type.Integer),parameter("n",Type.Integer),parameter("w",Type.Integer));  
  }  
  
  // ******************************************************************
  // *** The Standard OutFile Class PrintFile
  // ******************************************************************
  public static StandardClass PrintFile=new StandardClass("OutFile","PrintFile");
  static
  { BASICIO.addStandardClass(PrintFile);
  DirectFile.addStandardAttribute(Type.Integer,"LINES_PER_PAGE_");  
  DirectFile.addStandardAttribute(Type.Integer,"SPACING_");  
    DirectFile.addStandardAttribute(Type.Integer,"LINE_");  
    PrintFile.addStandardProcedure(Type.Integer,"line"); 
//    PrintFile.addStandardProcedure(Type.Integer,"page");  
    PrintFile.addStandardProcedure(Type.Integer,"linesperpage",parameter("n",Type.Integer));  
    PrintFile.addStandardProcedure(null,"spacing",parameter("n",Type.Integer));  
    PrintFile.addStandardProcedure(null,"eject",parameter("n",Type.Integer));  
    PrintFile.addStandardProcedure(Type.Boolean,"open",parameter("fileimage",Type.Text));  
    PrintFile.addStandardProcedure(Type.Boolean,"close");  
    PrintFile.addStandardProcedure(null,"outimage");  
//    PrintFile.addStandardProcedure(null,"outrecord");  
  }  
  
  // ******************************************************************
  // *** The Standard Class Simset
  // ******************************************************************
  public static StandardClass Simset=new StandardClass("OBJECT","Simset");
  static
  { ENVIRONMENT.addStandardClass(Simset);
  }  
  
  // ******************************************************************
  // *** The Standard Class Linkage
  // ******************************************************************
  public static StandardClass Linkage=new StandardClass("OBJECT","Linkage");
  static
  { Simset.addStandardClass(Linkage);
//  ref(link) procedure suc;
//  ref(link) procedure pred;
    Linkage.addStandardProcedure(Type.Ref("Link"),"suc");  
    Linkage.addStandardProcedure(Type.Ref("Link"),"pred");  
  }  
  
  // ******************************************************************
  // *** The Standard Linkage Class Head
  // ******************************************************************
  public static StandardClass Head=new StandardClass("Linkage","Head");
  static
  { Simset.addStandardClass(Head);
//  ref(link) procedure first;
//  ref(link) procedure last;
//  Boolean procedure empty;
//  Integer procedure cardinal;
//  procedure clear;
    Head.addStandardProcedure(Type.Ref("Link"),"first");  
    Head.addStandardProcedure(Type.Ref("Link"),"last");  
    Head.addStandardProcedure(Type.Boolean,"empty");  
    Head.addStandardProcedure(Type.Integer,"cardinal");  
    Head.addStandardProcedure(null,"clear");  
  }  
  
  // ******************************************************************
  // *** The Standard Linkage Class Link
  // ******************************************************************
  public static StandardClass Link=new StandardClass("Linkage","Link");
  static
  { Simset.addStandardClass(Link);
//  procedure out;
//  procedure follow(X); ref(linkage) X;
//  procedure precede(X); ref(linkage) X;
//  procedure into(S); ref(head) S;
    Link.addStandardProcedure(null,"out");  
    Link.addStandardProcedure(null,"follow",parameter("X",Type.Ref("Linkage")));  
    Link.addStandardProcedure(null,"precede",parameter("X",Type.Ref("Linkage")));  
    Link.addStandardProcedure(null,"into",parameter("S",Type.Ref("Head")));  
  }  
  
  private DeclarationList standardDeclarationList=new DeclarationList();
	
  public StandardClass(String prefix,String className)
  { this.prefix=NameTable.defineName(prefix);
	this.identifier=NameTable.defineName(className);
	this.type=Type.Ref(className);
  }
	
  public StandardClass(String className)
  { this.identifier=NameTable.defineName(className);
	this.type=Type.Ref(className);
  }
	
  public StandardClass(String prefix,String className,Specification p1)
  { this.prefix=NameTable.defineName(prefix);
    this.identifier=NameTable.defineName(className);
	this.type=Type.Ref(className);
    getParameterList().add(p1);
  }
	
  public StandardClass(String className,Specification p1)
  { this.identifier=NameTable.defineName(className);
	this.type=Type.Ref(className);
	getParameterList().add(p1);
  }
  
  private static Specification parameter(String ident,Type type)
  { return(new Specification(NameTable.defineName(ident),type,Kind.Simple)); }
  
  private static Specification parameter(String ident,Type type,Kind kind)
  { return(new Specification(NameTable.defineName(ident),type,kind)); }
  
  private static Specification parameter(String ident,Mode mode,Type type)
  { Specification spec=new Specification(NameTable.defineName(ident),type,Kind.Simple);
    spec.setMode(mode); return(spec);
  }
  
  public Declaration findAttribute(Identifier ident)
  { Declaration result;
   	//Util.BREAK("StandardClass("+this.getIdentifier()+").findAttribute("+ident+"): scope="+getScopeName()+", chain="+edScopeChain());
    result=this.standardDeclarationList.lookup(ident);
    if(result==null)
    { ClassDeclaration prfx=getPrefix();
      if(prfx!=null) result=prfx.findAttribute(ident);
    }
//	if(result==null && enclosure!=null) result=enclosure.findDefinition(identifier);  // TODO: ????
//    if(result!=null) Util.TRACE("END StandardClass.findAttribute("+ident+"): result="+result.getEnclosureName()+'.'+result);   
    return(result);
  }

  private void addStandardClass(StandardClass standardClass)
  {	standardClass.enclosure=this;
   	//Util.BREAK("StandardClass.addStandardClass("+standardClass+") to "+this+": scope="+getScopeName()+", chain="+standardClass.edScopeChain());
    standardDeclarationList.add(standardClass);
  }

  private void addStandardAttribute(Type type,String ident)
  { standardDeclarationList.add(new TypeDeclaration(type,new Identifier(ident))); }

  private void addStandardProcedure(Type type,String ident,Specification p1,Specification p2,Specification p3,Specification p4)
  {	standardDeclarationList.add(new StandardProcedure(this,type,ident,p1,p2,p3,p4)); }

  private void addStandardProcedure(Type type,String ident,Specification p1,Specification p2,Specification p3)
  {	standardDeclarationList.add(new StandardProcedure(this,type,ident,p1,p2,p3)); }

  private void addStandardProcedure(Type type,String ident,Specification p1,Specification p2)
  {	standardDeclarationList.add(new StandardProcedure(this,type,ident,p1,p2)); }

  private void addStandardProcedure(Type type,String ident,Specification param)
  {	standardDeclarationList.add(new StandardProcedure(this,type,ident,param)); }

  private void addStandardProcedure(Type type,String ident)
  {	standardDeclarationList.add(new StandardProcedure(this,type,ident)); }

}


