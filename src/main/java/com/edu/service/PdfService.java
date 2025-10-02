package com.edu.service;

import java.io.ByteArrayInputStream;

public interface PdfService {
    public ByteArrayInputStream generarPdfDesdeHtml(String html) throws Exception;
}
