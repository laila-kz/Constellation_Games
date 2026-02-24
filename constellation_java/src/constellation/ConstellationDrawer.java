package constellation;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;


public class ConstellationDrawer {
    public  static void main(String[] args) {


        JFrame frame = new JFrame("Constellation Drawer");
        frame.setSize(1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DrawingPanel myDrawingPanel = new DrawingPanel();
        frame.setLayout(new BorderLayout());

        //try to get the icon
        try{
            frame.setIconImage(new ImageIcon("src/constellation/images/icon.png").getImage());
        } catch (Exception e) {
            System.out.println("Icon image not found, using default");
        }

        //frame.setIconImage(new ImageIcon("src/constellation/images/icon.png").getImage());

        //create drawing area
        DrawingPanel MyDrawingPanel = new DrawingPanel();
        frame.add(MyDrawingPanel, BorderLayout.SOUTH);

        JPanel controlPanel = createStyledControlPanel(myDrawingPanel);
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        frame.setLocationRelativeTo(null); //center of the screen
        frame.setVisible(true); // should be after adding the componenets
        myDrawingPanel.setVisible(true);

    }
    private static JPanel createStyledControlPanel(DrawingPanel drawingPanel) {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(30, 30, 60));
        controlPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        //upgraded buttons
        JButton randomBgButton = createStyledButton("🌌 Random Background", new Color(70, 130, 180));
        JButton clearButton = createStyledButton("🗑️ Clear Stars", new Color(220, 80, 60));
        JButton exitButton = createStyledButton("🚪 Exit", new Color(100, 100, 100));
        JButton changeLineButton = createStyledButton("🔗 Line Style", new Color(50, 150, 150));

        controlPanel.add(randomBgButton);
        controlPanel.add(clearButton);
        controlPanel.add(exitButton);
        controlPanel.add(changeLineButton);

        //actions
        // Actions - FIXED: use drawingPanel parameter
        randomBgButton.addActionListener(e -> drawingPanel.setRandomBackground());
        clearButton.addActionListener(e -> drawingPanel.clearStars());
        exitButton.addActionListener(e -> System.exit(0));
        changeLineButton.addActionListener(e -> drawingPanel.cycleLineStyle());
        return controlPanel;
    }
    private static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.white);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(Color.white,1));
        button.setFont(new Font("Arial",Font.BOLD,14));
        button.setPreferredSize(new Dimension(180,40));

        //hover effect on button
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;

    }

}

