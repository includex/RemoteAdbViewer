import com.android.chimpchat.core.PhysicalButton;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class MainWindow {
    private JFrame mainFrame;

    RemoteScreenControlView remoteScreenView;
    RemoteDevice device;

    JButton btnHome;
    JButton btnBack;
    JButton btnMenu;
    JButton btnSearch;
    JButton btnWake;
    JButton btnDump;

    ArrayList<Rectangle> rects = new ArrayList<Rectangle>();

    ActionListener btnMouseListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == btnHome) {
                device.pressKey(PhysicalButton.HOME);
            } else if (e.getSource() == btnBack) {
                device.pressKey(PhysicalButton.BACK);
            } else if (e.getSource() == btnMenu) {
                device.pressKey(PhysicalButton.MENU);
            } else if (e.getSource() == btnSearch) {
                device.pressKey(PhysicalButton.SEARCH);
            } else if (e.getSource() == btnWake) {
                device.wake();
            } else if (e.getSource() == btnDump) {
                String xml = device.uiDump();
                int start = xml.indexOf("<");
                xml = xml.substring(start);
                try {
                    File f = File.createTempFile("uidump", ".xml");
                    if (f.exists()) {
                        f.delete();
                    }

                    f.createNewFile();

                    FileOutputStream fs = new FileOutputStream(f);
                    fs.write(xml.getBytes());
                    fs.close();

                    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f.getAbsoluteFile());
                    Element element = document.getDocumentElement();
                    recursive(element);

                } catch (Exception exception) {
                }
            }
        }
    };

    private void recursive(Element element) {
        if (element.getNodeName().equals("node")) {
            String rect = element.getAttribute("bounds");
            //DO
        }
    }

    public void show() {
        device = RemoteDevice.getInstance();

        if (device == null) {
            return;
        }

        int width = device.getWidth();
        int height = device.getHeight();

        mainFrame = new JFrame("Remote Adb Client");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(width + 10, height + 100);//magic-numbers are margins
        mainFrame.setLayout(new FlowLayout());

        remoteScreenView = new RemoteScreenControlView(device);
        remoteScreenView.setSize(width, height);

        btnWake = new JButton("Wake");
        btnHome = new JButton("Home");
        btnBack = new JButton("Back");
        btnMenu = new JButton("Menu");
        btnSearch = new JButton("Search");
        btnDump = new JButton("Select");

        btnHome.addActionListener(btnMouseListener);
        btnBack.addActionListener(btnMouseListener);
        btnMenu.addActionListener(btnMouseListener);
        btnSearch.addActionListener(btnMouseListener);
        btnWake.addActionListener(btnMouseListener);
        btnDump.addActionListener(btnMouseListener);

        mainFrame.add(btnWake);
        mainFrame.add(btnHome);
        mainFrame.add(btnBack);
        mainFrame.add(btnMenu);
        mainFrame.add(btnSearch);
        mainFrame.add(btnDump);
        mainFrame.add(remoteScreenView);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                remoteScreenView.stopScreenCapture();
                if (device != null) {
                    device.dispose();
                }
            }
        });

        mainFrame.setVisible(true);
        remoteScreenView.startScreenCapture();
    }

    static class Position {
        static Position parser(String data){
            String pos[] = data.split(",");
            int x = Integer.parseInt(pos[0].substring(1));
            int y = Integer.parseInt(pos[1].substring(0, pos[1].length() - 1));

            return new Position(x, y);
        }

        public int x;
        public int y;

        private Position(int x , int y){
            this.x = x;
            this.y = y;
        }
    }

    static class Rect {

        static Rect parser(String str) {
            int pos = str.indexOf("]") + 1;
            Position top = Position.parser(str.substring(0, pos));
            Position bottom = Position.parser(str.substring(pos + 1));

            return new Rect(top, bottom);
        }

        public Position top;
        public Position bottom;

        protected  Rect(Position top, Position bottom){
            this.top = top;
            this.bottom = bottom;
        }
    }
}
