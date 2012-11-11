package de.estudent.accesscontrol.nfc.reader;

import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NFCInitalizationException;
import de.estudent.accesscontrol.nfc.listener.BeamReceiveListener;

/**
 * 
 * @author Wilko Oley
 */
public interface NFCDevice {

    public void start() throws NFCException;

    public void setBeamReceiveListener(BeamReceiveListener listener);

    public void initalize(long timeout, int max_allowed_size)
            throws NFCInitalizationException;

    public void initalizeWithDefaultValues() throws NFCInitalizationException;

}
