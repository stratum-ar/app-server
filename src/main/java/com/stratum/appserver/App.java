package com.stratum.appserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class App {
    private final UUID id = UUID.randomUUID();

    private boolean handled = false;

    private byte[] uiPayload = new byte[] {0, 2};

    private DataOutputStream outputStream;

    public void log(String msg) {
        System.out.println(String.format("%s: %s", id.toString(), msg));
    }

    public String getId() {
        return id.toString();
    }

    public boolean isHandled() {
        return handled;
    }

    public byte[] getUIPayload() {
        return uiPayload;
    }

    public void sendInput(byte[] inputData) {
        if (outputStream == null) {
            return;
        }

        try {
            byte[] input = new byte[] {0x0F, inputData[0], inputData[1], inputData[2]};
            outputStream.write(input);
        } catch (IOException ignored) {
        }
    }

    public void handleClient(AppManager manager, Socket socket) throws IOException {
        Runnable clientHandler =
                () -> {
                    try {
                        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                        DataOutputStream outputStream =
                                new DataOutputStream(socket.getOutputStream());

                        this.outputStream = outputStream;

                        // Send 0x01 to signal that things can happen now
                        outputStream.writeByte(0x01);

                        // Command loop
                        while (true) {
                            // Retrieve command ID if available
                            int commandId = inputStream.readUnsignedByte();

                            switch (commandId) {
                                    // 00h - close it up
                                case 0x00:
                                    socket.close();

                                    log("App closed connection.");
                                    return;
                                    // 10h - set UI
                                case 0x10:
                                    int payloadSize = inputStream.readUnsignedShort();

                                    byte[] payload = new byte[payloadSize];
                                    inputStream.readFully(payload);

                                    uiPayload = payload;
                                    manager.updateUI();

                                    log("App requests UI change, payload size: " + payloadSize);
                                    break;
                                default:
                                    socket.close();

                                    log("App sent incorrect command, closing.");
                                    return;
                            }
                        }
                    } catch (IOException e) {
                    }
                };
        Thread thread = new Thread(clientHandler);
        thread.start();
        handled = true;
    }
}
