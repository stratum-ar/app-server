package com.stratum.appserver;

import com.stratum.reader.ManifestFinder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class InternalApp implements App {

    private final UUID id = UUID.randomUUID();

    private boolean handled = false;

    private List<String> availableApps = ManifestFinder.getAllAppsNames();

    private byte[] uiPayload = createPayload();

    private DataOutputStream outputStream;

    @Override
    public void log(String msg) {
        System.out.println(String.format("Internal app: %s", msg));
    }

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public boolean isHandled() {
        return handled;
    }

    @Override
    public byte[] getUIPayload() {
        return uiPayload;
    }

    @Override
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

    @Override
    public void handleClient(AppManager manager, Socket socket) throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        log(Arrays.toString(inputStream.readAllBytes()));
    }

    private byte[] createPayload() {
        byte[] uiPayload1 = new byte[] {1, 33, 16, 32, 120, 20, 0, 4};
        byte[] uiPayload2 = "abcd".getBytes(StandardCharsets.UTF_8);
        log("Available Apps: " + availableApps);

        return Utils.concatenateTwoArrays(
                Utils.concatenateTwoArrays(uiPayload1, uiPayload2), new byte[] {0, 2});
    }
}
