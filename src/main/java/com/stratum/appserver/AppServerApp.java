package com.stratum.appserver;

import com.stratum.reader.Manifest;
import com.stratum.reader.ManifestReader;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AppServerApp {
    private final AppManager appManager;

    public AppServerApp() {
        appManager = new AppManager();
    }

    public AppManager getAppManager() {
        return appManager;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(50665);

        while (true) {
            Socket clientSocket = serverSocket.accept();

            appManager.handleRequest(clientSocket);
        }
    }

    public static void main(String[] args) throws IOException {
        AppServerApp app = new AppServerApp();

        AppManager appManager = app.getAppManager();

        App testApp = new App();
        appManager.addApp(testApp);
        appManager.setActiveApp(testApp);

        // EXAMPLE APP
        Manifest manifestDTO =
                new ManifestReader()
                        .parseJson("/Users/hubertnakielski/stratum/apps/test-app/manifest.json");

        ProcessBuilder builder =
                new ProcessBuilder(
                        manifestDTO.getFirstCommand(),
                        manifestDTO.getAppIndexFile(),
                        testApp.getId());

        builder.directory(new File(manifestDTO.getWorkingDirectory()));
        builder.redirectErrorStream(true);
        builder.start();

        try {
            app.start();
        } catch (IOException e) {

        }
    }
}
