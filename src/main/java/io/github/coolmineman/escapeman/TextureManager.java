package io.github.coolmineman.escapeman;

import java.io.InputStream;
import java.nio.*;

import org.lwjgl.system.*;
import org.pmw.tinylog.Logger;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TextureManager {
    private TextureManager() { }

    //Loads texture from the classpath
    public static int createTexture(String name) {
        try { 
            try (InputStream is = TextureManager.class.getClassLoader().getResourceAsStream(name)) {
                ByteBuffer buf = ByteBuffer.allocateDirect(is.available());
                while (is.available() > 0) {
                    buf.put((byte) is.read());
                }
                buf.rewind();
                return createTexture(buf, name);
            }
        } catch (Exception e) {
            throw Sneak.sneakyThrow(e);
        }
    }

    //Loads texture from png ByteBuffer to an OpenGL texture
    //Based on https://github.com/LWJGL/lwjgl3/blob/e4a6cc863f469ea8acfe3c2158f2c77d0c0aa95d/modules/samples/src/test/java/org/lwjgl/demo/stb/Image.java
    //Why must this be so complicated?
    public static int createTexture(ByteBuffer buf, String name) {
        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            if (!stbi_info_from_memory(buf, w, h, comp)) {
                throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
            } else {
                Logger.debug("Loaded Texture " + name + ": " + stbi_failure_reason());
            }

            Logger.debug("Image width: " + w.get(0));
            Logger.debug("Image height: " + h.get(0));
            Logger.debug("Image components: " + comp.get(0));
            Logger.debug("Image HDR: " + stbi_is_hdr_from_memory(buf));

            // Decode the image
            ByteBuffer image = stbi_load_from_memory(buf, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            int jw = w.get(0);
            int jh = h.get(0);
            int jcomp = comp.get(0);

            int texID = glGenTextures();

            glBindTexture(GL_TEXTURE_2D, texID);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

            int format;
            if (jcomp == 3) {
                if ((jw & 3) != 0) {
                    glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (jw & 1));
                }
                format = GL_RGB;
            } else {
                int stride = jw * 4;
                for (int y = 0; y < jh; y++) {
                    for (int x = 0; x < jw; x++) {
                        int i = y * stride + x * 4;

                        float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                        image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
                        image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
                        image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
                    }
                }

                glEnable(GL_BLEND);
                glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

                format = GL_RGBA;
            }

            glTexImage2D(GL_TEXTURE_2D, 0, format, jw, jh, 0, format, GL_UNSIGNED_BYTE, image);

            ByteBuffer input_pixels = image;
            int        input_w      = jw;
            int        input_h      = jh;
            int        mipmapLevel  = 0;
            while (1 < input_w || 1 < input_h) {
                int output_w = Math.max(1, input_w >> 1);
                int output_h = Math.max(1, input_h >> 1);

                ByteBuffer output_pixels = memAlloc(output_w * output_h * jcomp);
                stbir_resize_uint8_generic(
                    input_pixels, input_w, input_h, input_w * jcomp,
                    output_pixels, output_w, output_h, output_w * jcomp,
                    jcomp, jcomp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                    STBIR_EDGE_CLAMP,
                    STBIR_FILTER_MITCHELL,
                    STBIR_COLORSPACE_SRGB
                );

                if (mipmapLevel == 0) {
                    stbi_image_free(image);
                } else {
                    memFree(input_pixels);
                }

                glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

                input_pixels = output_pixels;
                input_w = output_w;
                input_h = output_h;
            }
            if (mipmapLevel == 0) {
                stbi_image_free(image);
            } else {
                memFree(input_pixels);
            }

            return texID;
        }
    }
}
