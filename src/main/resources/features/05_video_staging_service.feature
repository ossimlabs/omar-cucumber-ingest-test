@staging_service
Feature: StagingService

Scenario Outline: [STG-01] Make a given VIDEO available for discovery
  Given the video <video-name> is not already staged
  When an AWS Remote <video-name> video is indexed into OMAR
  Then the video <video-name> should be discoverable

  Examples:
    | video-name |
    | 21FEB03000019071saMISP-_HD000999 |
