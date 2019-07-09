# @staging_multi_ingest_service
# Feature: StagingService

Scenario Outline: [MSTG-01] Make a given IMAGE available for discovery
  Given the image <image-name> is not already staged - multi ingest
  When the image <image-name> AVRO message is placed on the SQS - multi ingest
  Then the image <image-name> should be discoverable - multi ingest

  Examples:
    | image-name |
    | 16MAY02111607-P1BS-055998375010_01_P014 |
    | 14SEP12113301-M1BS-053951940020_01_P001 |
    | 14SEP15TS0107001_100021_SL0023L_25N121E_001X___SVV_0101_OBS_IMAG |
    | 11MAR08WV010500008MAR11071429-P1BS-005707719010_04_P003 |
    | 05FEB09OV05010005V090205P0001912264B220000100282M_001508507 |
    | 05FEB09OV05010005V090205M0001912264B220000100072M_001508507 |
    | 04DEC11050020-M2AS_R1C1-000000185964_01_P001 |
