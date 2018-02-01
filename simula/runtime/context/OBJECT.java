package simula.runtime.context;

import common.Util;

public class OBJECT {
	public static void error(String msg) { Util.ERROR(msg);}
	public static void warning(String msg) { Util.WARNING(msg);}
	public void detach() {}
	public void resume(OBJECT p) {}
	
	// ****************************************************
	// *** FRAMEWORK for Name-Parameters in Java Coding
	// *** The Abstract Generic Class ValueTypeByName_
	// *** is supposed to be defined in the Environment
	// ****************************************************
	public abstract class ValueTypeByName_<T> {
		public abstract T get();
		public void put(T x) { error("Illegal assignment. Name parameter is not a variable"); }
	}

}
