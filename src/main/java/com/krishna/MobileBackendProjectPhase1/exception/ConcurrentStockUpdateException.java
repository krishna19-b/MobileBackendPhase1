package com.krishna.MobileBackendProjectPhase1.exception;

public class ConcurrentStockUpdateException extends RuntimeException {

    public ConcurrentStockUpdateException(String message) {
        super(message);
    }
}