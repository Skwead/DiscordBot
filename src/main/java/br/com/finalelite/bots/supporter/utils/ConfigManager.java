package br.com.finalelite.bots.supporter.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;

import java.io.File;
import java.io.IOException;

public class ConfigManager {

    public static void saveConfigToFile(Config config) {
        val file = new File("config.json");
        try {
            if (!file.exists())
                file.createNewFile();

            val writer = Files.newWriter(file, Charsets.UTF_8);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(config));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Config loadConfigFromFile() {
        val file = new File("config.json");
        try {
            return new Gson().fromJson(String.join("\n", Files.readLines(file, Charsets.UTF_8)), Config.class);
        } catch (IOException e) {
            System.out.println("Cannot load the config file.");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

}
