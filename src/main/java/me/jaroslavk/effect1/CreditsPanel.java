package me.jaroslavk.effect1;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

public class CreditsPanel extends JPanel{ //I forgot about the existence of dialog window
    static {
        try {
            UIManager.setLookAndFeel(CheatWindow.laf);
        } catch (Exception e) {e.printStackTrace();}
    }

    public CreditsPanel() {
        super(new SpringLayout());
        Font fontBold = getFont().deriveFont(Font.BOLD, 16);
        SpringLayout lay = (SpringLayout)this.getLayout();
        CheatWindow.setColors(this);

        JLabel label = new JLabel(new ImageIcon(Effect1.folder.toString() + "\\data\\textures\\JAK.png"));
        //label.setPreferredSize(new Dimension(512, 512));
        add(label);
        label.setBorder(null);
        lay.putConstraint(SpringLayout.WEST, label, -62, SpringLayout.WEST, this);
        lay.putConstraint(SpringLayout.NORTH, label, -60, SpringLayout.NORTH, this);

        JSeparator separator = new JSeparator(JSeparator.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 140));
        CheatWindow.setColors(separator);
        add(separator);
        lay.putConstraint(SpringLayout.WEST, separator, -60, SpringLayout.EAST, label);
        lay.putConstraint(SpringLayout.NORTH, separator, 0, SpringLayout.NORTH, this);

        JSeparator separator2 = new JSeparator(JSeparator.HORIZONTAL);
        separator2.setPreferredSize(new Dimension(512, 2));
        CheatWindow.setColors(separator2);
        add(separator2);
        lay.putConstraint(SpringLayout.WEST, separator2, 0, SpringLayout.WEST, this);
        lay.putConstraint(SpringLayout.NORTH, separator2, 0, SpringLayout.SOUTH, this);

        JTextArea label2 = new JTextArea("CREDITS:");
        CheatWindow.setColorsNoBorders(label2);
        label2.setFont(fontBold);
        label2.setEditable(false);
        label2.setBorder(null);
        //label2.setPreferredSize(new Dimension(label2.getPreferredSize().width, 128));
        add(label2);
        lay.putConstraint(SpringLayout.WEST, label2, -45, SpringLayout.EAST, label);
        lay.putConstraint(SpringLayout.NORTH, label2, 0, SpringLayout.NORTH, this);

        JTextArea label3 = new JTextArea("Programmed by Jaroslav \"JaroslavK\" Koval | 2024\nGiant thanks to iTut for helping me in learning Java!\nShare this program! Version 17.11.2024");
        CheatWindow.setColorsNoBorders(label3);
        label3.setFont(getFont().deriveFont(16));
        label3.setEditable(false);
        label3.setPreferredSize(new Dimension(label3.getPreferredSize().width, 50));
        label3.setBorder(null);
        add(label3);
        lay.putConstraint(SpringLayout.WEST, label3, 0, SpringLayout.WEST, label2);
        lay.putConstraint(SpringLayout.NORTH, label3, 0, SpringLayout.SOUTH, label2);

        JButton legal = new JButton("Open CREDITS.txt");
        legal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().edit(new File(Effect1.folder.getAbsolutePath() + "/CREDITS.txt"));
                    }
                } catch (Exception ee) {ee.printStackTrace();}
            } 
        });
        legal.setFont(getFont().deriveFont(16));
        legal.setPreferredSize(new Dimension(200, 30));
        //setColors(shiftLock);
        add(legal);
        lay.putConstraint(SpringLayout.EAST, legal, -55, SpringLayout.EAST, this);
        lay.putConstraint(SpringLayout.SOUTH, legal, -30, SpringLayout.SOUTH, this);

        ////

        JButton license = new JButton("Open GPL V2 LICENSE.txt");
        license.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().edit(new File(Effect1.folder.getAbsolutePath() + "/GPL V2 LICENSE.txt"));
                    }
                } catch (Exception ee) {ee.printStackTrace();}
            } 
        });
        license.setFont(getFont().deriveFont(16));
        license.setPreferredSize(new Dimension(200, 30));
        //setColors(shiftLock);
        add(license);
        lay.putConstraint(SpringLayout.EAST, license, -55, SpringLayout.EAST, this);
        lay.putConstraint(SpringLayout.SOUTH, license, 0, SpringLayout.SOUTH, this);

        setPreferredSize(new Dimension(lay.getConstraint(SpringLayout.EAST, label3).getValue() + 4, 140));
    }
}
