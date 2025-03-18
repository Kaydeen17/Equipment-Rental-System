package com.applictation.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Userscreen extends JFrame {

    private static final long serialVersionUID = 1L;
    private static JButton btn;
    private static TrayIcon icon;
    private static JFrame frame;

    public Userscreen() {
        btn = new JButton();
    }

    public static void main(String[] args) {
        initializeFrame();
        initializeTabs();
        initializeTrayIcon();
    }

    private static void initializeFrame() {
        frame = new JFrame("Tabbed GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void initializeTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("UserDash Board", createUserDash());
        tabbedPane.addTab("Tab 2", createTab2());
        frame.add(tabbedPane, BorderLayout.CENTER);
    }

    private static JPanel createUserDash() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        JLabel label = new JLabel("Welcome to Tab 1");
        label.setBounds(50, 50, 200, 30); // Set bounds for label
        panel.add(label);

        btn = new JButton("Open UserDashboard");
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserDashboard().setVisible(true);
            }
        });
        btn.setBounds(100, 200, 200, 300); // Adjusted the size of the button
        panel.add(btn);

        return panel;
    }

    private static JPanel createTab2() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel label = new JLabel("Welcome to Tab 2");
        JTextField textField = new JTextField("Type something here...");
        panel.add(label, BorderLayout.NORTH);
        panel.add(textField, BorderLayout.SOUTH);
        return panel;
    }

    private static void initializeTrayIcon() {
        if (SystemTray.isSupported()) {
            SystemTray systemTray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().getImage(Userscreen.class.getResource("/app1.png"));

            if (image != null) {
                icon = new TrayIcon(image, "Event Scheduler");
                icon.setImageAutoSize(true);

                PopupMenu trayPopupMenu = new PopupMenu();
                MenuItem show = new MenuItem("Show");
                show.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.setVisible(true);
                    }
                });

                MenuItem exit = new MenuItem("Exit");
                exit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });

                trayPopupMenu.add(show);
                trayPopupMenu.add(exit);
                icon.setPopupMenu(trayPopupMenu);

                try {
                    systemTray.add(icon);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Image not found");
            }
        } else {
            System.out.println("System tray not supported");
        }
    }
}
