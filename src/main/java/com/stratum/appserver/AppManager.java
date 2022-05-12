package com.stratum.appserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppManager {
    private List<App> appList;
    private App activeApp;

    private LauncherApp launcherApp;

    public AppManager() {
        appList = new ArrayList<>();
    }

    public void setLauncherApp(LauncherApp launcherApp) {
        this.launcherApp = launcherApp;
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
        try {
            App app = LaunchedApp.loadApp(appName);
            addApp(app);
            setActiveApp(app);
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
                sendInput(inputBytes);
            }
        }
    }

    private void sendInput(byte[] inputBytes) {
        int x = inputBytes[0];
        int y = inputBytes[1];

        if (activeApp != launcherApp) {
            if (x >= 16 && y >= 16 && x < 40 && y < 36) {
                activeApp.close();
                appList.remove(activeApp);

                setActiveApp(launcherApp);
            } else {
                activeApp.sendInput(inputBytes);
            }
        } else {
            activeApp.sendInput(inputBytes);
        }
    }

    public void updateUI() {
        if (activeApp == null) {
            return;
        }

        byte[] payload = activeApp.getUIPayload();

        if (payload[0] == 0) {
            payload = new byte[] {0};
        }

        if (activeApp != launcherApp) {
            payload[payload.length-1] = 1;
            payload[0] += 1;

            byte[] uiPayload1 = new byte[] {33, 16, 16, 24, 20, 0, 0};

            payload = Utils.concatenateTwoArrays(payload, Utils.concatenateTwoArrays(
                    uiPayload1, new byte[] {1, 9, 2}));
            // inject
        }

        try {
            Socket socket = new Socket("localhost", 50666);
            socket.getOutputStream().write(payload);

            socket.close();
        } catch (IOException e) {
        }
    }
}
