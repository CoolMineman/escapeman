package io.github.coolmineman.escapeman;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class TitleScreen implements Screen {

    @Override
    public void keyCallback(long keywindow, int key, int scancode, int action, int mods) {
        //noop
    }

    @Override
    public void mouseCallback(long mousewindow, int button, int action, int mods) {
        if (button == GLFW_MOUSE_BUTTON_LEFT && action == GLFW_PRESS) {
            EscapeMan.INSTANCE.screen = new Level1Screen();
        }
    }

    @Override
    public void render() {
        glBindTexture(GL_TEXTURE_2D, EscapeMan.INSTANCE.deepcave);

        glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(-1f, 1f);
            glTexCoord2f(1, 0);
            glVertex2f(1f, 1f);
            glTexCoord2f(1, 1);
            glVertex2f(1f, -1f);
            glTexCoord2f(0, 1);
            glVertex2f(-1f, -1f);
        glEnd();

        glBindTexture(GL_TEXTURE_2D, EscapeMan.INSTANCE.titleOverlay);

        glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(-1f, 1f);
            glTexCoord2f(1, 0);
            glVertex2f(1f, 1f);
            glTexCoord2f(1, 1);
            glVertex2f(1f, -1f);
            glTexCoord2f(0, 1);
            glVertex2f(-1f, -1f);
        glEnd();
    }
    
}
