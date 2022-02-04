package com.github.rccookie.engine2d.impl.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.engine2d.impl.DisplayController;
import com.github.rccookie.geometry.performance.IVec2;

public class AWTDisplay extends JPanel implements Display {

    static DisplayController displayController;
    static AWTDisplay INSTANCE;

    final JFrame window;

    private IVec2 resolution;

    private DrawObject[] objects;
    private Color background;


    public AWTDisplay(String title) {
        window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().add(this);
        window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Application.getDisplayController().setResolution(new IVec2(window.getWidth() - 8, window.getHeight() - 39));
            }
        });

        setResolution(Display.DEFAULT_RESOLUTION);

//        this.setDoubleBuffered(true);

        window.setVisible(true);
    }

    @Override
    public void draw(DrawObject[] objects, Color background) {
        synchronized (this) {
            this.objects = objects;
            this.background = background;
        }
        repaint();
    }

    @Override
    public void setResolution(IVec2 resolution) {
        this.resolution = resolution;
        // Does not seem to have an impact even after a call to window.pack()
        setMinimumSize(new Dimension(resolution.x, resolution.y));
        window.setSize(resolution.x + 8, resolution.y + 39);
    }

    @Override
    public void allowResizingChanged(boolean allowed) {
        Execute.nextFrame(() -> window.setResizable(allowed));
    }

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

        for(DrawObject o : objects) {

            AffineTransform oldTransform = null;

            BufferedImage oImage = ((AWTImageImpl) o.image).image;
            IVec2 drawPos = o.screenLocation.subtracted(new IVec2(oImage.getWidth() / 2, oImage.getHeight() / 2));
            if(o.rotation != 0) {
                oldTransform = g.getTransform();
                g.rotate(Math.toRadians(o.rotation), o.screenLocation.x, o.screenLocation.y);
            }

            g.drawImage(oImage, drawPos.x, drawPos.y, null);

            if(oldTransform != null) g.setTransform(oldTransform);
        }
    }

    static void init() { }
}
