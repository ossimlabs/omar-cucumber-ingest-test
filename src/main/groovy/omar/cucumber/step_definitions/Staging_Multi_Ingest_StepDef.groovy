package omar.cucumber.step_definitions

import com.amazonaws.services.sqs.AmazonSQSClient
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import omar.cucumber.config.CucumberConfig

import omar.cucumber.ogc.wfs.WFSCall

import java.nio.charset.Charset
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

String defaultCharset = Charset.defaultCharset().displayName()

config = CucumberConfig.config
def bucketUrl = config.s3BucketUrl
def imageBucket = config.s3Bucket
def stagingService = config.stagingService
def wfsServer = config.wfsServerProperty
def waitForStageMultiIngest = config.waitForStageMultiIngest ?: 30
def sqsTimestampName = config.sqsTimestampName
Set<String> imageIds = new HashSet<>()

def imageId
HashMap imageInfo

msg_image_id_field = config.image_id_field_name
msg_observation_date_time_field = config.observation_date_time_field_name
msg_url_field = config.url_field_name

String getAvroMessage(String imageId, String url, String observationDateTime) {
  String messageStr= """{"Message":{\"${msg_image_id_field}\":\"${imageId}\",\"${msg_observation_date_time_field }\":\"${observationDateTime}\",\"${msg_url_field}\":\"${url}\"}}"""
  println messageStr
  return messageStr
}

HashMap getImageInfo(String id) {
    HashMap fileInfo = [:]
    config.image_files.each{ imagesList -> 
        imagesList.getValue().images.each{ imageData ->
            imageData.each { imageInformation ->
                if (id == imageInformation.getValue().image_id)
                {
                    fileInfo.image_id = imageInformation.getValue().image_id.toString()
                    fileInfo.observation_time = imageInformation.getValue().observation_time.toString()
                    fileInfo.url = imageInformation.getValue().url.toString()
                }
            }
        }         
    }

    return fileInfo
}

Given(~/^the image (.*) is not already staged - multi ingest$/) { String image ->

        imageInfo = getImageInfo(image)
        imageId = imageInfo.image_id
        println "Searching for ${imageId}"

        def filter = "filename LIKE '%${imageId}%'"
        def wfsQuery = new WFSCall(wfsServer, filter, "JSON", 1)
        def features = wfsQuery.result.features

        // if any files are found, delete them
        if (features.size() > 0)
        {
            println "... It's already staged!"
            features.each() {
                def filename = it.properties.filename
                println "Deleting ${filename}"
                def removeRasterUrl = "${stagingService}/removeRaster?deleteFiles=true&filename=${URLEncoder.encode(filename, defaultCharset)}"
                def command = ["curl",
                                        "-u",
                                        "${config.curlUname}",
                                        "-X",
                                        "POST",
                                        "${removeRasterUrl}"
                                    ]
                /*
                    add an ArrayList called curlOptions to the config file if
                    addition info needs to be added to the curl command.
                */
                if (config?.curlOptions)
                {
                    command.addAll(1, config.curlOptions)
                }
                println command
                def process = command.execute()
                process.waitFor()
                println "... Deleted!"
            }

            // redo the WFS query to see if the files have been removed
            println "Checking to make sure they are deleted..."
            wfsQuery = new WFSCall(wfsServer, filter, "JSON", 1)

            features = wfsQuery.result.features
            if (features.size() == 0)
            {
                println "... Yup, it's gone!"
            }
            else
            {
                println "... Uh oh, doesn't look like it was deleted."
            }
        }
        else
        {
            println "... Not staged yet."
        }

        assert features.size() == 0
}

When(~/^the image (.*) AVRO message is placed on the SQS - multi ingest$/) { String image ->
        println "Sending ${imageId}'s AVRO message to the SQS"

        String text = getAvroMessage(imageInfo.image_id, imageInfo.url, imageInfo.observation_time)

        LocalDateTime now = LocalDateTime.now()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        String formattedDate = now.format(formatter)

        def json = new JsonSlurper().parseText(text)
        //json."${sqsTimestampName}" = new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", TimeZone.getTimeZone("UTC"))
        json."${sqsTimestampName}" = formattedDate
        def newSqsText = new JsonBuilder(json).toString()

        def sqs = AmazonSQSClient.newInstance()
        sqs.sendMessage(config.sqsStagingQueue, newSqsText)

        println "... Sent!"
}

Then(~/^the image (.*) should be discoverable - multi ingest$/) { String image ->

        println "Adding ${imageId} to list of images to check for"
        imageIds.add(imageId)
        assert true
}

After("@last") {
    println "Checking status of images being ingested"
    def timer = 60 * waitForStageMultiIngest
    def allImagesIngested = false
    while (timer > 0)
    {
        sleep(10000)
        allImagesIngested = true
        def imagesIngested = 0
        imageIds.each {
            def filter = "filename LIKE '%${it}%'"
            try {
                def wfsQuery = new WFSCall(wfsServer, filter, "JSON", 1)
                features = wfsQuery.result.features
                if (features.size() > 0)
                {
                    println "Successfully ingested ${it}"
                    imagesIngested++
                }
                else
                {
                    allImagesIngested = false
                }
            } catch (Exception e) {
                allImagesIngested = false
                println "Failed WFS call for image ${it}"
            }
        }
        println "Ingested ${imagesIngested} of ${imageIds.size()} images\n\n\n"
        timer -= 10
        if (allImagesIngested) timer = 0
    }

    if (allImagesIngested) println "Successfully ingested ALL images"
    else println "Failed to ingest some images"
}
