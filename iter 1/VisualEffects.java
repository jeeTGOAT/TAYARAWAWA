import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;

public class VisualEffects {
    public static void drawBloom(Graphics2D g2d, Shape shape, Color color, int intensity) {
        for (int i = intensity; i > 0; i--) {
            float alpha = (float)i / intensity * 0.3f;
            g2d.setColor(new Color(
                color.getRed()/255f,
                color.getGreen()/255f,
                color.getBlue()/255f,
                alpha
            ));
            g2d.setStroke(new BasicStroke(i * 2));
            g2d.draw(shape);
        }
    }
    
    public static void drawGlowingText(Graphics2D g2d, String text, int x, int y, Color color, int glowIntensity) {
        // Draw glow
        g2d.setColor(new Color(
            color.getRed()/255f,
            color.getGreen()/255f,
            color.getBlue()/255f,
            0.2f
        ));
        
        for (int i = glowIntensity; i > 0; i--) {
            g2d.drawString(text, x - i, y);
            g2d.drawString(text, x + i, y);
            g2d.drawString(text, x, y - i);
            g2d.drawString(text, x, y + i);
        }
        
        // Draw main text
        g2d.setColor(color);
        g2d.drawString(text, x, y);
    }
    
    public static void drawModernPanel(Graphics2D g2d, int x, int y, int width, int height, Color baseColor) {
        // Draw panel background with gradient
        GradientPaint gradient = new GradientPaint(
            x, y, new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 200),
            x, y + height, new Color(baseColor.getRed()/2, baseColor.getGreen()/2, baseColor.getBlue()/2, 200)
        );
        
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, width, height, 15, 15);
        
        // Add highlight edge
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawLine(x + 2, y + 2, x + width - 2, y + 2);
        
        // Add shadow edge
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.drawLine(x + 2, y + height - 2, x + width - 2, y + height - 2);
    }
    
    public static void drawRadar(Graphics2D g2d, int centerX, int centerY, int radius, double angle) {
        // Draw radar background
        g2d.setColor(new Color(0, 50, 0, 100));
        g2d.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        
        // Draw radar rings
        g2d.setColor(new Color(0, 255, 0, 30));
        for (int i = 1; i <= 4; i++) {
            int ringRadius = (radius * i) / 4;
            g2d.drawOval(centerX - ringRadius, centerY - ringRadius, ringRadius * 2, ringRadius * 2);
        }
        
        // Draw radar sweep
        float[] dist = {0.0f, 1.0f};
        Color[] colors = {
            new Color(0, 255, 0, 180),
            new Color(0, 255, 0, 0)
        };
        
        double startAngle = angle - Math.PI/8;
        double endAngle = angle + Math.PI/8;
        
        for (int i = 0; i < 8; i++) {
            float alpha = (float)(8-i)/8f;
            Color sweepColor = new Color(0, 255, 0, (int)(alpha * 50));
            
            g2d.setColor(sweepColor);
            g2d.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                       (int)Math.toDegrees(startAngle - i*Math.PI/32),
                       (int)Math.toDegrees(endAngle - startAngle));
        }
    }
    
    public static void drawModernButton(Graphics2D g2d, int x, int y, int width, int height, 
                                      Color color, boolean isHovered, boolean isPressed) {
        // Create button shape
        RoundRectangle2D buttonShape = new RoundRectangle2D.Float(x, y, width, height, 10, 10);
        
        // Draw button background
        Color baseColor = isPressed ? color.darker() : isHovered ? color.brighter() : color;
        GradientPaint gradient = new GradientPaint(
            x, y, baseColor,
            x, y + height, baseColor.darker()
        );
        
        g2d.setPaint(gradient);
        g2d.fill(buttonShape);
        
        // Add glossy effect
        if (!isPressed) {
            GradientPaint gloss = new GradientPaint(
                x, y, new Color(1f, 1f, 1f, 0.2f),
                x, y + height/2, new Color(1f, 1f, 1f, 0f)
            );
            g2d.setPaint(gloss);
            g2d.fill(buttonShape);
        }
        
        // Add subtle border
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.setStroke(new BasicStroke(1f));
        g2d.draw(buttonShape);
    }
    
    public static BufferedImage createShadow(Shape shape, int shadowSize, float shadowOpacity) {
        // Create a buffered image to draw the shadow
        Rectangle bounds = shape.getBounds();
        BufferedImage shadow = new BufferedImage(
            bounds.width + shadowSize * 2,
            bounds.height + shadowSize * 2,
            BufferedImage.TYPE_INT_ARGB
        );
        
        Graphics2D g2d = shadow.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Translate to account for shadow size
        g2d.translate(shadowSize, shadowSize);
        
        // Fill with black (this will be our shadow)
        g2d.setColor(new Color(0f, 0f, 0f, shadowOpacity));
        g2d.fill(shape);
        
        g2d.dispose();
        
        // Blur the shadow
        return new BoxBlurFilter(shadowSize, shadowSize, 3).filter(shadow);
    }
    
    private static class BoxBlurFilter {
        private int hRadius;
        private int vRadius;
        private int iterations;
        
        public BoxBlurFilter(int hRadius, int vRadius, int iterations) {
            this.hRadius = hRadius;
            this.vRadius = vRadius;
            this.iterations = iterations;
        }
        
        public BufferedImage filter(BufferedImage src) {
            int width = src.getWidth();
            int height = src.getHeight();
            
            BufferedImage dst = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            
            int[] inPixels = new int[width * height];
            int[] outPixels = new int[width * height];
            src.getRGB(0, 0, width, height, inPixels, 0, width);
            
            for (int i = 0; i < iterations; i++) {
                blur(inPixels, outPixels, width, height, hRadius);
                blur(outPixels, inPixels, height, width, vRadius);
            }
            
            dst.setRGB(0, 0, width, height, inPixels, 0, width);
            return dst;
        }
        
        private void blur(int[] in, int[] out, int width, int height, int radius) {
            int widthMinus1 = width - 1;
            int tableSize = 2 * radius + 1;
            int[] divide = new int[256 * tableSize];
            
            // Create the lookup table
            for (int i = 0; i < 256 * tableSize; i++)
                divide[i] = i / tableSize;
            
            int inIndex = 0;
            
            for (int y = 0; y < height; y++) {
                int outIndex = y;
                int ta = 0, tr = 0, tg = 0, tb = 0;
                
                for (int i = -radius; i <= radius; i++) {
                    int rgb = in[inIndex + clamp(i, 0, width-1)];
                    ta += (rgb >> 24) & 0xff;
                    tr += (rgb >> 16) & 0xff;
                    tg += (rgb >> 8) & 0xff;
                    tb += rgb & 0xff;
                }
                
                for (int x = 0; x < width; x++) {
                    out[outIndex] = (divide[ta] << 24) |
                                  (divide[tr] << 16) |
                                  (divide[tg] << 8) |
                                  divide[tb];
                    
                    int i1 = x + radius + 1;
                    if (i1 > widthMinus1)
                        i1 = widthMinus1;
                    int i2 = x - radius;
                    if (i2 < 0)
                        i2 = 0;
                    int rgb1 = in[inIndex + i1];
                    int rgb2 = in[inIndex + i2];
                    
                    ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                    tr += ((rgb1 >> 16) & 0xff) - ((rgb2 >> 16) & 0xff);
                    tg += ((rgb1 >> 8) & 0xff) - ((rgb2 >> 8) & 0xff);
                    tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                    outIndex += height;
                }
                inIndex += width;
            }
        }
        
        private int clamp(int x, int a, int b) {
            return (x < a) ? a : (x > b) ? b : x;
        }
    }
} 