package nextstep.subway.path.domain;

import java.util.List;

public class FareCalculator {
    public static long calculateFare(long distance) {
        return FarePolicy.calculateTotalFare(distance);
    }
    public static long calculateFare(long distance, List<Long> additionalFares) {
        return FarePolicy.calculateTotalFare(distance, additionalFares);
    }
}
