package ar.edu.itba.ss;

public class Configuration {


    private double time;
    private double dt;
    private double fps;

    Configuration(double time, double dt, double fps) {
        this.time = time;
        this.dt = dt;
        this.fps = fps;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDt() {
        return dt;
    }

    public void setDt(double dt) {
        this.dt = dt;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }
}
