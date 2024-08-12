package nextstep.cucumber.steps;

import io.cucumber.java8.En;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

import static nextstep.subway.acceptance.line.LineUtils.responseToStationNames;
import static nextstep.subway.acceptance.line.LineUtils.지하철노선_생성_후_ID_반환;
import static nextstep.subway.acceptance.line.SectionUtils.지하철구간_생성;
import static nextstep.subway.acceptance.path.PathUtils.지하철역_경로조회;
import static nextstep.subway.acceptance.station.StationUtils.지하철역_생성_후_id_추출;
import static org.assertj.core.api.Assertions.assertThat;

public class PathStepDef implements En {

    private Map<String, Long> stationIds = new HashMap<>();
    private Map<String, Long> lineIds = new HashMap<>();
    private ExtractableResponse<Response> response;

    public PathStepDef() {
        Given("다음과 같은 지하철 역들이 존재한다", (io.cucumber.datatable.DataTable dataTable) -> {
            List<Map<String, String>> stations = dataTable.asMaps();
            for (Map<String, String> station : stations) {
                String name = station.get("역명");
                Long id = 지하철역_생성_후_id_추출(name);
                stationIds.put(name, id);
            }
        });

        Given("다음과 같은 지하철 노선들이 존재한다", (io.cucumber.datatable.DataTable dataTable) -> {
            List<Map<String, String>> lines = dataTable.asMaps();
            for (Map<String, String> line : lines) {
                String name = line.get("노선명");
                String color = line.get("색상");
                Long startId = stationIds.get(line.get("시작역"));
                Long endId = stationIds.get(line.get("종착역"));
                Long distance = Long.parseLong(line.get("거리"));
                Long id = 지하철노선_생성_후_ID_반환(name, color, startId, endId, distance);
                lineIds.put(name, id);
            }
        });

        Given("{string}에 새로운 구간이 등록되어 있다",
            (String lineName, io.cucumber.datatable.DataTable dataTable) -> {
                Map<String, String> section = dataTable.asMaps().get(0);
                Long lineId = lineIds.get(lineName);
                Long startId = stationIds.get(section.get("시작역"));
                Long endId = stationIds.get(section.get("종착역"));
                Long distance = Long.parseLong(section.get("거리"));
                지하철구간_생성(lineId, startId, endId, distance);
            });

        Given("{string}이 존재한다", (String stationName) -> {
            Long id = 지하철역_생성_후_id_추출(stationName);
            stationIds.put(stationName, id);
        });

        When("사용자가 {string}에서 {string}까지의 경로를 조회하면", (String start, String end) -> {
            Long startId = stationIds.getOrDefault(start, -1L);
            Long endId = stationIds.getOrDefault(end, -1L);
            response = 지하철역_경로조회(startId, endId);
        });

        Then("최단 경로가 다음과 같이 조회된다", (io.cucumber.datatable.DataTable dataTable) -> {
            List<Map<String, String>> rows = dataTable.asMaps();
            List<String> expectedStations = rows.stream()
                .map(row -> row.get("역명"))
                .collect(Collectors.toList());
            List<String> actualStations = responseToStationNames(response);
            assertThat(actualStations).containsExactlyElementsOf(expectedStations);
        });

        Then("예외가 발생한다", () -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        });
    }
}