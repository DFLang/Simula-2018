package simula.test.context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

import common.Token;
import common.Util;
import compiler.SimulaScanner;

public class SimulaExample {

	public static void main(String[] args)
	{
		ScannerExample2();

	}


	static void ScannerExample1()
	{ String s=
	   "BEGIN\n"
	  +"      INTEGER i;\n"
	  +"      REAL r;\n"
//    +"      CHARACTER c;\n"
//	  +"      TEXT t;\n"
//	  +"      i=12;\n"
//	  +"      i=16R2AB;\n"
      +"      r=12.234;\n"
      +"      r=.16&&45;\n"
      +"      r=&&75;\n"
//    +"      c='%';\n"
//	  +"      t=\"ab!120!racadab\"\n"
	  +"END OTHERWISE  YES ;\n";
	  SimulaScanner scan = new SimulaScanner(s);
      Token token=null;
      while((token = scan.nextToken())!=null) Util.log("TOKEN:'"+token+"'");
	  //scan.close();
	}

	static void ScannerExample2()
	{ try
	  {	FileReader reader = new FileReader(new File("C:/Users/Øystein/WorkSpaces/SimulaCompiler/Simula/src/examples/Sample4.sim"));
	    SimulaScanner scan = new SimulaScanner(reader);
	    Token token=null;
	    while((token = scan.nextToken())!=null) Util.log("TOKEN:'"+token+"'");
		//scan.close();
	  } catch (FileNotFoundException e)
	    { System.out.println("Error Reading File");
	      e.printStackTrace();
		}
	}

	static void ScannerExample3()
	{ try
	  {	FileReader reader = new FileReader(new File("C:/Users/Øystein/WorkSpaces/SimulaCompiler/Simula/src/examples/Sample2.sim"));
	    StreamTokenizer tokenizer=new StreamTokenizer(reader);
	    tokenizer.commentChar('%');
	    while((tokenizer.nextToken())!= StreamTokenizer.TT_EOF)
	    {
	    	//Debug.log("TOKEN:'"+tt+"'");
	    	Util.log(tokenizer.toString()+", ttype="+tokenizer.ttype+":"+edTtype(tokenizer)+", nval="+tokenizer.nval+", sval="+tokenizer.sval);
	    }
		//scan.close();
	  } catch (IOException e)
	    { System.out.println("Error Reading File");
	      e.printStackTrace();
		}
	}
	
	static String edTtype(StreamTokenizer tokenizer)
	{ int tt=tokenizer.ttype;
	  switch(tt)
	  { case StreamTokenizer.TT_WORD: return("WORD");
	    case StreamTokenizer.TT_NUMBER: return("NUMBER");
	    case StreamTokenizer.TT_EOL: return("EOL");
	    case StreamTokenizer.TT_EOF: return("EOF");
	    default: return(""+(char)tt);
	  
	  }
		
	}

}
