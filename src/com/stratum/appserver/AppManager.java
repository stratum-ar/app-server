package com.stratum.appserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
    }

    public void handleRequest(Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());

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
        } catch (IOException e) {}
    }
}
