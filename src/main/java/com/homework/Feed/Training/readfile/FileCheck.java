package com.homework.Feed.Training.readfile;

import com.homework.Feed.Training.archivefolder.ArchiveFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class FileCheck extends ReadFile {

    @Value("${processing.file.folder}")
    private Path processingFolder;

    @Value("${error.file.folder}")
    private Path errorFolder;

    public void fileCheck(List<String[]> list) {
        List<String[]> filteredList = new ArrayList<>();
        boolean b = false;

        for (String[] associate : list) {
            switch (associate.length) {
                case 6:
                    filteredList.add(associate);
                    break;
                default:
                    log.error("Incorrect number of fields in file. File moved to error folder.");
                    b = true;
                    break;
            }
        }
        if (b) {
            errorMove();
        }
        addToPojo(filteredList);
    }

    public void errorMove() {
        try {
            Files.createDirectories(errorFolder);
            Files.walk(processingFolder).filter(f -> f.toString().endsWith(".csv"))
                    .forEach(f -> {
                        Path destination = errorFolder.resolve(processingFolder.relativize(f));
                        try {
                            Files.createDirectories(destination.getParent());
                            Files.move(f, destination);
                        } catch (IOException i) {
                            i.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

