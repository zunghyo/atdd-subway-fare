package nextstep.subway.path.application;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FareCalculator {
    private static final int BASE_FARE = 1250;
    private static final int BASE_DISTANCE = 10;
    private static final int MEDIUM_DISTANCE = 50;

    private final List<FarePolicy> farePolicies;

    public FareCalculator() {
        this.farePolicies = new ArrayList<>();
        farePolicies.add(new BaseFarePolicy(BASE_FARE));
        farePolicies.add(new DistanceBasedFarePolicy(BASE_DISTANCE, MEDIUM_DISTANCE));
        farePolicies.add(new LongDistanceFarePolicy(MEDIUM_DISTANCE));
    }

    public long calculateFare(long distance) {
        return farePolicies.stream()
            .mapToLong(policy -> policy.calculateFare(distance))
            .sum();
    }
}
