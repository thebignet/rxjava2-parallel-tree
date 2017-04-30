package fr.edf.distribution.health.value;

public class SingleHealth implements Health {
    private final Status status;

    public SingleHealth(Status status) {
        this.status = status;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "SingleHealth{" +
                "status=" + status +
                '}';
    }
}
