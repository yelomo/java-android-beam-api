package de.estudent.accesscontrol.nfc.ndef;

import org.apache.commons.lang3.ArrayUtils;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;

/**
 * 
 * @author Wilko Oley
 */
public class NdefMessage {
    private NdefRecord[] ndefRecords = new NdefRecord[128];

    public NdefMessage(NdefRecord[] _ndefRecords) {
        ndefRecords = _ndefRecords;
    }

    public NdefMessage(byte[] data) throws NdefFormatException {
        parseNdefMessage(data);
    }

    public byte[] getPayload() {
        byte[] out = ArrayUtils.EMPTY_BYTE_ARRAY;
        for (int i = 0; i < ndefRecords.length - 1; i++) {
            if (ndefRecords[i] == null)
                return out;
            if (out.length == 0)
                out = ndefRecords[i].getPayload();
            else ArrayUtils.addAll(out, ndefRecords[i].getPayload());
        }
        return out;
    }

    public NdefRecord[] getNdefRecords() {
        return ndefRecords;
    }

    // TODO Allow parsing of Messages with multiple ndefRecords
    private void parseNdefMessage(byte[] data) throws NdefFormatException {
        ndefRecords[0] = new NdefRecord(data);
    }
}
