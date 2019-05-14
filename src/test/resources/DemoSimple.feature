Feature: Lot 001

# 

  Scenario: Test compliqué
    Given je charge "toto"
    And je charge "tata"
    When je me repose 2 secondes
    Then je dois trouver "tototata"

