package com.example.safensecurewheelchairbuddy;

public class Device
{
    private String name;
    private String id;

    public Device(String name, String id)
    {
        SetName(name);
        SetID(id);
    }

    public void SetName(String name)
    {
        this.name = name;
    }

    public void SetID(String id)
    {
        this.id = id;
    }

    public String GetName()
    {
        return name;
    }

    public String GetID()
    {
        return id;
    }
}
