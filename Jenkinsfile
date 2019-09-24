properties([
    parameters ([
        string(name: 'BUILD_NODE', defaultValue: 'omar-build', description: 'The build node to run on'),
        string(name: 'TARGET_DEPLOYMENT', defaultValue: 'dev', description: 'The deployment to run the tests against'),
        booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: true, description: 'Clean the workspace at the end of the run'),
    ]),
    pipelineTriggers([
            [$class: "GitHubPushTrigger"]
    ]),
    [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/ossimlabs/omar-cucmber-ingest-test'],
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '20')),
    disableConcurrentBuilds()
])

String gradleTask

gradleTask = "buildDockerImage"



node("${BUILD_NODE}"){

    stage("Checkout branch $BRANCH_NAME")
    {
        checkout(scm)
    }

        stage("Load Variables")
        {
            withCredentials([string(credentialsId: 'o2-artifact-project', variable: 'o2ArtifactProject')]) {
                step ([$class: "CopyArtifact",
                    projectName: o2ArtifactProject,
                    filter: "common-variables.groovy",
                    flatten: true])
                step ([$class: "CopyArtifact",
                    projectName: o2ArtifactProject,
                    filter: "cucumber-configs/cucumber-config-ingest.groovy",
                    flatten: true])
            }
            load "common-variables.groovy"
        }

        stage ("Build Docker Image")
        {
            withCredentials([[$class: 'UsernamePasswordMultiBinding',
                        credentialsId: 'curlCredentials',
                        usernameVariable: 'CURL_USER_NAME',
                        passwordVariable: 'CURL_PASSWORD']])
            {
                sh """
                    echo "TARGET_DEPLOYMENT = ${TARGET_DEPLOYMENT}"
                    export CUCUMBER_CONFIG_LOCATION="cucumber-config-ingest.groovy"
                    export DISPLAY=":1"
                    gradle ${gradleTask}
                """
            }
        }

        stage("Clean Workspace") {
            if ("${CLEAN_WORKSPACE}" == "true")
            step([$class: 'WsCleanup'])
        }
}
