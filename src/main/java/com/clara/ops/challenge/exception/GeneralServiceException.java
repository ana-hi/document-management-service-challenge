package com.clara.ops.challenge.exception;

public class GeneralServiceException extends RuntimeException {
  public GeneralServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
