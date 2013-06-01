package com.jboss.jbds.installer;


public class ConsoleCommandException extends Exception {
	
	private static final long serialVersionUID = -3163729601377331330L;

	private int errCode = 0;
	
	public ConsoleCommandException(int errCode, String errorOutput) {
		this(errCode,errorOutput,null);
	}
	
	public ConsoleCommandException(int errCode, String errorOutput, Throwable ex) {
		super(errorOutput,ex);
		this.errCode = errCode;
	}
}
