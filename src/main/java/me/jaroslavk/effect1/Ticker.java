package me.jaroslavk.effect1;
import static me.jaroslavk.effect1.Effect1.*;

public class Ticker extends Thread {
    public static int tickCount;
    static long tickLast;
    static boolean paused;

    public synchronized void run() {
        tickLast = System.nanoTime();
        while (true) {
            while (System.nanoTime() - tickLast < 10000000)
            try {
                Thread.sleep((System.nanoTime() - tickLast) / 1000000, (int)((System.nanoTime() - tickLast) % 1000000));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            tickLast += 10000000;
            if (paused)
                continue;
            tick();
            tickCount++;
        }
    }
    
    public int caveState = 0; // 1 in cave, 0 out of cave
    public boolean caveStateFinished = false, autoTransition = true;
    private boolean offbeat = false;

    private void tick() {
        if (!epilepsyWarning) return;

        if (epilepsyWarningFade == 0 && (snakeTextFadeTimer > 0 || snakeTextFadeTimer > -360) && snakeTextFadeTimer != -361 && snakeTextFadeTimer != 0) snakeTextFadeTimer--;

        if (snakeTextFadeTimer <= 320 && soundFile != null && soundFile.position() == 0 && !soundFile.isPlaying()) {
            snakeText = "Waiting for music";
            if ((snakeTextFadeTimer != -1 && snakeTextFadeTimer > -1) || snakeTextFadeTimer == -361)
            snakeTextFadeTimer = -1;
        }

        if (allowLookAround) {
            if (wKey) camPitch -= 1;
            if (sKey) camPitch += 1;
            if (aKey) camAngle += 1;
            if (dKey) camAngle -= 1;
        }

        if (!cameraStopRotate) {
            camAngle = (float)Math.sin(tickCount / 200f) * 6;
            camPitch = (float)Math.sin(tickCount / 300f) * 6;
        }
        if (!cameraStop && !spaceKey && !shiftKey) {
            camX += 0.8 * cheatWindow.soundSpeed;
            camY = (float)Math.sin(tickCount / 700f) * 9 + 25 - ((transition - 1) * 1.5f) + hillPreCalc[100] * hillsHeight;
            //camZ += (float)Math.sin(Math.toRadians(camAngle)) * 0.8 * cheatWindow.soundSpeed;
            camX = camX % 90000;
        }

        // if (chaos > 0) chaos -= 3.0f;
        if (spaceKey) {
            // camX += (float)Math.cos(Math.toRadians(camAngle)) * 2;
            // camY += (float)Math.tan(Math.toRadians(camPitch)) * 2;
            // camZ += (float)Math.sin(Math.toRadians(camAngle)) * 2;
            camY += 3;
        }
        if (shiftKey) {
            // camX -= (float)Math.cos(Math.toRadians(camAngle)) * 2;
            // camY -= (float)Math.tan(Math.toRadians(camPitch)) * 2;
            // camZ -= (float)Math.sin(Math.toRadians(camAngle)) * 2;
            camY -= 3;
        }

        if (autoTransition && tickCount % 4000 == 0) {
            caveState = caveState == 0 ? 1 : 0;
            // multiplier = caveState == 0 ? 19 : 1;
            caveStateFinished = false;
        }

        if (autoTransition && !caveStateFinished)
            if (caveState == 1) {
                transition += 0.03;
                caveStateFinished = transition > 19f;
            } else {
                transition -= 0.03;
                caveStateFinished = transition < 1.03f;
            }

        if (!shiftChangeLock && detector.isBeat()) {
            if (offbeat) {
                floorShift1 += (int)Math.round((Math.random() - 0.5) * 6);
                if (floorShift1 > 32 || floorShift1 < -32) floorShift1 = 1;
            } else {
                floorShift2 += (int)Math.round((Math.random() - 0.5) * 6);
                if (floorShift2 > 32 || floorShift2 < -32) floorShift2 = 1;
            }
            offbeat = !offbeat;
        }
    }
}