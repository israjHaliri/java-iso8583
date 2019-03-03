package com.israj.haliri;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    public static void main(String[] args) {
        App app = new App();
        try {
            String message = app.buildMessage();

            System.out.println("Message = " + message);

            app.printMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildMessage() {
        GenericPackager genericPackager = getGenericPackager();

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(genericPackager);// Setting packager
        isoMsg.set(3, "020000");// Setting processing code
        isoMsg.set(4, "5000");// Setting transaction amount
        isoMsg.set(7,  new SimpleDateFormat("MMddHHmmss").format(new Date()));// Setting transmission date and time
        isoMsg.set(11, "123456");// Setting system trace audit number
        isoMsg.set(48, "Example Value");// Setting data element #49

        byte[] result = new byte[0];
        try {
            isoMsg.setMTI("0100");// Setting MTI

            result = isoMsg.pack();
        } catch (ISOException e) {
            e.printStackTrace();
        }

        return new String(result);

    }

    private void printMessage(String message) {
        GenericPackager genericPackager = getGenericPackager();

        ISOMsg isoMsg = new ISOMsg();
        isoMsg.setPackager(genericPackager);

        //convert ISO8583 Message String to byte[]
        byte[] byteMessage = new byte[message.length()];

        for (int i = 0; i < byteMessage.length; i++) {
            byteMessage[i] = (byte) (int) message.charAt(i);
        }

        // unpack & print the unpacked ISO8583
        System.out.println("-----Unpack Message-----");

        try {
            isoMsg.unpack(byteMessage);

            System.out.println("MTI = " + isoMsg.getMTI() + "");
        } catch (ISOException e) {
            e.printStackTrace();
        }

        for (int i = 1; i <= isoMsg.getMaxField(); i++) {
            if (isoMsg.hasField(i))
                System.out.println(i + " = " + isoMsg.getString(i) + "");
        }

        System.out.println("-----End Unpack Message-----");
    }

    private GenericPackager getGenericPackager() {
        InputStream is = getClass().getResourceAsStream("/iso87ascii.xml");
        GenericPackager packager = null;
        try {
            packager = new GenericPackager(is);
        } catch (ISOException e) {
            e.printStackTrace();
        }

        return packager;
    }
}
