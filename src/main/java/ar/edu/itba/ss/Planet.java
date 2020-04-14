package ar.edu.itba.ss;


public class Planet implements Cloneable{

    private int id;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double mass;
    private double prevX;
    private double prevY;
    private double ax;
    private double ay;
    private double prevAx;
    private double prevAy;
    private double radius;
    private int[] colour;

    public Planet(int id, double x, double y, double vx, double vy, double mass, double radius, int[] colour) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.mass = mass;
        this.radius = radius;
        this.colour = colour;
    }

    public Planet(int id, double x, double y, double vx, double vy){
        this.id = id;
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getPrevX() {
        return prevX;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public double getAx() {
        return ax;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public double getAy() {
        return ay;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public double getPrevAx() {
        return prevAx;
    }

    public void setPrevAx(double prevAx) {
        this.prevAx = prevAx;
    }

    public double getPrevAy() {
        return prevAy;
    }

    public void setPrevAy(double prevAy) {
        this.prevAy = prevAy;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public int[] getColour() {
        return colour;
    }

    public void setColour(int[] colour) {
        this.colour = colour;
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