package ar.edu.itba.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileParser {

    static List<Planet> parseDynamicFile(String dynamicFilePath) throws FileNotFoundException {
        List<Planet> planets = new ArrayList<>();
        File dynamicFile = new File(dynamicFilePath);
        Scanner sc = new Scanner(dynamicFile);
        int planetCount = sc.nextInt();
        for (int i = 0; i < planetCount; i++) {
            double x = sc.nextDouble() * 1000;
            double y = sc.nextDouble() * 1000;
            double vx = sc.nextDouble() * 1000;
            double vy = sc.nextDouble() * 1000;
            Planet p = new Planet(i+1, x, y, vx, vy);
            planets.add(p);
        }
        sc.close();
        return planets;
    }


}
