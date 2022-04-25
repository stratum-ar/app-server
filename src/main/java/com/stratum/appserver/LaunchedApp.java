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
    private Manifest manifest;

    public App loadApp(String appName) {
        App testApp = new ExternalApp();

        String workingDir =
                System.getProperty("user.home") + "/stratum/apps/" + appName + "/manifest.json";

        setManifest(new ManifestReader().parseJson(workingDir));
        manifest.setAppId(testApp.getId());

        return testApp;
    }

    public void buildApp() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(manifest.getCommand());

        builder.directory(new File(manifest.getWorkingDirectory()));
        builder.redirectErrorStream(true);
        builder.start();
    }
}
