package ru.willdes.nginxplus;

public class ServersModel {

    private String name, state;
    private int id, active, requests;

    public ServersModel(int id, String name, String state, int active, int requests) {
        this.id = id;
        this.name = name;
        this.state = state;
        this.active = active;
        this.requests = requests;
    }

    @Override
    public String toString() {
        return "ServersModel{" +
                "id='" + id + '\'' +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", active=" + active +
                ", requests=" + requests +
                '}';
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public int getActive() {
        return active;
    }

    public int getRequests() {
        return requests;
    }

}
