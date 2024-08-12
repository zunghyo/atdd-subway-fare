package nextstep.subway.path.web;

import lombok.AllArgsConstructor;
import nextstep.subway.path.application.PathService;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.path.domain.PathType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
@AllArgsConstructor
public class PathController {

    private final PathService pathService;

    @GetMapping
    public ResponseEntity<PathResponse> findPath(@RequestParam Long source,
        @RequestParam Long target, @RequestParam PathType type) {
        if(type == PathType.DURATION) {
            return ResponseEntity.ok().body(pathService.findMinimumTimePath(source, target));
        }
        return ResponseEntity.ok().body(pathService.findShortestPath(source, target));
    }
}
