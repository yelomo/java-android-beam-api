package de.estudent.accesscontrol.nfc.listener;

/**
 * 
 * @author Wilko Oley
 */
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;

public interface BeamReceiveListener {

    public void beamRecieved(NdefMessage message);

}
