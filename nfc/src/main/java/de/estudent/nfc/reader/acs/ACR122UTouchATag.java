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
package de.estudent.nfc.reader.acs;

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

import de.estudent.nfc.NFCHelper;
import de.estudent.nfc.exceptions.NFCException;
import de.estudent.nfc.exceptions.NFCInitalizationException;
import de.estudent.nfc.exceptions.NdefFormatException;
import de.estudent.nfc.listener.BeamReceiveListener;
import de.estudent.nfc.ndef.NdefMessage;

/**
 * This is the class which handles the main Communication with the TouchATag
 * Reader.
 * 
 * @author Wilko Oley
 */
@SuppressWarnings("restriction")
public class ACR122UTouchATag extends AcsNFCDevice {

	private final static Logger LOG = LoggerFactory
			.getLogger(ACR122UTouchATag.class);

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
			NdefMessage message = whaitForAndroidBeam(timeout, max_allowed_size);
			listener.beamRecieved(message);
		} catch (NdefFormatException e) {
			LOG.error("Error", e);
			throw new NFCException("Format Error", e);
		}
	}

	public void setBeamReceiveListener(BeamReceiveListener _listener) {
		listener = _listener;

	}

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
		CommandAPDU c = new CommandAPDU(AcsConstants.GET_FIRMWARE_VERSION);
		String s = null;
		s = new String(cardChannel.transmit(c).getBytes());
		return s;
	}

	private void turnAntennaOn() throws NFCException {
		LOG.debug("Turning Readers Antenna On!");
		CommandAPDU c = new CommandAPDU(AcsConstants.ANTENNA_ON);
		try {
			cardChannel.transmit(c);
		} catch (CardException e) {
			throw new NFCException("Error", e);
		}
	}

	public void close() throws NFCException {
		try {
			if (cardChannel != null) {
				cardChannel.getCard().disconnect(false);
			}
		} catch (CardException e) {
			throw new NFCException("Error", e);
		}
	}

}
