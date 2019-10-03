properties([
    parameters ([
        string(name: 'BUILD_NODE', defaultValue: 'omar-build', description: 'The build node to run on'),
        string(name: 'TARGET_DEPLOYMENT', defaultValue: 'dev', description: 'The deployment to run the tests against'),
        booleanParam(name: 'CLEAN_WORKSPACE', defaultValue: true, description: 'Clean the workspace at the end of the run'),
    ]),
    pipelineTriggers([
            [$class: "GitHubPushTrigger"]
    ]),
    [$class: 'GithubProjectProperty', displayName: '', projectUrlStr: 'https://github.com/ossimlabs/omar-cucumber-ingest-test'],
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '3', daysToKeepStr: '', numToKeepStr: '20')),
    disableConcurrentBuilds()
])

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

    withCredentials([
                       [$class: 'UsernamePasswordMultiBinding',
                       credentialsId: 'curlCredentials',
                       usernameVariable: 'ORG_GRADLE_PROJECT_cUname',
                       passwordVariable: 'ORG_GRADLE_PROJECT_cPword'],
                       [$class: 'UsernamePasswordMultiBinding',
                       credentialsId: 'dockerCredentials',
                       usernameVariable: 'ORG_GRADLE_PROJECT_dockerRegistryUsername',
                       passwordVariable: 'ORG_GRADLE_PROJECT_dockerRegistryPassword']
                    ])
    {
        stage ("Build Docker Image")
        {
            sh """
                echo "TARGET_DEPLOYMENT = ${TARGET_DEPLOYMENT}"
                export CUCUMBER_CONFIG_LOCATION="cucumber-config-ingest.groovy"
                export DISPLAY=":1"
                gradle buildDockerImage
            """
        }

        stage ("Publish Docker App")
        {
            withCredentials([])
            {
                sh """
                   docker login $DOCKER_REGISTRY_URL \
                    --username=$ORG_GRADLE_PROJECT_dockerRegistryUsername \
                    --password=$ORG_GRADLE_PROJECT_dockerRegistryPassword

                   gradle pushDockerImage \
                       -PossimMavenProxy=${OSSIM_MAVEN_PROXY}
                """
            }
        }
    }

    stage("Clean Workspace") {
        if ("${CLEAN_WORKSPACE}" == "true")
        step([$class: 'WsCleanup'])
    }
}
