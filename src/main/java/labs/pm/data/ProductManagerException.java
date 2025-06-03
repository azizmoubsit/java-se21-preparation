/*
 * Copyright (c) 2025. Aziz
 */

package labs.pm.data;

/**
 * @author aziz
 **/
public class ProductManagerException extends Exception {
    public ProductManagerException(String message) {
        super(message);
    }

    public ProductManagerException(String message, Throwable cause) {
        super(message, cause);
    }
}
