package br.com.finalelite.discord.bot.manager;

import br.com.finalelite.discord.bot.entity.Config;
import br.com.finalelite.discord.bot.utils.SimpleLogger;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigManager {

    @Getter
    private Config config;

    public ConfigManager() {
        safeLoadConfig();
    }

    public void safeLoadConfig() {
        saveDefaultConfigIfNotExists();
        loadConfig();
        if (!isConfigValid()) {
            SimpleLogger.log("Cannot find `%s` in the config.",
                    getInvalidValues().stream().map(Field::getName).collect(Collectors.joining(", ")));
            SimpleLogger.log("Please, fix the config before run the bot.");
            System.exit(0);
        }
    }

    public void loadConfig() {
        config = loadConfigFromFile();
    }

    public boolean isConfigValid() {
        val nullValues = getInvalidValues();
        return nullValues.isEmpty();
    }

    public List<Field> getInvalidValues() {
        val fields = config.getClass().getDeclaredFields();
        return Arrays.stream(fields).filter(field -> {
            try {
                field.setAccessible(true);
                return field.get(config) == null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }).collect(Collectors.toList());
    }

    public void saveDefaultConfigIfNotExists() {
        val file = new File("config.json");
        if (!file.exists()) {
            val defaultConfig = Config.builder().build();
            saveConfigToFile(defaultConfig);
            SimpleLogger.log("Default config file created, please, configure and run the bot again.");
            System.exit(0);
        }
    }

    private void saveConfigToFile(Config config) {
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

    private Config loadConfigFromFile() {
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

    public void reloadConfig(Config config) {
        saveConfigToFile(config);
        loadConfig();
    }
}
