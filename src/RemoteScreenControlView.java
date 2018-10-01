import com.android.chimpchat.core.TouchPressType;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class RemoteScreenControlView extends JLabel {
    private Timer screenCapturer;
    private RemoteDevice device;

    public RemoteScreenControlView(RemoteDevice device) {
        super();
        this.device = device;

        bindEvent();
    }

    private void bindEvent() {
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.print("X : " + e.getX());
                System.out.print("Y : " + e.getY());
                device.touch(e.getX(), e.getY(), TouchPressType.DOWN);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                device.touch(e.getX(), e.getY(), TouchPressType.UP);
            }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                device.touch(e.getX(), e.getY(), TouchPressType.MOVE);
            }

            @Override
            public void mouseMoved(MouseEvent e) { }
        });
    }

    public void startScreenCapture() {
        if (null != screenCapturer) {
            if (screenCapturer.isRunning()) {
                return;
            }
            screenCapturer.start();
            return;
        }

        screenCapturer = new Timer(100, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RemoteScreenControlView.this.setIcon(new ImageIcon(device.captureScreen()));
            }
        });

        screenCapturer.start();
    }


    public void stopScreenCapture() {
        if(screenCapturer != null) {
            screenCapturer.stop();
        }
    }
}
