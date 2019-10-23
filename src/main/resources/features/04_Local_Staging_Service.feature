@local_staging_service
Feature: StagingService

Scenario Outline: [LSTG-01] Make a given IMAGE available for discovery
  Given the image <image-name> is not already indexed - local ingest
  When the image <image-name> is indexed into OMAR - local ingest
  Then the image <image-name> should be available - local ingest

  Examples:
    | image-name |
    | 2012-06-11_18-20-11.HSI.Scan_00007.scene.corrected.hsi |