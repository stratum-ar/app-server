package com.stratum.appserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class AppManager {
    private List<App> appList;
    private App activeApp;

    public AppManager() {
        appList = new ArrayList<>();
        activeApp = null;
    }

    public App getApp(String id) {
        for (App app : appList) {
            if (app.getId().equals(id)) {
                return app;
            }
        }

        return null;
    }

    public void addApp(App app) {
        appList.add(app);
    }

    public void setActiveApp(App activeApp) {
        this.activeApp = activeApp;
        updateUI();
    }

    public void launchApp(String appName) {
        LaunchedApp launchedApp = new LaunchedApp();
        App testApp = launchedApp.loadApp(appName);

        try {
            launchedApp.buildApp();
            addApp(testApp);
            setActiveApp(testApp);
        } catch (IOException ignored) {}
    }

    public void handleRequest(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

        int sourceFlag = inputStream.readUnsignedByte();

        if (sourceFlag == 0) { // App
            byte[] appIdBytes = new byte[36];
            inputStream.readFully(appIdBytes);

            App targetApp = getApp(new String(appIdBytes));

            if (targetApp == null) {
                socket.close();

                System.out.println("Target app not found, closing connection.");
                return;
            }

            if (targetApp.isHandled()) {
                socket.close();

                System.out.println("Target app already connected, closing connection.");
                return;
            }

            targetApp.log("App found, establishing connection.");
            targetApp.handleClient(this, socket);
        } else if (sourceFlag == 1) { // Input
            byte[] inputBytes = new byte[3];
            inputStream.readFully(inputBytes);

            if (activeApp != null) {
                activeApp.sendInput(inputBytes);
            }
        }
    }

    public void updateUI() {
        if (activeApp == null) {
            return;
        }

        byte[] payload = activeApp.getUIPayload();

        try {
            Socket socket = new Socket("localhost", 50666);
            socket.getOutputStream().write(payload);

            socket.close();
        } catch (IOException e) {
        }
    }
}
