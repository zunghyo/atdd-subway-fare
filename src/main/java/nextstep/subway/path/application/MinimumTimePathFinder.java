package nextstep.subway.path.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class MinimumTimePathFinder implements PathFinder {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
        DefaultWeightedEdge.class);

    @Override
    public PathResponse find(List<Line> lines, Station source, Station target) {
        validateSourceAndTargetStations(source, target);
        setUpGraph(lines, graph);
        GraphPath<Station, DefaultWeightedEdge> path = findMinimumTimePath(
            source, target, graph);
        return getPathResponse(lines, path);
    }
    private static void validateSourceAndTargetStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException(SubwayExceptionType.SOURCE_AND_TARGET_SAME);
        }
    }

    private void setUpGraph(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        addVertex(lines, graph);
        addEdge(lines, graph);
    }

    private void addVertex(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
            .flatMap(line -> line.getLineSections().getStations().stream())
            .forEach(graph::addVertex);
    }

    private void addEdge(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
            .flatMap(line -> line.getLineSections().stream())
            .forEach(section -> {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDuration());  // 여기서 거리 대신 시간을 사용합니다.
            });
    }

    private GraphPath<Station, DefaultWeightedEdge> findMinimumTimePath(
        Station source, Station target, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        validateVerticesExist(graph, source, target);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);
        if (path == null) {
            throw new SubwayException(SubwayExceptionType.PATH_NOT_FOUND);
        }
        return path;
    }

    private void validateVerticesExist(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station source, Station target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new SubwayException(SubwayExceptionType.PATH_NOT_FOUND);
        }
    }

    private PathResponse getPathResponse(List<Line> lines, GraphPath<Station, DefaultWeightedEdge> path) {
        List<Station> stations = path.getVertexList();
        double duration = path.getWeight();
        long distance = calculateTotalDistance(lines, path);
        return new PathResponse(
            stations.stream().map(StationResponse::from).collect(Collectors.toList()),
            distance,
            (long) duration
        );
    }

    private long calculateTotalDistance(List<Line> lines, GraphPath<Station, DefaultWeightedEdge> path) {
        long totalDistance = 0;
        List<Station> stations = path.getVertexList();
        for (int i = 0; i < stations.size() - 1; i++) {
            Station source = stations.get(i);
            Station target = stations.get(i + 1);
            LineSection section = findSectionBetweenStations(lines, source, target);
            totalDistance += section.getDistance();
        }
        return totalDistance;
    }


    private LineSection findSectionBetweenStations(List<Line> lines, Station source, Station target) {
        return lines.stream()
            .flatMap(line -> line.getLineSections().stream())
            .filter(section -> section.getUpStation().equals(source) && section.getDownStation().equals(target))
            .findFirst()
            .orElseThrow(() -> new SubwayException(SubwayExceptionType.LINE_SECTION_NOT_FOUND));
    }

}
