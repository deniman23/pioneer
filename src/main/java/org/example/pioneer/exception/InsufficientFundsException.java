package org.example.pioneer.exception;

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(Long userId, String detail) {
    super("Недостаточно средств на счете пользователя " + userId + ": " + detail);
  }
}
