package com.example.artnu;

import java.util.Arrays;
import java.util.List;

public class PaintingUtil {
    private static List<Painting> paintings = Arrays.asList(new Painting("Name 1", "qr1", "code1"),
            new Painting("Name2", "qr2", "code2"),
            new Painting("name3", "qr3", "code3"));

    public static List<Painting> getPaintings() {
        return paintings;
    }

    public static Painting getPaintingForQrValueOrNull(String qrCode) {
        for (Painting painting: paintings) {
            if (painting.getQrString().equals(qrCode)) {
                return painting;
            }
        }
        return null;
    }

    public static boolean matchPaintingWithCode(String name, String code) {
        for (Painting painting: paintings) {
            if (painting.getPaintingName().equals(name)) {
                return (painting.getCode().equals(code));
            }
        }
        return false;
    }

}
