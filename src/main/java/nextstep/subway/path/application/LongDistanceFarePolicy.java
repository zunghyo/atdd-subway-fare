package nextstep.subway.path.application;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LongDistanceFarePolicy implements FarePolicy{
    private final int startDistance;

    @Override
    public long calculateFare(long distance) {
        if (distance <= startDistance) {
            return 0;
        }
        long extraDistance = distance - startDistance;
        return (int) ((Math.ceil((extraDistance - 1) / 8) + 1) * 100);
    }
}
