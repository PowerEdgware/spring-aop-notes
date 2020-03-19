package com.study.instrument;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Arrays;

public class PerformanceXTransformer implements ClassFileTransformer {

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
		System.err.println("loader="+loader.getClass());
		System.err.println("className="+className);
		System.err.println("classBeingRedefined="+classBeingRedefined);
		System.err.println("protectionDomain="+protectionDomain);
		System.err.println("classfileBuffer="+Arrays.toString(classfileBuffer));
		
		
		return classfileBuffer;
	}

}
