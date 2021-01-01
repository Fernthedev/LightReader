package com.github.fernthedev.light.api;


import com.github.fernthedev.fernutils.reflection.ReflectionUtil;
import com.github.fernthedev.light.LightManager;
import com.github.fernthedev.light.api.annotations.LineArgument;
import com.github.fernthedev.light.api.annotations.LineData;
import com.github.fernthedev.light.api.annotations.LineUnparsedArguments;
import com.github.fernthedev.light.api.lines.*;
import com.github.fernthedev.light.exceptions.FileIsFolderException;
import com.github.fernthedev.light.exceptions.LightCommentNoEndException;
import com.github.fernthedev.light.exceptions.LightFileParseException;
import lombok.NonNull;
import okio.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class LightParser {

    private static final List<ILightLine> registeredLightLines = new ArrayList<>();

    static {
        registerLightLine(new LightPinLine(NullObject.NULL_OBJECT));
        registerLightLine(new LightSleepLine(NullObject.NULL_OBJECT));
        registerLightLine(new LightPrintLine(NullObject.NULL_OBJECT));
        registerLightLine(new LightAnimationLine(NullObject.NULL_OBJECT));
    }

    private LightParser() {}

    public static void validateField(Field field) {
        if (!field.isAnnotationPresent(LineArgument.class) && field.isAnnotationPresent(LineUnparsedArguments.class)) throw new IllegalArgumentException("The LineUnparsedArguments field must contain a LineArgument annotation");

        LineArgument lineArgument = field.getAnnotation(LineArgument.class);


        if(!field.isAnnotationPresent(LineUnparsedArguments.class) && lineArgument.classTypes().length > 0) {
            for (Class<?> aClass : lineArgument.classTypes()) {
                if(!aClass.isPrimitive() && !aClass.isAssignableFrom(String.class) && !aClass.isEnum()) {

                    if(field.isAnnotationPresent(LineUnparsedArguments.class)) {
                        LightManager.getLogger().warn("classTypes() value of annotation in field {} is unnecessary and will be ignored during parsing.", field);
                    } else
                        throw new IllegalArgumentException("LineArgument Class types are not primitive type or string which is not supported. Field: " + field + " ClassType: " + aClass + " type: " + aClass.getTypeName());

                }
            }
        }

        if (!field.isAnnotationPresent(LineUnparsedArguments.class) && !field.getType().isPrimitive()
                && !field.getType().isAssignableFrom(String.class)
                && !field.getType().isEnum()) throw new IllegalArgumentException("LineArgument Field type is not primitive type or string which is not supported. " +
                "Field: " + field.getName() + " type: " + field.getType().getTypeName() +
                "\nIf you want to support multiple object types, use the classTypes() {} function in annotation to define what class types are legal. They must follow the same class types allowed as a field type.");

    }

    public static void registerLightLine(ILightLine iLightLine) {
        if(!iLightLine.getClass().isAnnotationPresent(LineData.class)) throw new IllegalArgumentException("Class does not have LineData annotation set.");

        boolean existingLineRestArgument = false;

        for (Field field : iLightLine.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(LineArgument.class)) continue;

            validateField(field);

            if (field.isAnnotationPresent(LineUnparsedArguments.class)) {
                if (!field.getType().isAssignableFrom(String[].class)) {
                    throw new IllegalArgumentException("The field type must be String[] if it contains LineUnparsedArguments");
                }

                if(existingLineRestArgument) throw new IllegalArgumentException("There can only be one field with LineUnparsedArguments annotation");

                existingLineRestArgument = true;
            }




        }

        registeredLightLines.add(iLightLine);
    }

    public static void saveFile(@NonNull LightFile lightFile) throws IOException {
        File file = new File(lightFile.getFile().getPath());
        if(!file.exists()) {
            file.createNewFile();
        }
        try (Sink fileSink = Okio.sink(file);
             BufferedSink bufferedSink = Okio.buffer(fileSink)) {

            for(String s : lightFile.toStringList()) {
                bufferedSink.writeUtf8(s).writeUtf8(System.lineSeparator());
            }
        }

        //lightFile.setFile(Files.write(lightFile.getFile().toPath(),lightFile.toStringList(), Charset.forName("UTF-8")).toFile());
    }

    public static void saveFolder(@NonNull List<LightFile> files) throws IOException {
        for(LightFile lightFile : files) {
            saveFile(lightFile);
        }
    }


    public static LightFile parseFile(@NonNull File file) {
        if (file.isDirectory()) {
            throw new FileIsFolderException("The file provided is actually a folder.");
        }

        List<ILightLine> lightLines = new ArrayList<>();

        int lineNumber = 0;


        int commentedLineStart = -1;
        String concatenateComment = null;
        String[] commented1 = null;
        boolean commented = false;

        ILightLine lightLine = null;


        // Reads the file
        try (Source fileSource = Okio.source(file);
             BufferedSource bufferedSource = Okio.buffer(fileSource)) {

            while (true) {
                String argumentStart = bufferedSource.readUtf8Line();
                if (argumentStart == null) {

                    if (commented) {
                        throw new LightCommentNoEndException(new LightLine(commented1[0] + commented1[1], commentedLineStart), "The block comment does not end with a */");
                    }

                    break;
                }

                lineNumber++; // The argumentStart number id

                String[] checkMessage = argumentStart.split(" ", 2);
                List<String> messageWord = new ArrayList<>();


                if (checkMessage.length > 1) {
                    String[] messagewordCheck = argumentStart.split(" ");

                    int index = 0;

                    boolean isPar = false;
                    StringBuilder fullMessage = new StringBuilder();
                    int parStartIndex = -1;

                    for (String message : messagewordCheck) {
                        if (message == null) continue;

                        message = message.replaceAll(" {2}", " ");

                        index++;
                        if (index == 1 || message.equals("")) continue;

                        if (message.replaceAll("\\{", "").startsWith("{") && !isPar) {
                            isPar = true;
                            fullMessage.append(message.substring(message.replaceAll("\\{", "").indexOf('{')));
                            parStartIndex = index;
                        }

                        if (message.replaceAll("\\}", "").replaceAll("\\\\}", "").endsWith("}") && isPar) {
                            isPar = false;
                            messageWord.add(fullMessage + message.replaceAll("}", "").replaceAll("\\\\}", "").substring('}'));
                            fullMessage = new StringBuilder();
                        }

                        messageWord.add(message);
                    }

                    if (isPar) {
                        throw new RuntimeException("The string does have end with } at start " + parStartIndex);
                    }
                }

                argumentStart = checkMessage[0];

                argumentStart = argumentStart.replaceAll(" {2}", " ");
                String[] args = new String[messageWord.size()];
                args = messageWord.toArray(args);

                StringBuilder rawArgumentLineBuilder = new StringBuilder();

                rawArgumentLineBuilder.append(argumentStart);

                for (String ss : args) {
                    rawArgumentLineBuilder.append(" ").append(ss);
                }
                // All the lines above construct the LightLine instance

                lightLine = new LightLine(rawArgumentLineBuilder.toString(), lineNumber);

                if (argumentStart.startsWith(" ")) {
                    argumentStart = argumentStart.substring(argumentStart.indexOf(' ')) + 1;
                }


                // LINE COMMENT CHECK

                if (argumentStart.startsWith("//")) {
                    continue;
                }

                if (argumentStart.startsWith("#")) {
                    continue;
                }

                if (argumentStart.contains("//") || argumentStart.contains("#")) {
                    int index;
                    if (argumentStart.contains("//")) {
                        index = argumentStart.indexOf("//");
                    } else {
                        index = argumentStart.indexOf('#');
                    }

                    argumentStart = argumentStart.substring(index);
                }

                // LINE COMMENT CHECK

                // BLOCK COMMENT CHECK

                String rawArgumentLine = rawArgumentLineBuilder.toString();
                if (rawArgumentLine.contains("/*") && !commented) {
                    commented = true;
                    commented1 = rawArgumentLine.split("/\\*", 2);

                    concatenateComment = commented1[0];

                    commentedLineStart = lineNumber;
                }

                if (rawArgumentLine.contains("*/") && commented) {
                    String[] commented2 = commented1[1].split("\\*/", 2);

                    concatenateComment += commented2[1];

                    argumentStart = concatenateComment;
                    commented = false;
                }

                // BLOCK COMMENT CHECK

                if (commented) {
                    continue;
                }

                lightLines.add(parseLine(lightLine, argumentStart, args));
            }
        } catch (FileNotFoundException e) {
            LightManager.getLogger().error(e.getMessage(), e);
        } catch (Exception e) {
            if (lightLine != null)
                throw new LightFileParseException(lightLine, e);
            else e.printStackTrace();
        }

        return new LightFile(file, lightLines);
    }


    private static ILightLine parseLine(ILightLine lightLine, String command, String[] args) throws IllegalAccessException, NoSuchFieldException {


        for (ILightLine iLightLine : registeredLightLines) {
            if(iLightLine.getArgumentName().equalsIgnoreCase(command)) {

                if (iLightLine.requireManualArgumentHandling()) {
                    iLightLine.constructLightLine(lightLine, args);
                } else {
                    try {
                        Map<Field, Object> map = parseObjects(iLightLine, Arrays.asList(args));

                        ILightLine newLightLine = iLightLine.constructEmptyLightLine(lightLine);


                        boolean handleRestArguments = false;

                        for (Field field : map.keySet()) {

                            Field lightField = newLightLine.getClass().getDeclaredField(field.getName());

                            lightField.setAccessible(true);
                            lightField.set(newLightLine, map.get(field));


                            if(field.isAnnotationPresent(LineUnparsedArguments.class)) {
                                handleRestArguments = true;
                            }

                        }

                        if (handleRestArguments) newLightLine.validateUnparsedArguments();

                        return newLightLine;


                    } catch (IllegalArgumentException e) {
                        LightManager.getLogger().debug("Provided arguments: " + Arrays.toString(args));
                        e.printStackTrace();
                    }
                }
            }
        }

        throw new LightFileParseException(lightLine, "The line could not be parsed correctly at the start. Unrecognized: \"" + command + "\"");
    }

    public static String formatLightLineToString(ILightLine lightLine) {
        StringBuilder formattedString = new StringBuilder();

        formattedString.append(lightLine.getArgumentName());

        for (Field field : lightLine.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(LineArgument.class)) {
                LineArgument settingValue = field.getAnnotation(LineArgument.class);

                String name = settingValue.name();

                if(name.equals("")) name = field.getName();

                try {
                    formattedString
                            .append(" ")
                            .append(name)
                            .append("(")
                            .append(field.getDeclaringClass())
                            .append(")").append("={").append(field.get(lightLine)).append("}");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return formattedString.toString();
    }

    public static String formatLightLineToString(Class<? extends ILightLine> lightClass) {
        StringBuilder formattedString = new StringBuilder();

        if(!lightClass.isAnnotationPresent(LineData.class)) throw new IllegalArgumentException("Class does not have LineData annotation set.");

        formattedString.append(lightClass.getAnnotation(LineData.class).name());

        for (Field field : lightClass.getDeclaredFields()) {
            if(field.isAnnotationPresent(LineArgument.class)) {
                LineArgument argumentValue = field.getAnnotation(LineArgument.class);

                String name = argumentValue.name();

                if(name.equals("")) name = field.getName();

                formattedString
                        .append(" ")
                        .append(name)
                        .append("(")
                        .append(field.getDeclaringClass())
                        .append(")").append("={").append(field.getType()).append("}");
            }
        }

        return formattedString.toString();
    }

    /**
     * Read folder directory rather one file
     * @param path The folder directory
     * @throws FileNotFoundException When path is non-existent or not folder
     */
    public static List<LightFile> parseFolder(@NonNull File path) throws FileNotFoundException {
        File[] files = path.listFiles();

        List<LightFile> lightFiles = new ArrayList<>();

        if(!path.exists() || files == null) throw new FileNotFoundException("The folder specified, " + path.getAbsolutePath() + " is either not a folder or does not exist");

        for(File file : files) {

            if(FilenameUtils.getExtension(file.getName()).equals("pia")) {
                lightFiles.add(parseFile(file));
            }

        }
        return lightFiles;
    }

    public static Map<Field, Object> parseObjects(ILightLine iLightLine, String arg1, String... argsRest) {
        List<String> args = new ArrayList<>();
        args.add(arg1);
        Collections.addAll(args, argsRest);

        return parseObjects(iLightLine, args);
    }

    public static Map<Field, Object> parseObjects(ILightLine iLightLine, List<String> args) {
        Map<Field, Object> parsedObject = new HashMap<>();

        Queue<Field> argumentTypesToParse = new LinkedList<>();
        List<String> argsToBeUsed = new ArrayList<>(args);

        Field restArgsField = null;

        // Define all the required fields
        for (Field field : iLightLine.getClass().getDeclaredFields()) {

            if (!field.isAnnotationPresent(LineArgument.class)) continue;

            if(field.isAnnotationPresent(LineUnparsedArguments.class)) {
                restArgsField = field;
                continue;
            }

            LineArgument lineArgument = field.getAnnotation(LineArgument.class);

            String name = lineArgument.name();
            if (name.equals("")) name = field.getName();

            boolean defined = false;

            for (String s : args) {

                String formattedS = s.trim().replaceAll("\\=","").replace("\\\\=","").replaceAll(" =", "="); //remove space in between the name and =

                if(
                         formattedS
                                .startsWith(name + "=") // check if the field is the same
                ) {
                    defined = true;


                    String valueNoFormat = s
                            .trim()
                            .replace(name + " =", name + "=")
                            .substring(
                                    s.indexOf(name + "=")
                            );

                    Object parsedValue = ReflectionUtil.parse(valueNoFormat, field.getType());

                    parsedObject.put(field, parsedValue);

                    argsToBeUsed.remove(s);

                    break;
                }
            }

            if (!defined && lineArgument.required()) {
                argumentTypesToParse.add(field);
            }
        }

        List<String> restArgs = new ArrayList<>();

        // Parse the rest objects not defined literally
        for (String s : args) {

            if(argumentTypesToParse.isEmpty()) {
                if (restArgsField != null) {
                    restArgs.add(s);


                    continue;
                } else {
                throw new IllegalArgumentException("Too many arguments provided. Provided: " + parsedObject + " extra: " + args.toString());
                }
            }

            Field field = argumentTypesToParse.remove();

            LineArgument lineArgument = field.getAnnotation(LineArgument.class);

            Object parsedValue = ReflectionUtil.parse(s, field.getType());

            parsedObject.put(field, parsedValue);
        }

        // Not enough arguments provided
        if (!argumentTypesToParse.isEmpty()) {
            StringBuilder missingTypes = new StringBuilder("{");

            while (!argumentTypesToParse.isEmpty()) {
                Field field = argumentTypesToParse.remove();
                LineArgument lineArgument = field.getAnnotation(LineArgument.class);

                String name = lineArgument.name();
                if (name.equals("")) name = field.getName();

                missingTypes.append(name);

                if (!argumentTypesToParse.isEmpty()) missingTypes.append(",");
            }

            missingTypes.append("}");

            throw new IllegalArgumentException("Not enough arguments provided. Missing arguments: " + missingTypes);
        }

        if (restArgsField != null) {

//                restArgsField.setAccessible(true);
            String[] argArray = restArgs.toArray(new String[0]);
//                restArgsField.set(iLightLine, argArray);

            parsedObject.put(restArgsField, argArray);
        }

        return parsedObject;
    }
}
