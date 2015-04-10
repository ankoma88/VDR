# VDR
A virtual data room (VDR) is an online repository of information that is used for the storing and distribution of documents. 

Currently the following formats are supported: pdf, doc, xls, ppt, jpg, png.
Authorized users can download uploaded documents.
Users with no download permissions can view documents converted to pdf format containing confidentiality watermarks.
Users can ask questions and add comments to uploaded documents.

Requirements: Java EE 7 SDK, MySQL database, Maven. Tested on Glassfish 4.1.
Before starting the application an instance of StartOpenOffice.org should be running on port 8100. 
If on Linux, just write in terminal: "soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard;
This is needed by JODConverter API used in the project.

Used technologies:
Java EE: EJB, JSF, Primefaces components, Bootstrap.
