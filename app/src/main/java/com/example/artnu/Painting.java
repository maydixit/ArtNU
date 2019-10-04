package com.example.artnu;

public class Painting {
    final private String paintingName;
    final private String qrString;
    final private String code;

    public String getPaintingName() {
        return paintingName;
    }

    public String getQrString() {
        return qrString;
    }

    public String getCode() {
        return code;
    }

    public Painting(String name, String qrString, String code) {
        this.paintingName = name;
        this.qrString = qrString;
        this.code = code;
    }
}
