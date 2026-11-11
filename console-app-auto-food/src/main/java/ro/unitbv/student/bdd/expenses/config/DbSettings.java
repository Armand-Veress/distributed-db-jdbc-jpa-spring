package ro.unitbv.student.bdd.expenses.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public record DbSettings(String url, String user, String password) {

    private static final String FILE = "db.properties";

    public static DbSettings load(String prefix) {
        Properties properties = readFile();
        String url = required(properties, prefix + ".url");
        String user = required(properties, prefix + ".user");
        String password = required(properties, prefix + ".password");

        return new DbSettings(url, user, password);
    }

    private static Properties readFile() {
        Properties properties = new Properties();

        try (InputStream in = DbSettings.class.getClassLoader().getResourceAsStream(FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        FILE + " not found – copy db.properties.example to db.properties and fill in your credentials");
            }
            properties.load(in);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + FILE, e);
        }

        return properties;
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing property '" + key + "' in " + FILE);
        }

        return value.strip();
    }
}
