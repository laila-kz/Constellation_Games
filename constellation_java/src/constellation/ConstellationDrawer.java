package constellation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * ConstellationDrawer is the main entry point for the Java Swing Constellation application.
 * It initializes the main window, attaches the drawing panel and action controls,
 * and handles user interactions.
 */
public class ConstellationDrawer {

    /**
     * Main application entry point.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Constellation Drawer");
            frame.setSize(1000, 800);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            // Load window icon if available
            try {
                frame.setIconImage(new ImageIcon("src/constellation/images/icon.png").getImage());
            } catch (Exception e) {
                System.out.println("Icon image not found, using system default icon.");
            }

            // Initialize canvas and control panel
            DrawingPanel drawingPanel = new DrawingPanel();
            JPanel controlPanel = createStyledControlPanel(drawingPanel);

            frame.add(controlPanel, BorderLayout.NORTH);
            frame.add(drawingPanel, BorderLayout.CENTER);

            frame.setLocationRelativeTo(null); // Center window on screen
            frame.setVisible(true);
        });
    }

    /**
     * Creates a styled control toolbar containing interactive buttons.
     *
     * @param drawingPanel the canvas instance affected by the actions
     * @return a configured JPanel container
     */
    private static JPanel createStyledControlPanel(DrawingPanel drawingPanel) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(30, 30, 60));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton randomBgButton = createStyledButton("Random Background", new Color(70, 130, 180));
        JButton clearButton = createStyledButton("Clear Stars", new Color(220, 80, 60));
        JButton changeLineButton = createStyledButton("Line Style", new Color(50, 150, 150));
        JButton exitButton = createStyledButton("Exit", new Color(100, 100, 100));

        controlPanel.add(randomBgButton);
        controlPanel.add(clearButton);
        controlPanel.add(changeLineButton);
        controlPanel.add(exitButton);

        randomBgButton.addActionListener(e -> drawingPanel.setRandomBackground());
        clearButton.addActionListener(e -> drawingPanel.clearStars());
        changeLineButton.addActionListener(e -> drawingPanel.cycleLineStyle());
        exitButton.addActionListener(e -> System.exit(0));

        return controlPanel;
    }

    /**
     * Helper method to construct interactive buttons with hover styling.
     *
     * @param text    label text for the button
     * @param bgColor default background color
     * @return a styled JButton
     */
    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(Color.WHITE, 1));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(180, 40));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}

