package nextstep.subway.path.application;

import nextstep.subway.path.domain.FarePolicy;
import org.springframework.stereotype.Service;

@Service
public class FareCalculator {
    public long calculateFare(long distance) {
        return FarePolicy.calculateTotalFare(distance);
    }
}
