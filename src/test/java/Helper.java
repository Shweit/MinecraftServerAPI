import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nonapi.io.github.classgraph.json.JSONUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Helper {
    public static Map<String, Object> loadJsonAsMap(String jsonFilePath) {
        InputStream inputStream = JSONUtils.class.getClassLoader().getResourceAsStream(jsonFilePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + jsonFilePath);
        }

        InputStreamReader reader = new InputStreamReader(inputStream);
        JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

        return jsonObjectToMap(jsonObject);
    }

    private static Map<String, Object> jsonObjectToMap(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                map.put(entry.getKey(), jsonObjectToMap(value.getAsJsonObject()));
            } else if (value.isJsonArray()) {
                map.put(entry.getKey(), value.getAsJsonArray());
            } else {
                map.put(entry.getKey(), value.getAsString());
            }
        }
        return map;
    }
}
