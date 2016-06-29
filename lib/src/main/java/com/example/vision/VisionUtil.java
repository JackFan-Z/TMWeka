package com.example.vision;

import com.example.util.MyUtils;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jackf on 2016/6/29.
 */
public class VisionUtil {

    public static AnnotateImageResponse readResponseJson(String path) {
        String responseJson = MyUtils.ReadFile(path);
        if (responseJson.length() == 0) {
            return null;
        }

        GsonFactory gsonFactory = GsonFactory.getDefaultInstance();
        JsonParser jsonParser = gsonFactory.createJsonParser(responseJson);
        AnnotateImageResponse response;
        try {
            response = jsonParser.parse(AnnotateImageResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return response;
    }

    public static String GetPrintOutString(AnnotateImageResponse annotateImageResponse) {
        StringBuilder builder = new StringBuilder();
        List<EntityAnnotation> labels = annotateImageResponse.getTextAnnotations();
        if (labels != null) {
            for (int i = 0; i < labels.size(); i++ ) {
                EntityAnnotation label = labels.get(i);
                if (i == 0) {
                    builder.append("Locale: ");
                    builder.append(label.getLocale());
                    builder.append("\n");
                }
                builder.append(label.getDescription());
                builder.append("\n");

                break;
            }
        } else {
            builder.append("");
        }
        return builder.toString();
    }
}
