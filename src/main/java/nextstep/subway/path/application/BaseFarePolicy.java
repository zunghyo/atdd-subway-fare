package nextstep.subway.path.application;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseFarePolicy implements FarePolicy {
    private final int baseFare;

    @Override
    public long calculateFare(long distance) {
        return baseFare;
    }
}