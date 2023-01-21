package org.example;

import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * This class provided 'Switcher' for on/off bot in game
 */
public class BotStateSwitcher extends JPanel {
    private boolean active = false;
    private final Color switchColor = new Color(200, 200, 200);
    private final Color buttonColor = new Color(255, 255, 255);
    private final Color borderColor = new Color(50, 50, 50);
    private final Color activeSwitch = new Color(50, 205, 50);
    private transient BufferedImage puffer;
    private transient Graphics2D g;

    private BotStateSwitcher() {
        super();
        setVisible(true);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
                active = !active;
                repaint();
            }
        });
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBounds(0, 0, 41, 21);
    }

    @Override
    public void paint(Graphics gr) {
        if (g == null || puffer.getWidth() != getWidth() || puffer.getHeight() != getHeight()) {
            puffer = (BufferedImage) createImage(getWidth(), getHeight());
            g = (Graphics2D) puffer.getGraphics();
            RenderingHints rh = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHints(rh);
        }
        g.setColor(active ? activeSwitch : switchColor);
        int borderRadius = 10;
        g.fillRoundRect(0, 0, this.getWidth() - 1, getHeight() - 1, 5, borderRadius);
        g.setColor(borderColor);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, borderRadius);
        g.setColor(buttonColor);
        if (active) {
            g.fillRoundRect(getWidth() / 2, 1, (getWidth() - 1) / 2 - 2, (getHeight() - 1) - 2, borderRadius, borderRadius);
            g.setColor(borderColor);
            g.drawRoundRect((getWidth() - 1) / 2, 0, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius);
        } else {
            g.fillRoundRect(1, 1, (getWidth() - 1) / 2 - 2, (getHeight() - 1) - 2, borderRadius, borderRadius);
            g.setColor(borderColor);
            g.drawRoundRect(0, 0, (getWidth() - 1) / 2, (getHeight() - 1), borderRadius, borderRadius);
        }

        gr.drawImage(puffer, 0, 0, null);
    }

    public boolean isActive() {
        return active;
    }

    @NonNull
    public static BotStateSwitcher createSwitcher() {
        BotStateSwitcher switcher = new BotStateSwitcher();
        JFrame jFrame = new JFrame();
        jFrame.setSize(60, 25);
        jFrame.setAlwaysOnTop(true);
        jFrame.setUndecorated(true);
        jFrame.add(switcher);
        jFrame.setVisible(true);
        return switcher;
    }
}

