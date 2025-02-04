package fr.ubx.poo.ugarden.launcher;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.Character.isDigit;

public class MapLevelLoader {
    public MapLevel[] loadFromFile(File file) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        }

        boolean compression = Boolean.parseBoolean(properties.getProperty("compression"));

        int levels = Integer.parseInt(properties.getProperty("levels"));
        MapLevel[] mapLevels = new MapLevel[levels];

        for (int i = 0; i < levels; i++) {
            String levelKey = "level" + (i + 1);
            String levelData = properties.getProperty(levelKey);
            mapLevels[i] = loadLevel(levelData, compression);
        }
        return mapLevels;
    }

    private MapLevel loadLevel(String levelData, boolean compression) {
        String[] levels;
        if (compression) {
            levelData = decompress(levelData);
            levels = levelData.split("x");
        } else {
            levels = levelData.split("x");
        }
        int width = levels[0].length();
        int height = levels.length;
        MapLevel mapLevel = new MapLevel(width, height);

        for (int y = 0; y < height; y++) {
            String rowData = levels[y];
            System.out.println("Uncompressed:");
            for (int x = 0; x < rowData.length(); x++) {
                char code = rowData.charAt(x);
                MapEntity mapEntity = MapEntity.fromCode(code);
                if (mapEntity != null) {
                    mapLevel.set(x, y, mapEntity);
                } else {
                    throw new MapException("Invalid character " + code);
                }
            }
        }
        return mapLevel;
    }

/*
    private MapLevel loadLevel(String levelData, boolean compression) {
        String[] rows;
        if (compression) {
            levelData = decompress(levelData);
        }
        rows = levelData.split(";");
        int height = rows.length;
        int width = rows[0].length();
        MapLevel mapLevel = new MapLevel(width, height);

        for (int y = 0; y < height; y++) {
            String rowData = rows[y];
            for (int x = 0; x < width; x++) {
                char code = rowData.charAt(x);
                MapEntity mapEntity = MapEntity.fromCode(code);
                if (mapEntity != null) {
                    mapLevel.set(x, y, mapEntity);
                } else {
                    throw new MapException("Invalid character " + code);
                }
            }
        }
        return mapLevel;
    }
*/

    // FOR LOREN
    private String decompress(String compressedData) {
        StringBuilder decompressedString = new StringBuilder();
        for(int i = 0; i < compressedData.length() ; i++){
            char c = compressedData.charAt(i);
            if(isDigit(c)){
                char precedent = compressedData.charAt(i-1);
                int k = Character.getNumericValue(c);
                decompressedString.append(Character.toString(precedent).repeat(k-1));
            }else {
                decompressedString.append(c);
            }
        }
        //System.out.println(decompressedString.toString());
        return /*super.decompress(*/ decompressedString.toString();
    }
}
