/**
 * ﻿Copyright (C) 2008 Wilko Oley woley@tzi.de
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
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.NFCHelper;
import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NFCInitalizationException;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;
import de.estudent.accesscontrol.nfc.listener.BeamReceiveListener;
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;
import de.estudent.accesscontrol.nfc.reader.NFCDevice;

/**
 * This is the class which handles the main Communication with the TouchATag
 * Reader.
 * 
 * @author Wilko Oley
 */
@SuppressWarnings("restriction")
public class TouchATag implements NFCDevice {

    private final static Logger LOG = LoggerFactory.getLogger(TouchATag.class);

    CardTerminal terminal = null;
    CardChannel cardChannel = null;

    BeamReceiveListener listener;

    private int max_allowed_size;
    private long timeout;

    /**
     * Initalize the Touch a Tag with predefined Values for maximal allowed size
     * and timeout. max_allowed_size = 2048 bytes timeout = 3500 Milisecounds
     */
    public void initalizeWithDefaultValues() throws NFCInitalizationException {
        initalize(3500, 2048);
    }

    /**
     * Initalize the Touch a Tag.
     * 
     * @param timeout
     *            How Long we will whait for the user to push the beam UI.
     * 
     * @param max_allowed_size
     *            Size in bytes how big a beam could be maximal.
     * 
     */

    public void initalize(long timeout, int max_allowed_size)
            throws NFCInitalizationException {
        this.max_allowed_size = max_allowed_size;
        this.timeout = timeout;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> list = factory.terminals().list();

            if (list.size() == 0) {
                throw new NFCInitalizationException("Card Reader not found!");
            } else {
                terminal = list.get(0);
                LOG.info("Card Reader " + terminal.getName() + " found!");
            }
            if (terminal.isCardPresent()) {
                Card card = terminal.connect("*");
                cardChannel = card.getBasicChannel();
            } else {
                throw new NFCInitalizationException(
                        "Reader not supported! Please connect Touch a Tag Reader");
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Reader initalized succesfully!");
                LOG.debug("Firmeware: " + getFirmewareVersion());
            }
        } catch (CardException ex) {
            throw new NFCInitalizationException("Error", ex);
        }
    }

    public void start() throws NFCException {

        turnAntennaOn();
        putReaderInInitiatorMode();

        try {
            whaitForAndroidBeam(timeout);
        } catch (InterruptedException e) {
            LOG.error("Error", e);
            throw new NFCException("Interrupted", e);
        } catch (NdefFormatException e) {
            LOG.error("Error", e);
            throw new NFCException("Format Error", e);
        } catch (IOException e) {
            LOG.error("Error", e);
            throw new NFCException("IO Error", e);
        }

    }

    public void setBeamReceiveListener(BeamReceiveListener _listener) {
        listener = _listener;

    }

    private byte[] sendAndReceive(byte instr, byte[] payload)
            throws NFCException {
        int payloadLength = (payload != null) ? payload.length : 0;
        byte[] instruction = { (byte) 0xd4, instr };

        // ACR122 header
        byte[] header = { (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) (instruction.length + payloadLength) };

        byte[] cmd = NFCHelper.append(header, instruction);

        cmd = NFCHelper.append(cmd, payload);

        NFCHelper.debugAPDUs(LOG, cmd, null);

        CommandAPDU c = new CommandAPDU(cmd);
        ResponseAPDU r;
        try {
            r = cardChannel.transmit(c);
            NFCHelper.debugAPDUs(LOG, null, r.getBytes());
            if (r.getSW1() == 0x63 && r.getSW2() == 0x27) {
                throw new NFCException("Wrong checksum from Response!");
            } else if (r.getSW1() == 0x63 && r.getSW2() == 0x7f) {
                throw new NFCException("Wrong PN53x command!");
            } else if (r.getSW1() != 0x90 && r.getSW2() != 0x00) {
                throw new NFCException("General error");
            }
        } catch (CardException e) {
            throw new NFCException("Error", e);
        }

        return r.getBytes();
    }

    private String getFirmewareVersion() throws CardException {
        CommandAPDU c = new CommandAPDU(TouchATagConstants.GET_FIRMWARE_VERSION);
        String s = null;
        s = new String(cardChannel.transmit(c).getBytes());
        return s;
    }

    private void turnAntennaOn() throws NFCException {
        LOG.debug("Turning Readers Antenna On!");
        CommandAPDU c = new CommandAPDU(TouchATagConstants.ANTENNA_ON);
        try {
            cardChannel.transmit(c);
        } catch (CardException e) {
            throw new NFCException("Error", e);
        }
    }

    private void putReaderInInitiatorMode() throws NFCException {
        LOG.debug("Initiator Mode initalizing");
        sendAndReceive(TouchATagConstants.IN_JUMP_FOR_DEP,
                TouchATagConstants.INITIATOR_PAYLOAD);
    }

    private void whaitForAndroidBeam(long timeout) throws InterruptedException,
            NFCException, NdefFormatException, IOException {
        long endTime = System.currentTimeMillis() + timeout;
        while (true) {
            Thread.sleep(150);
            byte[] targetConnect = { (byte) 0x01, (byte) 0x01, (byte) 0x04,
                    (byte) 0x20 };
            byte[] response = sendAndReceive((byte) 0x40, targetConnect);
            if (response[3] == (byte) 0x11) {
                LOG.info("Beam recieved, starting handshake");
                byte[] BEAM_TARGET_CC = { 0x01, (byte) 0x81, (byte) 0x84 };
                sendAndReceive((byte) 0x40, BEAM_TARGET_CC);
                startBeamHandshake();
                break;
            }
            if (System.currentTimeMillis() > endTime)
                throw new NFCException(
                        "Timeout while whaiting for Touch To Beam!");
        }
    }

    // this is were the magic happens!
    private void startBeamHandshake() throws NFCException, NdefFormatException,
            InterruptedException, IOException {
        byte[] BEAM_TARGET_CC = { 0x01, (byte) 0x81, (byte) 0x84 };
        byte[] target = { 0x01 };

        byte[] targetResponse = { 0x01, (byte) 0x83, (byte) 0x04, 0x00, 0x10,
                (byte) 0x80 }; // success send me more fragment

        byte[] targetResponseSuccess = { 0x01, (byte) 0x83, (byte) 0x04, 0x00,
                (byte) 0x10, (byte) 0x81 }; // success no more fragment

        byte[] targetDM = { 0x01, (byte) 0x81, (byte) 0xc4, 0x00 };

        byte[] targetResponseNDEF = { 0x01, (byte) 0x83, (byte) 0x04,
                (byte) 0x00, 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, 0x00 }; // send empty NDEF to Android to tell him
                                     // we finished
        int sent = 0;
        int recieved = 0;
        NdefMessage ndefMessage = null;
        try {
            byte[] response = sendAndReceive(
                    TouchATagConstants.IN_DATA_EXCHANGE, BEAM_TARGET_CC);
            recieved++;

            // SNEP Message
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] snep = getRecievedSNEP(response);
            stream.write(snep);

            int size = (((snep[2] & 0xff) << 24) | ((snep[3] & 0xff) << 16) | (snep[4] & 0xff) << 8)
                    | (snep[5] & 0xff);

            // TODO put this in configuration
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
                sendAndReceive(TouchATagConstants.IN_DATA_EXCHANGE,
                        targetResponseNDEF);
            } else {
                // recieve more fragments
                targetResponse[3] = (byte) ((sent * 16) + recieved);

                response = sendAndReceive(TouchATagConstants.IN_DATA_EXCHANGE,
                        targetResponse);
                sent++;

                stream.write(getRecievedSNEP(response));
                while (size > recievedBytes) {
                    targetResponseNDEF[3] = (byte) ((sent * 16) + recieved);

                    byte[] data = sendAndReceive(
                            TouchATagConstants.IN_DATA_EXCHANGE,
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
        } finally {
            targetResponseSuccess[3] = (byte) ((sent * 16) + recieved);

            sendAndReceive(TouchATagConstants.IN_DATA_EXCHANGE,
                    targetResponseSuccess);

            sendAndReceive(TouchATagConstants.IN_DATA_EXCHANGE, targetDM);

            sendAndReceive(TouchATagConstants.IN_RELEASE, target);
        }
        listener.beamRecieved(ndefMessage);
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

    private void close() throws NFCException {
        try {
            cardChannel.getCard().disconnect(false);
        } catch (CardException e) {
            throw new NFCException("Error", e);
        }
    }

}
