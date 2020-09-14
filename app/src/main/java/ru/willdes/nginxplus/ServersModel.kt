package ru.willdes.nginxplus

class ServersModel(val name: String, val state: String, val active: Int, val requests: Int) {
    override fun toString(): String {
        return "ServersModel{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", active=" + active +
                ", requests=" + requests +
                '}'
    }

}