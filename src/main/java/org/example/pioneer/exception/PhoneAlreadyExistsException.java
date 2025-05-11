package org.example.pioneer.exception;

public class PhoneAlreadyExistsException extends RuntimeException {
    public PhoneAlreadyExistsException(String phone) {
        super("Phone уже используется: " + phone);
    }
}
