pipeline {
    agent any

    options {
        timestamps()
    }

    environment {
        GITHUB_USER = "NeutralPlasma"
        GITHUB_REPO = "MagicBees"
        GITHUB_PUBLIC_REPO = "MagicBees"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Repository Mining') {
            steps {
                withChecks(name: 'MagicBees', includeStage: true) {
                    // Discover baseline for delta/comparison
                    mineRepository()
                    gitDiffStat()
                }
            }
        }

        stage('Build') {
            when {
                anyOf {
                    branch 'main'
                    tag pattern: ".*", comparator: "REGEXP"
                }
            }
            steps {
                withChecks(name: 'MagicBees', includeStage: true) {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean :plugin:shadowJar'
                }
            }
        }

        stage('Publish API') {
            when {
                tag pattern: ".*", comparator: "REGEXP"
            }
            steps {
                script {
                    catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                        withChecks(name: 'MagicBees', includeStage: true) {
                            sh 'chmod +x ./gradlew'
                            sh './gradlew :api:publish'
                        }
                    }
                }
            }
        }

        stage('Release to GitHub') {
            when {
                tag pattern: ".*", comparator: "REGEXP"
            }
            steps {
                withChecks(name: 'MagicChests', includeStage: true) {
                    script {
                        def tagName = env.TAG_NAME
                        if (!tagName) {
                            tagName = env.GIT_TAG
                        }
                        if (!tagName) {
                            tagName = sh(
                                returnStdout: true,
                                script: 'git describe --tags --exact-match 2>/dev/null || true'
                            ).trim()
                        }
                        def commitSha = env.GIT_COMMIT
                        def releaseNotes = ""
                        try {
                            releaseNotes = generateChangelog(currentTag: tagName)
                        } catch (e) {
                            echo "Changelog generation failed: ${e}"
                            releaseNotes = "Automated release for ${tagName}"
                        }

                        def paperJar = sh(
                            returnStdout: true,
                            script: 'ls -t paper/build/libs/*.jar | head -n 1'
                        ).trim()

                        if (!tagName) {
                            error("Tag name not found. Ensure this is a tag build or TAG_NAME/GIT_TAG is set.")
                        }
                        if (!paperJar) {
                            error("No Paper shadow JAR found in paper/build/libs")
                        }

                        uploadToGithub(
                            credentials: 'GITHUB_JENKINS',
                            user: env.GITHUB_USER,
                            repository: env.GITHUB_REPO,
                            tag: tagName,
                            commitSha: commitSha,
                            description: releaseNotes,
                            artifacts: [
                                [name: "MagicBees-${tagName}.jar", path: paperJar]
                            ]
                        )
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'plugin/build/libs/*.jar,api/build/libs/*.jar', allowEmptyArchive: true, fingerprint: true
        }
    }
}
