package nextstep.subway.line.domain.entity;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;

@Getter
@Embeddable
@NoArgsConstructor
public class Duration {

    private Long duration;

    public Duration(Long duration) {
        if (duration <= 0) {
            throw new SubwayException(SubwayExceptionType.INVALID_DURATION, duration.toString());
        }

        this.duration = duration;
    }

    public void updateDuration(Long duration) {
        if (duration <= 0) {
            throw new SubwayException(SubwayExceptionType.INVALID_DURATION, duration.toString());
        }
        this.duration = duration;
    }
}
