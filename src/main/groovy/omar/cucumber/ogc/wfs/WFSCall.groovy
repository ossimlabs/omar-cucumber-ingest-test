package omar.cucumber.ogc.wfs

import groovy.json.JsonSlurper
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.nio.charset.Charset
import java.security.SecureRandom
import java.security.cert.X509Certificate
import geoscript.geom.Bounds
import geoscript.geom.Geometry

class WFSCall
{
    def result
    String text

    WFSCall(){}

    static void initSsl()
    {
        TrustManager[] trustAllCerts = [
                new X509TrustManager() {
                    X509Certificate[] getAcceptedIssuers()
                    {
                        return null
                    }

                    void checkClientTrusted(X509Certificate[] certs, String authType)
                    {
                    }

                    void checkServerTrusted(X509Certificate[] certs, String authType)
                    {
                    }
                }
        ]

        SSLContext sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, new SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory())

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session)
            {
                return true
            }
        }
    }

    WFSCall(wfsServer, filter, outputFormat, maxFeatures)
    {
        HashMap wfsParams = [
                service     : "WFS",
                version     : "1.1.0",
                request     : "GetFeature",
                typeName    : "omar:raster_entry",
                resultType  : "results",
                outputFormat: outputFormat,
                filter      : URLEncoder.encode(filter, Charset.defaultCharset().displayName()),
                maxFeatures : maxFeatures,
                startIndex  : 0
        ]

        String wfsParamsString = urlParamsToString(wfsParams)
        String wfsUrlString = "${wfsServer}/getFeature?${wfsParamsString}"
        URL wfsText = new URL(wfsUrlString)

        text = wfsText.text
        result = outputFormat == "JSON" ? new JsonSlurper().parseText(text) : text
    }

    int getNumFeatures()
    {
        result.features.size()
    }

    int getLayer(Integer featureIdx = 0)
    {
        result.features[featureIdx].properties.id
    }

    String getName()
    {
        result.features
    }

    Integer getNumberOfResLevels(Integer featureIdx = 0)
    {
        result.features[featureIdx].properties.number_of_res_levels
    }

    Integer getWidth(Integer featureIdx = 0)
    {
        result.features[featureIdx].properties.width
    }

    Integer getHeight(Integer featureIdx = 0)
    {
        result.features[featureIdx].properties.height
    }

    String getFilename(Integer featureIdx = 0)
    {
        result.features[featureIdx].properties.filename
    }

    Double getGsd(Integer featureIdx = 0)
    {
        return result.features[featureIdx].properties.gsdy
    }

    Geometry getGeometry(Integer featureIdx = 0)
    {
        def feature = result.features[featureIdx]
        String wktGeometryString
        String polyType = feature.geometry.type.toUpperCase()
        switch (polyType)
        {
            case ~/.*MULTIPOLYGON/:
                def tempValue = feature.geometry.coordinates[0][0].collect() {
                    "${it[0]} ${it[1]}"
                }.join(",").toString()
                wktGeometryString = "MultiPolygon(((${tempValue})))"
                break
            case ~/.*POLYGON/:
                def tempValue = feature.geometry.coordinates[0][0].collect() {
                    "${it[0]} ${it[1]}"
                }.join(",").toString()
                wktGeometryString = "Polygon((${tempValue}))"
                break
            default:
                wktGeometryString = ""
                break
        }
        Geometry.fromWKT(wktGeometryString)
    }

    Bounds getBounds(Integer featureIdx = 0)
    {
        getGeometry(featureIdx).bounds
    }


    String urlParamsToString(HashMap urlParams)
    {
        urlParams.collect() { k, v -> "${k}=${v}" }.join("&")
    }

    String getResultText()
    {
        return text
    }
}
