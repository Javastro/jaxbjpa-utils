package org.javastro.ivoa.jpa.exceptions;

public class NonexistentEntityException extends RuntimeException {
    /** serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    public NonexistentEntityException(String message, Throwable cause) {
        super(message, cause);
    }
    public NonexistentEntityException(String message) {
        super(message);
    }
}
