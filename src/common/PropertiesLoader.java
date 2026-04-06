package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesLoader {

    private PropertiesLoader() {
    }

    public static Properties load(String path) {
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(path)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties from: " + path, e);
        }
    }

    public static String getRequired(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required property: " + key);
        }
        return value.trim();
    }

    public static int getRequiredInt(Properties properties, String key) {
        return Integer.parseInt(getRequired(properties, key));
    }

    public static boolean getBoolean(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value.trim());
    }

    public static int getInt(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Integer.parseInt(value.trim());
    }

    public static long getLong(Properties properties, String key, long defaultValue) {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Long.parseLong(value.trim());
    }
}