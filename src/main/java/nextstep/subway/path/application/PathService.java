package nextstep.subway.path.application;

import java.util.List;
import java.util.Map;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import nextstep.member.domain.LoginMember;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineSection;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.path.domain.AgeGroup;
import nextstep.subway.path.domain.FareCalculator;
import nextstep.subway.path.domain.PathType;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class PathService {

    private final PathFinder shortestPathFinder;
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final MemberRepository memberRepository;
    
    public PathResponse findShortestPath(Long sourceId, Long targetId, PathType pathType, LoginMember loginMember) {
        Station source = stationRepository.findByIdOrThrow(sourceId);
        Station target = stationRepository.findByIdOrThrow(targetId);
        List<Line> lines = lineRepository.findAll();

        List<Station> shortestPath = shortestPathFinder.find(lines, source, target, pathType);
        Map<String, LineSection> sectionMap = createSectionMap(lines);

        List<StationResponse> stationResponses = shortestPath.stream().map(StationResponse::from).collect(Collectors.toList());
        long totalDistance = calculateTotal(shortestPath, sectionMap, LineSection::getDistance);
        long totalDuration = calculateTotal(shortestPath, sectionMap, LineSection::getDuration);
        List<Long> additionalFares = collectAdditionalFares(shortestPath, sectionMap);

        long fare = FareCalculator.calculateFare(totalDistance, additionalFares);

        AgeGroup ageGroup = AgeGroup.ADULT;
        if(loginMember != null) {
            Member member = memberRepository.findByEmailOrElseThrow(loginMember.getEmail());
            ageGroup = AgeGroup.of(member.getAge());
        }

        return new PathResponse(
            stationResponses,
            totalDistance,
            totalDuration,
            ageGroup.applyDiscount(fare)
        );
    }

    public void existsPath(Long sourceId, Long targetId) {
        Station source = stationRepository.findByIdOrThrow(sourceId);
        Station target = stationRepository.findByIdOrThrow(targetId);
        List<Line> lines = lineRepository.findAll();

        shortestPathFinder.find(lines, source, target, PathType.DURATION);
    }

    private Map<String, LineSection> createSectionMap(List<Line> lines) {
        return lines.stream()
            .flatMap(line -> line.getLineSections().stream())
            .collect(Collectors.toMap(
                section -> section.getUpStation().getId() + "-" + section.getDownStation().getId(),
                section -> section
            ));
    }

    private long calculateTotal(List<Station> stations, Map<String, LineSection> sectionMap, ToLongFunction<LineSection> valueExtractor) {
        return IntStream.range(0, stations.size() - 1)
            .mapToLong(i -> {
                String key = stations.get(i).getId() + "-" + stations.get(i + 1).getId();
                LineSection section = sectionMap.get(key);
                if (section == null) {
                    throw new SubwayException(SubwayExceptionType.LINE_SECTION_NOT_FOUND);
                }
                return valueExtractor.applyAsLong(section);
            })
            .sum();
    }

    private List<Long> collectAdditionalFares(List<Station> stations, Map<String, LineSection> sectionMap) {
        return IntStream.range(0, stations.size() - 1)
            .mapToObj(i -> {
                String key = stations.get(i).getId() + "-" + stations.get(i + 1).getId();
                LineSection section = sectionMap.get(key);
                if (section == null) {
                    throw new SubwayException(SubwayExceptionType.LINE_SECTION_NOT_FOUND);
                }
                return section.getLineAdditionalFare();
            })
            .distinct()
            .collect(Collectors.toList());
    }
}
