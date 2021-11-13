package com.github.rccookie.engine2d.impl.awt;

import com.github.rccookie.engine2d.Color;
import com.github.rccookie.engine2d.Execute;
import com.github.rccookie.engine2d.core.DrawObject;
import com.github.rccookie.engine2d.impl.CameraControls;
import com.github.rccookie.engine2d.impl.Display;
import com.github.rccookie.geometry.performance.IVec2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class AWTDisplay extends JPanel implements Display {

    static JFrame latestWindow = null;
    static AWTDisplay latestDisplay = null;

    BufferedImage image;
    private IVec2 resolution;

    final JFrame window;
    final CameraControls cameraControls;

    public AWTDisplay(IVec2 resolution, CameraControls cameraControls) {
        this.cameraControls = cameraControls;
        latestDisplay = this;
        latestWindow = window = new JFrame("Hello");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.getContentPane().add(this);
        window.addComponentListener(new ComponentListener() {
                                        @Override
                                        public void componentResized(ComponentEvent e) {
                                            cameraControls.setResolution(new IVec2(window.getWidth() - 8, window.getHeight() - 19));
                                        }
                                        @Override
                                        public void componentMoved(ComponentEvent e) { }
                                        @Override
                                        public void componentShown(ComponentEvent e) { }
                                        @Override
                                        public void componentHidden(ComponentEvent e) { }
                                    });

        window.addMouseListener(new MouseListener() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        //
                                    }

                                    @Override
                                    public void mousePressed(MouseEvent e) {

                                    }

                                    @Override
                                    public void mouseReleased(MouseEvent e) {

                                    }

                                    @Override
                                    public void mouseEntered(MouseEvent e) { }
                                    @Override
                                    public void mouseExited(MouseEvent e) { }
                                });

        setResolution(resolution);

        window.setVisible(true);
    }

    @Override
    public void draw(java.util.List<DrawObject> objects, Color background) {
        Graphics2D g = image.createGraphics();
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
        g.dispose();

        repaint();
    }

    @Override
    public void setResolution(IVec2 resolution) {
        this.resolution = resolution;
        image = new BufferedImage(resolution.x, resolution.y, BufferedImage.TYPE_INT_ARGB);
        // Does not seem to have an impact even after a call to window.pack()
        setMinimumSize(new Dimension(resolution.x, resolution.y));
        window.setSize(resolution.x + 8, resolution.y + 19);
    }

    @Override
    public void allowResizingChanged(boolean allowed) {
        Execute.nextFrame(() -> window.setResizable(allowed));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
    }
}
