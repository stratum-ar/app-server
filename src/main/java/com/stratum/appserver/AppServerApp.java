package com.stratum.appserver;

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
        LauncherApp internalApp = new LauncherApp(appManager);

        appManager.addApp(internalApp);
        appManager.setLauncherApp(internalApp);

        appManager.setActiveApp(internalApp);

        //        launchedApp.buildApp();
        try {
            app.start();
        } catch (IOException e) {

        }
    }
}
