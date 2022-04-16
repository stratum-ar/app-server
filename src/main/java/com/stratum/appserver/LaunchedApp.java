package com.stratum.appserver;

import com.stratum.reader.Manifest;
import com.stratum.reader.ManifestReader;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;

@Getter
@Setter
public class LaunchedApp extends App{
    private Manifest manifest;


    public App loadApp(String appName) {
        App testApp = new App();

        String workingDir = manifest.getWorkingDirectory();

        ///home/achreko/stratum/apps/test-app/manifest.json"
        setManifest(new ManifestReader().
                parseJson(workingDir));
        manifest.setAppId(testApp.getId());

        return testApp;
    }

    public void  buildApp() throws IOException {
        ProcessBuilder builder = new ProcessBuilder(manifest.getCommand());

        builder.directory(new File(manifest.getWorkingDirectory()));
        builder.redirectErrorStream(true);
        builder.start();
    }

}
