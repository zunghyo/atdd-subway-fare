package nextstep.cucumber.steps;

import static nextstep.subway.acceptance.line.LineUtils.responseToStationNames;
import static nextstep.subway.acceptance.path.PathUtils.지하철역_최소시간_경로조회;
import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java8.En;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

public class TimeBasedPathStepDef implements En {

    private Map<String, Long> stationIds = new HashMap<>();
    private ExtractableResponse<Response> response;

    public TimeBasedPathStepDef() {
        When("사용자가 {string}에서 {string}까지의 최소 시간 기준으로 경로를 조회하면", (String start, String end) -> {
            Long startId = stationIds.getOrDefault(start, 1L);
            Long endId = stationIds.getOrDefault(end, 3L);
            response = 지하철역_최소시간_경로조회(startId, endId);
        });

        Then("최소 시간 기준 경로를 응답한다", () -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<String> stationNames = response.jsonPath().getList("stations.name");
            assertThat(stationNames).isNotEmpty();
        });

        Then("최소 시간 경로가 다음과 같이 조회된다", (io.cucumber.datatable.DataTable dataTable) -> {
            List<Map<String, String>> rows = dataTable.asMaps();
            List<String> expectedStations = rows.stream()
                .map(row -> row.get("역명"))
                .collect(Collectors.toList());
            List<String> actualStations = responseToStationNames(response);
            assertThat(actualStations).containsExactlyElementsOf(expectedStations);
        });

        Then("총 거리와 소요 시간을 함께 응답한다", () -> {
            int expectedDistance = 20;
            int expectedDuration = 2;

            assertThat(response.jsonPath().getInt("distance")).isEqualTo(expectedDistance);
            assertThat(response.jsonPath().getInt("duration")).isEqualTo(expectedDuration);
        });
    }
}
