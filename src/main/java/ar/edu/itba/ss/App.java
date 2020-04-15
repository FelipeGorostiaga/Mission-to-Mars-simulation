package ar.edu.itba.ss;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class App {

    private static double dt;
    private static double time;
    private static double fps;

    private static final int SUN_ID = 0;

    private static double G = 6.673 * Math.pow(10, -11);
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

        // Get earth-sun angle
        assert planets != null;
        Planet earth = planets.get(0);
        double earthSunAngle = getEarthSunAngle(earth);
        // Get earth-sun velocity angle
        double velocityAngle = getEarthSunVelocityAngle(earth);

        // Check velocity with teacher?
        double spaceshipX = earth.getX() + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.cos(earthSunAngle);
        double spaceshipY = earth.getY() + (SPACESHIP_DISTANCE + EARTH_RADIUS) * Math.sin(earthSunAngle);
        double spaceshipVx = earth.getVx() + ((SPACESHIP_SPEED + SPACE_STATION_SPEED) * Math.cos(velocityAngle));
        double spaceshipVy = earth.getVy() + ((SPACESHIP_SPEED + SPACE_STATION_SPEED) * Math.sin(velocityAngle));

        // Add Sun and Spaceship
        planets.add(new Planet(SUN_ID, 0.0, 0.0, 0, 0, SUN_MASS, 0.14));
        planets.add(new Planet(4, spaceshipX, spaceshipY, spaceshipVx, spaceshipVy, SPACESHIP_MASS, 0.08));

        List<Planet> oldPlanets = clonePlanets(planets);
        initializePlanets(planets, oldPlanets);
        int iterations = 0;
        printPlanets(planets, iterations);

        int frame = 0;
        for(double t = 0 ; t < time ; t += dt) {
            oldPlanets = clonePlanets(planets);
            for (Planet p : planets) {
                if (p.getId() != SUN_ID) {
                    double[] force = force(p, oldPlanets);
                    p.setAx(force[0]);
                    p.setAy(force[1]);

                    p.setX(p.getX() + p.getVx() * dt + (2.0/3) * p.getAx() * Math.pow(dt,2) - (1.0/6) * p.getPrevAx() *
                            Math.pow(dt,2));
                    p.setY(p.getY() + p.getVy() * dt + (2.0/3) * p.getAy() * Math.pow(dt,2) - (1.0/6) * p.getPrevAy() *
                            Math.pow(dt,2));
                }
            }
            for (Planet p : planets) {
                if (p.getId() != SUN_ID) {

                    double[] newForce = force(p, planets);
                    double newAx = newForce[0];
                    double newAy = newForce[1];

                    p.setVx(p.getVx() + (1.0 / 3) * newAx * dt + (5.0 / 6) * p.getAx() * dt - (1.0/6) * p.getPrevAx() * dt);
                    p.setVy(p.getVy() + (1.0 / 3) * newAy * dt + (5.0 / 6) * p.getAy() * dt - (1.0/6) * p.getPrevAy() * dt);
                    p.setPrevAx(p.getAx());
                    p.setPrevAy(p.getPrevAy());
                }
            }

            if(frame++ % fps == 0) {
                printPlanets(planets, iterations++);
            }
        }

    }

    private static double getEarthSunAngle(Planet earth) {
        double earthSunAngle;
        if (earth.getX() == 0) {
            return Math.signum(earth.getY()) * Math.PI / 2;
        }
        else{
            earthSunAngle = Math.atan(earth.getY() / earth.getX());
            if ((earth.getX() < 0 && earth.getY() > 0) || (earth.getX() < 0 && earth.getY() < 0)){
                earthSunAngle += Math.PI;
            }
            return earthSunAngle;
        }
    }

    private static double getEarthSunVelocityAngle(Planet earth) {
        double velocityAngle;
        if (earth.getVx() == 0) {
            return Math.signum(earth.getVy()) * Math.PI / 2;
        }
        else {
            velocityAngle = Math.atan(earth.getVy() / earth.getVx());
            if ((earth.getVx() < 0 && earth.getVy() > 0) || (earth.getVx() < 0 && earth.getVy() < 0)) {
                velocityAngle += Math.PI;
            }
            return velocityAngle;
        }
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
        planet.setPrevAx(force[0]);
        planet.setPrevAy(force[1]);
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

    private static void printPlanets(List<Planet> planets, int iterations) {
        System.out.println(planets.size());
        System.out.println(iterations);

        for (Planet p : planets){
            double auX  = (p.getX() / 1000) / AU;
            double auY = (p.getY() / 1000) / AU;
            System.out.println(p.getId() + "\t" + auX + "\t" + auY + "\t" + p.getVx() + "\t" + p.getVy() + "\t" + p.getRadius());
        }
    }

}



