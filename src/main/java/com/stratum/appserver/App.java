package com.stratum.appserver;

import java.io.IOException;
import java.net.Socket;

public interface App {

    void log(String msg);

    String getId();

    boolean isHandled();

    byte[] getUIPayload();

    void sendInput(byte[] inputData);

    void handleClient(AppManager manager, Socket socket) throws IOException;

    void close();
}
