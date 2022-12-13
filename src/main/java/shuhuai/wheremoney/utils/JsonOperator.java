package shuhuai.wheremoney.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;


public class JsonOperator {
    public static String readFile(String path) throws IOException {
        File file = new File(path);
        FileReader fileReader = new FileReader(file);
        Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        int ch;
        StringBuilder stringBuilder = new StringBuilder();
        while ((ch = reader.read()) != -1) {
            stringBuilder.append((char) ch);
        }
        fileReader.close();
        reader.close();
        return stringBuilder.toString();
    }

    public static JSONArray getMapFromJson(String name) {
        String json = null;
        try {
            json = readFile(System.getProperty("user.dir") + "\\src\\main\\resources\\json\\" + name + ".json");
        } catch (IOException error) {
            error.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(json);
        if (jsonObject != null) {
            Object objArray = jsonObject.get(name);
            return JSON.parseArray(objArray.toString());
        } else {
            return null;
        }
    }
}