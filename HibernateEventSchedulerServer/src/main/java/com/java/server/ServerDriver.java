package com.java.server;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ServerDriver {

    private JFrame frame;
    private JButton startButton;
    private JTextPane textPane;  

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ServerDriver window = new ServerDriver();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ServerDriver() {
        initialize();
    }

    private void initialize() {
        // Initialize frame
        frame = new JFrame("Server Control");
        frame.setBounds(100, 100, 1000, 700); //frame size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(20, 20)); //padding between components

        // Heading panel
        JPanel headingPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gradient background for the header
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, new Color(33, 150, 243), 0, getHeight(), new Color(0, 123, 255));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headingPanel.setPreferredSize(new Dimension(1000, 70)); 
        headingPanel.setLayout(new BorderLayout());
        JLabel headingLabel = new JLabel("Java Equipment Rental Server", JLabel.CENTER);
        headingLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); 
        headingLabel.setForeground(Color.WHITE);
        headingPanel.add(headingLabel, BorderLayout.CENTER);
        frame.getContentPane().add(headingPanel, BorderLayout.NORTH);

        // JTextPane
        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setFont(new Font("Segoe UI", Font.PLAIN, 16)); 
        textPane.setBackground(new Color(245, 245, 245));
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(960, 500)); 
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Panel to hold the button, centered with padding
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 40)); // Centered with more padding
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Create and add the Start Server button
        startButton = createModernButton("Start Server", "startServerButton");
        buttonPanel.add(startButton);

        // Button click event to start server
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
    }

    private void startServer() {
        // Redirect server console output to the text area
        PrintStream printStream = new PrintStream(new TextAreaOutputStream(textPane));  // Updated to JTextPane
        System.setOut(printStream);
        System.setErr(printStream);

        // Start the server
        System.out.println("Starting the server...");
        Server.startServer();
    }

    // Create a modern button with larger settings
    private JButton createModernButton(String text, String cardName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 18)); // Larger font size
        button.setBackground(new Color(33, 150, 243)); // Blue background
        button.setForeground(Color.WHITE); // White text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Gray border
        button.setPreferredSize(new Dimension(200, 60)); // Larger button size
        return button;
    }
    private static class TextAreaOutputStream extends OutputStream {
        private JTextPane textPane;
        private StringBuilder buffer;

        public TextAreaOutputStream(JTextPane textPane) {
            this.textPane = textPane;
            this.buffer = new StringBuilder();
        }

        @Override
        public void write(int b) throws IOException {
            // Accumulate characters into the buffer
            if (b == '\n') {
                // Process the complete line when a newline character is encountered
                processLine(buffer.toString());
                buffer.setLength(0); // Clear the buffer for the next line
            } else {
                // Add the character to the buffer
                buffer.append((char) b);
            }
        }

        private void processLine(String line) {
            // Determine the log level and assign a color based on that
            Color color = Color.BLACK;  // Default color (black)

            if (line.contains("INFO")) {
                color = Color.BLUE;
            } else if (line.contains("WARN")) {
                color = Color.BLACK;
            } else if (line.contains("ERROR")) {
                color = Color.RED;
            }

            // Append the line to the JTextPane with the appropriate color
            appendToTextPane(line + "\n", color);
        }

        private void appendToTextPane(String text, Color color) {
            StyledDocument doc = textPane.getStyledDocument();
            Style style = doc.addStyle("Style", null);
            StyleConstants.setForeground(style, color);
            
            try {
                doc.insertString(doc.getLength(), text, style);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
            
            textPane.setCaretPosition(textPane.getDocument().getLength());  // Auto-scroll
        }
    }
}