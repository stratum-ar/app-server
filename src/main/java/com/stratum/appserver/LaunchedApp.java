package com.stratum.appserver;

import com.stratum.reader.Manifest;
import com.stratum.reader.ManifestReader;
import java.io.File;
import java.io.IOException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LaunchedApp extends ExternalApp {
    private Process process;
    private Manifest manifest;

    public void setProcess(Process process) {
        this.process = process;
    }

    public static App loadApp(String appName) throws IOException {
        LaunchedApp launchedApp = new LaunchedApp();

        String workingDir =
                System.getProperty("user.home") + "/stratum/apps/" + appName + "/manifest.json";

        Manifest manifest = new ManifestReader().parseJson(workingDir);
        launchedApp.setManifest(manifest);
        manifest.setAppId(launchedApp.getId());

        ProcessBuilder builder = new ProcessBuilder(manifest.getCommand());

        builder.directory(new File(manifest.getWorkingDirectory()));
        builder.redirectErrorStream(true);

        launchedApp.setProcess(builder.start());

        return launchedApp;
    }

    @Override
    public void close() {
        process.destroy();
    }
}
