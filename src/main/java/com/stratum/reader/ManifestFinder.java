package com.stratum.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ManifestFinder {

    public static List<String> getAllAppsNames() {

        List<String> appsList = new ArrayList<>();
        File[] directories =
                new File(System.getProperty("user.home") + "/stratum/apps")
                        .listFiles(File::isDirectory);

        assert directories != null;
        for (File file : directories) {
            appsList.add(getAppNameFromDir(file));
        }
        return appsList;
    }

    private static String getAppNameFromDir(File directory) {
        if (directory.listFiles() == null)
            throw new IllegalArgumentException(
                    "Empty apps list in directory: " + directory.getPath());
        String manifestPath =
                Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                        .filter(path -> path.getName().endsWith("manifest.json"))
                        .findFirst()
                        .get()
                        .getPath();
        return new ManifestReader().parseJson(manifestPath).getApp();
    }
}
