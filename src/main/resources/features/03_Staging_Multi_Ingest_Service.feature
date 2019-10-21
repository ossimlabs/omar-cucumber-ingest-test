@staging_multi_ingest_service
Feature: StagingMultiIngestService

  Scenario: [STG-01] Make a GeoEye PAN GeoTIFF image available for discovery
    Given a GeoEye PAN GeoTIFF is not already staged - multi ingest
    When a GeoEye PAN GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a GeoEye PAN GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-02] Make a GeoEye MSI GeoTIFF image available for discovery
    Given a GeoEye MSI GeoTIFF is not already staged - multi ingest
    When a GeoEye MSI GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a GeoEye MSI GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-03] Make a GeoEye PAN NITF21 image available for discovery
    Given a GeoEye PAN NITF21 is not already staged - multi ingest
    When a GeoEye PAN NITF21 AVRO message is placed on the SQS - multi ingest
    Then a GeoEye PAN NITF21 should be discoverable - multi ingest

  Scenario: [STG-04] Make a GeoEye MSI NITF21 image available for discovery
    Given a GeoEye MSI NITF21 is not already staged - multi ingest
    When a GeoEye MSI NITF21 AVRO message is placed on the SQS - multi ingest
    Then a GeoEye MSI NITF21 should be discoverable - multi ingest

  Scenario: [STG-05] Make a QuickBird PAN GeoTIFF image available for discovery
    Given a QuickBird PAN GeoTIFF is not already staged - multi ingest
    When a QuickBird PAN GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a QuickBird PAN GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-06] Make a QuickBird MSI GeoTIFF image available for discovery
    Given a QuickBird MSI GeoTIFF is not already staged - multi ingest
    When a QuickBird MSI GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a QuickBird MSI GeoTIFF should be discoverable - multi ingest

#  Scenario: [STG-07] Make a RapidEye MSI GeoTIFF image available for discovery
#    Given a RapidEye MSI GeoTIFF is not already staged - multi ingest
#    When a RapidEye MSI GeoTIFF AVRO message is placed on the SQS - multi ingest
#    Then a RapidEye MSI GeoTIFF should be discoverable - multi ingest

#  Disable use of TerraSAR image until NITF's larger than 4k X 4k pixels are allowed.
#  Scenario: [STG-08] Make a TerraSAR-X SAR NITF20 image available for discovery
#    Given a TerraSAR-X SAR NITF20 is not already staged - multi ingest
#    When a TerraSAR-X SAR NITF20 AVRO message is placed on the SQS - multi ingest
#    Then a TerraSAR-X SAR NITF20 should be discoverable - multi ingest

  Scenario: [STG-09] Make another WorldView2 PAN GeoTIFF image available for discovery
    Given another WorldView2 PAN GeoTIFF is not already staged - multi ingest
    When another WorldView2 PAN GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then another WorldView2 PAN GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-10] Make a WorldView2 PAN GeoTIFF image available for discovery
    Given a WorldView2 PAN GeoTIFF is not already staged - multi ingest
    When a WorldView2 PAN GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a WorldView2 PAN GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-11] Make a WorldView2 MSI GeoTIFF image available for discovery
    Given a WorldView2 MSI GeoTIFF is not already staged - multi ingest
    When a WorldView2 MSI GeoTIFF AVRO message is placed on the SQS - multi ingest
    Then a WorldView2 MSI GeoTIFF should be discoverable - multi ingest

  Scenario: [STG-12] Make a WorldView2 PAN NITF20 image available for discovery
    Given a WorldView2 PAN NITF20 is not already staged - multi ingest
    When a WorldView2 PAN NITF20 AVRO message is placed on the SQS - multi ingest
    Then a WorldView2 PAN NITF20 should be discoverable - multi ingest

  @last
  Scenario: [STG-13] Make an IKONOS PAN NITF image with error model available for discovery
    Given an IKONOS PAN NITF is not already staged - multi ingest
    When an IKONOS PAN NITF AVRO message is placed on the SQS - multi ingest
    Then an IKONOS PAN NITF should be discoverable - multi ingest

  Examples:
    | image-name |
    | 16MAY02111607-P1BS-055998375010_01_P014 |
    | 14SEP12113301-M1BS-053951940020_01_P001 |
    | 14SEP15TS0107001_100021_SL0023L_25N121E_001X___SVV_0101_OBS_IMAG |
    | 11MAR08WV010500008MAR11071429-P1BS-005707719010_04_P003 |
    | 05FEB09OV05010005V090205P0001912264B220000100282M_001508507 |
    | 05FEB09OV05010005V090205M0001912264B220000100072M_001508507 |
    | 04DEC11050020-M2AS_R1C1-000000185964_01_P001 |