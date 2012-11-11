/**
 * ﻿Copyright (C) 2012 Wilko Oley woley@tzi.de
 *
 * This file is part of java-android-beam-api.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von java-android-beam-api.
 *
 * Fubar ist Freie Software: Sie können es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 * veröffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Fubar wird in der Hoffnung, dass es nützlich sein wird, aber
 * OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
