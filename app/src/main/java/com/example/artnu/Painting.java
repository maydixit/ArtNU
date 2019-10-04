package com.example.artnu;

public class Painting {
    final private Integer id;
    final private String paintingName;
    final private String painter;
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

    public Painting(Integer id, String name, String painter, String qrString, String code) {
        this.id = id;
        this.paintingName = name;
        this.painter = painter;
        this.qrString = qrString;
        this.code = code;
    }
}
