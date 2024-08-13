Feature: 지하철 경로 조회 기능
  Background:
    Given 다음과 같은 지하철 역들이 존재한다
      | 역명 |
      | 교대역 |
      | 강남역 |
      | 양재역 |
      | 남부터미널역 |
    And 다음과 같은 지하철 노선들이 존재한다
      | 노선명 | 색상 | 시작역 | 종착역 | 거리 | 소요시간 |
      | 2호선 | green | 교대역 | 강남역 | 10 | 1 |
      | 신분당선 | red | 강남역 | 양재역 | 10 | 1 |
      | 3호선 | orange | 교대역 | 남부터미널역 | 2 | 2 |
    And "3호선"에 새로운 구간이 등록되어 있다
      | 시작역 | 종착역 | 거리 | 소요시간 |
      | 남부터미널역 | 양재역 | 3 | 1 |

  Scenario: 경로 조회
    When 사용자가 "교대역"에서 "양재역"까지의 경로를 조회하면
    Then 최단 경로가 다음과 같이 조회된다
      | 역명 |
      | 교대역 |
      | 남부터미널역 |
      | 양재역 |

  Scenario: 출발역과 도착역이 같은 경우
    When 사용자가 "교대역"에서 "교대역"까지의 경로를 조회하면
    Then 예외가 발생한다

  Scenario: 연결되지 않은 역 사이의 경로 조회
    Given "역삼역"이 존재한다
    When 사용자가 "강남역"에서 "역삼역"까지의 경로를 조회하면
    Then 예외가 발생한다

  Scenario: 존재하지 않는 출발역으로 경로 조회
    When 사용자가 "존재하지 않는 역"에서 "남부터미널역"까지의 경로를 조회하면
    Then 예외가 발생한다

  Scenario: 존재하지 않는 도착역으로 경로 조회
    When 사용자가 "강남역"에서 "존재하지 않는 역"까지의 경로를 조회하면
    Then 예외가 발생한다