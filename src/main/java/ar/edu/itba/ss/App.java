package ar.edu.itba.ss;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static double dt;
    private static double time;
    private static double fps;

    private static final int SUN_ID = 0;

    private static double G = 6.693*Math.pow(10, -11);
    private static final double AU = 149598073;

    // Spaceship
    private static final double SPACESHIP_DISTANCE = 1500000;
    private static final double SPACESHIP_SPEED = 8000;
    private static final double SPACESHIP_MASS = 2 * Math.pow(10,5);

    // Station
    private static final double SPACE_STATION_SPEED = 7120;

    // Mars
    private static final double MARS_MASS = 6.4171 * Math.pow(10,23);
    private static final double MARS_RADIUS = 3389500;

    // Sun
    private static final double SUN_MASS = 1.988544 * Math.pow(10,30);
    private static final double SUN_RADIUS = 696340000;‬

    // Earth
    private static final double EARTH_MASS = 5.97219 * Math.pow(10,24);
    private static final double EARTH_RADIUS = 6371000;


    public static void main( String[] args ) {

        Configuration configuration = CommandLineParser.parseCommandLine(args);
        List<Planet> planets = null;
        try {
            planets = FileParser.parseDynamicFile(configuration.getDynamicFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Error reading dynamic file...");
        }
        time = configuration.getTime();
        dt = configuration.getDt();
        fps = configuration.getFps();

        //////////////////////////////
        ///////// SIMULATION /////////
        //////////////////////////////

        assert planets != null;
        Planet earth = planets.get(0);

        // Get earth-sun angle
        double earthSunAngle;
        if (earth.getX() == 0) {
            earthSunAngle = Math.signum(earth.getY()) * Math.PI / 2;
        }
        else{
            earthSunAngle = Math.atan(earth.getY() / earth.getX());
            if ((earth.getX() < 0 && earth.getY() > 0) || (earth.getX() < 0 && earth.getY() < 0)){
                earthSunAngle += Math.PI;
            }
        }

        // Get earth-sun velocity angle
        double velocityAngle;
        if (earth.getVx() == 0) {
            velocityAngle = Math.signum(earth.getVy()) * Math.PI / 2;
        }
        else {
            velocityAngle = Math.atan(earth.getVy() / earth.getVx());
            if ((earth.getVx() < 0 && earth.getVy() > 0) || (earth.getVx() < 0 && earth.getVy() < 0)){
                velocityAngle += Math.PI;
            }
        }

        double spaceshipX = earth.getX() + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(earthSunAngle);
        double spaceshipY = earth.getY() + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(earthSunAngle);
        double spaceshipVx = earth.getVx() + ((SPACESHIP_SPEED + SPACE_STATION_SPEED) * Math.cos(velocityAngle));
        double spaceshipVy = earth.getVy() + ((SPACESHIP_SPEED + SPACE_STATION_SPEED) * Math.sin(velocityAngle));

        // Add Sun and Spaceship
        planets.add(new Planet(SUN_ID, 0.0, 0.0, 0, 0, SUN_MASS, 0.14));
        planets.add(new Planet(4, spaceshipX, spaceshipY, spaceshipVx, spaceshipVy, SPACESHIP_MASS, 0.08));

        List<Planet> oldPlanets = clonePlanets(planets);


    }


    private static void initializePlanets(List<Planet> planets, List<Planet> oldPlanets) {
        for (Planet p : planets){
            if (p.getId() != SUN_ID) {
                firstStep(p, oldPlanets);
            }
        }
    }

    private static void firstStep(Planet planet, List<Planet> planets) {
        double[] force = force(planet, planets);
        planet.getPrevAx() = force[0];
        planet.getPrevAy() = force[1];
        planet.setVx(planet.getVx() + dt * planet.getPrevAx());
        planet.setVy(planet.getVy() + dt * planet.getPrevAy());
        planet.setX(planet.getX() + dt * planet.getVx() + Math.pow(dt,2) * planet.getPrevAx());
        planet.setY(planet.getY() + dt * planet.getVy() + Math.pow(dt,2) * planet.getPrevAy());

    }

    private static double[] force (Planet p, List<Planet> oldPlanets){
        double[] force = {0,0};
        for (Planet otherPlanet : oldPlanets) {
            if (p.getId() != otherPlanet.getId()) {
                double f = gravitationalForce(p, otherPlanet);
                double dx = otherPlanet.getX() - p.getX();
                double dy = otherPlanet.getY() - p.getY();
                double mod = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy,2));
                force[0] += f * (dx/mod);
                force[1] += f * (dy/mod);
            }
        }
        force[0] = force[0]/p.getMass();
        force[1] = force[1]/p.getMass();
        return force;
    }
    
    private static double gravitationalForce(Planet p1, Planet p2){
        double distance = Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
        return G * (p1.getMass() * p2.getMass() / Math.pow(distance, 2));
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

    private static void printPlanets(List<Planet> planets) {
        for (Planet p : planets){
            System.out.println(p.getX() + "\t" + p.getY() + "\t" + p.getVx() + "\t" + p.getVy() + "\t" + p.getRadius());
        }
    }

}



