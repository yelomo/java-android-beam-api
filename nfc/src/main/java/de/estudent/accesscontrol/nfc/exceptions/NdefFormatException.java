package de.estudent.accesscontrol.nfc.exceptions;

/**
 * 
 * @author Wilko Oley
 */
public class NdefFormatException extends Exception {

    private static final long serialVersionUID = 1L;

    public NdefFormatException(String msg, Throwable prev) {
        super(msg, prev);
    }

    public NdefFormatException(Throwable prev) {
        super(prev);
    }

    public NdefFormatException(String msg) {
        super(msg);
    }

}
