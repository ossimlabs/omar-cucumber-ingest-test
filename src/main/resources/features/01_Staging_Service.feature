@staging_service
Feature: StagingService

    Scenario: [STG--02] Make a GeoEye MSI GeoTIFF image available for discovery
        Given a GeoEye MSI GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-04] Make a GeoEye MSI NITF21 image available for discovery
        Given a GeoEye MSI NITF21 is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-01] Make a GeoEye PAN GeoTIFF image available for discovery
        Given a GeoEye PAN GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-03] Make a GeoEye PAN NITF21 image available for discovery
        Given a GeoEye PAN NITF21 is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-17] Make an IKONOS PAN NITF image with error model available for discovery
        Given an IKONOS PAN NITF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    @C2S
    Scenario: Make an NTM IR NITF image with error model available for discovery
        Given an NTM IR NITF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    @C2S
    Scenario: Make an NTM PAN NITF image with error model available for discovery
        Given an NTM PAN NITF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    @C2S
    Scenario: Make an NTM SAR NITF image with error model available for discovery
        Given an NTM SAR NITF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-06] Make a QuickBird MSI GeoTIFF image available for discovery
        Given a QuickBird MSI GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-05] Make a QuickBird PAN GeoTIFF image available for discovery
        Given a QuickBird PAN GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    # Scenario: [STG-09] Make a RapidEye MSI GeoTIFF image available for discovery
    #     Given a RapidEye MSI GeoTIFF is not already staged
    #     When its AVRO message is placed on the SQS
    #     Then it should be discoverable
    #     And it should have a thumbnail
    #     And a WMS call should produce an image

    Scenario: [STG-10] Make a TerraSAR-X SAR NITF20 image available for discovery
        Given a TerraSAR-X SAR NITF20 is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-13] Make a WorldView2 MSI GeoTIFF image available for discovery
        Given a WorldView2 MSI GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-12] Make a WorldView2 PAN GeoTIFF image available for discovery
        Given a WorldView2 PAN GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: Make another WorldView2 PAN GeoTIFF image available for discovery
        Given another WorldView2 PAN GeoTIFF is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

    Scenario: [STG-14] Make a WorldView2 PAN NITF20 image available for discovery
        Given a WorldView2 PAN NITF20 is not already staged
        When its AVRO message is placed on the SQS
        Then it should be discoverable
        And it should have a thumbnail
        And a WMS call should produce an image

  Examples:
    | image-name |
    | 16MAY02111607-P1BS-055998375010_01_P014 |
    | 14SEP12113301-M1BS-053951940020_01_P001 |
    | 14SEP15TS0107001_100021_SL0023L_25N121E_001X___SVV_0101_OBS_IMAG |
    | 11MAR08WV010500008MAR11071429-P1BS-005707719010_04_P003 |
    | 05FEB09OV05010005V090205P0001912264B220000100282M_001508507 |
    | 05FEB09OV05010005V090205M0001912264B220000100072M_001508507 |
    | 04DEC11050020-M2AS_R1C1-000000185964_01_P001 |