package ar.edu.itba.ss;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.YEARS;

public class App {

    private static final LocalDate baseDate = LocalDate.parse("2020-04-06", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    private static final LocalDate secondDate = LocalDate.parse("2021-07-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    private static double dt;

    private static final double AU = 149598073;

    private static final int[] EARTH_COLOUR = {0, 0, 255};
    private static final int[] VOYAGER_COLOUR = {255, 255, 255};
    private static final int[] SUN_COLOUR = {255, 255, 0};
    private static final int[] MARS_COLOUR = {255,0,0};
    private static final int[] JUPITER_COLOUR = {200, 100, 0};

    private static final int SUN_ID = 0;
    private static final int EARTH_ID = 1;
    private static final int MARS_ID = 2;
    private static final int SPACESHIP_ID = 4;
    private static final int JUPITER_ID = 3;

    private static double G = 6.674 * Math.pow(10, -11);

    private static final double SECONDS_IN_DAY = 86400;
    private static final double MISSION_SUCCESS_DISTANCE = 3000000; //3000km
    private static final double MAX_TRAVELLING_TIME = 172800000; //2000 days in seconds

    // Spaceship
    private static final double SPACESHIP_DISTANCE = 1500000; //1500km
    private static final double SPACESHIP_SPEED = 8000 + 7120;
    private static final double SPACESHIP_MASS = 2 * Math.pow(10,5);
    private static final double SPACESHIP_RADIUS = 10000; // 10km?

    // Mars
    private static final double MARS_MASS = 6.4171 * Math.pow(10,23);
    private static final double MARS_RADIUS = 3389500;

    // Sun
    private static final double SUN_MASS = 1.988544 * Math.pow(10,30);
    private static final double SUN_RADIUS = 696340000;

    // Earth
    private static final double EARTH_MASS = 5.97219 * Math.pow(10,24);
    private static final double EARTH_RADIUS = 6371000;

    //Jupiter
    private static final double JUPITER_MASS = 1.898 * Math.pow(10, 27);
    private static final double JUPITER_RADIO = 69911000;


    public static void main( String[] args ) {
        //File file = new File("output.txt");

        PrintWriter writer2 = null;
        try {
            writer2 = new PrintWriter("output.txt", "UTF-8");
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

        Planet earth = getPlanetById(planets, EARTH_ID);
        earth.mass = EARTH_MASS;
        earth.radius = 0.03;
        earth.colour = EARTH_COLOUR;

        double earthSunAngle = getEarthSunAngle(earth);
        double velocityAngle = getEarthSunVelocityAngle(earth);
        double spaceshipX = earth.x + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(earthSunAngle);
        double spaceshipY = earth.y + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(earthSunAngle);
        double spaceshipVx = earth.vx + SPACESHIP_SPEED * Math.cos(velocityAngle);
        double spaceshipVy = earth.vy + SPACESHIP_SPEED  * Math.sin(velocityAngle);
        System.out.println("Starting speed " + Math.sqrt( Math.pow(spaceshipVx,2) + Math.pow(spaceshipVy,2))/1000  + " km/s" );

        // Add Sun and Spaceship
        planets.add(new Planet(SUN_ID, 0.0, 0.0, 0, 0, SUN_MASS, 0.15, SUN_COLOUR));
        planets.add(new Planet(SPACESHIP_ID, spaceshipX, spaceshipY, spaceshipVx, spaceshipVy, SPACESHIP_MASS, 0.005, VOYAGER_COLOUR));

        Planet mars = getPlanetById(planets, MARS_ID);
        mars.mass = MARS_MASS;
        mars.radius = 0.015;
        mars.colour = MARS_COLOUR;

        Planet jupiter = getPlanetById(planets, JUPITER_ID);
        jupiter.mass = JUPITER_MASS;
        jupiter.radius = 0.09;
        jupiter.colour = JUPITER_COLOUR;

        Planet spaceship = getPlanetById(planets, SPACESHIP_ID);

        BaseValues baseValues = new BaseValues(earth, mars, spaceship, jupiter);
        List<Planet> oldPlanets = clonePlanets(planets);
         int iterations = 0;

        boolean tripSuccess = false;

        // Change starting date
        int difDays = (int) baseDate.until(secondDate, DAYS);
        int hour = 0;
        LocalDate startDate = baseDate;
        double timeTaken = MAX_TRAVELLING_TIME;
        for(int i = 0 ; i < 1000 && !tripSuccess ; i++) {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter("output.xyz", "UTF-8");
            } catch (Exception e) {
                System.out.println("Couldn't write output to file...");
                System.exit(1);
            }

            double minDistanceToMars = Double.POSITIVE_INFINITY;
            // restore to values to 06/04/2020 and evolve positions to new day
            if(i != 0) {
                restoreToBaseValues(planets, baseValues);
                initializePlanets(planets, oldPlanets);
                if(i <= difDays) {
                    evolvePlanetStates(planets, SECONDS_IN_DAY);
                }else{
                    evolvePlanetStates(planets, SECONDS_IN_DAY/2);
                }
                double angle1 = getEarthSunAngle(earth);
                double angle2 = getEarthSunVelocityAngle(earth);
                double sx = earth.x + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(angle1);
                double sy = earth.y + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(angle1);
                double sVx = earth.vx + SPACESHIP_SPEED * Math.cos(angle2);
                double sVy = earth.vy + SPACESHIP_SPEED  * Math.sin(angle2);
                spaceship.x = sx;
                spaceship.y = sy;
                spaceship.vx = sVx;
                spaceship.vy = sVy;
                double[] force = force(spaceship, planets);
                spaceship.prevAx = force[0];
                spaceship.prevAy = force[1];
                if(i <= difDays){
                    startDate = baseDate.plusDays(i);
                }else{
                    if((i - difDays) % 2 == 0){
                        startDate = baseDate.plusDays(difDays + (i - difDays)/2);
                        hour = 0;
                    }else{
                        hour++;
                    }
                }

                baseValues.newBaseValues(earth, mars, spaceship, jupiter);
            }
            else {
                initializePlanets(planets, oldPlanets);
            }

            int frame = 0;
            iterations = 0;

            //System.out.println(earth.x/1000 + "\t" + earth.y/1000  + "\t" + earth.vx/1000 + "\t" + earth.vy/1000);
            //System.out.println(mars.x/1000 + "\t" + mars.y/1000  + "\t" + mars.vx/1000 + "\t" + mars.vy/1000);
            printPlanets(writer, planets, iterations++);

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
                double distanceToJupiter = calculateDistanceToJupiter(planets);


                if(distanceToMars <= 0) {
                    break;
                }

                if(distanceToJupiter < minDistanceToMars) {
                    minDistanceToMars = distanceToJupiter;
                    if(distanceToJupiter < MISSION_SUCCESS_DISTANCE) {
                        timeTaken = t;
                        tripSuccess = true;
                        break;
//                        if(distanceToMars <= 0){
//                            minDistanceToMars = 0;
//                            break;
//                        }
                    }

                }

                if(frame++ % fps == 0) {
                    printPlanets(writer, planets, iterations++);
                }
            }
            int min = 0;
            int hs =   12 * hour;
            if(hour % 4 == 0){
                hs = hour/4;
                min = 0;
            }if(hour % 4 == 1){
                hs = (int) Math.floor(hour/4);
                min = 15;
            }if(hour % 4 == 2){
                hs = (int) Math.floor(hour/4);
                min = 30;
            }if(hour % 4 == 3){
                hs = (int) Math.floor(hour/4);
                min = 45;
            }
            double speed = Math.sqrt(Math.pow(spaceship.vx,2) + Math.pow(spaceship.vy,2));
            double days = tripSuccess? (timeTaken /SECONDS_IN_DAY) : (MAX_TRAVELLING_TIME/SECONDS_IN_DAY);
            System.out.println(startDate +" "+ hs +":"+ min +":00\t" + minDistanceToMars/1000 + "\t" + speed/1000 + "\t" + days);
            printDateCalculations(writer2, startDate, hs, min, minDistanceToMars/1000);
            tripSuccess = false;
            writer.close();
        }


        writer2.close();


    }

    private static double calculateDistanceToMars(List<Planet> planets) {
        Planet spaceship = getPlanetById(planets, SPACESHIP_ID);
        Planet mars = getPlanetById(planets, MARS_ID);
        return Math.sqrt(Math.pow(spaceship.x - mars.x, 2) + Math.pow(spaceship.y - mars.y, 2)) - MARS_RADIUS;
    }

    private static double calculateDistanceToJupiter(List<Planet> planets) {
        Planet spaceship = getPlanetById(planets, SPACESHIP_ID);
        Planet jupiter = getPlanetById(planets, JUPITER_ID);
        return Math.sqrt(Math.pow(spaceship.x - jupiter.x, 2) + Math.pow(spaceship.y - jupiter.y, 2)) - JUPITER_RADIO;
    }


    private static void restoreToBaseValues(List<Planet> planets, BaseValues baseValues) {

        Planet earth = getPlanetById(planets, EARTH_ID);
        Planet mars = getPlanetById(planets, MARS_ID);
        Planet ship = getPlanetById(planets, SPACESHIP_ID);
        Planet jupiter = getPlanetById(planets, JUPITER_ID);

        earth.x = baseValues.earthX;
        earth.y = baseValues.earthY;
        earth.vx = baseValues.earthVx;
        earth.vy = baseValues.earthVy;

        mars.x = baseValues.marsX;
        mars.y = baseValues.marsY;
        mars.vx = baseValues.marsVX;
        mars.vy = baseValues.marsVy;

        ship.x = baseValues.spaceshipX;
        ship.y = baseValues.spaceshipY;
        ship.vx = baseValues.spaceshipVx;
        ship.vy = baseValues.spaceshipVy;

        jupiter.x = baseValues.jupiterX;
        jupiter.y = baseValues.jupiterY;
        jupiter.vx = baseValues.jupiterVx;
        jupiter.vy = baseValues.jupiterVy;
    }

    private static void evolvePlanetStates(List<Planet> planets, double seconds) {
        List<Planet> oldPlanets;
        for(double t = 0; t < seconds; t += dt) {
            oldPlanets = clonePlanets(planets);
            for (Planet p : planets) {
                if (p.id != SUN_ID && p.id != SPACESHIP_ID) {
                    double[] force = force(p, oldPlanets);
                    p.ax = force[0];
                    p.ay = force[1];
                    p.x = p.x + p.vx * dt + (2.0 / 3) * p.ax * Math.pow(dt, 2) - (1.0 / 6) * p.prevAx * Math.pow(dt, 2);
                    p.y = p.y + p.vy * dt + (2.0 / 3) * p.ay * Math.pow(dt, 2) - (1.0 / 6) * p.prevAy * Math.pow(dt, 2);
                }
            }
            for (Planet p : planets) {
                if (p.id != SUN_ID && p.id != SPACESHIP_ID) {
                    double[] newForce = force(p, planets);
                    double newAx = newForce[0];
                    double newAy = newForce[1];
                    p.vx = p.vx + (1.0 / 3) * newAx * dt + (5.0 / 6) * p.ax * dt - (1.0 / 6) * p.prevAx * dt;
                    p.vy = p.vy + (1.0 / 3) * newAy * dt + (5.0 / 6) * p.ay * dt - (1.0 / 6) * p.prevAy * dt;
                    p.prevAx = p.ax;
                    p.prevAy = p.ay;
                }
            }
            Planet spaceship = planets.get(SPACESHIP_ID);
            Planet earth = planets.get(EARTH_ID);
            double earthSunAngle = getEarthSunAngle(earth);
            double velocityAngle = getEarthSunVelocityAngle(earth);
            spaceship.x = earth.x + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(earthSunAngle);
            spaceship.y = earth.y + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(earthSunAngle);
            spaceship.vx = earth.vx + SPACESHIP_SPEED * Math.cos(velocityAngle);
            spaceship.vy = earth.vy + SPACESHIP_SPEED  * Math.sin(velocityAngle);

        }
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

    //Print positions in kms, and velocities in km/s
    private static void printPlanets(PrintWriter writer, List<Planet> planets, int iterations) {
        writer.println(planets.size());
        writer.println(iterations);
        for (Planet p : planets) {
            writer.println(p.id + "\t" + p.x/1000/AU + "\t" + p.y/1000/AU + "\t" + p.vx + "\t" + p.vy + "\t" + p.radius
                    + "\t" + p.colour[0] + "\t" + p.colour[1] + "\t" + p.colour[2]);
        }
    }

    private static void printDateCalculations(PrintWriter writer, LocalDate date, int hour, int min, double minDist){
        writer.println(date + "-" + hour + ":" + min + "\t" + minDist);
        writer.flush();
    }

    private static Planet getPlanetById(List<Planet> planets, int id) {
        for(Planet p : planets) {
            if(p.id == id) return p;
        }
        return null;
    }

}





