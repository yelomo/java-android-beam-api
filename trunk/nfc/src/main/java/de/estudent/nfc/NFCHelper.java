/**
 * ﻿Copyright (C) 2012 Wilko Oley woley@tzi.de
 *
 * This file is part of java-android-beam-api.
 *
 * java-android-beam-api is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-android-beam-api is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-android-beam-api.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von java-android-beam-api.
 *
 * java-android-beam-api ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * java-android-beam-api wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.estudent.nfc;

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
