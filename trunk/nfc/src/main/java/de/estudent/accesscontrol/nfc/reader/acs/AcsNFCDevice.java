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
package de.estudent.accesscontrol.nfc.reader.acs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.NFCHelper;
import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;
import de.estudent.accesscontrol.nfc.reader.NFCDevice;

/**
 * 
 * This class will provide general methods which should be the same for all the
 * ACS Readers.
 * 
 * @author Wilko Oley
 * 
 */
abstract class AcsNFCDevice implements NFCDevice {

    private final static Logger LOG = LoggerFactory
            .getLogger(AcsNFCDevice.class);

    protected abstract byte[] sendAndReceive(byte instr, byte[] payload)
            throws NFCException;

    protected NdefMessage whaitForAndroidBeam(long timeout, int max_allowed_size)
            throws NFCException, NdefFormatException {
        long endTime = System.currentTimeMillis() + timeout;
        while (true) {
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                throw new NFCException("Interrupted!", e);
            }
            byte[] targetConnect = { (byte) 0x01, (byte) 0x01, (byte) 0x04,
                    (byte) 0x20 };
            byte[] response = sendAndReceive((byte) 0x40, targetConnect);
            if (response[3] == (byte) 0x11) {
                LOG.info("Beam recieved, starting handshake");
                byte[] BEAM_TARGET_CC = { 0x01, (byte) 0x81, (byte) 0x84 };
                sendAndReceive((byte) 0x40, BEAM_TARGET_CC);
                return startBeamHandshake(max_allowed_size);
            }
            if (System.currentTimeMillis() > endTime)
                throw new NFCException(
                        "Timeout while whaiting for Touch To Beam!");
        }
    }

    // this is were the magic happens!
    private NdefMessage startBeamHandshake(int max_allowed_size)
            throws NFCException, NdefFormatException {
        byte[] BEAM_TARGET_CC = { 0x01, (byte) 0x81, (byte) 0x84 };
        byte[] target = { 0x01 };

        byte[] targetResponse = { 0x01, (byte) 0x83, (byte) 0x04, 0x00, 0x10,
                (byte) 0x80 }; // More fragments!

        byte[] targetResponseSuccess = { 0x01, (byte) 0x83, (byte) 0x04, 0x00,
                (byte) 0x10, (byte) 0x81 }; // All fragments received

        byte[] targetDM = { 0x01, (byte) 0x81, (byte) 0xc4, 0x00 };

        byte[] targetResponseNDEF = { 0x01, (byte) 0x83, (byte) 0x04,
                (byte) 0x00, 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, 0x00 }; // emtpy NDEF Message

        int sent = 0;
        int recieved = 0;
        NdefMessage ndefMessage = null;
        try {
            byte[] response = sendAndReceive(
                    ACSConstants.IN_DATA_EXCHANGE, BEAM_TARGET_CC);
            recieved++;

            // SNEP Message
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] snep = getRecievedSNEP(response);
            stream.write(snep);

            int size = (((snep[2] & 0xff) << 24) | ((snep[3] & 0xff) << 16) | (snep[4] & 0xff) << 8)
                    | (snep[5] & 0xff);

            if (size > max_allowed_size)
                throw new NFCException(
                        "Message is to long to beam. max_allowed_size "
                                + max_allowed_size);

            byte[] ndef = NFCHelper.subByteArray(snep, 6, snep.length - 6);
            int recievedBytes = ndef.length;

            LOG.debug("Recieved Bytes: " + recievedBytes + "," + "Size: "
                    + size);

            if (recievedBytes == size) {
                // will not recieve more so create NdefMessage
                ndefMessage = new NdefMessage(ndef);

                targetResponseNDEF[3] = (byte) ((sent * 16) + recieved);
                sendAndReceive(ACSConstants.IN_DATA_EXCHANGE,
                        targetResponseNDEF);
            } else {
                // recieve more fragments
                targetResponse[3] = (byte) ((sent * 16) + recieved);

                response = sendAndReceive(ACSConstants.IN_DATA_EXCHANGE,
                        targetResponse);
                sent++;

                stream.write(getRecievedSNEP(response));
                while (size > recievedBytes) {
                    targetResponseNDEF[3] = (byte) ((sent * 16) + recieved);

                    byte[] data = sendAndReceive(
                            ACSConstants.IN_DATA_EXCHANGE,
                            targetResponseNDEF);
                    sent++;
                    if (data[3] == 0x13 & data[4] == 0x20) {
                        LOG.debug("Fragment recieved");

                        recieved++;
                        // SNEP Fragment
                        byte[] fragment = getRecievedSNEP(data);

                        stream.write(fragment);
                        recievedBytes += fragment.length;
                        LOG.debug("Recieved Bytes: " + recievedBytes + ","
                                + "Size: " + size);
                    }
                }

                byte[] data = stream.toByteArray();
                byte[] ndefLong = NFCHelper.subByteArray(data, 6,
                        data.length - 6);
                ndefMessage = new NdefMessage(ndefLong);
            }
        } catch (IOException e) {
            throw new NFCException("IO-Error", e);
        } finally {
            targetResponseSuccess[3] = (byte) ((sent * 16) + recieved);

            sendAndReceive(ACSConstants.IN_DATA_EXCHANGE,
                    targetResponseSuccess);

            sendAndReceive(ACSConstants.IN_DATA_EXCHANGE, targetDM);

            sendAndReceive(ACSConstants.IN_RELEASE, target);
        }
        return ndefMessage;
    }

    private byte[] getRecievedSNEP(byte[] data) {

        // Received LLCP
        byte[] llcp = NFCHelper.subByteArray(data, 3, data.length - 5);

        // to SNEP protocol message
        byte[] snep = NFCHelper.subByteArray(llcp, 3, llcp.length - 3);

        return snep;
        // // SNEP to NDEF Message
        // byte[] ndef = NFCHelper.subByteArray(snep, 6, snep.length - 6);

    }

}
