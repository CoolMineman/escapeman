package io.github.coolmineman.escapeman;

public class Player {
    public static final float GRAVITY = -0.05f;

    public final Level level;
    public float x = 0;
    public float y = 3;
    public float velocityx = 0;
    public float velocityy = 0;

    public Player(Level level) {
        this.level = level;
    }

    public void tick() {
        if (velocityy > GRAVITY) velocityy += GRAVITY;

        float targetminx = x + velocityx;
        float targetmaxx = targetminx + 0.5f;
        float targetminy = y + velocityy;
        float targetmaxy = targetminy + 1f;

        int minx = (int)targetminx;
        int maxx = (int)targetmaxx;
        int miny = (int)targetminy;
        int maxy = (int)targetmaxy;

        for (int i = minx; i <= maxx; i++) {
            for (int j = miny; j <= maxy; j++) {
                int tile = level.getTile(i, j);
                boolean isSpikes = tile == EscapeMan.INSTANCE.spikes;
                if (tile == 0) continue;
                float tileminx = i;
                float tilemaxx = i + 1f;
                float tileminy = j;
                float tilemaxy = j + (isSpikes ? 0.3f : 1f);
                boolean collidesY = targetmaxy - 0.1 > tileminy && targetminy + 0.1 < tilemaxy;
                boolean collidesX = targetmaxx - 0.1 > tileminx && targetminx + 0.1 < tilemaxx;

                if (targetminx < tilemaxx && targetmaxx > tileminx && collidesY) {
                    velocityx = 0f;
                    if (isSpikes) {
                        die();
                        return;
                    }
                }
                if (targetmaxy > tileminy && targetminy < tilemaxy && collidesX) {
                    velocityy = 0f;
                    if (isSpikes) {
                        die();
                        return;
                    }
                }
            }
        }

        x += velocityx;
        y += velocityy;

        velocityx *= 0.9f;
        velocityy *= 0.9f;

        if (y < -5) die();
    }

    public void die() {
        x = 0;
        y = 3;
        velocityx = 0;
        velocityy = 0;
    }
}
