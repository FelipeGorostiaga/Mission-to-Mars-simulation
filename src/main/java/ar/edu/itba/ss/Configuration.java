package ar.edu.itba.ss;

public class Configuration {


    private double time;
    private double dt;
    private double fps;
    private String dynamicFile;

    Configuration(double time, double dt, double fps, String dynamicFile) {
        this.time = time;
        this.dt = dt;
        this.fps = fps;
        this.dynamicFile = dynamicFile;
    }

    public String getDynamicFile() {
        return dynamicFile;
    }

    public void setDynamicFile(String dynamicFile) {
        this.dynamicFile = dynamicFile;
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
