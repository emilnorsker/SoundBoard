package norsker.keyboard;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.ArrayList;

public class KeyListener implements NativeKeyListener {
    public static ArrayList<Integer> currentKeysPressed = new ArrayList<Integer>();


    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent)
    {
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent)
    {
        boolean isBeingPressed = false;
        int key = nativeKeyEvent.getKeyCode();

        if (!currentKeysPressed.contains(key))
            currentKeysPressed.add(key);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent)
    {
        boolean isBeingPressed = false;
        int key = nativeKeyEvent.getKeyCode();

        if (currentKeysPressed.contains(key))
            currentKeysPressed.remove(key);
    }
}
