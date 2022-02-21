package com.stratum.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ManifestReader {

    ObjectMapper mapper = new ObjectMapper();

    public Manifest parseJson(String jsonPath) {
        try {
            return mapper.readValue(new File(jsonPath), Manifest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
