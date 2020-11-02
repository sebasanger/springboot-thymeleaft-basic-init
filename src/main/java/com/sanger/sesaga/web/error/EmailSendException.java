package com.sanger.sesaga.web.error;

public class EmailSendException extends RuntimeException {
    private static final long serialVersionUID = 5861310537366287163L;

    public EmailSendException() {
        super();
    }

    public EmailSendException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EmailSendException(final String message) {
        super(message);
    }

    public EmailSendException(final Throwable cause) {
        super(cause);
    }
}
