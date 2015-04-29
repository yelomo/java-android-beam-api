# java-android-beam-api
Automatically exported from code.google.com/p/java-android-beam-api

This Project will provide an easy way to communicate between an NFC-enabled Android Device and an ACR Reader connected to your Desktop. Further informations can be found in the Wiki or Google Group.
# News
13.12.2012
Now there is a google group for this project and about Android Beam with the ACR Readers in general. Join it here!

11.11.2012
The Version 0.1.0 of the java-android-beam-api has been released!

It can be seen as a stable Beta Version. Further Informations on compatibility can be found in the wiki.

26.10.2012
First release will follow in the next day, when we clarified code licensing and completed final testing.

java-android-beam-api is developed, by students of University Bremen, in the context of the eStudent Project.

# What is this?
This API for Java will provide an easy way to access an NFC Reader (like the Touch a Tag devices) to receive "beams" from your android device. So you can receive any data which can be send via android beam. Native applications, like the contact app (which will beam vcards), and your own apps are supported.

_Currently it only supports recieving data from the Android Device, sending may be supported in a further version._

## General Prerequisites

* NFC Reader
* Java SE
* Android Device with NFC Chip
* driver for the NFC Reader _(optional)_

## NFC Reader

The API is currently compatible (and tested) with the following NFC-Reader:

* ACR122U Firmeware: 103

_other readers have not been tested yet, please contact us if you got it running with another reader_
## Android Phones
The API is currently compatible (and tested) with the following Android phones/ Versions:

* Galaxy Nexus (4.1.2 Stock Rom)
* Samsung Galaxy S3 (4.1.1 Stock Rom)

# Software Prerequisites
* Java SE Development Kit 7u9
* Maven _(recommended)_

_The API is currently tested under Windows 7 only, but it should be no problem to get it running on a linux mashine. _

## Install in your local Maven Repository
To install our Artifact in your local Maven Repository, to use it in your maven projects, please follow these steps:

1. Download the API jar file

2. Unpack the Files and execude the following command:
```
mvn install:install-file -Dfile="<jar-name>"
```

3. Now you installed the pom file and jar in your local repository
include the NFC-API with the following lines in your pom:
```
<dependency>
  <groupId>de.estudent.accesscontrol</groupId>
  <artifactId>nfc</artifactId>
   <version> VERSION NUMBER </version>
</dependency>
```
## Start Coding
Now you are ready to use our API in your Maven Project.

Next step would be to implement a BeamReceiveListener like this
``` java
public class MyListener implements BeamReceiveListener {

    public void beamRecieved(NdefMessage message) {
        // handle the message here
    }
}
```

Now you can initalize your NFC Reader and start the Android Beam Handshake.

``` java
public static void main(String[] args) {
    NFCDevice device = NFCDeviceFactory
            .createNFCDevice(NFCDeviceType.AUTODETECT);
    device.setBeamReceiveListener(this);
    device.initalizeWithDefaultValues();
    device.start();  // the device will whait for the Android Device now
}
```

# General Resources
This Section is for general Samples and Help

## Firmeware Version via direct connect
This will work only on Windows machines! 
``` java
TerminalFactory factory;
List<CardTerminal> terminals;
factory = TerminalFactory.getDefault();
terminals = factory.terminals().list();
CardTerminal terminal = terminals.get(0);

byte[] GET_FIRMWARE_VERSION = { (byte) 0xff, (byte) 0x00, (byte) 0x48,
        (byte) 0x00, (byte) 0x00 };

int controlCode = 0x310000 + 3500 * 4;
Card card = terminal.connect("DIRECT");
byte[] response = card.transmitControlCommand(controlCode,
        GET_FIRMWARE_VERSION);

System.out.println("Firmeware: " + new String(response));
```

## PCSC-lite and linux

You need the following packages, so execude 

```
sudo apt-get install libpcsclite1 pcscd libccid libpcsclite-dev
```

In some Versions the JVM only searches for the pcsclite libs under /usr/lib64:/usr/local/lib:/usr/local/lib64. 
If your libpcsclite.so.1 is in another directory you can correct it by setting the System Variable JAVA_OPTIONS. 

For example
```
JAVA_OPTIONS="-Dsun.security.smartcardio.library=/lib/x86_64-linux-gnu/libpcsclite.so.1"
```

Maybe this page will help you with some problems:
https://wiki.archlinux.org/index.php/Touchatag_RFID_Reader

_We had to set the ifdDriverOptions, to work with our reader_

This part is under construction and will be updated frequently! If you have some comments please contact me!
