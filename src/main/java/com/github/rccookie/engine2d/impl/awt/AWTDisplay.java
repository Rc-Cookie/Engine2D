package com.github.rccookie.engine2d.impl.awt;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.image.Color;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.geometry.performance.int2;

/**
 * AWT implementation of {@link Display} using a double-buffered JFrame.
 */
public class AWTDisplay extends JPanel implements Display {

    /**
     * The display controller.
     */
    static DisplayController displayController;

    /**
     * Only one instance.
     */
    static AWTDisplay INSTANCE;


    /**
     * The window containing the JPanel.
     */
    final JFrame window;

    /**
     * Currently set resolution.
     */
    private int2 resolution;


    /**
     * Draw objects for the next rendering pass.
     */
    private DrawObject[] objects;
    /**
     * Background color for the next rendering pass.
     */
    private Color background;


    /**
     * Creates a new AWTDisplay with the given window title.
     *
     * @param title The window title
     */
    public AWTDisplay(String title) {
        window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().add(this);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Application.getDisplayController().setResolution(new int2(window.getWidth() - 16, window.getHeight() - 39));
            }
        });

        setResolution(Display.DEFAULT_RESOLUTION);

//        this.setDoubleBuffered(true);

        window.setVisible(true);
    }

    @Override
    public void draw(DrawObject[] objects, Color background) {
        synchronized (this) {
//            this.objects = Utils.deepClone(objects);
            this.objects = new DrawObject[objects.length];
            for(int i=0; i<objects.length; i++)
                this.objects[i] = objects[i].clone();
            this.background = background;
        }
        repaint();
    }

    @Override
    public void setResolution(int2 resolution) {
        this.resolution = resolution;
        // Does not seem to have an impact even after a call to window.pack()
        setMinimumSize(new Dimension(resolution.x, resolution.y));
        window.setSize(resolution.x + 16, resolution.y + 39);
    }

    @Override
    public void allowResizingChanged(boolean allowed) {
        Execute.nextFrame(() -> window.setResizable(allowed));
    }

    /**
     * Draws the last set draw objects onto the buffer and sets the buffer.
     *
     * @param g1d The Graphics2D to draw onto
     */
    @Override
    protected void paintComponent(Graphics g1d) {
        super.paintComponent(g1d);

        Graphics2D g = (Graphics2D) g1d;

        DrawObject[] objects;
        Color background;
        synchronized (this) {
            objects = this.objects;
            background = this.background;
        }

        if(objects == null) return;

        if(background.a != 255)
            g.clearRect(0, 0, resolution.x, resolution.y);
        g.setColor(background.getAwtColor());
        g.fillRect(0, 0, resolution.x, resolution.y);

        boolean wasTransparent = false;
        Composite plain = g.getComposite();

        for(DrawObject o : objects) {

            AffineTransform oldTransform = null;

            AWTImageImpl impl = (AWTImageImpl) o.image;

            int2 drawPos = o.screenLocation.subed(new int2(impl.image.getWidth() / 2, impl.image.getHeight() / 2));
            if(o.rotation != 0) {
                oldTransform = g.getTransform();
                g.rotate(Math.toRadians(o.rotation), o.screenLocation.x, o.screenLocation.y);
            }
            if(impl.transparency != 255) {
                wasTransparent = true;
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Num.clamp(impl.transparency / 255f, 0, 1)));
            }
            else if(wasTransparent) {
                g.setComposite(plain);
            }

            g.drawImage(impl.image, drawPos.x, drawPos.y, null);

            if(oldTransform != null) g.setTransform(oldTransform);
        }
        g.setComposite(plain);
    }

    /**
     * For class initialization.
     */
    static void init() { }
}
