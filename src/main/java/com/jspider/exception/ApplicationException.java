package com.jspider.exception;

public class ApplicationException extends Exception {
	private static final long serialVersionUID = -7813277731835734632L;

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
