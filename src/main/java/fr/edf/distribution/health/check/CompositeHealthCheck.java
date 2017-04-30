package fr.edf.distribution.health.check;

import fr.edf.distribution.health.value.CompositeHealth;
import fr.edf.distribution.health.value.Health;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import java.util.AbstractMap;
import java.util.Collection;

public class CompositeHealthCheck implements HealthCheck {
    private final String name;
    private final Collection<HealthCheck> checks;
    private Flowable<String> healthName;
    private Flowable<HealthCheck> healthCheck;

    public CompositeHealthCheck(String name, Collection<HealthCheck> checks) {
        this.name = name;
        this.checks = checks;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public Single<? extends Health> check() {
        Flowable<HealthCheck> healthCheck = Flowable.fromIterable(checks);
        Flowable<? extends Health> healthValue = healthCheck
                .parallel()
                .runOn(Schedulers.io())
                .flatMap(hc -> hc.check().toFlowable())
                .sequential();
        Flowable<String> healthName = healthCheck.map(HealthCheck::name);
        return healthValue.zipWith(healthName, (h, n) -> new AbstractMap.SimpleEntry<>(n, h))
                .toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)
                .map(CompositeHealth::new);
    }

}
