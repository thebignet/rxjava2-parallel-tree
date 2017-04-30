package fr.edf.distribution.health.value;

import java.util.Map;

public class CompositeHealth implements Health {
    private final Map<String, ? extends Health> healths;

    public CompositeHealth(Map<String, ? extends Health> healths) {
        this.healths = healths;
    }

    @Override
    public Status getStatus() {
        //TODO en fonction de la stratégie
        return Status.OK;
    }

    @Override
    public String toString() {
        return "CompositeHealth{" +
                "healths=" + healths +
                ", status="+getStatus()+
                '}';
    }
}
