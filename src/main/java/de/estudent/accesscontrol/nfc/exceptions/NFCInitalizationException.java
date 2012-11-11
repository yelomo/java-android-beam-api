package de.estudent.accesscontrol.nfc.exceptions;

/**
 * 
 * @author Wilko Oley
 */
public class NFCInitalizationException extends Exception {

    private static final long serialVersionUID = 1L;

    public NFCInitalizationException(String msg, Throwable prev) {
        super(msg, prev);
    }

    public NFCInitalizationException(String msg) {
        super(msg);
    }

}
