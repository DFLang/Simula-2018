package compiler;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;

import common.Util;

//*************************************************************************************
//**                                                                      CLASS INPUT
//*************************************************************************************
public final class INPUT
{ static boolean DEBUG=false; //true; 
  private static boolean closed=true;
  private static PushbackReader pushbackReader;     // underlying input source
  private static LineNumberReader lineNumberReader; // keeps track of line numbers
  private static final int PUSHBACK_BUFFER_SIZE=16; // buffer to unread
  public static final int EOF=-1;           // used to denote end-of-input
  private static int current;
	  
  static void open(Reader source)
  { lineNumberReader=new LineNumberReader(source);
    lineNumberReader.setLineNumber(1);  // it would be 0-based otherwise
    pushbackReader=new PushbackReader(lineNumberReader, PUSHBACK_BUFFER_SIZE);
    closed=false;
  }
  
  static boolean isClosed() { return(closed); }

  static void close() throws IOException
  { pushbackReader.close();
    closed=true;
  }

  static int getLineNumber()
  { return(lineNumberReader.getLineNumber()); }

  static String readUntilEndOfLine()
  { String line="";
	try { line=lineNumberReader.readLine(); }
    catch(IOException e) { pushBack(EOF); }
	pushBack('\n');
	return(line);
  }
	    
  // advances one character in the input
  static int getNext()
  { if(closed) throw new IllegalStateException();
    try { INPUT.current=pushbackReader.read(); }
	catch(IOException e) { INPUT.current=EOF; closed=true; }
	if(DEBUG) Util.log("Input: '"+(char)INPUT.current+"'="+INPUT.current);
	if(current== -1) closed=true;
	return(INPUT.current);
  }
		  
  // put given value back into the input stream
  static void pushBack(int chr)
  { if(DEBUG) Util.log("Pushback: '"+(char)chr+"'="+chr);
	try { pushbackReader.unread(chr); }
	catch(IOException e) { Util.EXCEPTION("",e); System.exit(-1); }
	current= -1;
  }

  // put given value back into the input stream
  static void pushBack(String s)
  { int i=s.length();
	while((i--)>0) pushBack(s.charAt(i));
  }
}
