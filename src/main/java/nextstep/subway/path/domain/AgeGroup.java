package nextstep.subway.path.domain;

import java.util.Arrays;
import java.util.function.LongUnaryOperator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AgeGroup {
    ADULT(19, 100, fare -> fare),
    TEENAGER(13, 19, fare -> {
        long discountedFare = fare - 350;
        return Math.max(discountedFare - (long)(discountedFare * 0.2), 0);
    }),
    CHILD(6, 13, fare -> {
        long discountedFare = fare - 350;
        return Math.max(discountedFare - (long)(discountedFare * 0.5), 0);
    });

    private final int minAge;
    private final int maxAge;
    private final LongUnaryOperator discountFunction;

    public long applyDiscount(long fare) {
        return discountFunction.applyAsLong(fare);
    }

    public static AgeGroup of(int age) {
        return Arrays.stream(AgeGroup.values())
            .filter(group -> age >= group.minAge && age < group.maxAge)
            .findFirst()
            .orElse(CHILD);
    }
}
