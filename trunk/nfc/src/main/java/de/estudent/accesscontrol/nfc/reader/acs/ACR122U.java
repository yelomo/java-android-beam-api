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

import java.util.List;

import javax.smartcardio.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.NFCHelper;
import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NFCInitalizationException;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;
import de.estudent.accesscontrol.nfc.listener.BeamReceiveListener;
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;

@SuppressWarnings("restriction")
public class ACR122U extends AcsNFCDevice {

    private final static Logger LOG = LoggerFactory.getLogger(ACR122U.class);

    BeamReceiveListener listener;
    CardTerminal terminal = null;
    Card card = null;

    private String osName = null;

    private int max_allowed_size;
    private long timeout;

    public void initalizeWithDefaultValues() throws NFCInitalizationException {
        initalize(3500, 2048);
    }

    public void initalize(long timeout, int max_allowed_size)
            throws NFCInitalizationException {
        this.max_allowed_size = max_allowed_size;
        this.timeout = timeout;
        osName = System.getProperty("os.name");

        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> list = factory.terminals().list();
            if (list.size() == 0) {
                throw new NFCInitalizationException("Card Reader not found!");
            } else {
                terminal = list.get(0);
                LOG.info("Card Reader " + terminal.getName() + " found!");
            }
        } catch (CardException e) {
            throw new NFCInitalizationException(
                    "Error initializing Card Reader", e);
        }
    }

    public void start() throws NFCException {
        try {
            terminal.waitForCardPresent(0);
            Thread.sleep(100);
            card = terminal.connect("DIRECT");
            card.beginExclusive();
            putReaderInInitiatorMode();
            NdefMessage message = whaitForAndroidBeam(timeout, max_allowed_size);
            listener.beamRecieved(message);
            card.endExclusive();

        } catch (CardException e) {
            throw new NFCException("Error connecting to Phone!", e);
        } catch (NdefFormatException e) {
            throw new NFCException("wrong format received", e);
        } catch (InterruptedException e) {
            throw new NFCException("Interrupted while sleeping", e);
        }

    }

    public void setBeamReceiveListener(BeamReceiveListener _listener) {
        listener = _listener;
    }

    public void close() throws NFCException {
        throw new RuntimeException("not yet implemented!");
    }

    @Override
    protected byte[] sendAndReceive(byte instr, byte[] payload)
            throws NFCException {
        int payloadLength = (payload != null) ? payload.length : 0;
        byte[] instruction = { (byte) 0xd4, instr };

        // ACR122 header
        byte[] header = { (byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) (instruction.length + payloadLength) };

        byte[] cmd = NFCHelper.append(header, instruction);

        cmd = NFCHelper.append(cmd, payload);

        NFCHelper.debugAPDUs(LOG, cmd, null);
        int controlCode;
        if (osName.startsWith("Windows")) {
            controlCode = 0x310000 + 3500 * 4;
        } else {
            controlCode = 0x42000000 + 3500;
        }

        byte[] response = null;
        try {
            response = card.transmitControlCommand(controlCode, cmd);
        } catch (CardException e) {
            throw new NFCException("Error transmitting!", e);
        }
        return response;
    }

}
