package nextstep.subway.path.domain;


public class FareCalculator {
    public static long calculateFare(long distance) {
        return FarePolicy.calculateTotalFare(distance);
    }
}
