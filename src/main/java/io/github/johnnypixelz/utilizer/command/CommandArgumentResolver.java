package io.github.johnnypixelz.utilizer.command;

public interface CommandArgumentResolver<String, T> {

    T resolve(String argument);

}
