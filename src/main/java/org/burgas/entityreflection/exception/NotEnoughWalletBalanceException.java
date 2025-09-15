package org.burgas.entityreflection.exception;

public class NotEnoughWalletBalanceException extends RuntimeException {

    public NotEnoughWalletBalanceException(String message) {
        super(message);
    }
}
