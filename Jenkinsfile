properties([
        parameters([
                string(name: 'BUILD_NODE', defaultValue: 'omar-build', description: 'The build node to run on'),
                booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: true, description: 'Clean the workspace at the end of the run'),
                string(name: 'DOCKER_REGISTRY_DOWNLOAD_URL', defaultValue: 'nexus-docker-private-group.ossim.io', description: 'Repository of docker images')
        ]),
        pipelineTriggers([
                [$class: "GitHubPushTrigger"]
        ]),
        [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/ossimlabs/omar-cucumber-ingest-test'],
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '20')),
        disableConcurrentBuilds()
])

podTemplate(
  containers: [
    containerTemplate(
      name: 'docker',
      image: 'docker:latest',
      ttyEnabled: true,
      command: 'cat',
      privileged: true
    ),
    containerTemplate(
      image: "${DOCKER_REGISTRY_DOWNLOAD_URL}/omar-builder:1.0.0",
      name: 'builder',
      command: 'cat',
      ttyEnabled: true
    ),
    containerTemplate(
      image: "${DOCKER_REGISTRY_DOWNLOAD_URL}/alpine/helm:3.2.3",
      name: 'helm',
      command: 'cat',
      ttyEnabled: true
    )
  ],
  volumes: [
    hostPathVolume(
      hostPath: '/var/run/docker.sock',
      mountPath: '/var/run/docker.sock'
    ),
  ]
)
{
timeout(time: 30, unit: 'MINUTES') {
    node(POD_LABEL) {

        stage("Checkout branch $BRANCH_NAME") {
            checkout(scm)
        }

        stage("Load Variables") {
            withCredentials([string(credentialsId: 'o2-artifact-project', variable: 'o2ArtifactProject')]) {
                step([$class     : "CopyArtifact",
                      projectName: o2ArtifactProject,
                      filter     : "common-variables.groovy",
                      flatten    : true])
            }
            load "common-variables.groovy"
        }

        try {
            container('builder'){
                stage("Run Test") {
                    sh """
                        export DISPLAY=":1"
                        ./gradlew run
                        ./gradlew assemble
                    """
                }
            }
        } finally {
            stage("Publish Report") {
                container('builder'){
                step([$class             : 'CucumberReportPublisher',
                      buildStatus        : 'FAILURE',
                      fileExcludePattern : '',
                      fileIncludePattern : '**/ingest.json',
                      ignoreFailedTests  : false,
                      jenkinsBasePath    : '',
                      jsonReportDirectory: "src/main/groovy/omar/webapp/reports/json",
                      parallelTesting    : false,
                      pendingFails       : false,
                      skippedFails       : false,
                      undefinedFails     : false])
                }
            }

            withCredentials([
                    [$class          : 'UsernamePasswordMultiBinding',
                     credentialsId   : 'curlCredentials',
                     usernameVariable: 'ORG_GRADLE_PROJECT_cUname',
                     passwordVariable: 'ORG_GRADLE_PROJECT_cPword'],
                    [$class          : 'UsernamePasswordMultiBinding',
                     credentialsId   : 'dockerCredentials',
                     usernameVariable: 'ORG_GRADLE_PROJECT_dockerRegistryUsername',
                     passwordVariable: 'ORG_GRADLE_PROJECT_dockerRegistryPassword']
            ]) {
                    stage('Docker build') {
                    container('docker') {
                        withDockerRegistry(credentialsId: 'dockerCredentials', url: "https://${DOCKER_REGISTRY_DOWNLOAD_URL}") {  //TODO
                        sh """
                            docker build --network=host -t "${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}"/omar-cucumber-ingest-test:${BRANCH_NAME} .
                        """
                        }
                    }
                    stage('Docker push'){
                        container('docker') {
                        withDockerRegistry(credentialsId: 'dockerCredentials', url: "https://${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}") {
                        sh """
                            docker push "${DOCKER_REGISTRY_PUBLIC_UPLOAD_URL}"/omar-cucumber-ingest-test:${BRANCH_NAME}
                        """
                        }
                        }
                    }
                    }
            }

            stage('Package chart'){
                container('helm') {
                    sh """
                        mkdir packaged-chart
                        helm package -d packaged-chart chart
                    """
                }
            }
            stage('Upload chart'){
                container('builder') {
                    withCredentials([usernameColonPassword(credentialsId: 'helmCredentials', variable: 'HELM_CREDENTIALS')]) {
                    sh "curl -u ${HELM_CREDENTIALS} ${HELM_UPLOAD_URL} --upload-file packaged-chart/*.tgz -v"
                    }
                }
            }

            stage("Clean Workspace") {
                if ("${CLEAN_WORKSPACE}" == "true")
                    step([$class: 'WsCleanup'])
            }
        }
    }
}
}
/**
 * Returns the docker image tag suffix, including the colon, or an empty string.
 *
 * @return Valid docker tag suffix, (e.g. ":someTag")
 */
String dockerTagSuffixOrEmpty() {
    // We want to use the branch name if built in a multi-branch pipeline.
    // Otherwise we want no tag to be used in order to not override the default tag.
    if (env.BRANCH_NAME != null) return "${env.BRANCH_NAME}" else return ""
}
