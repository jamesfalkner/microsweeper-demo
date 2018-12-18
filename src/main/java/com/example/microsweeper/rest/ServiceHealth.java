import javax.inject.Inject;

import com.example.microsweeper.service.ScoreboardService;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

public class ServiceHealth implements HealthCheck {

    @Inject
    ScoreboardService svc;

    @Override
    public HealthCheckResponse call() {
        if (svc.getScoreboard() != null) {
            return HealthCheckResponse.named("service-health").up().build();

        } else {
            return HealthCheckResponse.named("service-health").withData("reason", "service unavailable").down().build();
        }
	}

}