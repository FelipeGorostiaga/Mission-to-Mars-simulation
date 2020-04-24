package ar.edu.itba.ss;

public class BaseValues {


    double earthX;
    double earthY;
    double earthVx;
    double earthVy;
    double marsX;
    double marsY;
    double marsVX;
    double marsVy;
    double spaceshipX;
    double spaceshipY;
    double spaceshipVx;
    double spaceshipVy;
    double jupiterX;
    double jupiterY;
    double jupiterVx;
    double jupiterVy;




    BaseValues(Planet earth, Planet mars, Planet spaceship) {
        earthX = earth.x;
        earthY = earth.y;
        earthVx = earth.vx;
        earthVy = earth.vy;
        marsX = mars.x;
        marsY = mars.y;
        marsVX = mars.vx;
        marsVy = mars.vy;
        spaceshipX = spaceship.x;
        spaceshipY = spaceship.y;
        spaceshipVx = spaceship.vx;
        spaceshipVy = spaceship.vy;
    }

    BaseValues(Planet earth, Planet mars, Planet spaceship, Planet jupiter) {
        earthX = earth.x;
        earthY = earth.y;
        earthVx = earth.vx;
        earthVy = earth.vy;
        marsX = mars.x;
        marsY = mars.y;
        marsVX = mars.vx;
        marsVy = mars.vy;
        spaceshipX = spaceship.x;
        spaceshipY = spaceship.y;
        spaceshipVx = spaceship.vx;
        spaceshipVy = spaceship.vy;
        jupiterX = jupiter.x;
        jupiterY = jupiter.y;
        jupiterVx = jupiter.vx;
        jupiterVy = jupiter.vy;

    }

    public BaseValues(double earthX, double earthY, double earthVx, double earthVy, double marsX, double marsY, double marsVX, double marsVy) {
        this.earthX = earthX;
        this.earthY = earthY;
        this.earthVx = earthVx;
        this.earthVy = earthVy;
        this.marsX = marsX;
        this.marsY = marsY;
        this.marsVX = marsVX;
        this.marsVy = marsVy;
    }


    public void newBaseValues(Planet earth, Planet mars, Planet spaceship){
        earthX = earth.x;
        earthY = earth.y;
        earthVx = earth.vx;
        earthVy = earth.vy;
        marsX = mars.x;
        marsY = mars.y;
        marsVX = mars.vx;
        marsVy = mars.vy;
        spaceshipX = spaceship.x;
        spaceshipY = spaceship.y;
        spaceshipVx = spaceship.vx;
        spaceshipVy = spaceship.vy;
    }

    public void newBaseValues(Planet earth, Planet mars, Planet spaceship, Planet jupiter){
        earthX = earth.x;
        earthY = earth.y;
        earthVx = earth.vx;
        earthVy = earth.vy;
        marsX = mars.x;
        marsY = mars.y;
        marsVX = mars.vx;
        marsVy = mars.vy;
        spaceshipX = spaceship.x;
        spaceshipY = spaceship.y;
        spaceshipVx = spaceship.vx;
        spaceshipVy = spaceship.vy;
        jupiterX = jupiter.x;
        jupiterY = jupiter.y;
        jupiterVx = jupiter.vx;
        jupiterVy = jupiter.vy;
    }
}
