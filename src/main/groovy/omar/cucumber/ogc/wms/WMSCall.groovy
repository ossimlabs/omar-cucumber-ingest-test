package omar.cucumber.ogc.wms

import groovy.json.JsonSlurper
//import com.vividsolutions.jts.io.WKTReader
import geoscript.geom.io.WktReader
import groovy.time.TimeCategory
import groovy.time.TimeDuration
import omar.cucumber.ogc.wfs.WFSCall

import java.nio.charset.Charset

class WMSCall {
    String bbox
    URL wmsUrl
    String wktGeometryString
    Double elapsedTime

    WMSCall(){}
    URL getImage(wmsServer, height=512, width=512, return_image_type, bbox, filter) {

      HashMap wmsParams = [
        SERVICE:"WMS",
        REQUEST:"GetMap",
        LAYERS:"omar:raster_entry",
        BBOX:"${bbox}",
        SRS:"EPSG:4326",
        WIDTH:"${width}",
        HEIGHT:"${height}",
        FORMAT:"image/${return_image_type}",
        STYLES:URLEncoder.encode("""{"nullPixelFlip": false}""", Charset.defaultCharset().displayName()),
        TRANSPARENT:true,
        FILTER: URLEncoder.encode(filter, Charset.defaultCharset().displayName())
      ]

      String wmsParamsString = urlParamsToString(wmsParams)
      String wmsUrlString = "${wmsServer}/getMap?${wmsParamsString}"

      //start timer
      Date startWmsCall = new Date()

      //creates a URL object from the wms call
      wmsUrl = new URL(wmsUrlString.toString())

      //end timer
      Date endWmsCall = new Date()

      //calculate how long the process took
      TimeDuration duration = TimeCategory.minus( endWmsCall, startWmsCall )

      //convert the duration from milliseconds to seconds
      elapsedTime = duration.toMilliseconds()/1000

      //returns the URL object
      wmsUrl
    }//end getImage

    String getBBox(String wfsServer, String filter){

        def wfsCall = new WFSCall(wfsServer, filter, "JSON", 1)
        String wfsResult = wfsCall.getResultText()
        def jsonWfs = new JsonSlurper().parseText(wfsResult)

        if(jsonWfs){
          jsonWfs.features.each{ feature ->
            String polyType = feature.geometry.type.toUpperCase()
            switch(polyType){
              case ~/.*MULTIPOLYGON/:
                def tempValue =  feature.geometry.coordinates[0][0].collect(){"${it[0]} ${it[1]}"}.join(",").toString()
                wktGeometryString = "MultiPolygon(((${tempValue})))"
                break
              case ~/.*POLYGON/:
                def tempValue =  feature.geometry.coordinates[0][0].collect(){"${it[0]} ${it[1]}"}.join(",").toString()
                wktGeometryString = "Polygon((${tempValue}))"
                break
              default:
                wktGeometryString = ""
                break
            }
          }
        }

      def geom = new WktReader().read(wktGeometryString)
      def envelope = geom.envelopeInternal
      "${envelope.minX},${envelope.minY},${envelope.maxX},${envelope.maxY}"
    }//end getBBox

    static String urlParamsToString(HashMap urlParams)
    {
      urlParams.collect(){k,v->"${k}=${v}" }.join("&")?.toString()
    }//end urlParamsToString
}
