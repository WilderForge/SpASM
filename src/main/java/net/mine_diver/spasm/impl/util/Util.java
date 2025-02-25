package net.mine_diver.spasm.impl.util;

import org.jetbrains.annotations.Contract;

import java.util.function.Consumer;

public final class Util {
    @Contract("_, _ -> param1")
    public static <T> T make(final T instance, final Consumer<T> initializer) {
        initializer.accept(instance);
        return instance;
    }
}
