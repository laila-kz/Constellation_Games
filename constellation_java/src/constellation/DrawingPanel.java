package constellation;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;


public class DrawingPanel extends JPanel {

    private ArrayList<Star> stars;
    private Color backgroundColor;
    private Random rand;
    private BufferedImage backgroundImage;
    private int lineStyle; // 0=solid, 1=dashed, 2=gradient, 3=glowing
    private String[] backgroundImages={
            "src/constellation/images/space1.jpg",
            "src/constellation/images/space2.jpg",
            "src/constellation/images/nebula1.jpg",
            "src/constellation/images/nebula2.jpg"

    };


    public DrawingPanel() {
        stars = new ArrayList<>();
        rand = new Random();
        backgroundColor = new Color(10, 10, 40);
        lineStyle = 0;

        //initial background
        loadRandomBackgroundImage();


        //muse click adds stars
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addStar(e.getPoint());
            }
        });

        setPreferredSize(new Dimension(1000,700));

    }


    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);


        Graphics2D g2 = (Graphics2D) g;
        if (backgroundImage != null) {
            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2.setColor(backgroundColor);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        drawConstellationLines(g2);
        drawStars(g2);
    }

    //draw small white circle as stars
    private void drawStars(Graphics2D g2){
        for(Star s : stars){
            s.draw(g2);
        }
    }

    //draw lines between stars
    private void drawConstellationLines(Graphics2D g2){
        if(stars.size() <2) return;

        for(int i=0;i<stars.size() -1;i++){
            Star star1 = stars.get(i);
            Star star2 = stars.get(i+1);
            switch (lineStyle) {
                case 0: // Solid
                    drawSolidLine(g2, star1, star2);
                    break;
                case 1: // Dashed
                    drawDashedLine(g2, star1, star2);
                    break;
                case 2: // Gradient
                    drawGradientLine(g2, star1, star2);
                    break;
                case 3: // Glowing
                    drawGlowingLine(g2, star1, star2);
                    break;
            }

        }
    }

    private void drawSolidLine(Graphics2D g2, Star star1, Star star2){
        g2.setColor(new Color(100,200,255,200));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());
    }

    private void drawDashedLine(Graphics2D g2, Star star1, Star star2){
        float[] dashPattern = {10, 5, 5, 5};
        g2.setColor(new Color(255, 255, 100, 180));
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND, 1.0f, dashPattern, 0));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());
    }

    private void drawGradientLine(Graphics2D g2, Star star1, Star star2){
        GradientPaint gradient = new GradientPaint(
                star1.getX(), star1.getY(), star1.getColor(),
                star2.getX(), star2.getY(), star2.getColor()
        );
        g2.setPaint(gradient);
        g2.setStroke(new BasicStroke(3));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());
    }

    private void drawGlowingLine(Graphics2D g2, Star star1, Star star2){
        // Draw multiple lines for glow effect
        g2.setColor(new Color(100, 200, 255, 50));
        g2.setStroke(new BasicStroke(8));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());

        g2.setColor(new Color(150, 220, 255, 150));
        g2.setStroke(new BasicStroke(4));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());

        g2.setColor(new Color(200, 240, 255, 200));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(star1.getX(), star1.getY(), star2.getX(), star2.getY());
    }

    // addStar
    public void addStar(Point p) {
        stars.add(new Star(p.x, p.y, rand));
        repaint();
    }

    //clear stars
    public void clearStars(){
        stars.clear();
        repaint();
    }
    public void cycleLineStyle(){
        lineStyle = (lineStyle + 1) % 4;
        repaint();
    }
    public void setRandomBackground() {
        loadRandomBackgroundImage();
        repaint();
    }
    public void loadRandomBackgroundImage(){
        try{
            String randomImagePath  = backgroundImages[rand.nextInt(backgroundImages.length)];
            File imageFile = new File(randomImagePath);
            if(imageFile.exists()){
                backgroundImage = ImageIO.read(imageFile);
            }else{
                backgroundImage = null;
                backgroundColor = createSpaceGradient();
            }
        }catch(Exception e){
            System.out.println("Background image not found, using gradient instead");
            backgroundImage = null;
            backgroundColor = createSpaceGradient();

        }
    }
    private Color createSpaceGradient() {
        return new Color(
                10 + rand.nextInt(20),
                10 + rand.nextInt(20),
                40 + rand.nextInt(30)
        );
    }

}
