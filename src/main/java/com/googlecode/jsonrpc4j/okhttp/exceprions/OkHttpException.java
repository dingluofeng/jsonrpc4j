package com.googlecode.jsonrpc4j.okhttp.exceprions;

import java.io.IOException;

public class OkHttpException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public OkHttpException(String message, IOException cause) {
		super(message, cause);
	}
}