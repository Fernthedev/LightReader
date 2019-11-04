package com.github.fernthedev.light.api;

import com.github.fernthedev.light.api.lines.ILightLine;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class LightFile {

    @NonNull
    private File file;

    @NonNull
    private List<ILightLine> lineList;

    public List<String> toStringList() {
        List<String> lines = new ArrayList<>();

        for(ILightLine line : lineList) {
            lines.add(line.getLine());
        }

        return lines;
    }

}
