Feature: ModelDefinitionLoader loads model definition yaml document to ModelDefinition class.

  Scenario: Loads given Heimdall yaml document into a ModelDefinition class instance.
    Given The Heimdall Model is defined as below
      """
      default:
        policy:
          - role:
              - hierarchy: 15
              - application
          - subject:
              - user:
                  - organization
              - group
          - object
          - action
          - rule:
              - permit
              - recommend
              - oblige
              - prohibit
          - priority
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then A ModelDefinition is loaded into a map with key "default" and following values
      | role             | true                                |
      | roleHierarchy    | true                                |
      | maxRoleHierarchy | 15                                  |
      | application      | true                                |
      | user             | true                                |
      | organization     | true                                |
      | group            | true                                |
      | rules            | permit, recommend, oblige, prohibit |
      | object           | true                                |
      | action           | true                                |
      | priority         | true                                |













