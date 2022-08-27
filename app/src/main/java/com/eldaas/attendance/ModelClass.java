package com.eldaas.attendance;

public class ModelClass {
    String Name, ID, inTime,outTime;

    ModelClass() { }

    public ModelClass(String name, String ID, String inTime, String outTime) {
        Name = name;
        this.ID = ID;
        this.inTime = inTime;
        this.outTime = outTime;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getInTime() {
        return inTime;
    }

    public void setInTime(String inTime) {
        this.inTime = inTime;
    }

    public String getOutTime() {
        return outTime;
    }

    public void setOutTime(String outTime) {
        this.outTime = outTime;
    }
}
