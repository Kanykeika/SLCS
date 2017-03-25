package com.kanykei.slcs;

public class Room {

    private int id;
    private String name;
    private String wake_time;
    private String sleep_time;
    private int state;

    public Room(int id, String name, int state, String wake_time, String sleep_time) {
        super();
        this.id = id;
        this.name = name;
        this.state = state;
        this.wake_time = wake_time;
        this.sleep_time = sleep_time;
    }

    // getters
    public int getId() {return id;}
    public String getName(){return name;}
    public String getWake(){return wake_time;}
    public String getSleep(){ return sleep_time; }
    public int getState() {return state;}

    // setters
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setWake(String wake_time) {this.wake_time = wake_time;}
    public void setSleep(String sleep_time) {this.sleep_time = sleep_time;}
    public void setState(int state) {
        this.state = state;
    }

}