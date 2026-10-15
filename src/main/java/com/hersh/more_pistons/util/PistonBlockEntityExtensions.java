package com.hersh.more_pistons.util;

public interface PistonBlockEntityExtensions {
    default boolean hasSourceArm() {
        return false;
    }

    default void setSourceArm(boolean value) {
    }
}