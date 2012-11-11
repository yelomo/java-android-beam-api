package de.estudent.accesscontrol.nfc.reader.acs;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NFCInitalizationException;
import de.estudent.accesscontrol.nfc.listener.BeamReceiveListener;
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;
import de.estudent.accesscontrol.nfc.reader.NFCDevice;
import de.estudent.accesscontrol.nfc.reader.NFCDeviceFactory;
import de.estudent.accesscontrol.nfc.reader.NFCDeviceType;

/**
 * 
 * @author Wilko Oley
 */
public class IntegrationTestTouchATag implements BeamReceiveListener {
    private final static Logger LOG = LoggerFactory
            .getLogger(IntegrationTestTouchATag.class);

    @Test
    public void test() throws NFCInitalizationException, InterruptedException {
        NFCDevice device = NFCDeviceFactory
                .createNFCDevice(NFCDeviceType.TOUCH_A_TAG);
        device.setBeamReceiveListener(this);
        device.initalizeWithDefaultValues();
        while (true) {
            try {
                device.start();
            } catch (NFCException e) {
                // TODO: handle exception
            }
            Thread.sleep(2000);
            LOG.info("FINISHED");
        }

    }

    public void beamRecieved(NdefMessage message) {
        LOG.info("Recieved \n" + new String(message.getPayload()));
    }

}
