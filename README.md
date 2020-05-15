# omar-cucumber-ingest-test
Testing the ingest for https://omar-dev.ossim.io/omar-cucumber-ingest-test/ingest-test/

A java application that runs a cucumber test against the O2 environment sqs-stager service.

# Quickstart

## Gradle Commands
You'll need to set up an AWS config and credentials files for local testing.
    
    1. Create a directory in your home called `.aws`. Example `~/.aws`.
    
    2. Create a file called `config` in `~/.aws`. The below is an example of the `config` file.
        [default]
        region=us-east-1
        output=json
    
    3. Create a file called `credentials` in `~/.aws`. The below is an example of the `credentials` file.
        [default]
        aws_access_key_id=<the AWS key from an IAM user>
        aws_secret_access_key=<the AWS access key from an IAM user>

To run ingest test in Jenkins or locally
 ```
gradle run -PrunEnvironment="local"
 ```

To run multi ingest test in Jenkins or locally
 ```
gradle run -PrunEnvironment="local" -Pmulti=true
 ```

To build the fat jar
 ```
gradle
 ```

To create dockerfile
 ```
gradle createDockerfile
 ```

 To build the docker image (if the CURL_USERNAME and CURL_PASSWORD environment variable is set)
 ```
gradle buildImage
 ```

To build the docker image (if the CURL_USERNAME and CURL_PASSWORD environment variable is NOT set)
 ```
gradle buildImage -PcUname=<remove-raster-username> -PcPword=<remove-raster-password>
 ```

## Run Commands

To run the docker image
```
docker run -p 8080:8080 omar-cucumber-ingest-test:<tag>
```
To run the Fat Jar (jars must be at the same level of src directory)
```
java -jar <path to jar>/omar-cucumber-ingest-test-<version>.jar
```
or
```
java -DmainPath=src/main -DresourcePath=src/main/resources -DcUname=<username> -DcPword="<password>" -jar <path to jar>/omar-cucumber-ingest-test-<version>.jar
```

## Openshift Environment Variables
### Overides the values in the cucumber config file
- TARGET_DEPLOYMENT
- DOMAIN_NAME
- TEST_IMAGE_S3_BUCKET
- TEST_IMAGE_S3_BUCKET_URL
- SQS_STAGING_QUEUE
- RBT_CLOUD_ROOT_DIR

### Overides the values for CURL_USER_NAME and CURL_PASSWORD
- CURL_USER_NAME
- CURL_PASSWORD
