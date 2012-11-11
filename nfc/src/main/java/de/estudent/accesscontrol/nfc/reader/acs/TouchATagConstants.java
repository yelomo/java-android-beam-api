package de.estudent.accesscontrol.nfc.reader.acs;

/**
 * 
 * This class provides general Commands for the PN53x Chip
 * 
 * @author Wilko Oley
 */
public class TouchATagConstants {

    public static final byte[] ANTENNA_ON = { (byte) 0xFF, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0xD4, (byte) 0x32,
            (byte) 0x01, (byte) 0x01 };

    public static final byte[] GET_FIRMWARE_VERSION = { (byte) 0xff,
            (byte) 0x00, (byte) 0x48, (byte) 0x00, (byte) 0x00 };

    // Commands
    public static final byte IN_JUMP_FOR_DEP = (byte) 0x56;
    public static final byte IN_DATA_EXCHANGE = (byte) 0x40;
    public static final byte IN_RELEASE = (byte) 0x52;

    // Payload
    public static final byte[] INITIATOR_PAYLOAD = { (byte) 0x01, (byte) 0x02,
            (byte) 0x04, (byte) 0x46, (byte) 0x66, (byte) 0x6D, (byte) 0x01,
            (byte) 0x01, (byte) 0x10, (byte) 0x03, (byte) 0x02, (byte) 0x00,
            (byte) 0x01, (byte) 0x04, (byte) 0x01, (byte) 0x96 };
    public static final byte[] BEAM_TARGET_CONNECT = { (byte) 0x01,
            (byte) 0x01, (byte) 0x04, (byte) 0x01 };

}
