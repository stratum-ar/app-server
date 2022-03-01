package com.stratum.reader;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manifest {

    String app;
    Map<String, String> info;
    Map<String, String> launch;

    public String getAppIndexFile() {
        String fileName = launch.get("command").split(" ")[1];
        return getWorkingDirectory() + fileName;
    }

    public String getWorkingDirectory() {
        String userHomeDir = System.getProperty("user.home");
        String appDir = String.format("%s/stratum/apps/%s/", userHomeDir, app);

        return appDir;
    }

    public String getFirstCommand() {
        return launch.get("command").split(" ")[0];
    }
}
