package constellation;

import java.awt.*;
import java.util.Random;
import java.util.UUID;

/**
 * Represents an individual celestial star in the constellation visualization.
 * Supports multiple visual rendering styles (Simple, Sparkle, Glowing, Twinkle).
 */
public class Star {
    private final int x;
    private final int y;
    private final int size;
    private final Color color;
    private final Color glowColor;
    private final String id;
    private final int starType; // 0=simple, 1=sparkle, 2=glowing, 3=twinkle
    private final float twinklePhase;

    /**
     * Constructs a Star at the specified screen coordinates with randomized attributes.
     *
     * @param x    the horizontal coordinate on the panel
     * @param y    the vertical coordinate on the panel
     * @param rand Random instance used to initialize star properties
     */
    public Star(int x, int y, Random rand) {
        this.x = x;
        this.y = y;
        this.size = 8 + rand.nextInt(12);
        this.color = generateStarColor(rand);
        this.glowColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        this.id = UUID.randomUUID().toString();
        this.starType = rand.nextInt(4);
        this.twinklePhase = rand.nextFloat() * (float) Math.PI * 2;
    }

    /**
     * Generates a random star color palette.
     *
     * @param rand Random number generator instance
     * @return a Color object representing the star's color
     */
    private Color generateStarColor(Random rand) {
        int colorType = rand.nextInt(5);
        switch (colorType) {
            case 0: return new Color(255, 255, 200); // Warm white
            case 1: return new Color(200, 220, 255); // Cool blue
            case 2: return new Color(255, 200, 150); // Orange
            case 3: return new Color(200, 255, 200); // Greenish
            case 4: return new Color(255, 180, 255); // Pinkish
            default: return Color.WHITE;
        }
    }

    /**
     * Renders the star on the graphics context based on its visual style type.
     *
     * @param g2 the Graphics2D context
     */
    public void draw(Graphics2D g2) {
        switch (starType) {
            case 0: drawSimpleStar(g2); break;
            case 1: drawSparkleStar(g2); break;
            case 2: drawGlowingStar(g2); break;
            case 3: drawTwinkleStar(g2); break;
        }
    }

    /**
     * Renders a simple glowing orb star.
     */
    private void drawSimpleStar(Graphics2D g2) {
        // Glow effect
        g2.setColor(glowColor);
        g2.fillOval(x - size / 2 - 2, y - size / 2 - 2, size + 4, size + 4);

        // Main star body
        g2.setColor(color);
        g2.fillOval(x - size / 2, y - size / 2, size, size);

        // Bright center highlight
        g2.setColor(Color.WHITE);
        g2.fillOval(x - size / 4, y - size / 4, size / 2, size / 2);
    }

    /**
     * Renders a star with starburst rays.
     */
    private void drawSparkleStar(Graphics2D g2) {
        // Central glow
        g2.setColor(glowColor);
        g2.fillOval(x - size / 2, y - size / 2, size, size);

        // Sparkle rays
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int rayLength = size;
            int endX = x + (int) (Math.cos(angle) * rayLength);
            int endY = y + (int) (Math.sin(angle) * rayLength);
            g2.drawLine(x, y, endX, endY);
        }

        // Bright center dot
        g2.setColor(Color.WHITE);
        g2.fillOval(x - 2, y - 2, 4, 4);
    }

    /**
     * Renders a star with multi-layered radial halo glow.
     */
    private void drawGlowingStar(Graphics2D g2) {
        // Multiple radial layers for soft glow
        for (int i = 3; i >= 0; i--) {
            int glowSize = size + i * 4;
            int alpha = 60 - i * 15;
            Color glow = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
            g2.setColor(glow);
            g2.fillOval(x - glowSize / 2, y - glowSize / 2, glowSize, glowSize);
        }

        // Main star body
        g2.setColor(color);
        g2.fillOval(x - size / 2, y - size / 2, size, size);
    }

    /**
     * Renders a dynamically pulsing / twinkling star.
     */
    private void drawTwinkleStar(Graphics2D g2) {
        double time = System.currentTimeMillis() * 0.005;
        float pulse = (float) Math.sin(time + twinklePhase) * 0.3f + 0.7f;
        int currentSize = (int) (size * pulse);

        g2.setColor(glowColor);
        g2.fillOval(x - currentSize / 2, y - currentSize / 2, currentSize, currentSize);

        g2.setColor(color);
        g2.fillOval(x - currentSize / 2 + 1, y - currentSize / 2 + 1, currentSize - 2, currentSize - 2);
    }

    // Getters

    public int getX() { return x; }

    public int getY() { return y; }

    public int getSize() { return size; }

    public Color getColor() { return color; }

    public String getId() { return id; }

    @Override
    public String toString() {
        return String.format("Star{id='%s', x=%d, y=%d, size=%d}", id, x, y, size);
    }
}
