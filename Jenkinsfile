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
                stage ("Publish Docker App")
                        {
                            withCredentials([])
                                    {
                                        sh """
                   export DISPLAY=":1"
                   docker login $DOCKER_REGISTRY_URL \
                    --username=$ORG_GRADLE_PROJECT_dockerRegistryUsername \
                    --password=$ORG_GRADLE_PROJECT_dockerRegistryPassword
                   gradle pushDockerImage \
                       -PossimMavenProxy=${OSSIM_MAVEN_PROXY} \
                       -PbuildVersion=${dockerTagSuffixOrEmpty()}
                """
                                    }
                        }
            }

    stage("Clean Workspace") {
        if ("${CLEAN_WORKSPACE}" == "true")
            step([$class: 'WsCleanup'])
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
