package ar.edu.itba.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static double dt;
    private static final int SUN_ID = 0;
    private static double G = 6.673 * Math.pow(10, -11);

    private static final double MISSION_SUCCESS_DISTANCE = 3000000; //3000km

    // Spaceship
    private static final double SPACESHIP_DISTANCE = 1500000; //15000km
    private static final double SPACESHIP_SPEED = 8000;
    private static final double SPACESHIP_MASS = 2 * Math.pow(10,5);
    private static final double SPACESHIP_RADIUS = 10000; //check for simulation
    // Mars
    private static final double MARS_MASS = 6.4171 * Math.pow(10,23);
    private static final double MARS_RADIUS = 3389500;
    // Sun
    private static final double SUN_MASS = 1.988544 * Math.pow(10,30);
    private static final double SUN_RADIUS = 696340000;
    // Earth
    private static final double EARTH_MASS = 5.97219 * Math.pow(10,24);
    private static final double EARTH_RADIUS = 6371000;

    // Saturn - part 3
    // private static final double JUPITER_MASS = ;
    // private static final double JUPITER_RADIUS = ;

    public static void main( String[] args ) {
        File file = new File("output.txt");
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("output.xyz", "UTF-8");
        } catch (Exception e) {
            System.out.println("Couldn't write output to file...");
            System.exit(1);
        }
        Configuration configuration = CommandLineParser.parseCommandLine(args);
        List<Planet> planets = null;
        try {
            planets = FileParser.parseDynamicFile(configuration.getDynamicFile());
        } catch (FileNotFoundException e) {
            System.out.println("Error reading dynamic file...");
            System.exit(1);
        }
        double time = configuration.getTime();
        double fps = configuration.getFps();
        dt = configuration.getDt();

        assert planets != null;
        Planet earth = planets.get(0);
        double earthSunAngle = getEarthSunAngle(earth);
        double velocityAngle = getEarthSunVelocityAngle(earth);

        double spaceshipX = earth.x + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(earthSunAngle);
        double spaceshipY = earth.y + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(earthSunAngle);
        double spaceshipVx = earth.vx + SPACESHIP_SPEED  * Math.cos(velocityAngle);
        double spaceshipVy = earth.vy + SPACESHIP_SPEED * Math.sin(velocityAngle);

        // Add Sun and Spaceship
        planets.add(new Planet(SUN_ID, 0.0, 0.0, 0, 0, SUN_MASS, SUN_RADIUS));
        planets.add(new Planet(4, spaceshipX, spaceshipY, spaceshipVx, spaceshipVy, SPACESHIP_MASS, SPACESHIP_RADIUS));
        earth.mass = EARTH_MASS;
        earth.radius = EARTH_RADIUS;
        Planet mars = planets.get(1);
        mars.mass = MARS_MASS;
        mars.radius = MARS_RADIUS;

        double minDistanceToMars = Double.POSITIVE_INFINITY;

        List<Planet> oldPlanets = clonePlanets(planets);
        initializePlanets(planets, oldPlanets);
        int iterations = 0;
        printPlanets(writer, planets, iterations++);
        int frame = 0;
        for(double t = 0; t < time; t += dt) {
            oldPlanets = clonePlanets(planets);
            for (Planet p : planets) {
                if (p.id != SUN_ID) {
                    double[] force = force(p, oldPlanets);
                    p.ax = force[0];
                    p.ay = force[1];
                    p.x = p.x + p.vx * dt + (2.0 / 3) * p.ax * Math.pow(dt, 2) - (1.0 / 6) * p.prevAx * Math.pow(dt, 2);
                    p.y = p.y + p.vy * dt + (2.0 / 3) * p.ay * Math.pow(dt, 2) - (1.0 / 6) * p.prevAy * Math.pow(dt, 2);
                }
            }
            for (Planet p : planets) {
                if (p.id != SUN_ID) {
                    double[] newForce = force(p, planets);
                    double newAx = newForce[0];
                    double newAy = newForce[1];
                    p.vx = p.vx + (1.0 / 3) * newAx * dt + (5.0 / 6) * p.ax * dt - (1.0 / 6) * p.prevAx * dt;
                    p.vy = p.vy + (1.0 / 3) * newAy * dt + (5.0 / 6) * p.ay * dt - (1.0 / 6) * p.prevAy * dt;
                    p.prevAx = p.ax;
                    p.prevAy = p.ay;
                }
            }
            double distanceToMars = calculateDistanceToMars(planets);
            System.out.println(distanceToMars/1000 + "\t" + t);
            if(distanceToMars < minDistanceToMars) {
                minDistanceToMars = distanceToMars;
                if(distanceToMars < MISSION_SUCCESS_DISTANCE + MARS_RADIUS) {
                    System.out.println("Mission Success!! Spaceship reached Mars " + minDistanceToMars/1000 + "km");
                    break;
                }
            }
            if(frame++ % fps == 0) {
                printPlanets(writer, planets, iterations++);
            }
        }
        writer.close();
        System.out.println("Tiempo total: " + time + "s");
        System.out.println("Distancia minima a Marte: " + (minDistanceToMars/1000) + "km");
    }

    private static double calculateDistanceToMars(List<Planet> planets) {
        Planet spaceship = planets.stream().filter(planet -> planet.id == 4).findAny().orElse(null);
        Planet mars = planets.stream().filter(planet -> planet.id == 2).findAny().orElse(null);
        return Math.sqrt(Math.pow(spaceship.x - mars.x, 2) + Math.pow(spaceship.y - mars.y, 2)) - MARS_RADIUS;
    }

    private static double getEarthSunAngle(Planet earth) {

        double earthSunAngle;
        if (earth.x == 0) {
            return Math.signum(earth.y) * Math.PI / 2;
        }
        else{
            earthSunAngle = Math.atan(earth.y/earth.x);
            if ((earth.x < 0 && earth.y > 0) || (earth.x < 0 && earth.y < 0)) {
                earthSunAngle += Math.PI;
            }
            return earthSunAngle;
        }
    }

    private static double getEarthSunVelocityAngle(Planet earth) {

        double velocityAngle;
        if (earth.vx == 0) {
            return Math.signum(earth.vy) * Math.PI / 2;
        }
        else {
            velocityAngle = Math.atan(earth.vy/earth.vx);
            if ((earth.vx < 0 && earth.vy > 0) || (earth.vx < 0 && earth.vy < 0)) {
                velocityAngle += Math.PI;
            }
            return velocityAngle;
        }
    }

    private static void initializePlanets(List<Planet> planets, List<Planet> oldPlanets) {
        for (Planet p : planets){
            if (p.id != SUN_ID) {
                firstStep(p, oldPlanets);
            }
        }
    }

    private static void firstStep(Planet p, List<Planet> planets) {
        double[] force = force(p, planets);
        p.prevAx = force[0];
        p.prevAy = force[1];
        p.vx = p.vx + dt * p.prevAx;
        p.vy = p.vy + dt * p.prevAy;
        p.x = p.x + dt * p.vx + Math.pow(dt, 2) * p.prevAx;
        p.y = p.y + dt * p.vy + Math.pow(dt, 2) * p.prevAy;
    }

    private static double[] force (Planet p, List<Planet> oldPlanets){
        double[] force = {0,0};
        for (Planet otherPlanet : oldPlanets) {
            if (p.id != otherPlanet.id) {

                double f = gravitationalForce(p, otherPlanet);

                double dx = otherPlanet.x - p.x;
                double dy = otherPlanet.y - p.y;

                double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy,2));
                force[0] += f * (dx/mod);
                force[1] += f * (dy/mod);
            }
        }
        force[0] = force[0]/p.mass;
        force[1] = force[1]/p.mass;
        return force;
    }

    private static double gravitationalForce(Planet p1, Planet p2) {
        double distance = Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        return G*(p1.mass*p2.mass/Math.pow(distance, 2));
    }

    private static List<Planet> clonePlanets(List<Planet> planets) {
        List<Planet> clones = new ArrayList<>();
        for (Planet p: planets){
            try {
                clones.add(p.getClone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        return clones;
    }

    private static void printPlanets(PrintWriter writer, List<Planet> planets, int iterations) {
        writer.println(planets.size());
        writer.println(iterations);
        for (Planet p : planets) {
            writer.println(p.id + "\t" + p.x/1000 + "\t" + p.y/1000 + "\t" + p.vx/1000 + "\t" + p.vy/1000 + "\t" + p.radius/1000);
        }
    }

}



