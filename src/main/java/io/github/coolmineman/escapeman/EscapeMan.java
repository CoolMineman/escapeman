package io.github.coolmineman.escapeman;

import java.nio.IntBuffer;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.APIUtil;
import org.lwjgl.system.MemoryStack;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Logger;

public class EscapeMan {
    private long window;

    public int deepcave;
    public int titleOverlay;

    public void run() {
        Logger.info("Starting EscapeMan");
        Logger.info("LWGL Version " + Version.getVersion());
        init();
        gameLoop();
    }

    @SuppressWarnings("all")
    private void init() {
        //Setup GLFW (The window creation library) Errors to Get Logged
        //Based on GLFWErrorCallback.createPrint
        GLFWErrorCallback.create(
            new GLFWErrorCallback() {
                private Map<Integer, String> ERROR_CODES = APIUtil.apiClassTokens((field, value) -> 0x10000 < value && value < 0x20000, null, GLFW.class);

                @Override
                public void invoke(int error, long description) {
                    String msg = getDescription(description);

                    Logger.error("[LWJGL] " + ERROR_CODES.get(error) + " error");
                    Logger.error("\tDescription: " + msg);
                    Logger.error("\tStacktrace:");
                    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                    for ( int i = 4; i < stack.length; i++ ) {
                        Logger.error("\t\t" + stack[i].toString());
                    }
                }
            }
        ).set();
        
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(1600, 900, "EscapeMan", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, this::keyCallback);

        try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(
				window,
				(vidmode.width() - pWidth.get(0)) / 2,
				(vidmode.height() - pHeight.get(0)) / 2
			);
		}

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); //v-sync
        glfwShowWindow(window);

        GL.createCapabilities();

        glEnable(GL_TEXTURE_2D);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f); //black

        deepcave = TextureManager.createTexture("deepcave.png");
        titleOverlay = TextureManager.createTexture("title_overlay.png");
    }

    private void gameLoop() {
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glBindTexture(GL_TEXTURE_2D, deepcave);

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

            glBindTexture(GL_TEXTURE_2D, titleOverlay);

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

			glfwSwapBuffers(window);

			glfwPollEvents(); // Polls input events and call the callbacks
		}
    }

    public void keyCallback(long keywindow, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
    }

    public static void main(String[] args) {
        Configurator.defaultConfig().writingThread(true).formatPattern("{date} [{thread}] {level}: {message}").activate();
        (new EscapeMan()).run();
    }
}
