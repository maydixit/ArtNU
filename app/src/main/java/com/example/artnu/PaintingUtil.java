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
import java.util.Random;

public class PaintingUtil {
    private static List<Integer> FIRST_CHOICE_LIST = Arrays.asList(1, 2, 3, 6, 8, 9);

    private static List<Painting> paintings = Arrays.asList(
            new Painting(0,"1. Bicentennial Print", "Roy Lichtenstein", "renaissance19_00", "1975"),
            new Painting(1,"2. Les Femmes d'Alger", "Pablo Picasso", "renaissance19_01", "1955"),
            new Painting(2,"3. Tête de Clown", "Joseph Kutter", "renaissance19_02", "1937"),
            new Painting(3,"4. Cavalli in riva al mare", "Giorgio de Chirico", "renaissance19_03", "1928"),
            new Painting(4,"5. Kaleidoscope", "none", "renaissance19_04", "2102"),
            new Painting(5,"6. Losanges", "none", "renaissance19_05", "2080"),
            new Painting(6,"7. Coquelicots", "Claude Monet", "renaissance19_06", "1873"),
            new Painting(7,"8. Ritmo Plastico", "Gino Severini", "renaissance19_07", "1913"),
            new Painting(8,"9. La Nuit Etoilée", "Vincent Van Gogh", "renaissance19_08", "1889"),
            new Painting(9,"10. Le Cri", "Edvard Munch", "renaissance19_09", "1893"),
            new Painting(10,"11. Le Procès", "Wolfgang Lettl", "renaissance19_10", "1981"),

            new Painting(101,"La Naissance de Vénus", "Botticelli", "renaissance19_101", "1485"),
            new Painting(102, "La Méduse", "Caravage", "renaissance19_102", "1598"),
            new Painting(103, "La Lippina", "Lippi", "renaissance19_103", "1465"),
            new Painting(104, "Le Sacrifice d'Isaac", "Caravage", "renaissance19_104", "1603"),
            new Painting(105, "Autumn", "Arcimboldo", "renaissance19_105", "1573"));

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
            statusMap.put(FIRST_CHOICE_LIST.get(new Random().nextInt(FIRST_CHOICE_LIST.size())), STATUS.UNLOCKED);
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
