package com.github.fernthedev.light;

import org.apache.commons.lang3.EnumUtils;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class ReflectionUtil {

    public static final Class<?>[] validClassTypes = {
            Short.class, short.class,
            Float.class, float.class,
            Integer.class, int.class,
            Long.class, long.class,
            Double.class, double.class,
            Boolean.class, boolean.class,
            Enum.class,
            String.class,
    };

    public static Object parseObject(Class<?> type, @Nullable Class<?>[] validClasses, String arg) {
        if (validClasses == null || validClasses.length == 0) {

            if (type.isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) type;

                Object o = searchEnum(enumClass, arg);
                if(o != null) {
                    return o;
                } else {
                    throw new IllegalArgumentException("The argument " + arg + " is not valid for enum " + enumClass.getTypeName());
                }

            } else if (boolean.class.equals(type)) {
                return Boolean.parseBoolean(arg);
            } else if (int.class.equals(type)) {
                return Integer.parseInt(arg);
            } else if (long.class.equals(type)) {
                return Long.parseLong(arg);
            } else if (double.class.equals(type)) {
                return Double.parseDouble(arg);
            } else if (short.class.equals(type)) {
                return Short.parseShort(arg);
            } else if (String.class.equals(type)) {
                return arg;
            } else if (type.isEnum()) {
                return searchEnum((Class<Enum>) type, arg);
            }
        } else {

            NumberFormat nf = NumberFormat.getInstance();
            boolean isNumber = false;
            try {
                nf.parse(arg);
                isNumber = true;
            } catch (ParseException e) {}



            for (Class<?> aClass : validClasses) {

                if (aClass.isEnum()) {
                    Class<? extends Enum> enumClass = (Class<? extends Enum>) aClass;
                    if(EnumUtils.isValidEnum(enumClass, arg)) {
                        return searchEnum(enumClass, arg);
                    }
                }

                if(isNumber) {
                    if (int.class.equals(aClass)) {
                        return Integer.parseInt(arg);
                    } else if (long.class.equals(aClass)) {
                        return Long.parseLong(arg);
                    } else if (double.class.equals(aClass)) {
                        return Double.parseDouble(arg);
                    } else if (short.class.equals(aClass)) {
                        return Short.parseShort(arg);
                    }
                }

                if ((arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false")) && boolean.class.equals(aClass)) {
                    return Boolean.parseBoolean(arg);
                }



                if(String.class.equals(aClass)) {
                    return arg;
                }
            }

            throw new IllegalArgumentException("The type could not be recognized: IsArgNumber: " + isNumber + " Classes Checked: " + Arrays.toString(validClasses) + " Value: " + arg);
        }

        throw new IllegalArgumentException("The type could not be recognized: " + type.getTypeName());
    }

    /**
     * Checks if the argument provided is valid
     * @param classes
     * @param arg
     * @return
     */
    public static boolean isValid(List<Class<?>> classes, Object arg) {

        for (Class<?> aClass : classes) {
            if(aClass.isInstance(arg)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the argument provided is valid
     * @param arg
     * @return
     */
    public static boolean isValid(Object arg) {
        return isValid(Arrays.asList(validClassTypes), arg);
    }

    /**
     * Checks if the argument provided is valid
     * @param classes
     * @param arg
     * @return
     */
    public static boolean isValid(List<Class<?>> classes, Class<?> arg) {

        for (Class<?> aClass : classes) {
            if(aClass.equals(arg) || aClass.isAssignableFrom(arg)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the argument provided is valid
     * @param arg
     * @return
     */
    public static boolean isValid(Class<?> arg) {
        return isValid(Arrays.asList(validClassTypes), arg);
    }

    public static <T extends Enum<?>> T searchEnum(Class<T> enumeration,
                                                   String search) {
        for (T each : enumeration.getEnumConstants()) {
            if (each.name().equalsIgnoreCase(search)) {
                return each;
            }
        }
        return null;
    }
}
