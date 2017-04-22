package ru.atom.lecture09.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class NIOFileAPI {
    @Test
    public void writeFile() throws IOException {
        List<String> lines = Arrays.asList("The first line by ru.atom.lecture09.nio Files", "The second line by ru.atom.lecture09.nio Files");
        Path file = Paths.get("src/main/resources/to.txt");
        Files.write(file, lines, Charset.forName("UTF-8"));
    }

    @Test
    public void readFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("src/main/resources/to.txt"));
        StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s);
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
