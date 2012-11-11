package de.estudent.accesscontrol.nfc;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author Wilko Oley
 */
public class TestNFCHelper {

    @Test
    public void TestToHexString() {
        String hex = NFCHelper.encodeToHexString(new Byte("127"));

        assertEquals("Hex String", "0x7F", hex);
    }

    @Test
    public void TestByteArrayToString() {
        byte[] array = { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0F, (byte) 0xD1 };
        assertEquals("Byte Array Hex", "0x10 0x02 0x00 0x00 0x00 0x0F 0xD1 ",
                NFCHelper.byteArrayToString(array));
    }

    @Test
    public void TestByteArrayToTestData() {
        byte[] array = { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0F, (byte) 0xD1 };

        assertEquals(
                "Byte Test Array Hex",
                "byte[] array = { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0xD1 };",
                NFCHelper.printJavaTestArray((array)));
    }

    @Test
    public void TestAppendToByteArray() {
        byte[] array1 = { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0F, (byte) 0xD1 };

        byte[] array2 = { (byte) 0x12, (byte) 0x02, (byte) 0x11, (byte) 0x00,
                (byte) 0xFF, (byte) 0x0F, (byte) 0xD1 };

        byte[] result = { (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                (byte) 0x00, (byte) 0x0F, (byte) 0xD1, (byte) 0x12,
                (byte) 0x02, (byte) 0x11, (byte) 0x00, (byte) 0xFF,
                (byte) 0x0F, (byte) 0xD1 };

        assertArrayEquals(result, NFCHelper.append(array1, array2));

    }

}
