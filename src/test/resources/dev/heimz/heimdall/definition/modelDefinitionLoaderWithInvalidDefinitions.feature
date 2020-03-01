Feature: ModelDefinitionLoader throws proper exceptions when invalid model definitions are provided.

  Scenario: Malformed YAML document at the start
    Given The Heimdall Model is defined as below
      """
      ,**model:
        - policy:
          - role
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Provided Heimdall model is a malformed YAML document!
      while parsing a block node
       in 'reader', line 1, column 1:
          ,**model:
          ^
      expected the node content, but found ','
       in 'reader', line 1, column 1:
          ,**model:
          ^

      """

  Scenario: Malformed YAML document in the middle
    Given The Heimdall Model is defined as below
      """
      model:
        - policy:
          - role
          *,#$#!
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Provided Heimdall model is a malformed YAML document!
      while scanning an alias
       in 'reader', line 4, column 5:
              *,#$#!
              ^
      expected alphabetic or numeric character, but found ,(44)
       in 'reader', line 4, column 6:
              *,#$#!
               ^

      """

  Scenario: Empty model definition
    Given The Heimdall Model is defined as below
      """
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Provided Heimdall model is either blank or empty!
      """

  Scenario: Blank model definition
    Given The Heimdall Model is defined as below
      """

      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Provided Heimdall model is either blank or empty!
      """

  Scenario: Multiple blank model definitions
    Given The Heimdall Model is defined as below
      """
      ---

      ---

      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Provided Heimdall model is either blank or empty!
      """

  Scenario: Invalid root object of type string
    Given The Heimdall Model is defined as below
      """
      asdf
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      The root object must be a map with at least one key, but was: String
      """

  Scenario: Invalid root object with multiple keys
    Given The Heimdall Model is defined as below
      """
      m1:
      m2:
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      The root object must be a map with only one key, but was: LinkedHashMap with 2 keys
      """

  Scenario: Neither 'use' or 'policy' object is defined
    Given The Heimdall Model is defined as below
      """
      model:
        someKey1:
          - someValue1
          - someValue2
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      Either 'use' or 'policy' object must be defined!
      """

  Scenario: 'policy' not having a sequence of objects
    Given The Heimdall Model is defined as below
      """
      model:
        policy:
          role:
          subject:
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      The policy object must be a sequence of objects with at least one object, but was: LinkedHashMap
      """

  Scenario: 2nd policy item object is a map but having 2 keys
    Given The Heimdall Model is defined as below
      """
      model:
        policy:
          - role:
            - hierarchy: 15
            - application
          - subject:
            invalidKey:
          - object
          - action
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      The policy-item-2 object must be a map with only one key, but was: LinkedHashMap with 2 keys
      """

  Scenario: 3rd rule item is an invalid rule
    Given The Heimdall Model is defined as below
      """
      model:
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
              - invalidRule
              - prohibit
          - priority
      """
    When The ModelDefinitionLoader loads the given Heimdall Model
    Then The ModelDefinitionException is thrown with following message
      """
      The rule-item-3 object must be one of [permit, recommend, oblige, prohibit], but was: invalidRule
      """