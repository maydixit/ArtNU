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
            new Painting(0,"La Naissance de Vénus", "Botticelli", "renaissance19_01", "1485"),
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
            new Painting(1, "", "", "renaissance19_14", ""));

    private static Map<Integer, STATUS> statusMap = new HashMap<>();

    public static boolean isUnlocked(Integer id) {
        if (statusMap.containsKey(id)) {
            return statusMap.get(id) == STATUS.UNLOCKED;
        }
        return false;
    }

    public static boolean codeMatch(Painting painting, String code) {
        return painting.getCode().equals(code);
    }

    enum STATUS {
        UNLOCKED,
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

    public static void setStatus(int id, STATUS status, Context context) {
                statusMap.put(id, status);
                writeConfig(context);
    }

    private static void writeConfig(Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config_artnu", Context.MODE_PRIVATE));
            for (int k : statusMap.keySet()) {
                outputStreamWriter.write(String.valueOf(k) + "\t" + statusMap.get(k).name() + "\n");
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    static void readConfig(Context context) {
        statusMap = new HashMap<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput("config_artnu")));
            String receiveString = "";
            while ((receiveString = bufferedReader.readLine()) != null) {
                String[] values = receiveString.split("\t");
                statusMap.put(Integer.valueOf(values[0]), STATUS.valueOf(values[1]));
            }
            bufferedReader.close();
        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
        if (statusMap.size() == 0) {
            statusMap.put(0, STATUS.UNLOCKED);
        }
    }

    static void writeChoice(Context context, int choice) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("choice_artnu", Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(choice));
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    static int readChoice(Context context) {
        int choice = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.openFileInput("choice_artnu")));
            String receiveString = "";

            while ((receiveString = bufferedReader.readLine()) != null) {
                try {
                    choice = Integer.parseInt(receiveString);
                    if (choice < 0) choice = 0;
                }
                catch (Exception e) {
                }
            }
            bufferedReader.close();

        } catch (IOException e) {
            Log.e("Exception", "File read failed: " + e.toString());
        }
        return choice;
    }
}
