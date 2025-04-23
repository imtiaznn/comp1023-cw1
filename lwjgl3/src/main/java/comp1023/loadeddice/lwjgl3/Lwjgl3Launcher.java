package comp1023.loadeddice.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import comp1023.loadeddice.MainGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Loaded Dice");
        config.setWindowedMode(1850, 1250);
        config.setResizable(false);
        config.useVsync(true);
        
        new Lwjgl3Application(new MainGame(), config);
    }
}