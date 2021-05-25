package io.github.coolmineman.escapeman;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Level {
    public int[][] world = new int[16][6];

    public Level(String name) {
        try {
            try (
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        Level.class.getClassLoader().getResourceAsStream(name)
                    )
                )
            ) {
                for (int i = 0; reader.ready(); i++) {
                    String line = reader.readLine();
                    for (int j = 0; j < line.length(); j++) {
                        char c = line.charAt(j);
                        if (c == 'x') {
                            world[j][world[j].length - 1 - i] = EscapeMan.INSTANCE.tile1;
                        } else if (c == 's') {
                            world[j][world[j].length - 1 - i] = EscapeMan.INSTANCE.spikes;
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            throw Sneak.sneakyThrow(e);
        }
    }

    public int getTile(int x, int y) {
        return (x < 0 || x > world.length - 1 || y < 0 || y > world[x].length - 1) ? 0 : world[x][y];
    }
}
