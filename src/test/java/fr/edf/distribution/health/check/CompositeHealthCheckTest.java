package fr.edf.distribution.health.check;

import fr.edf.distribution.health.value.Health;
import fr.edf.distribution.health.value.SingleHealth;
import fr.edf.distribution.health.value.Status;
import io.reactivex.Single;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CompositeHealthCheckTest {

    public static final int WAIT_1 = 1000;
    public static final int WAIT_2 = 2000;
    public static final int WAIT_5 = 5000;

    public static Single<SingleHealth> singleWithStatusAfterWaiting(Status status, int wait) {
        return Single.fromCallable(() -> {
            Thread.sleep(wait);
            return new SingleHealth(status);
        });
    }

    @Test
    public void shouldProcessInParallel() {
        HealthCheck toto = new SingleHealthCheck("toto", singleWithStatusAfterWaiting(Status.OK, WAIT_1));
        HealthCheck titi = new SingleHealthCheck("titi", singleWithStatusAfterWaiting(Status.UNSTABLE, WAIT_2));
        HealthCheck tata = new SingleHealthCheck("titi", singleWithStatusAfterWaiting(Status.FAIL, WAIT_5));
        HealthCheck tutu = new CompositeHealthCheck("tutu", Collections.singleton(toto));
        HealthCheck tyty = new CompositeHealthCheck("tyty", Arrays.asList(toto, tata, titi));
        HealthCheck global = new CompositeHealthCheck("global", Arrays.asList(tutu, tyty));

        long startNanos = System.nanoTime();
        Health health = global.check().blockingGet();
        System.out.println(health);
        long deltaNanos = System.nanoTime() - startNanos;
        long deltaMillis = deltaNanos / 1_000_000;
        System.out.println("Duration = " + deltaMillis);
        assertThat(deltaMillis).isLessThan(WAIT_1 + WAIT_2 + WAIT_5);
    }
}