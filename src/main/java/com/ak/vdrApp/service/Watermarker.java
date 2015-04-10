package com.ak.vdrApp.service;


import com.ak.vdrApp.util.Constants;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDExtendedGraphicsState;

import java.awt.*;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


public final class Watermarker {
    public static String addWatermark(String filePath) throws IOException {
        PDDocument realDoc = PDDocument.load(filePath);

        int pages = realDoc.getDocumentCatalog().getAllPages().size();

        for(int i=0;i<pages;i++) {

            PDPage page = (PDPage) realDoc.getDocumentCatalog().getAllPages().get(i);

            PDExtendedGraphicsState extendedGraphicsState = new PDExtendedGraphicsState();
            extendedGraphicsState.setNonStrokingAlphaConstant(0.3f);
            PDResources resources = page.findResources();
            Map<String, PDExtendedGraphicsState> graphicsStateDictionary = resources.getGraphicsStates();
            if (graphicsStateDictionary == null) {
                graphicsStateDictionary = new TreeMap<>();
            }
            graphicsStateDictionary.put("TransparentState", extendedGraphicsState);
            resources.setGraphicsStates(graphicsStateDictionary);

            PDPageContentStream contentStream = new PDPageContentStream(realDoc, page, true, true);
            contentStream.appendRawCommands("/TransparentState gs\n");
            contentStream.setNonStrokingColor(Color.lightGray);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 32);
            contentStream.moveTextPositionByAmount(10, 10);
            contentStream.setTextRotation(1, 200, 200);
            contentStream.drawString(Constants.WATERMARK);
            contentStream.close();
        }

        String newFilePath = filePath+"_stamped.pdf";

        try {
            realDoc.save(newFilePath);
        } catch (COSVisitorException e) {
            e.printStackTrace();
        }


        return newFilePath;
    }


}