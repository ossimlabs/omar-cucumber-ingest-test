package omar.cucumber.step_definitions

import omar.cucumber.config.CucumberConfig

import omar.cucumber.ogc.wfs.WFSCall

import java.nio.charset.Charset

this.metaClass.mixin(cucumber.api.groovy.Hooks)
this.metaClass.mixin(cucumber.api.groovy.EN)

String defaultCharset = Charset.defaultCharset().displayName()

config = CucumberConfig.config
def stagingService = config.stagingService

HashMap imageInfo

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

Given(~/^the image (.*) is not already indexed - direct s3 ingest$/) { String image ->

    imageInfo = getImageInfo(image)
    def filename = imageInfo.url
    println "Searching for ${filename}"

    def filter = "filename = '${filename}'"
    def wfsQuery = new WFSCall(config.wfsServerProperty, filter, "JSON", 1)
    def features = wfsQuery.result.features

    // if any files are found, delete them
    if (features.size() > 0) {
        println "... It's already indexed!"
        features.each() {
            String file = it.properties.filename
            println "Removing ${file} from database"
            def removeRasterUrl = "${stagingService}/removeRaster?filename=${URLEncoder.encode(file, defaultCharset)}"
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
            println "... Removed!"
        }

        // redo the WFS query to see if the files have been removed
        println "Checking to make sure they are removed..."
        wfsQuery = new WFSCall(config.wfsServerProperty, filter, "JSON", 1)

        features = wfsQuery.result.features
        if (features.size() == 0) { println "Image has been removed" }
        else { println "... doesn't look like it was removed." }
    }
    else { println "... Not indexed yet." }


    assert features.size() == 0
}

When( ~/^the image (.*) is indexed into OMAR - direct s3 ingest$/ ) { String image ->

    def filename = imageInfo.url
    def addRasterUrl = "${stagingService}/addRaster?buildOverviews=false&buildHistograms=false&background=false&filename=${URLEncoder.encode(filename, defaultCharset)}"
    def command = ["curl",
                            "-X",
                            "POST",
                            "${addRasterUrl}"
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

    println "addRaster Result: ${process.text}"
}

Then(~/^the image (.*) should be available - direct s3 ingest$/) { String image ->

    def filename = imageInfo.url
    def features

    println new Date()
    println "Has ${filename} been ingested?..."

    def filter = "filename = '${filename}'"
    def wfsQuery = new WFSCall(config.wfsServerProperty, filter, "JSON", 1)
    features = wfsQuery.result.features
    if (features.size() > 0) {
        println "... Yes!!!"
    }

    assert features.size() == 1
}
