package fr.edf.distribution.health.check;

import com.pacoworks.rxtuples2.RxTuples;
import fr.edf.distribution.health.value.CompositeHealth;
import fr.edf.distribution.health.value.Health;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import org.javatuples.Pair;

import java.util.Collection;

public class CompositeHealthCheck implements HealthCheck {
    private final String name;
    private final Collection<HealthCheck> checks;

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

        Flowable<String> healthNames = healthCheck.map(HealthCheck::name);

        Flowable<? extends Health> healthValues = healthCheck
                .parallel()
                .runOn(Schedulers.io())
                .flatMap(hc -> hc.check().toFlowable())
                .sequential();

        return healthNames.zipWith(healthValues, RxTuples.<String, Health>toPair())
                .toMap(Pair::getValue0, Pair::getValue1)
                .map(CompositeHealth::new);
    }

}
