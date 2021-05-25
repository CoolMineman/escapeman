package io.github.coolmineman.escapeman;

public interface Screen {
    void keyCallback(long keywindow, int key, int scancode, int action, int mods);
    void mouseCallback(long mousewindow, int button, int action, int mods);
    void render();
    default void tick() { }
}
