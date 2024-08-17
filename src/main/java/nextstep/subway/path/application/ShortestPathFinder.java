package nextstep.subway.path.application;

import java.util.List;
import lombok.AllArgsConstructor;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShortestPathFinder implements PathFinder {

    @Override
    public List<Station> find(List<Line> lines, Station source, Station target, PathType pathType) {

        validateSourceAndTargetStations(source, target);

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = createGraph(lines, pathType);
        GraphPath<Station, DefaultWeightedEdge> path = findShortestPath(
            source, target, graph);

        return path.getVertexList();
    }

    private void validateSourceAndTargetStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException(SubwayExceptionType.SOURCE_AND_TARGET_SAME);
        }
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> createGraph(List<Line> lines, PathType pathType) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertex(lines, graph);
        addEdge(lines, graph, pathType);
        return graph;
    }

    private void addVertex(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
            .flatMap(line -> line.getLineSections().getStations().stream())
            .forEach(graph::addVertex);
    }

    private void addEdge(List<Line> lines, WeightedMultigraph<Station, DefaultWeightedEdge> graph, PathType pathType) {
        lines.stream()
            .flatMap(line -> line.getLineSections().stream())
            .forEach(section -> {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                if (pathType == PathType.DISTANCE) {
                    graph.setEdgeWeight(edge, section.getDistance());
                    return;
                }
                graph.setEdgeWeight(edge, section.getDuration());
            });
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(
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
}
