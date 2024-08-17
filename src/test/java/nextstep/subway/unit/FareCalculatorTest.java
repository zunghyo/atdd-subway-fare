package nextstep.subway.unit;

import nextstep.subway.path.domain.FareCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
}