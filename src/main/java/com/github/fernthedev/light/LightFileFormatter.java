package com.github.fernthedev.light;

import com.github.fernthedev.light.api.LightParser;
import com.github.fernthedev.light.api.lines.ILightLine;
import com.github.fernthedev.light.api.LightFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileNotFoundException;

public class LightFileFormatter {

    /**
     * Executes the .pia file
     * It is async
     * @param file
     */
    public static void executeLightFile(File file) {

        LightFile lightFile = LightParser.parseFile(file);

        for (ILightLine curLine : lightFile.getLineList()) {
            curLine.execute();
        }


    }

    /**
     * Read folder directory rather one file
     * @param path The folder directory
     * @throws FileNotFoundException When path is non-existent or not folder
     */
    public static void readDirectory(File path) throws FileNotFoundException {
        File[] files = path.listFiles();

        if(!path.exists() || files == null) throw new FileNotFoundException("The folder specified, " + path.getAbsolutePath() + " is either not a folder or does not exist");

        for(File file : files) {

            if(FilenameUtils.getExtension(file.getName()).equals("pia")) {
                executeLightFile(file);
            }

        }
    }





}
