package simula.runtime.context;

import simula.test.context.ArrayAsParameter;
import simula.test.context.HelloWord;
import simula.test.context.JensensDevice;
import simula.test.context.VariableByName;


public class SimulaProgram {
	
	public static void main(String[] args)
	{ System.out.println("Start Execution of Simula Program");
	  //new HelloWord();
	  new JensensDevice();
	  //new ArrayAsParameter();
	  //new VariableByName();
	  System.out.println("Simula Program Terminates Normally");
	}

}
