package me.jaroslavk.effect1;

import java.io.File;
import java.awt.Font;

import javax.swing.*;

import com.jogamp.newt.opengl.GLWindow;

import processing.opengl.*;
import processing.core.*;
import processing.sound.*;

public class Effect1 extends PApplet {
    //Mining Pioneers -> Effect1 👍

    public static Effect1 sketch;
    public static GLWindow window;
    public static Ticker ticker = new Ticker();
    public static File folder;
    public static CheatWindow cheatWindow;

    public Effect1() { sketch = this; }

    public static void main(String[] args) {
        try {
            folder = new File(Effect1.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
            if (folder.toPath().resolve("data/textures/the_rat.png").toFile().isFile() == false) { // THE RAT
                ticker = null;
                throw new RuntimeException("The rat is gone.");
            }
            PApplet.runSketch(new String[]{"--sketch-path=" + folder, "Effect1"}, sketch = new Effect1());
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(null, "Fatal error on launch!\nCaused by:\n" + e.getLocalizedMessage(), "Uh oh", JOptionPane.ERROR_MESSAGE);
                }
            });            
        }
    }

    public PSurface initSurface() {
        PSurface surface = super.initSurface();
        surface.setTitle("JAK Effect1 audio visualizer");
        window = (GLWindow)surface.getNative();
        window.setMaximized(true, true);
        // window.setUndecorated(true);
        // window.setSize(window.getScreen().getWidth(), window.getScreen().getHeight());
        return surface;
    }

    public static int floorShift1 = 0, floorShift2 = 0;
    public static float transition = 1f, chaos = 0, chaosSens = 25;
    public static int bgColor = 0, dotColor = 0xFF99FFFF;
    public static float[] hillPreCalc = new float[200], chaosAnalysis = new float[200];
    public static File musicFile;
    public static SoundFile soundFile;
    public static Sound sound = new Sound(sketch);
    public static UhhDetector pitchDetector = new UhhDetector(sketch, 0.05f);
    public static Amplitude amplitude = new Amplitude(sketch);
    public static Waveform waveform = new Waveform(sketch, 200);
    public static BeatDetector detector = new BeatDetector(sketch);
    public static float[][] theNoise;

    static {
        for (int i = 0; i < 200; i++) {
            hillPreCalc[i] = (float)(Math.sin(i * (Math.PI / 100) - (Math.PI / 2)) + 1) * 40f;
        }
        theNoise = new float[6400][200];
        long millis = System.currentTimeMillis();
        for(int x = 0; x < 6400; x++) for(int y = 0; y < 200; y++) theNoise[x][y] = (OpenSimplex2S.noise2(millis, x / 400f, y / 25f) * 20f);
    }

    private static void drawScreen() {
        PGraphics3D g = (PGraphics3D)viewport;
        
        if (!epilepsyWarning) return;

        float cameraLookX = camX + (float)Math.cos(Math.toRadians(camAngle)),
              cameraLookY = camY + (float)Math.tan(Math.toRadians(camPitch)),
              cameraLookZ = camZ + (float)Math.sin(Math.toRadians(camAngle)),
              yPos = -5,
              xPos = 0,
              zPos = 0,
              yPosWave, 
              amplitudeAnalysis = amplitude.analyze();

              
        int uhhX, tick = Ticker.tickCount;

        float[] sinPreCalc = new float[200];
        for (int i = 0; i < 200; i++) sinPreCalc[i] = (float)Math.sin((i + tick / 100f) / 200) * 24;
        
              
        if (amplitudeAnalysis <= 0.004) amplitudeAnalysis = 1f;
        if (soundFile != null) {
            pitchDetector.minimumConfidence = amplitudeAnalysis - 0.7f;
            chaos = (pitchDetector.analyze() / chaosSens); //the uhh detector is stoopid
            chaosAnalysis = waveform.analyze();
        }

        g.camera(camX, camY, camZ, cameraLookX, cameraLookY, cameraLookZ, 0, -1, 0);
        g.noStroke();        
        g.background(bgColor);
        g.beginShape(TRIANGLES);
        g.texture(atlas);

        int startCamX = (int)Math.round(camX);
        for (int x = 0; x < 200; x++) {
            uhhX =  x +          (200 * (int)((startCamX - (x * 3.5f - 700f)) / 700f)); 
            xPos = (x * 3.5f) + (700f * (int)((startCamX - (x * 3.5f - 700f)) / 700f));
            for (int z = -100; z < 100; z++) {
                yPos = sinPreCalc[(int)(x + tick / 10f) % 199] / theNoise[(int)(uhhX * 16f + (tick / 10f)) % 6399][z+100];
                yPos *= transition;
                if (yPos > (12 + (Math.abs(transition) - 1) * 20)) continue;
                zPos = z * 3.5f;

                yPos += hillPreCalc[z + 100] * hillsHeight; //almost unused but I don't want to delete this
                yPos += ((chaosAnalysis[z + 100] * chaosAnalysis[uhhX % 199]) * 10 * chaos) / amplitudeAnalysis;

                g.tint(((dotColor ^ ((uhhX % 255) << floorShift1) ^ ((z + (int)(tick / 100f)) << floorShift2)) | 0xFF000000) - ((int)(((xPos - camX) / 700f) * 255f) << 24));
                g.vertex(xPos,        yPos,                                  zPos - 1.25f, 0, 0);
                g.vertex(xPos + 2.5f, yPos + (yPos > camY ? -1.25f : 1.25f), zPos        , 1, 1);
                g.vertex(xPos,        yPos,                                  zPos + 1.25f, 2, 0);
            }
        }

        g.tint((0xFFffffff - bgColor) | 0xFF000000);
        for (int i = 0; i < 200; i++) {
            yPosWave = camY + chaosAnalysis[i] * 20 / amplitudeAnalysis;
            g.vertex(camX + 201, yPosWave + 1, camZ + i - 100   , 1, 0);
            g.vertex(camX + 201, yPosWave    , camZ + i - 100.5f, 0, 1);
            g.vertex(camX + 201, yPosWave    , camZ + i - 99.5f , 2, 1);
        }
        g.endShape(TRIANGLES);
        // g.strokeWeight(2);
        // g.stroke(0xFFffff00);
        // g.hint(DISABLE_DEPTH_TEST);
        // g.stroke(0xFFff0000);
        // g.line(0, 0, 0, 32, 0, 0);
        // g.stroke(0xFF00ff00);
        // g.line(0, 0, 0, 0, 32, 0);
        // g.stroke(0xFF0000ff);
        // g.line(0, 0, 0, 0, 0, 32);
        // g.hint(ENABLE_DEPTH_TEST);
    }

    public static String snakeText = "Waiting for music";
    public static float snakeTextMove;
    public static int snakeTextFadeTimer = -1;

    private static void drawScreen2D() {
        PGraphics2D g = (PGraphics2D)viewport2D;
        g.clear();

        g.push();
        g.textFont(stdFont, 128);
        g.noStroke();
        g.textAlign(CENTER, CENTER);
        g.textSize(20);

        if (epilepsyWarningFade > 0) {
            g.fill(0x00000000 | epilepsyWarningFade << 24);
            g.rect(0,0,900,600);
            g.fill(0x00ff5555 | epilepsyWarningFade << 24);
            g.text("EPILEPSY WARNING:\nThis program WILL unintentionally trigger seizures in any person that has\nepilepsy or similar diseases/disabilities .\nIf any person that is watching your monitor has aforementioned problems\nEXIT THIS PROGRAN NOW!\n Otherwise press 'Y' on your keyboard to continue.", 450, 300);
            if (epilepsyWarningFade < 256) epilepsyWarningFade--;
        } else {
            g.colorMode(HSB, 360);
            snakeTextMove += 6;
            float temp = snakeTextFadeTimer != -361 ? Math.abs(snakeTextFadeTimer) : 360; //stoopid
            for (int i = 0; i < snakeText.length(); i++) {
                g.fill((snakeTextMove + 25 * i) % 360, 360, 360, temp);
                g.text(snakeText.charAt(i), (i * 15 + snakeTextMove) % 900, (100f + (i / 60f) * 20f) + 10 * (float)Math.sin(Math.toRadians(snakeTextMove + i * (360f / snakeText.length())))); 
            }
        }
        //iTut **almost** had a heart attack when he found out how the snake text is drawn
        g.pop();
    }

    public static PImage theRat; //The Rat.

    public static PFont stdFont;
    public static PGraphics viewport, viewport2D;
    public static PImage atlas;
    public static float camX = 0,
                        camY = 25,
                        camZ = 0,
                        camAngle = 0,
                        camPitch = 0,
                        hillsHeight = 0,
                        targetFramerate = 30,
                        currentFramerate = 0;

    public static boolean wKey, sKey, aKey, dKey, spaceKey, shiftKey, shiftChangeLock, cameraStop = false, cameraStopRotate = false, allowLookAround = true, 
                           epilepsyWarning = false;

    public static int epilepsyWarningContinueKey = 'Y', epilepsyWarningFade = 0x0000ffff;

    private static String cheatCode = "";

    public void settings() {
        size(1280, 720, P2D);
        PJOGL.setIcon(folder + "/data/textures/icon.png");
    }

    public void setup() {
        theRat = loadTexture("textures/the_rat.png");
        cheatWindow = new CheatWindow(sketch);

        ((PGraphicsOpenGL)g).textureSampling(2);

        //viewport = createGraphics(2560, 1440, P3D);
        viewport = createGraphics(900, 600, P3D);
        ((PGraphicsOpenGL)viewport).textureSampling(2);
        ((PGraphicsOpenGL)viewport).defCameraFOV = 45f;
        ((PGraphicsOpenGL)viewport).defCameraNear = 0.5f; //why this exist?

        viewport2D = createGraphics(900, 600, P2D);
        ((PGraphicsOpenGL)viewport2D).textureSampling(2);

        atlas = loadTexture("textures/atlas.png");

        Font tempFont = CheatWindow.stdFont.deriveFont(Font.BOLD); //Yoink!
        stdFont = new PFont(tempFont, false, PFont.CHARSET, true, 128);

        ticker.start();
    }

    public void draw() {
        if (currentFramerate != targetFramerate) {
            frameRate(targetFramerate);
            currentFramerate = targetFramerate;
        }

        if (epilepsyWarningFade == 1) CheatWindow.start(cheatWindow);

        viewport.beginDraw();
        //--hint bullshit--
        viewport.hint(DISABLE_OPENGL_ERRORS);
        viewport.hint(DISABLE_TEXTURE_MIPMAPS);
        viewport.hint(DISABLE_DEPTH_SORT);
        //viewport.hint(DISABLE_DEPTH_TEST);
        viewport.hint(DISABLE_STROKE_PURE);
        viewport.hint(DISABLE_STROKE_PERSPECTIVE);
        viewport.hint(DISABLE_BUFFER_READING);
        viewport.hint(DISABLE_KEY_REPEAT);
        drawScreen();
        viewport.endDraw();

        viewport2D.beginDraw();
        drawScreen2D();
        viewport2D.endDraw();

        image(viewport, 0, 0, width, height);
        image(viewport2D, 0, 0, width, height);

        cheatWindow.update();
    }

    public void keyPressed() {
        if ((char)keyCode == 'J' || (char)keyCode == 'A' || (char)keyCode == 'K') {
            cheatCode += (char)keyCode;
        } else cheatCode = "";
        if (cheatCode.contains("JAK")) {
            cheatCode = "";
            snakeText = "JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD! JAK IS DEAD!";
            snakeTextFadeTimer = 1000;
            bgColor = 0;
            dotColor = 0;
            floorShift1 = -8;
            floorShift2 = -8;
            if (soundFile != null) soundFile.stop();
            for (int i = 0; i < 200; i++) chaosAnalysis[i] = 9999;
        }

        if (!epilepsyWarning && keyCode == epilepsyWarningContinueKey) {
            epilepsyWarningFade = 255;
            epilepsyWarning = true;
        }

        switch (keyCode) {
            case 'W' -> {
                wKey = true;
            }
            case 'E' -> {
                viewport.save("screen " + System.currentTimeMillis() + ".png");
                snakeText = "Screenshot!";
                snakeTextFadeTimer = 700;
            }
            case 'R' -> {
                if (viewport.width == 900) {
                    viewport = createGraphics(2560, 1440, P3D);
                    snakeText = "High resolution on! (laggy)";
                    snakeTextFadeTimer = 700;
                } else {
                    viewport = createGraphics(900, 600, P3D);
                    snakeText = "High resolution off! (default)";
                    snakeTextFadeTimer = 700;
                }
                ((PGraphicsOpenGL)viewport).textureSampling(2);
                ((PGraphicsOpenGL)viewport).defCameraFOV = 45f;
                ((PGraphicsOpenGL)viewport).defCameraNear = 0.5f;
            }
            case 'S' -> {
                sKey = true;
            }
            case 'A' -> {
                aKey = true;
            }
            case 'D' -> {
                dKey = true;
            }
            case ' ' -> {
                spaceKey = true;
            }
            case SHIFT -> {
                shiftKey = true;
            }
        }
    }

    public void keyReleased() {
        switch (keyCode) {
            case 'W' -> {
                wKey = false;
            }
            case 'S' -> {
                sKey = false;
            }
            case 'A' -> {
                aKey = false;
            }
            case 'D' -> {
                dKey = false;
            }
            case ' ' -> {
                spaceKey = false;
            }
            case SHIFT -> {
                shiftKey = false;
            }
        }
    }

    private static PImage loadTexture(String path) {
        PImage image = sketch.loadImage(sketch.dataPath(path));
        if (image != null) return image;
        return theRat;
    }

    // private static void textDraw(PGraphics g, int x, int y, String text, int size, int color, int alignX, int alignY) {
    //     g.noStroke();
    //     g.textAlign(alignX, alignY);
    //     g.textSize(size);
    //     g.textLeading(25);
    //     g.fill(color);
    //     g.text(text, x, y); 
    // }
    //yPos = sinPreCalc[(int)(x + Ticker.tickCount / 100f) % 199] / (OpenSimplex2S.noise2(123, (uhhX + Ticker.tickCount / 100f) / 25f, z / 25f) * 20f);
}