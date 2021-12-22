package com.stratum.appserver;

import java.io.*;
import java.net.*;

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
        ProcessBuilder builder = new ProcessBuilder(
                "node", "P:\\STRATUM\\test-app\\test.js", testApp.getId()
        );
        builder.redirectErrorStream(true);
        builder.start();

        try {
            app.start();
        } catch (IOException e) {

        }
    }
}
