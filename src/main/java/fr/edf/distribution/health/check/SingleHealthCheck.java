package fr.edf.distribution.health.check;

import fr.edf.distribution.health.value.Health;
import fr.edf.distribution.health.value.SingleHealth;
import io.reactivex.Single;

public class SingleHealthCheck implements HealthCheck {
    private final String name;
    private final Single<SingleHealth> check;

    public SingleHealthCheck(String name, Single<SingleHealth> check) {
        this.name = name;
        this.check = check;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Single<? extends Health> check() {
        return check;
    }
}
