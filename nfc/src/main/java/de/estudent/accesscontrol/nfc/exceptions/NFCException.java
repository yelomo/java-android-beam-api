package de.estudent.accesscontrol.nfc.exceptions;

/**
 * 
 * @author Wilko Oley
 */
public class NFCException extends Exception {

    private static final long serialVersionUID = 1L;

    public NFCException(String msg, Throwable prev) {
        super(msg, prev);
    }

    public NFCException(Throwable prev) {
        super(prev);
    }

    public NFCException(String msg) {
        super(msg);
    }

}
