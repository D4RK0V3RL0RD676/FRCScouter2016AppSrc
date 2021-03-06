package org.ncfrcteams.frcscoutinghub2016.matchdata.bluealliance.jsonparse;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kyle Brown on 4/7/2016.
 */
public class JsonParser {

    public static JPObject getObject(JsonReader reader) {
        try {
            switch (reader.peek()) {
                case BEGIN_OBJECT:
                    JPMap map = new JPMap();

                    reader.beginObject();
                    while (reader.peek() != JsonToken.END_OBJECT) {
                        map.put(reader.nextName(),getObject(reader));
                    }
                    reader.endObject();

                    return new JPObject<>(map);

                case BEGIN_ARRAY:
                    JPList list = new JPList();

                    reader.beginArray();
                    while (reader.peek() != JsonToken.END_ARRAY) {
                        list.add(getObject(reader));
                    }
                    reader.endArray();

                    return new JPObject<>(list);

                case NUMBER:
                case STRING:
                    return new JPObject<>(reader.nextString());

                default:
                    reader.skipValue();
                    return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
