package com.study.instrument;

import java.lang.instrument.Instrumentation;

/**
 * 参考：{@link https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/package-summary.html}
 * @author shangcj
 *
 */
public class PerformXAgent {

	static private volatile Instrumentation instrumentation;

	// public static void premain(String agentArgs);

	// TODO
	//After the Java Virtual Machine (JVM) has initialized, each premain method will be called in the order the agents were specified, then the real application main method will be called. 
	//Each premain method must return in order for the startup sequence to proceed.

	public static void premain(String agentArgs, Instrumentation inst) {
		System.out.println("PerformXAgent preMain was Called " + PerformXAgent.class.getClassLoader());

		instrumentation = inst;

		PerformanceXTransformer transformer = new PerformanceXTransformer();
		System.out.println("Add a transformer to JVM." + transformer);
		
		instrumentation.addTransformer(transformer, false);

	}

	//1.The agent class may also have an agentmain method for use when the agent is started after VM startup.
	//When the agent is started using a command-line option, the agentmain method is not invoked.
	
	//2.The agent class may also have an premain method for use when the agent is started using a command-line option. 
	//When the agent is started after VM startup the premain method is not invoked.
	public static void agentmain(String agentArgs, Instrumentation inst) {
		System.out.println("PerformXAgent agentmain was Called " + PerformXAgent.class.getClassLoader());
		instrumentation = inst;

		PerformanceXTransformer transformer = new PerformanceXTransformer();
		System.out.println("Add a transformer to JVM Via agentmain " + transformer);
		
		instrumentation.addTransformer(transformer, false);
	}
}
