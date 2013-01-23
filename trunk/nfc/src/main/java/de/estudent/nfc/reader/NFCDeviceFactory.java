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
package de.estudent.nfc.reader;

import java.awt.CardLayout;
import java.util.List;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.nfc.reader.acs.ACR122U;
import de.estudent.nfc.reader.acs.ACR122UTouchATag;

/**
 * 
 * @author Wilko Oley
 */
public class NFCDeviceFactory {

    private final static Logger LOG = LoggerFactory
            .getLogger(NFCDeviceFactory.class);

    /**
     * This Method will create a new NFCDevice in the given
     * {@link NFCDeviceType}. The Type "autodetection" should be handled with
     * care, it can lead to wrong results and should only be used for initial
     * testing and not in production!
     * 
     * @param type
     *            The type of the connectet NFCReader
     * @return an NFCDevice with the given type
     */
    public static NFCDevice createNFCDevice(NFCDeviceType type) {

        if (type == NFCDeviceType.AUTODETECT)
            type = findConnectedDevice();

        switch (type) {
        case TOUCH_A_TAG:
            return new ACR122UTouchATag();
        case ACR122U:
            return new ACR122U();
        default:
            return null;
        }

    }

    @SuppressWarnings("restriction")
    private static NFCDeviceType findConnectedDevice() {
        CardTerminal terminal;
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            List<CardTerminal> list = factory.terminals().list();
            if (list.isEmpty())
                return null;
            terminal = list.get(0);
            LOG.info("Found " + terminal.getName());
            if (!terminal.getName().startsWith("ACS ACR122"))
                return null;

        } catch (CardException e) {
            LOG.error("Not Reader found!", e);
            return null;
        }
        try {
            if (!terminal.isCardPresent())
                throw new CardException("no SAM Module");
            terminal.connect("*");
        } catch (CardException e1) {
            try {
                terminal.connect("DIRECT");
                return NFCDeviceType.ACR122U;
            } catch (CardException e) {
                LOG.debug("No compatible Reader found trough autodetection!");
            }
        }
        return NFCDeviceType.TOUCH_A_TAG;
    }
}
