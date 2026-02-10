package com.armarizki.chromatic.controller.fileio;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public final class FileIO {

    static final String FILE_EXT = ".json";

    private FileIO() {}

    static void writeToFile(Context context, String filepath, String contents) throws IOException {
        try (FileOutputStream fos = context.openFileOutput(filepath, Context.MODE_PRIVATE)) {
            fos.write(contents.getBytes());
        }
    }

    static String readFromFile(Context context, String filepath) throws IOException {
        FileInputStream fis = context.openFileInput(filepath);
        InputStreamReader inputStreamReader = new InputStreamReader(fis);
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line).append('\n');
                line = reader.readLine();
            }
        }
        return stringBuilder.toString();
    }
}
