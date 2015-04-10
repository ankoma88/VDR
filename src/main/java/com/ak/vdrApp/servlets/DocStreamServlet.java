package com.ak.vdrApp.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/streamedDoc.pdf")
public class DocStreamServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        byte[] content = (byte[]) request.getSession().getAttribute("streamedBytes");

        response.setContentType("application/pdf");
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
    }

}
