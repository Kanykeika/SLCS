package com.kanykei.slcs;

public class Room {

    private int id;
    private String name;
    private String wake_time;
    private String sleep_time;
    private int state;
    private int relay_pin;


    private long delay_wake;
    private long delay_sleep;

    public Room(int id, String name, int state, String wake_time, String sleep_time, int relay_pin, long delay_wake, long delay_sleep) {
        super();
        this.id = id;
        this.name = name;
        this.state = state;
        this.wake_time = wake_time;
        this.sleep_time = sleep_time;
        this.relay_pin = relay_pin;
        this.delay_wake = delay_wake;
        this.delay_sleep = delay_sleep;
    }

    public Room(int id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    // getters
    public int getId() {return id;}
    public String getName(){return name;}
    public String getWake(){return wake_time;}
    public String getSleep(){ return sleep_time; }
    public int getState() {return state;}
    public int getRelayPin() {return relay_pin;}
    public long getDelay_wake() {
        return delay_wake;
    }

    public long getDelay_sleep() {
        return delay_sleep;
    }



    // setters
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setWake(String wake_time) {this.wake_time = wake_time;}
    public void setSleep(String sleep_time) {this.sleep_time = sleep_time;}
    public void setState(int state) {
        this.state = state;
    }
    public void setRelayPin(int state) { this.relay_pin = relay_pin; }

    public void setDelay_wake(long delay_wake) {
        this.delay_wake = delay_wake;
    }
    public void setDelay_sleep(long delay_sleep) {
        this.delay_sleep = delay_sleep;
    }
}