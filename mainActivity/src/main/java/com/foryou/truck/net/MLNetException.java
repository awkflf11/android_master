package com.foryou.truck.net;

public class MLNetException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public MLNetException() {
		super();
	}

	public MLNetException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MLNetException(String detailMessage) {
		super(detailMessage);
	}

	public MLNetException(Throwable throwable) {
		super(throwable);
	}

}
