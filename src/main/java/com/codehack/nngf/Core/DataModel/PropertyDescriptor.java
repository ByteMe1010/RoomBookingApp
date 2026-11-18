package com.codehack.nngf.Core.DataModel;

import java.util.function.Consumer;

public class PropertyDescriptor<T> {
    public final String name;
    public final String label;
    public final PropertyType type;
    public final String group;
    public final int level;
    public T value;
    public final Consumer<T> setter;
    public final String[] options;
    public final boolean editable;

    private PropertyDescriptor(String name, String label, PropertyType type, T value, Consumer<T> setter, String group, int level, String[] options, boolean editable) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.value = value;
        this.setter = setter;
        this.group = group;
        this.level = level;
        this.options = options;
        this.editable = editable;
    }

    public static <T> PropertyDescriptor<T> of(String name, String label, PropertyType type, T value, Consumer<T> setter, String group, int level) {
        return new PropertyDescriptor<>(name, label, type, value, setter, group, level, null, true);
    }

    public static PropertyDescriptor<String> text(String name, String label, String value, Consumer<String> setter, String group, int level) {
        return new PropertyDescriptor<>(name, label, PropertyType.TEXT, value, setter, group, level, null, true);
    }

    public static PropertyDescriptor<Boolean> bool(String name, String label, boolean value, Consumer<Boolean> setter, String group, int level) {
        return new PropertyDescriptor<>(name, label, PropertyType.BOOLEAN, value, setter, group, level, null, true);
    }

    public static PropertyDescriptor<String> number(String name, String label, String value, Consumer<String> setter, String group, int level) {
        return new PropertyDescriptor<>(name, label, PropertyType.NUMBER, value, setter, group, level, null, true);
    }

    public static PropertyDescriptor<String> color(String name, String label, String value, Consumer<String> setter, String group, int level) {
        return new PropertyDescriptor<>(name, label, PropertyType.COLOR, value, setter, group, level, null, true);
    }

    public static PropertyDescriptor<String> select(String name, String label, String value, Consumer<String> setter, String group, int level, String... options) {
        return new PropertyDescriptor<>(name, label, PropertyType.SELECT, value, setter, group, level, options, true);
    }

    public static PropertyDescriptor<String> readonly(String name, String label, String value) {
        return new PropertyDescriptor<>(name, label, PropertyType.READONLY, value, null, "Base", 0, null, false);
    }

    public void setValue(Object val) {
        @SuppressWarnings("unchecked")
        T casted = (T) val;
        this.value = casted;
        if (setter != null && editable) {
            setter.accept(casted);
        }
    }
}
