package io.github.fernthedev.light.api;


import io.github.fernthedev.light.LightManager;
import io.github.fernthedev.light.api.annotations.LineArgument;
import io.github.fernthedev.light.api.annotations.LineData;
import io.github.fernthedev.light.api.lines.*;
import io.github.fernthedev.light.exceptions.FileIsFolderException;
import io.github.fernthedev.light.exceptions.LightCommentNoEndException;
import io.github.fernthedev.light.exceptions.LightFileParseException;
import lombok.NonNull;
import okio.*;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LightParser {

    private static boolean init = false;
    static {
        if(!init) {
            init = true;
            registerLightLine(new LightPinLine(NullObject.NULL_OBJECT));
            registerLightLine(new LightSleepLine(NullObject.NULL_OBJECT));
            registerLightLine(new LightPrintLine(NullObject.NULL_OBJECT));
        }
    }

    private LightParser() {}


    private static final List<ILightLine> parseList = new ArrayList<>();

    public static void registerLightLine(ILightLine iLightLine) {
        parseList.add(iLightLine);
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
        if(file.isDirectory()) {
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

                    if(commented) {
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


                    for (String message : messagewordCheck) {
                        if (message == null) continue;

                        message = message.replaceAll(" {2}", " ");

                        index++;
                        if (index == 1 || message.equals("")) continue;


                        messageWord.add(message);
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
                {
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
                }
                // LINE COMMENT CHECK

                // BLOCK COMMENT CHECK
                {
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
                }
                // BLOCK COMMENT CHECK

                if (commented) {
                    continue;
                }

                boolean foundOnList = false;

                for (ILightLine iLightLine : parseList) {
                    if(iLightLine.getArgumentName().equalsIgnoreCase(argumentStart)) {
                        lightLines.add(iLightLine.constructLightLine(lightLine, args));
                        foundOnList = true;
                    }
                }


                if(!foundOnList) {
                    throw new LightFileParseException(lightLine, "The line could not be parsed correctly at the start. Unrecognized: \"" + argumentStart + "\"");
                }

//                if (argumentStart.equalsIgnoreCase("print") && args.length > 1) {
//                    StringBuilder st = new StringBuilder();
//                    int t = 0;
//                    for (String se : args) {
//                        if (t > 0) st.append(" ");
//                        st.append(se);
//
//                        t++;
//                    }
//
//                    lightLines.add(new LightPrintLine(lightLine, st.toString()));
//                }

//                if (argumentStart.equalsIgnoreCase("pin") && args.length > 1) {
//                    if (args[0].equalsIgnoreCase("all")) {
//
//
//                        String newPar = args[1];
//
//                        boolean toggle;
//
//                        if (newPar.equalsIgnoreCase("on")) {
//                            toggle = true;
//                        } else if (newPar.equalsIgnoreCase("off")) {
//                            toggle = false;
//                        } else {
//                            throw new LightFileParseException(lightLine, "Could not find parameter " + newPar);
//                        }
//
//                        lightLines.add(new LightPinLine(lightLine, true, toggle));
//
//
//                    } else if (args[0].matches("[0-9]+")) {
//                        int pinInt = Integer.parseInt(args[0]);
//
//
//                        String newPar = args[1];
//                        boolean toggle;
//
//                        if (newPar.equalsIgnoreCase("on")) {
//                            toggle = true;
//                        } else if (newPar.equalsIgnoreCase("off")) {
//                            toggle = false;
//                        } else {
//                            throw new LightFileParseException(lightLine, "Could not find parameter " + newPar);
//                        }
//
//                        lightLines.add(new LightPinLine(lightLine, pinInt, toggle));
//
//
//                    } else {
//                        throw new LightFileParseException(lightLine, "Argument " + args[0] + " can only be numerical.");
//                    }
//                }

//                if (argumentStart.equalsIgnoreCase("sleep")) {
//                    if (args.length > 0) {
//                        String amount = args[0];
//                        if (amount.replaceAll("\\.", "").matches("[0-9]+")) {
//                            double time = Double.parseDouble(amount);
//                            lightLines.add(new LightSleepLine(lightLine, time));
//                        }
//                    }
//                }

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
}
