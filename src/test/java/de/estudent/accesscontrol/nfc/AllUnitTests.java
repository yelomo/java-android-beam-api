package de.estudent.accesscontrol.nfc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.estudent.accesscontrol.nfc.ndef.TestNdefMessage;
import de.estudent.accesscontrol.nfc.ndef.TestNdefRecord;

@RunWith(Suite.class)
@SuiteClasses({ TestNdefMessage.class, TestNdefRecord.class })
public class AllUnitTests {

}
