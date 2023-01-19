package ru.practicum.yandex.kanban.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;

public class GsonInstantAdapter extends TypeAdapter<Instant> {
    @Override
    public void write(JsonWriter jsonWriter, Instant instant) throws IOException {
        if (instant == null) {
            jsonWriter.value(Instant.ofEpochMilli(0).toEpochMilli());
        } else {
            jsonWriter.value(instant.toEpochMilli());
        }
    }

    @Override
    public Instant read(JsonReader jsonReader) throws IOException {
        return Instant.ofEpochMilli(Long.parseLong(jsonReader.nextString()));
    }
}
