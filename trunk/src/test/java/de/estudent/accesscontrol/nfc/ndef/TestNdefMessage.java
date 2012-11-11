package de.estudent.accesscontrol.nfc.ndef;

import static org.junit.Assert.*;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.NFCHelper;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;

/**
 * 
 * @author Wilko Oley
 */
public class TestNdefMessage {

    private final static Logger LOG = LoggerFactory
            .getLogger(TestNdefMessage.class);

    @Test
    public void parseShortNdefMessage() throws NdefFormatException {

        byte[] ndefData = NFCHelper.subByteArray(
                SNEPTestData.GALAXY_NEXUS_SNEP_SHORT, 6,
                SNEPTestData.GALAXY_NEXUS_SNEP_SHORT.length - 6);

        NdefMessage ndefMessage = new NdefMessage(ndefData);

        LOG.info(new String(ndefMessage.getPayload()));

        assertArrayEquals(SNEPTestData.GALAXY_NEXS_SHORT_PAYLOAD,
                ndefMessage.getPayload());

    }

    @Test
    public void parseLongNdefMessage() throws NdefFormatException {

        byte[] ndefData = NFCHelper.subByteArray(
                SNEPTestData.GALAXY_NEXUS_SNEP_LONG, 6,
                SNEPTestData.GALAXY_NEXUS_SNEP_LONG.length - 6);

        NdefMessage ndefMessage = new NdefMessage(ndefData);
        LOG.info(new String(ndefMessage.getPayload()));

        assertArrayEquals(SNEPTestData.GALAXY_NEXUS_LONG_PAYLOAD,
                ndefMessage.getPayload());

    }
}
