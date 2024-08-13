package nextstep.subway.path.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.station.application.dto.StationResponse;

@Getter
@AllArgsConstructor
public class PathResponse {

    private List<StationResponse> stations;
    private Long distance;
    private Long duration;

    public PathResponse(List<StationResponse> stations, Long distance) {
        this.stations = stations;
        this.distance = distance;
    }
}
