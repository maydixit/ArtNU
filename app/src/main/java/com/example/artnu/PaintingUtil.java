package com.example.artnu;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaintingUtil {
    private static List<Painting> paintings = Arrays.asList(
            new Painting(1,"La Naissance de Vénus", "Botticelli", "renaissance19_01", "1485"),
            new Painting(2, "La Méduse", "Caravage", "renaissance19_02", "1598"),
            new Painting(3, "La Lippina", "Lippi", "renaissance19_03", "1465"),
            new Painting(4, "Le Sacrifice d'Isaac", "Caravage", "renaissance19_04", "1603"),
            new Painting(5, "Autumn", "Arcimboldo", "renaissance19_05", "1573"),
            new Painting(6, "", "", "renaissance19_06", ""),
            new Painting(7, "", "", "renaissance19_07", ""),
            new Painting(8, "", "", "renaissance19_08", ""),
            new Painting(9, "", "", "renaissance19_09", ""),
            new Painting(10, "", "", "renaissance19_10", ""),
            new Painting(11, "", "", "renaissance19_11", ""),
            new Painting(12, "", "", "renaissance19_12", ""),
            new Painting(13, "", "", "renaissance19_13", ""),
            new Painting(14, "", "", "renaissance19_14", ""));

    private static Map<String, STATUS> statusMap = new HashMap<>();

    enum STATUS {
        UNLOCKED,
        PARTIAL,
        LOCKED
    }

    public static List<Painting> getPaintings() {
        return paintings;
    }

    // write get methods to use this 

    public static Painting getPaintingForQrValueOrNull(String qrCode) {
        for (Painting painting : paintings) {
            if (painting.getQrString().equals(qrCode)) {
                return painting;
            }
        }
        return null;
    }

    public static boolean matchPaintingWithCode(String name, String code) {
        for (Painting painting : paintings) {
            if (painting.getPaintingName().equals(name)) {
                return (painting.getCode().equals(code));
            }
        }
        return false;
    }

    public void upgradeStatus(String paintingName, Context context) {
        for (String name : statusMap.keySet()) {
            if (paintingName.equals(name)) {
                switch (statusMap.get(paintingName)) {
                    case LOCKED:
                        statusMap.put(paintingName, STATUS.PARTIAL);
                        writeConfig(context);
                        break;
                    case PARTIAL:
                        statusMap.put(paintingName, STATUS.UNLOCKED);
                        writeConfig(context);
                        break;
                }
            }
        }
    }

    public static void writeConfig(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config_artnu", Context.MODE_PRIVATE));
            for (String k : statusMap.keySet()) {
                outputStreamWriter.write(k + "\t" + statusMap.get(k).name() + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void readConfig(Context context) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.openFileInput("config_artnu"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString = "";
            statusMap = new HashMap<>();
            while ((receiveString = bufferedReader.readLine()) != null) {
                String[] values = receiveString.split("\t");
                statusMap.put(values[0], STATUS.valueOf(values[1]));
            }
            inputStreamReader.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
