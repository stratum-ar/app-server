package com.stratum.appserver;

import com.stratum.reader.ManifestFinder;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class LauncherApp extends InternalApp {
    private final int optionsPerPage = 7;
    private int pageCount;
    private int pageIndex = 0;

    private final List<String> availableApps = ManifestFinder.getAllAppsNames();

    private final AppManager appManager;

    public LauncherApp(AppManager appManager) {
        this.appManager = appManager;
    }

    int convertToUnsigned(byte x) {
        if (x < 0) {
            return x + 255;
        }

        return x;
    }

    @Override
    public void sendInput(byte[] inputData) {
        int x = convertToUnsigned(inputData[0]);
        int y = convertToUnsigned(inputData[1]);

        int start = optionsPerPage * pageIndex;
        int finish = optionsPerPage * (pageIndex + 1);


        if(x >= 16 && y >= 16 && x < 224 && y < 198){
            int offset = (y - 16) / 26;
            if(start + offset < availableApps.size()) {
                appManager.launchApp(availableApps.get(start + offset));
            }
        }

    }

    @Override
    public byte[] getUIPayload() {
        int start = optionsPerPage * pageIndex;
        int finish = optionsPerPage * (pageIndex + 1);

        byte[] payload = new byte[] {0};

        for(int i = start; i < finish; i++) {
            if(i >= availableApps.size())
                break;
            int offset = i % optionsPerPage;
            byte[] buttonPayload = makeButton(availableApps.get(i), (byte) 16, (byte) (16 + 26 * offset),(byte) 208, (byte) 24 );

            payload = Utils.concatenateTwoArrays(payload, Utils.concatenateTwoArrays(
                    buttonPayload, new byte[] {1}
            ));
            payload[0] += 1;
        }

        payload[payload.length - 1] = 2;

        return payload;
    }

    private byte[] makeButton(String text, byte x, byte y, byte width, byte height) {
        byte[] uiPayload1 = new byte[] {33, x, y, width, height, 0, (byte) text.length()};
        byte[] uiPayload2 = text.getBytes(StandardCharsets.UTF_8);

        return Utils.concatenateTwoArrays(
                Utils.concatenateTwoArrays(uiPayload1, uiPayload2), new byte[] {0});
    }
}
