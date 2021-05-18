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
    public static final EscapeMan INSTANCE = new EscapeMan();

    private long window;

    public int deepcave;
    public int titleOverlay;
    public int tile1;
    public Screen screen = new TitleScreen();

    public void run() {
        Logger.info("Starting EscapeMan");
        Logger.info("LWJGL Version " + Version.getVersion());
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
        glfwSetMouseButtonCallback(window, this::mouseCallback);

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
        tile1 = TextureManager.createTexture("tile1.png");
    }

    private void gameLoop() {
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (screen != null) {
                screen.render();
            }

			glfwSwapBuffers(window);

			glfwPollEvents(); // Polls input events and call the callbacks
		}
    }

    public void keyCallback(long keywindow, int key, int scancode, int action, int mods) {
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true);
        }
        if (screen != null) {
            screen.keyCallback(keywindow, key, scancode, action, mods);
        }
    }

    public void mouseCallback(long mousewindow, int button, int action, int mods) {
        if (screen != null) {
            screen.mouseCallback(mousewindow, button, action, mods);
        }
    }

    public static void main(String[] args) {
        Configurator.defaultConfig().writingThread(true).formatPattern("{date} [{thread}] {level}: {message}").activate();
        INSTANCE.run();
    }
}
