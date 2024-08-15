package nextstep.subway.path.application;

public interface FarePolicy {
    long calculateFare(long distance);
}