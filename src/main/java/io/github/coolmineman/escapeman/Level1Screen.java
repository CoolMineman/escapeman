package io.github.coolmineman.escapeman;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWVidMode;

public class Level1Screen implements Screen {
    Level level = new Level("deepcave.txt");
    Player player = new Player(level);
    boolean left;
    boolean right;
    boolean up;

    @Override
    public void keyCallback(long keywindow, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_LEFT) {
            left = action == GLFW_PRESS || action == GLFW_REPEAT;
        } else if (key == GLFW_KEY_RIGHT) {
            right = action == GLFW_PRESS || action == GLFW_REPEAT;
        } else if (key == GLFW_KEY_UP) {
            up = action == GLFW_PRESS || action == GLFW_REPEAT;
        }
    }

    @Override
    public void tick() {
        if (left && !right) {
            player.velocityx = Math.min(player.velocityx, -0.05f);
        }
        if (right && !left) {
            player.velocityx = Math.max(player.velocityx, 0.05f);
        }
        if (player.velocityy == 0f && up) player.velocityy = 0.7f;
        player.tick();
    }

    @Override
    public void mouseCallback(long mousewindow, int button, int action, int mods) {
        //noop
    }

    void drawTile(int x, int y, int tile) {
        glBindTexture(GL_TEXTURE_2D, tile);

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

    void drawBackground() {
        glBindTexture(GL_TEXTURE_2D, EscapeMan.INSTANCE.deepcave);

        glPushMatrix();
        float scroll = (player.x / 5f) % 2;
        glTranslatef(-scroll, 0, 0);
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
        glTranslatef(2, 0, 0);
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
        glPopMatrix();
    }

    void drawPlayer() {
        glBindTexture(GL_TEXTURE_2D, EscapeMan.INSTANCE.player);

        glTranslatef(-0.025f, player.y * 0.2f, 0);
        glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2f(-0.05f, 0.1f);
            glTexCoord2f(1, 0);
            glVertex2f(0.05f, 0.1f);
            glTexCoord2f(1, 1);
            glVertex2f(0.05f, -0.1f);
            glTexCoord2f(0, 1);
            glVertex2f(-0.05f, -0.1f);
        glEnd();
        glTranslatef(0.025f, player.y * -0.2f, 0);
    }

    @Override
    public void render() {
        drawBackground();
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        float maxSide = Math.max(vidmode.width(), vidmode.height());
        glPushMatrix();
        glScalef(maxSide / vidmode.width(), maxSide / vidmode.height(), 1);
        glTranslatef(0, -0.5f, 0);
        drawPlayer();
        glTranslatef(-player.x * 0.2f, 0, 0);
        for (int i = 0; i < level.world.length; i++) {
            for (int j = 0; j < level.world[i].length; j++) {
                if (level.world[i][j] != 0) {
                    drawTile(i, j, level.world[i][j]);
                }
            }
        }
        glPopMatrix();
    }
    
}
