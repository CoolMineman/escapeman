package io.github.coolmineman.escapeman;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Level {
    public boolean[][] world = new boolean[16][16];

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
                            world[i][j] = true;
                        }
                    }
                }
                
            }
        } catch (Exception e) {
            throw Sneak.sneakyThrow(e);
        }
    }
}
