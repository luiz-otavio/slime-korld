/*
 * MIT License
 *
 * Copyright (c) [2022] [LUIZ O. F. CORRÊA]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.luizotavio.minecraft.common.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Property to be used as world settings.
 * Anyone can make your own property, but it must implement this abstract class.
 *
 * @author Luiz Otávio de Farias Corrêa
 * @since 11/08/2022
 */
public abstract class SettingsProperty<T> implements Cloneable {

    /**
     * Create a string property.
     * @param name The name of the property.
     * @param defaultValue The default value of the property.
     * @param value The value of the property.
     * @return The property.
     */
    public static SettingsProperty<String> createStringProperty(String name, String defaultValue, @Nullable String value) {
        return new StringProperty(name, value == null ? defaultValue : value, defaultValue);
    }

    /**
     * Create an enum property.
     * @param name The name of the property.
     * @param defaultValue The default value of the property.
     * @param value The value of the property.
     * @param clazz The class of the enum.
     * @return The property.
     * @param <T> The type of the enum.
     */
    public static <T extends Enum<T>> SettingsProperty<T> createEnumProperty(String name, T defaultValue, @Nullable T value, @NotNull Class<T> clazz) {
        return new EnumProperty<>(name, value == null ? defaultValue : value, defaultValue, clazz);
    }

    /**
     * Create an array based property.
     * @param name The name of the property.
     * @param defaultValue The default value of the property.
     * @param value The value of the property.
     * @param clazz The class of the array.
     * @return The property.
     * @param <T> The type of the array.
     */
    public static <T> SettingsProperty<T[]> createArrayProperty(String name, T[] defaultValue, @Nullable T[] value, @NotNull Class<T> clazz) {
        return new ArrayProperty<>(name, value == null ? defaultValue : value, defaultValue, clazz);
    }

    /**
     * Create an integer property.
     * @param name The name of the property.
     * @param defaultValue The default value of the property.
     * @param value The value of the property.
     * @return The property.
     */
    public static SettingsProperty<Integer> createIntegerProperty(String name, int defaultValue, int value) {
        return new IntegerProperty(name, value == 0 ? defaultValue : value, defaultValue);
    }

    /**
     * Create a boolean property.
     * @param name The name of the property.
     * @param defaultValue The default value of the property.
     * @param value The value of the property.
     * @return The property.
     */
    public static SettingsProperty<Boolean> createBooleanProperty(String name, boolean defaultValue, boolean value) {
        return new BooleanProperty(name, value, defaultValue);
    }

    /**
     * Retrieve the name of the property.
     * @return The name of the property.
     */
    @NotNull
    public abstract String getName();

    /**
     * Retrieve the current value of the property.
     * @return The current value of the property.
     */
    @NotNull
    public abstract T getValue();

    /**
     * Retrieve the default value of the property.
     * @return The default value of the property.
     */
    @NotNull
    public abstract T getDefaultValue();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SettingsProperty) {
            SettingsProperty<?> property = (SettingsProperty<?>) obj;
            return getName().equals(property.getName()) && getValue().equals(property.getValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getName().hashCode() + getValue().hashCode();
    }

    @Override
    public SettingsProperty<T> clone() {
        try {
            return (SettingsProperty) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public static class BooleanProperty extends SettingsProperty<Boolean> {

        private final String name;
        private final Boolean value;
        private final Boolean defaultValue;

        public BooleanProperty(String name, Boolean value, Boolean defaultValue) {
            this.name = name;
            this.value = value;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull Boolean getValue() {
            return value;
        }

        @Override
        public Boolean getDefaultValue() {
            return defaultValue;
        }

    }

    public static class StringProperty extends SettingsProperty<String> {

        private final String name;
        private final String value;
        private final String defaultValue;

        public StringProperty(String name, String value, String defaultValue) {
            this.name = name;
            this.value = value;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull String getValue() {
            return value;
        }

        @Override
        public String getDefaultValue() {
            return defaultValue;
        }

    }

    public static class IntegerProperty extends SettingsProperty<Integer> {

        private final String name;
        private final Integer value;
        private final Integer defaultValue;

        public IntegerProperty(String name, Integer value, Integer defaultValue) {
            this.name = name;
            this.value = value;
            this.defaultValue = defaultValue;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull Integer getValue() {
            return value;
        }

        @Override
        public Integer getDefaultValue() {
            return defaultValue;
        }
    }

    public static class EnumProperty<E extends Enum<E>> extends SettingsProperty<E> {

        private final String name;
        private final E value;
        private final E defaultValue;
        private final Class<E> enumClass;

        public EnumProperty(String name, E value, E defaultValue, Class<E> enumClass) {
            this.name = name;
            this.value = value;
            this.defaultValue = defaultValue;
            this.enumClass = enumClass;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull E getValue() {
            return value;
        }

        @Override
        public E getDefaultValue() {
            return defaultValue;
        }

        public Class<E> getEnumClass() {
            return enumClass;
        }
    }

    public static class ArrayProperty<T> extends SettingsProperty<T[]> {

        private final String name;
        private final T[] value;
        private final T[] defaultValue;
        private final Class<T> arrayClass;

        public ArrayProperty(String name, T[] value, T[] defaultValue, Class<T> arrayClass) {
            this.name = name;
            this.value = value;
            this.defaultValue = defaultValue;
            this.arrayClass = arrayClass;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public T @NotNull [] getValue() {
            return value;
        }

        @Override
        public T[] getDefaultValue() {
            return defaultValue;
        }

        public Class<T> getArrayClass() {
            return arrayClass;
        }
    }

}
