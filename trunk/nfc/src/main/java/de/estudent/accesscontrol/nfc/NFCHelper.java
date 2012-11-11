package de.estudent.accesscontrol.nfc;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides some helpers for debugging, converting Strings and byte
 * array operations needed by this API.
 * 
 * 
 * @author Wilko Oley
 */
public class NFCHelper {

    private final static Logger LOG = LoggerFactory.getLogger(NFCHelper.class);

    public static String encodeToHexString(byte b) {
        byte[] array = new byte[1];
        array[0] = b;
        String result = new String(Hex.encodeHex(array));
        result = "0x" + result.toUpperCase();
        return result;
    }

    public static String byteArrayToString(byte[] a) {
        StringBuilder builder = new StringBuilder();
        for (byte b : a) {
            builder.append(encodeToHexString(b) + " ");
        }
        return builder.toString();
    }

    public static String printJavaTestArray(byte[] a) {

        StringBuilder builder = new StringBuilder();
        builder.append("byte[] array = { ");
        for (int i = 0; i < a.length; i++) {
            if (i < a.length - 1) {
                builder.append("(byte) " + encodeToHexString(a[i]) + ", ");
            } else {
                builder.append("(byte) " + encodeToHexString(a[i]));
            }
        }
        builder.append(" };");
        return builder.toString();
    }

    public static byte[] append(byte[] first, byte[] second) {
        return ArrayUtils.addAll(first, second);
    }

    public static byte[] subByteArray(byte[] array, int offset, int length) {
        if (array == null)
            throw new NullPointerException();
        if (length < 0 || offset < 0)
            throw new ArrayIndexOutOfBoundsException();
        byte[] result = new byte[length];
        System.arraycopy(array, offset, result, 0, length);
        return result;
    }

    public static void logAPDUs(byte[] c, byte[] r) {
        if (c != null)
            LOG.debug("sending:   | [" + c.length + " bytes] | "
                    + byteArrayToString(c));
        if (r != null)
            LOG.debug("recieving: | [" + r.length + " bytes] | "
                    + byteArrayToString(r));
    }

    public static void debugAPDUs(Logger log, byte[] c, byte[] r) {
        if (c != null)
            log.debug("sending:   | [" + c.length + " bytes] | "
                    + byteArrayToString(c));
        if (r != null)
            log.debug("recieving: | [" + r.length + " bytes] | "
                    + byteArrayToString(r));
    }

}
