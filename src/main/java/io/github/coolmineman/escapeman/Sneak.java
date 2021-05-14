package io.github.coolmineman.escapeman;

public class Sneak {
    private Sneak() { }

    @SuppressWarnings("all")
    public static <T extends Throwable> RuntimeException sneakyThrow(Throwable t) throws T {
        throw (T)t;
    }
}
