Feature: Lot 001

#

  Scenario Outline: Test xpath du scenario 1
    Given je charge scenario 1
    When je cherche <xpath>
    Then je dois trouver : <answer> entite

  Examples:
    | xpath           | answer |
    | "/root/a/b/c/"  |  1     |
    | "/root/a/"      |  1     |    