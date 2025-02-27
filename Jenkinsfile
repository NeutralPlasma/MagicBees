pipeline {
	agent any
	environment {
		gradleVersion = 'gradle8'
		gradleHome = "${tool gradleVersion}"
	}


	stages {

		stage('Checkout') {
			steps {
				git url: 'https://github.com/NeutralPlasma/MagicBees.git', branch: 'master'
			}
		}


		stage('Build'){

			steps {
				withCredentials([
				usernamePassword(credentialsId: 'NEXUS1', usernameVariable: 'NEXUS1_USERNAME', passwordVariable: 'NEXUS1_PASSWORD')
				]) {
					sh 'chmod +x gradlew'
					sh '''
						export NEXUS1_USERNAME=${NEXUS1_USERNAME}
						export NEXUS1_PASSWORD=${NEXUS1_PASSWORD}
						./gradlew clean
						./gradlew build --info  --stacktrace
					'''
				}
			}

		}

	}

	post {
		always {
			archiveArtifacts artifacts: '**/plugin/build/libs/MagicBees.jar', allowEmptyArchive: true
		}
	}
}