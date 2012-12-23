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

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.exceptions.NFCException;
import de.estudent.accesscontrol.nfc.exceptions.NFCInitalizationException;
import de.estudent.accesscontrol.nfc.listener.BeamReceiveListener;
import de.estudent.accesscontrol.nfc.ndef.NdefMessage;
import de.estudent.accesscontrol.nfc.reader.NFCDevice;
import de.estudent.accesscontrol.nfc.reader.NFCDeviceFactory;
import de.estudent.accesscontrol.nfc.reader.NFCDeviceType;

public class IntegrationTestACR122U implements BeamReceiveListener {
    private final static Logger LOG = LoggerFactory
            .getLogger(IntegrationTestACR122U.class);

    @Test
    public void test() throws NFCInitalizationException, InterruptedException,
            NFCException {
        NFCDevice device = NFCDeviceFactory
                .createNFCDevice(NFCDeviceType.ACR122U);
        device.setBeamReceiveListener(this);
        device.initalizeWithDefaultValues();
        device.start();

        Thread.sleep(2000);
        LOG.info("FINISHED");

    }

    public void beamRecieved(NdefMessage message) {
        LOG.info("Recieved \n" + new String(message.getPayload()));
    }

}
