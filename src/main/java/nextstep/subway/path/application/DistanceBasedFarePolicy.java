package nextstep.subway.path.application;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DistanceBasedFarePolicy implements FarePolicy {
    private final int startDistance;
    private final int endDistance;

    @Override
    public long calculateFare(long distance) {
        if (distance <= startDistance) {
            return 0;
        }
        long extraDistance = Math.min(distance, endDistance) - startDistance;
        return (int) ((Math.ceil((extraDistance - 1) / 5) + 1) * 100);
    }
}
