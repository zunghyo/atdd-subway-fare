package nextstep.subway.unit;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import nextstep.subway.path.domain.FareCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class FareCalculatorTest {

    private FareCalculator fareCalculator;

    @BeforeEach
    void setUp() {
        fareCalculator = new FareCalculator();
    }

    @ParameterizedTest
    @CsvSource({
        "5, 1250",
        "10, 1250",
        "11, 1350",
        "15, 1350",
        "50, 2050",
        "51, 2150",
        "60, 2250"
    })
    @DisplayName("거리에 따른 요금 계산")
    void calculateFareForVariousDistances(long distance, long expectedFare) {
        assertThat(fareCalculator.calculateFare(distance)).isEqualTo(expectedFare);
    }


    @ParameterizedTest
    @MethodSource("provideDistancesAndAdditionalFares")
    @DisplayName("거리와 추가 요금에 따른 총 요금 계산")
    void calculateTotalFareWithAdditionalFares(long distance, List<Long> additionalFares, long expectedTotalFare) {
        assertThat(FareCalculator.calculateFare(distance, additionalFares)).isEqualTo(expectedTotalFare);
    }

    private static Stream<Arguments> provideDistancesAndAdditionalFares() {
        return Stream.of(
            Arguments.of(10, Arrays.asList(0L, 500L, 900L), 2150L),
            Arguments.of(15, Collections.singletonList(700L), 2050L),
            Arguments.of(55, Arrays.asList(300L, 500L), 2650L),
            Arguments.of(5, Collections.emptyList(), 1250L),
            Arguments.of(60, Arrays.asList(1000L, 1500L), 3750L)
        );
    }

}