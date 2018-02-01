package compiler;


public class JavaSourceCoder
{ ProgramModule program;
  public JavaSourceCoder(ProgramModule program)
  { this.program=program;
	  
  }
  
  public void doCoding() { program.doCoding(""); }

}
