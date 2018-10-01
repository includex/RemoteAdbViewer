import com.android.chimpchat.ChimpChat;
import com.android.chimpchat.core.IChimpDevice;
import com.android.chimpchat.core.PhysicalButton;
import com.android.chimpchat.core.TouchPressType;
import com.android.chimpchat.hierarchyviewer.HierarchyViewer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class RemoteDevice {
    private IChimpDevice device;
    static volatile RemoteDevice instance;
    int width;
    int height;

    public static RemoteDevice getInstance() {
        if (instance == null) {
            RemoteDevice t = new RemoteDevice();
            boolean suc = t.init();

            if (suc == true) {
                instance = t;
            }
        }

        return instance;
    }

    private RemoteDevice() {
    }


    public int getWidth() {
        if (width <= 0) {
            width = Integer.parseInt(device.getProperty("display.width"));
        }

        return width;
    }

    public int getHeight() {
        if (height <= 0) {
            height = Integer.parseInt(device.getProperty("display.height"));
        }

        return height;
    }

    public boolean init() {
        ChimpChat chimpchat = ChimpChat.getInstance(makeOption());
        device = chimpchat.waitForConnection();
        device.wake();
        return true;
    }

    private Map<String, String> makeOption() {
        Map<String, String> options = new HashMap<String, String>();
        options.put("backend", "adb");
        options.put("adbLocation", "/Users/jeongdujin/adb/adb");

        return options;
    }

    public BufferedImage captureScreen() {
        return device.takeSnapshot().getBufferedImage();
    }

    public void dispose() {
        device.dispose();
    }

    public void wake() {
        device.wake();
    }

    public String uiDump(){
        return device.shell("/system/bin/uiautomator dump /sdcard/uidump.xml;cat /sdcard/uidump.xml");
    }

    public void pressKey(PhysicalButton btnType) {
        device.press(btnType, TouchPressType.DOWN_AND_UP);
    }

    public void touch(int x, int y, TouchPressType type) {
        device.touch(x, y, type);
    }
};
