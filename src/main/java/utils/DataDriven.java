package utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DataDriven {
    private static final String JSON_FILE_PATH = "src/testData/testData.json";
    public static JSONObject jsonReader(String key) {
        try (InputStream inputStream = new FileInputStream(JSON_FILE_PATH)) {
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject fullData = new JSONObject(tokener);
            return fullData.getJSONObject(key);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("testData.json not found at path: " + JSON_FILE_PATH, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read key '" + key + "' from testData.json", e);
        }
    }

    /**
     * Reads a JSON array of strings stored under the given key.
     * Used for data-driven lists, e.g. jsonReaderList("cartProducts")
     * -> ["Sauce Labs Backpack", "Sauce Labs Bolt T-Shirt", "Sauce Labs Onesie"]
     *
     * @param key the top-level key in testData.json that holds a JSON array
     * @return List of strings from that array, in the order they appear in the file
     */
    public static List<String> jsonReaderList(String key) {
        try (InputStream inputStream = new FileInputStream(JSON_FILE_PATH)) {
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject fullData = new JSONObject(tokener);
            JSONArray array = fullData.getJSONArray(key);
            List<String> result = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                result.add(array.getString(i));
            }
            return result;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("testData.json not found at path: " + JSON_FILE_PATH, e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read array '" + key + "' from testData.json", e);
        }
    }
}
