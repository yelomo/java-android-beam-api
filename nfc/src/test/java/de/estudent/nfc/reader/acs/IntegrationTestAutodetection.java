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

import static org.junit.Assert.*;

import org.junit.Test;

import de.estudent.nfc.reader.NFCDevice;
import de.estudent.nfc.reader.NFCDeviceFactory;
import de.estudent.nfc.reader.NFCDeviceType;

public class IntegrationTestAutodetection {

	@Test
	public void testCreateNFCDeviceTouchATag() {
		NFCDevice device = NFCDeviceFactory
				.createNFCDevice(NFCDeviceType.AUTODETECT);

		if (device instanceof ACR122UTouchATag)
			assertTrue(true);
		else
			fail("Reader not as TouchATag detected!" + device.getClass());

	}

	@Test
	public void testCreateNFCDeviceACR122() {
		NFCDevice device = NFCDeviceFactory
				.createNFCDevice(NFCDeviceType.AUTODETECT);

		if (device instanceof ACR122U)
			assertTrue(true);
		else
			fail("Reader not as ACR122U detected!" + device.getClass());

	}

}
