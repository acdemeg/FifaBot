package org.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.Cursor;

import javax.swing.*;

public class BotStateSwitcher extends JPanel {
    public static boolean IS_ACTIVE = false;
    private final Color switchColor = new Color(200, 200, 200);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color borderColor = new Color(50, 50, 50);
    private final Color activeSwitch = new Color(50,205,50);
    private BufferedImage puffer;
    private Graphics2D g;

    private BotStateSwitcher() {
        super();
        setVisible(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
                IS_ACTIVE = !IS_ACTIVE;
                repaint();
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBounds(0, 0, 41, 21);
    }
    @Override
    public void paint(Graphics gr) {
        if(g == null || puffer.getWidth() != getWidth() || puffer.getHeight() != getHeight()) {
            puffer = (BufferedImage) createImage(getWidth(), getHeight());
            g = (Graphics2D)puffer.getGraphics();
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHints(rh);
        }
        g.setColor(IS_ACTIVE ? activeSwitch : switchColor);
        int borderRadius = 10;
        g.fillRoundRect(0, 0, this.getWidth()-1,getHeight()-1, 5, borderRadius);
        g.setColor(borderColor);
        g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, borderRadius);
        g.setColor(buttonColor);
        if(IS_ACTIVE) {
            g.fillRoundRect(getWidth()/2, 1,  (getWidth()-1)/2 -2, (getHeight()-1) - 2, borderRadius, borderRadius);
            g.setColor(borderColor);
            g.drawRoundRect((getWidth()-1)/2, 0, (getWidth()-1)/2, (getHeight()-1), borderRadius, borderRadius);
        }
        else {
            g.fillRoundRect(1, 1, (getWidth()-1)/2 -2, (getHeight()-1) - 2, borderRadius, borderRadius);
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, (getWidth()-1)/2, (getHeight()-1), borderRadius, borderRadius);
        }

        gr.drawImage(puffer, 0, 0, null);
    }

    public static void createSwitcher() {
        JFrame jFrame = new JFrame();
        jFrame.setSize(60, 25);
        jFrame.setAlwaysOnTop(true);
        jFrame.setUndecorated(true);
        jFrame.add(new BotStateSwitcher());
        jFrame.setVisible(true);
    }
}

