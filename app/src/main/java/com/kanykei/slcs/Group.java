package com.kanykei.slcs;

public class Group {

    private int id;
    private String name;
    private int room_id;
    private int state;

    public Group(int id, String name, int room_id, int state) {
        super();
        this.id = id;
        this.name = name;
        this.room_id = room_id;
        this.state = state;
    }
    // getters
    public int getId() {return id;}
    public String getName(){return name;}
    public int getRoom_id() {return room_id;}
    public int getState() {return state;}

    // setters
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setRoom_id(int room_id) {this.room_id = room_id;}
    public void setState(int state) {this.state = state;}

}