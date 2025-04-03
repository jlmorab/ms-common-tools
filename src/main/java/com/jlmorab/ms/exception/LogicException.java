package com.jlmorab.ms.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Domain logic exception
 */
public class LogicException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	@Getter
	private final HttpStatus httpStatus;

	public LogicException(HttpStatus httpStatus) {
		super();
		this.httpStatus = httpStatus;
	}

	public LogicException(String message) {
		super(message);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}
	
	public LogicException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public LogicException(String message, Throwable cause) {
		super(message, cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}
	
	public LogicException(String message, Throwable cause, HttpStatus httpStatus) {
		super(message, cause);
		this.httpStatus = httpStatus;
	}

	public LogicException(Throwable cause) {
		super(cause);
		this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	}
	
	public LogicException(Throwable cause, HttpStatus httpStatus) {
		super(cause);
		this.httpStatus = httpStatus;
	}
	
}
