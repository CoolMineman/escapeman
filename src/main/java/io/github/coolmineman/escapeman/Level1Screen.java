package io.github.coolmineman.escapeman;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWVidMode;

public class Level1Screen implements Screen {
    Level level = new Level("deepcave.txt");

    @Override
    public void keyCallback(long keywindow, int key, int scancode, int action, int mods) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseCallback(long mousewindow, int button, int action, int mods) {
        // TODO Auto-generated method stub
        
    }

    void drawTile(int x, int y) {
        glBindTexture(GL_TEXTURE_2D, EscapeMan.INSTANCE.tile1);

        glTranslatef(x * 0.2f, y * 0.2f, 0);
        glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(-0.1f, 0.1f);
            glTexCoord2f(1, 0);
            glVertex2f(0.1f, 0.1f);
            glTexCoord2f(1, 1);
            glVertex2f(0.1f, -0.1f);
            glTexCoord2f(0, 1);
            glVertex2f(-0.1f, -0.1f);
        glEnd();
        glTranslatef(x * -0.2f, y * -0.2f, 0);
    }

    @Override
    public void render() {
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        float maxSide = Math.max(vidmode.width(), vidmode.height());
        glPushMatrix();
        glScalef(maxSide / vidmode.width(), maxSide / vidmode.height(), 1);
        for (int i = 0; i < level.world.length; i++) {
            for (int j = 0; j < level.world[i].length; j++) {
                if (level.world[i][j] != 0) {
                    drawTile(j, level.world.length - i);
                }
            }
        }
        glPopMatrix();
    }
    
}
