package com.jspider.exception;


public class ApplicationException extends Exception {
	public ApplicationException() {
	}

	public ApplicationException(String paramString) {
		super(paramString);
	}

	public ApplicationException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public ApplicationException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}
