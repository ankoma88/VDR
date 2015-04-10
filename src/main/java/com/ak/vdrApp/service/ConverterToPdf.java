package com.ak.vdrApp.service;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import java.io.File;
import java.io.IOException;

public final class ConverterToPdf {

    public static String convertToPdf(String pathToFile) throws IOException {

        // StartOpenOffice.org instance should be running on port 8100. If on Linux: "soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard";

        File inputFile = new File(pathToFile);
        File outputFile = new File(pathToFile+".pdf");

        OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
        connection.connect();

        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
        converter.convert(inputFile, outputFile);

        connection.disconnect();

        return outputFile.getAbsolutePath();

    }
}
