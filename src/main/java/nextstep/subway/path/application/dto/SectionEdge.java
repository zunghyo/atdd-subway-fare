package nextstep.subway.path.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.line.domain.entity.LineSection;
import org.jgrapht.graph.DefaultWeightedEdge;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SectionEdge extends DefaultWeightedEdge {
    private LineSection section;
}
