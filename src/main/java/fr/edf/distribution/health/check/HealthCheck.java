package fr.edf.distribution.health.check;

import fr.edf.distribution.health.value.Health;
import io.reactivex.Single;

public interface HealthCheck {
    String name();

    Single<? extends Health> check();
}
