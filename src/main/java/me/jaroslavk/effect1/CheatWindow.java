package me.jaroslavk.effect1;

import java.io.*;
import java.nio.file.Files;
import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.AudioFormat;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.nimbus.*;

import com.sipgate.mp3wav.Converter;

import processing.sound.*;

public class CheatWindow extends JPanel {
    //r3dwbt -> Evolution CheatWindow -> Effect1 CheatWindow 👍
    //to whoever will try to read this code, sorry for mess :<

    public static final Color beige = new Color(0xFF968c7d),
                               darkerBeige = new Color(0xFF7b7266),
                               lighterBeige = new Color(0xFF9c9283);

    // public static final Color beige = new Color(0xFFb226e9),
    //                         darkerBeige = new Color(0xFF8e2eb4),
    //                         lighterBeige = new Color(0xFFce5bfb);

    public static final BevelBorder normalBorder = new BevelBorder(BevelBorder.RAISED, darkerBeige, lighterBeige);
    public static LookAndFeel laf = new NimbusLookAndFeel();

    static {
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception e) {e.printStackTrace();}
    }

    private Effect1 parent;
    //private CheatWindow thePanel;
    private JTextArea infoText;
    private JSlider shiftSlider1 = new JSlider(JSlider.HORIZONTAL, -32, 32, 0);
    private JSlider shiftSlider2 = new JSlider(JSlider.HORIZONTAL, -32, 32, 0);
    private JSlider multSlider = new JSlider(JSlider.HORIZONTAL, -10, 200, 1);
    private JButton shiftLock = new JButton("Toggle automatic color shift");
    private JButton stopCamera = new JButton("Toggle camera moving");
    private JButton stopCameraRotation = new JButton("Toggle camera rotating");
    private JButton allowLookAroundButton = new JButton("Toggle camera control (WASD): " + Effect1.allowLookAround);
    private JButton chooseColorsButton = new JButton("Change colors");
    private JButton chooseMusicFile = new JButton("Load music");
    private JButton stopButton = new JButton("Stop music");;
    private JButton pauseButton = new JButton();
    private JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
    private JSlider speedSlider;
    private JTextArea progressText = new JTextArea();
    private JSlider hillsSlider = new JSlider(JSlider.HORIZONTAL, -2, 2, 0);
    private JSlider detectorSensSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 1);
    private JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
    private JSlider chaosSensSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 75);
    public static ImageIcon icon = new ImageIcon(Effect1.folder.toString() + "\\data\\textures\\icon.png");
    private JFrame creditsWindow = new JFrame();
    private JButton advancedButton;
    public static Font stdFont;

    private static CreditsPanel creditsPanel = new CreditsPanel();

    public float soundSpeed = 1;

    public static void start(CheatWindow c) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {open(c);}
        });
    }
    
    private SpringLayout lay;


    public CheatWindow(Effect1 p) {
        super(new SpringLayout());
        lay = (SpringLayout)this.getLayout();
        stdFont = getFont();
        //setBackground(beige);
        Component thePanel = this;
        parent = p;

        JTextArea someText = new JTextArea("R - High resolution E - Screenshot");
        someText.setFont(stdFont.deriveFont(Font.BOLD));
        someText.setPreferredSize(new Dimension(550, 0));
        setColors(someText);
        someText.setEditable(false);
        someText.setAutoscrolls(true);
        add(someText);
        lay.putConstraint(SpringLayout.WEST, someText, 0, SpringLayout.WEST, this);
        lay.putConstraint(SpringLayout.NORTH, someText, 0, SpringLayout.NORTH, this);

        chooseColorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c = JColorChooser.showDialog(thePanel, "Choose color for BG", new Color(Effect1.bgColor));
                if (c != null) Effect1.bgColor = c.getRGB();
                Color c2 = JColorChooser.showDialog(thePanel, "Choose color for dots", new Color(Effect1.dotColor));
                if (c2 != null) Effect1.dotColor = c2.getRGB();
            } 
        });
        chooseColorsButton.setFont(stdFont);
        chooseColorsButton.setPreferredSize(new Dimension(275, 40));
        //setColors(shiftLock);
        add(chooseColorsButton);
        lay.putConstraint(SpringLayout.WEST, chooseColorsButton, 0, SpringLayout.WEST, someText);
        lay.putConstraint(SpringLayout.NORTH, chooseColorsButton, 0, SpringLayout.SOUTH, someText);

        pauseButton = new JButton("Pause music");
        pauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Effect1.soundFile == null) return;
                if (Effect1.soundFile.isPlaying()) {
                    Effect1.soundFile.pause();
                    pauseButton.setText("Unpause music");
                    Effect1.snakeText = "Paused!"; 
                    Effect1.snakeTextFadeTimer = -361;
                } else {
                    Effect1.soundFile.play();
                    pauseButton.setText("Pause music");
                    Effect1.snakeText = ""; 
                    Effect1.snakeTextFadeTimer = -361;
                }
            } 
        });
        pauseButton.setFont(stdFont);
        pauseButton.setPreferredSize(new Dimension(275, 40));
        //setColors(shiftLock);
        add(pauseButton);
        lay.putConstraint(SpringLayout.WEST, pauseButton, 0, SpringLayout.EAST, chooseColorsButton);
        lay.putConstraint(SpringLayout.NORTH, pauseButton, 0, SpringLayout.NORTH, chooseColorsButton);

        chooseMusicFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser jfc = new JFileChooser(Effect1.musicFile == null ? Effect1.folder : Effect1.musicFile);
                jfc.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (e.getActionCommand() == JFileChooser.CANCEL_SELECTION || 
                                       e.getID() == JFileChooser.ERROR_OPTION) jfc.setSelectedFile(null);;
                    }
                });
    
                if (Math.random() < 0.95)
                    jfc.setDialogTitle("Select music file");
                else
                    jfc.setDialogTitle("Select your favorite music :3");

                jfc.setFileFilter(new FileNameExtensionFilter("music files (.wav)", "wav", "mp3"));
                jfc.showOpenDialog(Effect1.cheatWindow);
                File f = jfc.getSelectedFile();
                if (f == null || f.isDirectory() || !f.exists()) {
                    if (Effect1.soundFile != null && !Effect1.soundFile.isPlaying()) {
                        Effect1.snakeText = "Waiting for music"; 
                        Effect1.snakeTextFadeTimer = -361;
                    }
                    return;
                }
                Effect1.musicFile = f;
                if (Effect1.soundFile != null) Effect1.soundFile.stop();
                Effect1.snakeText = "Loading...";
                Effect1.snakeTextFadeTimer = -361;
                System.out.println(f.getName());
                try {
                    if (Effect1.soundFile != null) Effect1.soundFile.removeFromCache();
                    if (f.getName().endsWith(".wav")) {
                        Effect1.soundFile = new SoundFile(parent, Effect1.musicFile.getAbsolutePath(), true);
                    } else {
                        Effect1.snakeText = "Converting mp3 to wav...";
                        try (
                            final InputStream inputStream = Effect1.createInput(f);
                            final ByteArrayOutputStream output = new ByteArrayOutputStream();
                        ) {
                            final AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, true);
            
                            Converter.convertFrom(inputStream).withTargetFormat(audioFormat).to(output);
            
                            final byte[] wavContent = output.toByteArray();
                            output.close();
                            inputStream.close();
            
                            // final AudioFileFormat actualFileFormat = AudioSystem
                            // .getAudioFileFormat(new ByteArrayInputStream(wavContent));
                            
                            Files.write(new File(Effect1.folder.getAbsolutePath() + "/temp.wav").toPath(), wavContent);
                            Effect1.soundFile = new SoundFile(parent, Effect1.folder.getAbsolutePath() + "/temp.wav", true);
                        } finally {
                            // huh
                        }
                    }
                } catch (Throwable ee) {
                    ee.printStackTrace();
                    Effect1.soundFile = null;
                    Effect1.snakeTextFadeTimer = -361;
                    Effect1.snakeText = "ERROR! while loading " + f.getName();
                    return;
                }
                Effect1.soundFile.play();
                while (!Effect1.soundFile.isPlaying()) {}; //stoopid
                pauseButton.setText("Pause music");
                Effect1.pitchDetector.input(Effect1.soundFile);
                Effect1.waveform.input(Effect1.soundFile);
                Effect1.amplitude.input(Effect1.soundFile);
                Effect1.detector.input(Effect1.soundFile);
                Effect1.soundFile.rate(speedSlider.getValue() / 100f);
                Effect1.snakeText = "Now playing " + f.getName();
                Effect1.snakeTextFadeTimer = 1000;
            } 
        });
        chooseMusicFile.setFont(stdFont);
        chooseMusicFile.setPreferredSize(new Dimension(275, 40));

        add(chooseMusicFile);
        lay.putConstraint(SpringLayout.WEST, chooseMusicFile, 0, SpringLayout.WEST, chooseColorsButton);
        lay.putConstraint(SpringLayout.NORTH, chooseMusicFile, 0, SpringLayout.SOUTH, chooseColorsButton);

        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Effect1.soundFile == null) return;
                Effect1.soundFile.stop();
                Effect1.snakeText = "Waiting for music"; 
                Effect1.snakeTextFadeTimer = -1;
            } 
        });
        stopButton.setFont(stdFont);
        stopButton.setPreferredSize(new Dimension(275, 40));
        //setColors(shiftLock);
        add(stopButton);
        lay.putConstraint(SpringLayout.WEST, stopButton, 0, SpringLayout.EAST, chooseMusicFile);
        lay.putConstraint(SpringLayout.NORTH, stopButton, 0, SpringLayout.NORTH, chooseMusicFile);


        add(progressText);
        setColors(progressText);
        progressText.setEditable(false);
        progressText.setPreferredSize(new Dimension(550, 20));
        lay.putConstraint(SpringLayout.WEST, progressText, 0, SpringLayout.WEST, chooseMusicFile);
        lay.putConstraint(SpringLayout.NORTH, progressText, 0, SpringLayout.SOUTH, chooseMusicFile);

        progressBar.setPreferredSize(new Dimension(550, 20));
        progressBar.setName("uhhh");
        add(progressBar);
        lay.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, progressText);
        lay.putConstraint(SpringLayout.NORTH, progressBar, 0, SpringLayout.SOUTH, progressText);

        JLabel speedLabel = new JLabel("Set playback speed");
        speedLabel.setFont(stdFont);
        add(speedLabel);
        lay.putConstraint(SpringLayout.WEST, speedLabel, 0, SpringLayout.WEST, progressBar);
        lay.putConstraint(SpringLayout.NORTH, speedLabel, 0, SpringLayout.SOUTH, progressBar);

        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 100);
        speedSlider.setMajorTickSpacing(25);
        speedSlider.setMinorTickSpacing(5);
        speedSlider.setSnapToTicks(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        speedSlider.setFont(stdFont);
        speedSlider.setToolTipText("Change playback speed in percent");
        speedSlider.setName("floorShift2");
        speedSlider.setPreferredSize(new Dimension(550, 50));
        setColors(speedSlider);
        speedSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                soundSpeed = speedSlider.getValue() / 100f;
                if (Effect1.soundFile == null) return;
                Effect1.soundFile.rate(speedSlider.getValue() / 100f);
            }
        });

        add(speedSlider);
        lay.putConstraint(SpringLayout.WEST, speedSlider, 0, SpringLayout.WEST, speedLabel);
        lay.putConstraint(SpringLayout.NORTH, speedSlider, 0, SpringLayout.SOUTH, speedLabel);

        JLabel volumeLabel = new JLabel("Volume");
        volumeLabel.setFont(stdFont);
        add(volumeLabel);
        lay.putConstraint(SpringLayout.WEST, volumeLabel, 0, SpringLayout.WEST, speedSlider);
        lay.putConstraint(SpringLayout.NORTH, volumeLabel, 0, SpringLayout.SOUTH, speedSlider);

        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setFont(stdFont);
        volumeSlider.setToolTipText("Change volume");
        volumeSlider.setName("floorShift2");
        volumeSlider.setPreferredSize(new Dimension(550, 50));
        setColors(volumeSlider);
        volumeSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Sound.volume(volumeSlider.getValue()/100f);
            }
        });
        add(volumeSlider);
        lay.putConstraint(SpringLayout.WEST, volumeSlider, 0, SpringLayout.WEST, volumeLabel);
        lay.putConstraint(SpringLayout.NORTH, volumeSlider, 0, SpringLayout.SOUTH, volumeLabel);

        JLabel chaosLabel = new JLabel("Chaos sensitivity ");
        chaosLabel.setFont(stdFont);
        add(chaosLabel);
        lay.putConstraint(SpringLayout.WEST, chaosLabel, 0, SpringLayout.WEST, volumeSlider);
        lay.putConstraint(SpringLayout.NORTH, chaosLabel, 0, SpringLayout.SOUTH, volumeSlider);

        chaosSensSlider.setMajorTickSpacing(4);
        chaosSensSlider.setMinorTickSpacing(2);
        chaosSensSlider.setPaintTicks(true);
        chaosSensSlider.setPaintLabels(true);
        chaosSensSlider.setFont(stdFont);
        chaosSensSlider.setToolTipText("Change sensitivity of the chaos");
        chaosSensSlider.setName("floorShift2");
        chaosSensSlider.setPreferredSize(new Dimension(550, 50));
        setColors(chaosSensSlider);
        chaosSensSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Effect1.chaosSens = 100 - (float)chaosSensSlider.getValue();
            }
        });
        add(chaosSensSlider);
        lay.putConstraint(SpringLayout.WEST, chaosSensSlider, 0, SpringLayout.WEST, chaosLabel);
        lay.putConstraint(SpringLayout.NORTH, chaosSensSlider, 0, SpringLayout.SOUTH, chaosLabel);


        JLabel detectorLabel = new JLabel("Change beat detector sensitivity (in milliseconds)");
        detectorLabel.setFont(stdFont);
        add(detectorLabel);
        lay.putConstraint(SpringLayout.WEST, detectorLabel, 0, SpringLayout.WEST, chaosSensSlider);
        lay.putConstraint(SpringLayout.NORTH, detectorLabel, 0, SpringLayout.SOUTH, chaosSensSlider);

        detectorSensSlider.setMajorTickSpacing(50);
        detectorSensSlider.setMinorTickSpacing(0);
        detectorSensSlider.setPaintTicks(true);
        detectorSensSlider.setPaintLabels(true);
        detectorSensSlider.setFont(stdFont);
        detectorSensSlider.setToolTipText("beat detector (dots color change) sensetivity (milliseconds)");
        detectorSensSlider.setName("");
        detectorSensSlider.setPreferredSize(new Dimension(550, 50));
        setColors(detectorSensSlider);
        detectorSensSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                Effect1.detector.sensitivity(detectorSensSlider.getValue());
            }
        });

        add(detectorSensSlider);
        lay.putConstraint(SpringLayout.WEST, detectorSensSlider, 0, SpringLayout.WEST, detectorLabel);
        lay.putConstraint(SpringLayout.NORTH, detectorSensSlider, 0, SpringLayout.SOUTH, detectorLabel);



        //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA hello :)


        advancedButton = new JButton("Advanced Settings");
        advancedButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //frame.setResizable(true);
                addAdvancedSettings();
                remove(advancedButton);
                //frame.setPreferredSize(getPreferredSize());
                frame.pack();
            } 
        });
        advancedButton.setFont(stdFont);
        advancedButton.setPreferredSize(new Dimension(550, 20));
        add(advancedButton);
        lay.putConstraint(SpringLayout.WEST, advancedButton, 0, SpringLayout.WEST, detectorSensSlider);
        lay.putConstraint(SpringLayout.NORTH, advancedButton, 0, SpringLayout.SOUTH, detectorSensSlider);

        setPreferredSize(new Dimension(555, lay.getConstraint(SpringLayout.SOUTH, advancedButton).getValue() + 20));

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
        JButton creditsButton = new JButton("Credits");
        creditsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                creditsWindow.add(creditsPanel);
                creditsWindow.setResizable(false);
                creditsWindow.setTitle("Credits");
                creditsWindow.setVisible(true);
                creditsWindow.setFocusable(true);
                creditsWindow.pack();
                creditsWindow.setFocusTraversalKeysEnabled(false);
                creditsWindow.setIconImage(icon.getImage());
            } 
        });
        creditsButton.setFont(stdFont);
        creditsButton.setPreferredSize(new Dimension(275, 20));

        add(creditsButton);
        lay.putConstraint(SpringLayout.WEST, creditsButton, 0, SpringLayout.WEST, this);
        lay.putConstraint(SpringLayout.SOUTH, creditsButton, 0, SpringLayout.SOUTH, this);

        JButton lowFpsButton = new JButton("Set low FPS");
        lowFpsButton.setToolTipText("In case your GPU is on fire");
        lowFpsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Effect1.targetFramerate == 30) {
                    Effect1.targetFramerate = 24;
                    lowFpsButton.setText("Back to 30 FPS");
                } else {
                    Effect1.targetFramerate = 30;
                    lowFpsButton.setText("Set low FPS");
                }
            } 
        });
        lowFpsButton.setFont(stdFont);
        lowFpsButton.setPreferredSize(new Dimension(275, 20));

        add(lowFpsButton);
        lay.putConstraint(SpringLayout.WEST, lowFpsButton, 0, SpringLayout.EAST, creditsButton);
        lay.putConstraint(SpringLayout.SOUTH, lowFpsButton, 0, SpringLayout.SOUTH, this);

        // JLabel jakGamesLabel = new JLabel(new ImageIcon(Effect1.folder.toString() + "\\data\\textures\\JAK.png"));
        // add(jakGamesLabel); SAY 
        //                     GEX
        // //jakGamesLabel.setBorder(normalBorder);
        // lay.putConstraint(SpringLayout.HORIZONTAL_CENTER, jakGamesLabel, 0, SpringLayout.HORIZONTAL_CENTER, this);
        // lay.putConstraint(SpringLayout.SOUTH, jakGamesLabel, 64, SpringLayout.SOUTH, this);
    }

    public JFrame frame = null;
    private static void open(CheatWindow t) {
        t.frame = new JFrame("JAK Effect1 audio visualizer settings window");
        t.frame.addWindowListener(new WindowListener() {
            public void windowOpened(java.awt.event.WindowEvent e) {}
            public void windowClosing(java.awt.event.WindowEvent e) {t.parent.exit();}
            public void windowClosed(java.awt.event.WindowEvent e) {}
            public void windowIconified(java.awt.event.WindowEvent e) {}
            public void windowDeiconified(java.awt.event.WindowEvent e) {}
            public void windowActivated(java.awt.event.WindowEvent e) {}
            public void windowDeactivated(java.awt.event.WindowEvent e) {} //god mothefucking dayum
            
        });
        t.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        t.frame.setAlwaysOnTop(true);
        JOptionPane.showMessageDialog(t.frame,
        "Hello and welcome to the JAK Effect1 audio visualizer! \nChoose your music of choice by clicking \"Load music\" button. (close this window first)",
        "Welcome message",
        JOptionPane.INFORMATION_MESSAGE, icon);
        t.frame.setAlwaysOnTop(false);

        try {
            t.frame.add(t);
            t.setFocusable(true);
            t.setFocusTraversalKeysEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(420);
        }
        
        t.frame.setFocusable(true);
        t.frame.setFocusTraversalKeysEnabled(false);
        t.frame.setIconImage(icon.getImage());
        
        setColors(t);
        t.frame.setResizable(false);
        t.frame.setVisible(true);
        t.frame.pack();
        System.out.println(t.frame.getSize());
    }

    public static void setColors(JComponent c) {
        c.setBorder(normalBorder);
        c.setBackground(lighterBeige);
        c.setVisible(true);
        //NimbusLookAndFeel.installColorsAndFont(c, "idk", "idk", "idk");
    }

    public static void setColorsNoBorders(JComponent c) {
        c.setBackground(lighterBeige);
        c.setVisible(true);
    }

    public void update() {
        String musicFilename = "Not available";
        String musicProgress = "0 : 0";
        if (Effect1.soundFile != null) { 
            musicProgress = String.format("%.2f : %.2f", Effect1.soundFile.duration(), Effect1.soundFile.position());
            if (Effect1.musicFile != null) musicFilename = Effect1.musicFile.getName();
            progressBar.setMaximum((int)Effect1.soundFile.duration());
            progressBar.setValue((int)Effect1.soundFile.position());
        }
        progressText.setText(musicFilename  + " | " + musicProgress);

        if (infoText != null) {
            infoText.setText(String.format("Debug info:\nFPS: %.1f (%.1f) offset1: %d offset2: %d cameraAngle: %.1f cameraPitch: %.1f shiftChangeLock: %b \ncamera XYZ: | %.1f | %.1f | %.1f | multiplier: %.3f hills height: %.2f\n music file name: %s music progress: %s", 
            parent.frameRate, Effect1.targetFramerate, Effect1.floorShift1, Effect1.floorShift2, Effect1.camAngle, Effect1.camPitch, Effect1.shiftChangeLock, Effect1.camX, Effect1.camY, Effect1.camZ, Effect1.transition, Effect1.hillsHeight, musicFilename, musicProgress));
            if (!Effect1.shiftChangeLock) {
                shiftSlider1.setValue(Effect1.floorShift1);
                shiftSlider2.setValue(Effect1.floorShift2);
            }
            if (!Effect1.ticker.caveStateFinished)
                multSlider.setValue((int)Math.round(Effect1.transition * 10f));
        }
    }

    private void addAdvancedSettings() {
        infoText = new JTextArea("uhhh");
        infoText.setFont(stdFont);
        infoText.setPreferredSize(new Dimension(550, 70));
        setColors(infoText);
        infoText.setEditable(false);
        infoText.setAutoscrolls(true);
        add(infoText);
        lay.putConstraint(SpringLayout.WEST, infoText, 0, SpringLayout.WEST, detectorSensSlider);
        lay.putConstraint(SpringLayout.NORTH, infoText, 0, SpringLayout.SOUTH, detectorSensSlider);

        JLabel shiftLabel = new JLabel("Change color shift in bits");
        shiftLabel.setFont(stdFont);
        add(shiftLabel);
        lay.putConstraint(SpringLayout.WEST, shiftLabel, 0, SpringLayout.WEST, infoText);
        lay.putConstraint(SpringLayout.NORTH, shiftLabel, 0, SpringLayout.SOUTH, infoText);

        shiftSlider1.setMajorTickSpacing(2);
        shiftSlider1.setMinorTickSpacing(1);
        shiftSlider1.setPaintTicks(true);
        shiftSlider1.setPaintLabels(true);
        shiftSlider1.setFont(stdFont);
        shiftSlider1.setToolTipText("Change color shift on X axis");
        shiftSlider1.setName("floorShift1");
        shiftSlider1.setPreferredSize(new Dimension(550, 50));
        setColors(shiftSlider1);
        shiftSlider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //if (!Effect1.shiftChangeLock) return;
                Effect1.floorShift1 = shiftSlider1.getValue();
            }
        });
        add(shiftSlider1);
        lay.putConstraint(SpringLayout.WEST, shiftSlider1, 0, SpringLayout.WEST, shiftLabel);
        lay.putConstraint(SpringLayout.NORTH, shiftSlider1, 0, SpringLayout.SOUTH, shiftLabel);

        shiftSlider2.setMajorTickSpacing(2);
        shiftSlider2.setMinorTickSpacing(1);
        shiftSlider2.setPaintTicks(true);
        shiftSlider2.setPaintLabels(true);
        shiftSlider2.setFont(stdFont);
        shiftSlider2.setToolTipText("Change color shift on Z axis");
        shiftSlider2.setName("floorShift2");
        shiftSlider2.setPreferredSize(new Dimension(550, 50));
        setColors(shiftSlider2);
        shiftSlider2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                //if (!Effect1.shiftChangeLock) return;
                Effect1.floorShift2 = shiftSlider2.getValue();
            }
        });
        add(shiftSlider2);
        lay.putConstraint(SpringLayout.WEST, shiftSlider2, 0, SpringLayout.WEST, shiftSlider1);
        lay.putConstraint(SpringLayout.NORTH, shiftSlider2, 0, SpringLayout.SOUTH, shiftSlider1);

        shiftLock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Effect1.shiftChangeLock = !Effect1.shiftChangeLock;
            } 
        });
        shiftLock.setFont(stdFont);
        shiftLock.setPreferredSize(new Dimension(550, 20));
        //setColors(shiftLock);
        add(shiftLock);
        lay.putConstraint(SpringLayout.WEST, shiftLock, 0, SpringLayout.WEST, shiftSlider2);
        lay.putConstraint(SpringLayout.NORTH, shiftLock, 0, SpringLayout.SOUTH, shiftSlider2);

        JButton stopTransitionButton = new JButton("Toggle automatic transition: " + Effect1.ticker.autoTransition);
        stopTransitionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Effect1.ticker.autoTransition = !Effect1.ticker.autoTransition;
                if (!Effect1.ticker.autoTransition) Effect1.ticker.caveStateFinished = true;
                stopTransitionButton.setText("Toggle automatic transition: " + Effect1.ticker.autoTransition);
            } 
        });
        stopTransitionButton.setFont(stdFont);
        stopTransitionButton.setPreferredSize(new Dimension(550, 20));
        add(stopTransitionButton);
        lay.putConstraint(SpringLayout.WEST, stopTransitionButton, 0, SpringLayout.WEST, shiftLock);
        lay.putConstraint(SpringLayout.NORTH, stopTransitionButton, 0, SpringLayout.SOUTH, shiftLock);

        stopCamera.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Effect1.cameraStop = !Effect1.cameraStop;
            } 
        });
        stopCamera.setFont(stdFont);
        stopCamera.setPreferredSize(new Dimension(550, 20));
        add(stopCamera);
        lay.putConstraint(SpringLayout.WEST, stopCamera, 0, SpringLayout.WEST, stopTransitionButton);
        lay.putConstraint(SpringLayout.NORTH, stopCamera, 0, SpringLayout.SOUTH, stopTransitionButton);

        stopCameraRotation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Effect1.cameraStopRotate = !Effect1.cameraStopRotate;
            } 
        });
        stopCameraRotation.setFont(stdFont);
        stopCameraRotation.setPreferredSize(new Dimension(550, 20));
        //setColors(shiftLock);
        add(stopCameraRotation);
        lay.putConstraint(SpringLayout.WEST, stopCameraRotation, 0, SpringLayout.WEST, stopCamera);
        lay.putConstraint(SpringLayout.NORTH, stopCameraRotation, 0, SpringLayout.SOUTH, stopCamera);

        allowLookAroundButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Effect1.allowLookAround = !Effect1.allowLookAround;
                allowLookAroundButton.setText("Toggle camera control (WASD): " + Effect1.allowLookAround);
            } 
        });
        allowLookAroundButton.setFont(stdFont);
        allowLookAroundButton.setPreferredSize(new Dimension(550, 20));
        //setColors(shiftLock);
        add(allowLookAroundButton);
        lay.putConstraint(SpringLayout.WEST, allowLookAroundButton, 0, SpringLayout.WEST, stopCameraRotation);
        lay.putConstraint(SpringLayout.NORTH, allowLookAroundButton, 0, SpringLayout.SOUTH, stopCameraRotation);

        JLabel multLabel = new JLabel("Change transition parameter");
        multLabel.setFont(stdFont);
        add(multLabel);
        lay.putConstraint(SpringLayout.WEST, multLabel, 0, SpringLayout.WEST, allowLookAroundButton);
        lay.putConstraint(SpringLayout.NORTH, multLabel, 0, SpringLayout.SOUTH, allowLookAroundButton);

        multSlider.setMajorTickSpacing(10);
        multSlider.setMinorTickSpacing(5);
        multSlider.setPaintTicks(true);
        multSlider.setPaintLabels(true);
        multSlider.setFont(stdFont);
        multSlider.setToolTipText("Change multiplication parameter");
        multSlider.setName("");
        multSlider.setPreferredSize(new Dimension(550, 50));
        setColors(multSlider);
        multSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!Effect1.ticker.caveStateFinished) return;
                Effect1.transition = multSlider.getValue() / 10f;
            }
        });
        add(multSlider);
        lay.putConstraint(SpringLayout.WEST, multSlider, 0, SpringLayout.WEST, multLabel);
        lay.putConstraint(SpringLayout.NORTH, multSlider, 0, SpringLayout.SOUTH, multLabel);

        JLabel hillsLabel = new JLabel("Change hills \"height\"");
        hillsLabel.setFont(stdFont);
        add(hillsLabel);
        lay.putConstraint(SpringLayout.WEST, hillsLabel, 0, SpringLayout.WEST, multSlider);
        lay.putConstraint(SpringLayout.NORTH, hillsLabel, 0, SpringLayout.SOUTH, multSlider);

        hillsSlider.setMajorTickSpacing(1);
        hillsSlider.setMinorTickSpacing(0);
        hillsSlider.setPaintTicks(true);
        hillsSlider.setPaintLabels(true);
        hillsSlider.setFont(stdFont);
        hillsSlider.setToolTipText("Change height of the hills parameter");
        hillsSlider.setName("");
        hillsSlider.setPreferredSize(new Dimension(550, 50));
        setColors(hillsSlider);
        hillsSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (!Effect1.ticker.caveStateFinished) return;
                Effect1.hillsHeight = hillsSlider.getValue();
            }
        });

        add(hillsSlider);
        lay.putConstraint(SpringLayout.WEST, hillsSlider, 0, SpringLayout.WEST, hillsLabel);
        lay.putConstraint(SpringLayout.NORTH, hillsSlider, 0, SpringLayout.SOUTH, hillsLabel);

        setPreferredSize(new Dimension(555, lay.getConstraint(SpringLayout.SOUTH, hillsSlider).getValue() + 20));
    }
}
//Why are you here iTut?