package com.appoxee.testapp;

import com.appoxee.internal.geo.Region;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JSONParsingTest {

    private final String testJson = "[{\"duration\":-1,\"durationFrom\":1000,\"durationTo\":2000,\"id\":1,\"lat\":42.0,\"lng\":20.0,\"log\":{},\"name\":\"Region 1\",\"radius\":300},{\"duration\":-1,\"durationFrom\":1000,\"durationTo\":2000,\"id\":2,\"lat\":42.0,\"lng\":20.0,\"log\":{},\"name\":\"Region 2\",\"radius\":300},{\"duration\":-1,\"durationFrom\":1000,\"durationTo\":2000,\"id\":3,\"lat\":42.0,\"lng\":20.0,\"log\":{},\"name\":\"Region 3\",\"radius\":300}]";

    private Gson gson;

    @Before
    public void setup() {
        gson = new GsonBuilder().create();
    }

    @Test
    public void parse_array_to_json_test() {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region(1, 42, 20, 300, "Region 1", 1000, 2000));
        regions.add(new Region(2, 42, 20, 300, "Region 2", 1000, 2000));
        regions.add(new Region(3, 42, 20, 300, "Region 3", 1000, 2000));

        String json = gson.toJson(regions);

        assert json != null && !json.isEmpty() && Objects.equals(testJson, json);
    }

    @Test
    public void parse_json_string_to_json_array_test() {

        Type jsonType = new TypeToken<ArrayList<Region>>(){}.getType();
        ArrayList<Region> regions = gson.fromJson(testJson, jsonType);

        assert regions != null && regions.size() == 3;
    }
}
