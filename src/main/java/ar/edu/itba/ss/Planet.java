package ar.edu.itba.ss;


public class Planet implements Cloneable{

    int id;
    double x;
    double y;
    double vx;
    double vy;
    double mass;
    double prevX;
    double prevY;
    double ax;
    double ay;
    double prevAx;
    double prevAy;
    double radius;


    Planet(int id, double x, double y, double vx, double vy, double mass, double radius) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
    }

    Planet(int id, double x, double y, double vx, double vy){
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    Planet getClone() throws CloneNotSupportedException {
        return (Planet) super.clone();
    }

    @Override
    public String toString() {
        return "Planet{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", vx=" + vx +
                ", vy=" + vy +
                '}';
    }
}