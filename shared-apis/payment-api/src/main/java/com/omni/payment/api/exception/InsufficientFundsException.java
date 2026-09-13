package com.omni.payment.api.exception;

/**
 * InsufficientFundsException - 餘額不足例外
 */
public class InsufficientFundsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InsufficientFundsException(String message) {
		super(message);
	}
}
