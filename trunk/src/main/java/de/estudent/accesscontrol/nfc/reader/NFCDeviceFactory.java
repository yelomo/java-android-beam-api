package de.estudent.accesscontrol.nfc.reader;

import de.estudent.accesscontrol.nfc.reader.acs.TouchATag;

/**
 * 
 * @author Wilko Oley
 */
public class NFCDeviceFactory {

    public static NFCDevice createNFCDevice(NFCDeviceType type) {

        switch (type) {
        case TOUCH_A_TAG:
            return new TouchATag();
        default:
            return new TouchATag();
        }

    }
}
