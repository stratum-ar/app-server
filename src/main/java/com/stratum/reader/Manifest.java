package com.stratum.reader;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manifest {

    String app;
    Map<String, String> info;
    Map<String, String> launch;
    private List<String> command = new LinkedList<>();

    public String getWorkingDirectory() {
        String userHomeDir = System.getProperty("user.home");
        String appDir = String.format("%s/stratum/apps/%s/", userHomeDir, app);

        return appDir;
    }

    public void instantiateCommandList() {
        command.addAll(List.of(launch.get("command").split(" ")));
    }

    public void setAppId(String appId) {
        command.set(command.indexOf("$$APPID$$"), appId);
    }

    protected void runPostInstall() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(launch.get("postinstall")).inheritIO();
        processBuilder.directory(new File(getWorkingDirectory()));
        processBuilder.redirectErrorStream(true);
        processBuilder.start();
    }
}
