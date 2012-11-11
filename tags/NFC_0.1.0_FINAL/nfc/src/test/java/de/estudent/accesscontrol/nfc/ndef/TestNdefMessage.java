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
package de.estudent.accesscontrol.nfc.ndef;

import static org.junit.Assert.*;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.estudent.accesscontrol.nfc.NFCHelper;
import de.estudent.accesscontrol.nfc.exceptions.NdefFormatException;

/**
 * 
 * @author Wilko Oley
 */
public class TestNdefMessage {

    private final static Logger LOG = LoggerFactory
            .getLogger(TestNdefMessage.class);

    @Test
    public void parseShortNdefMessage() throws NdefFormatException {

        byte[] ndefData = NFCHelper.subByteArray(
                SNEPTestData.GALAXY_NEXUS_SNEP_SHORT, 6,
                SNEPTestData.GALAXY_NEXUS_SNEP_SHORT.length - 6);

        NdefMessage ndefMessage = new NdefMessage(ndefData);

        LOG.info(new String(ndefMessage.getPayload()));

        assertArrayEquals(SNEPTestData.GALAXY_NEXS_SHORT_PAYLOAD,
                ndefMessage.getPayload());

    }

    @Test
    public void parseLongNdefMessage() throws NdefFormatException {

        byte[] ndefData = NFCHelper.subByteArray(
                SNEPTestData.GALAXY_NEXUS_SNEP_LONG, 6,
                SNEPTestData.GALAXY_NEXUS_SNEP_LONG.length - 6);

        NdefMessage ndefMessage = new NdefMessage(ndefData);
        LOG.info(new String(ndefMessage.getPayload()));

        assertArrayEquals(SNEPTestData.GALAXY_NEXUS_LONG_PAYLOAD,
                ndefMessage.getPayload());

    }
}
