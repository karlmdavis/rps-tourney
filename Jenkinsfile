/**
 * <p>
 * This is the script that will be run by Jenkins to build and test this
 * project. This drives the project's continuous integration and delivery.
 * </p>
 * <p>
 * This script is run by Jenkins' Pipeline feature. A good intro tutorial for
 * working with Jenkins Pipelines can be found here:
 * <a href="https://jenkins.io/doc/book/pipeline/">Jenkins > Pipeline</a>.
 * </p>
 * <p>
 * The canonical Jenkins server job for this project is located here:
 * <a href="https://justdavis.com/jenkins/job/rps-tourney/">rps-tourney</a>.
 * </p>
 */

properties([
	disableConcurrentBuilds(),
	parameters([
		/*
		 * Benchmarks aren't run by default as they're only valid when the
		 * build server isn't busy with anything else.
		 */
		booleanParam(name: 'tests_skip', description: 'Whether to skip unit and integration tests.', defaultValue: false),
		booleanParam(name: 'deploy_non_master', description: 'Whether to deploy non-master-branch builds to production.', defaultValue: false),
		booleanParam(name: 'benchmarks_run', description: 'The app benchmarks will be run if this is set to true.', defaultValue: false),
		string(name: 'benchmarks_forks', description: 'How many forks to run of each benchmark.', defaultValue: '10'),
		string(name: 'benchmarks_iterations', description: 'How many measurement iterations to run of each benchmark (per fork).', defaultValue: '20')
	])
])

node {
	stage('Checkout') {
		// Grab the commit that triggered the build.
		checkout scm
	}

	stage('Build') {
		/*
		 * Only `master` branch builds should be installed to the local Maven
		 * repo or deployed to the Nexus repo.
		 */
		def goal = env.BRANCH_NAME == "master" ? "deploy" : "verify"
		
		// Run the build. Make sure to archive build results even if it fails.
		try {
			mvn "--update-snapshots -DskipTests=${params.tests_skip} -DskipITs=${params.tests_skip} clean ${goal}"
		} finally {
			/*
			 * Fingerprint the output artifacts and archive the test results.
			 * (Archiving the output artifacts here would waste space, as the
			 * build deploys them to the local Maven repository.)
			 */
			fingerprint '**/target/*.jar'
			fingerprint '**/target/*.war'
			junit testResults: '**/target/*-reports/TEST-*.xml', keepLongStdio: true, allowEmptyResults: true
			archiveArtifacts artifacts: '**/target/*-reports/*.txt', allowEmptyArchive: true
		}
	}

	stage('Benchmark') {
		if (params.benchmarks_run) {
			dir('rps-tourney-benchmarks') {
				java "-jar target/benchmarks.jar -foe true -rf json -rff target/jmh-result.json -f ${benchmarks_forks} -i ${benchmarks_iterations}"
				archiveArtifacts artifacts: 'target/jmh-result.json', allowEmptyArchive: true
			}
		}
	}

	stage('Quality Analysis') {
		/*
		 * The 'justdavis-sonarqube' SonarQube server will be sent the analysis
		 * results. See
		 * https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner+for+Jenkins
		 * for details. Note that the free version of SonarQube we're using
		 * doesn't separate results by branch.
		 */
		withSonarQubeEnv('justdavis-sonarqube') {
			mvn "org.sonarsource.scanner.maven:sonar-maven-plugin:3.4.0.905:sonar"
		}
	}

	stage('Deployment') {
		if (params.deploy_non_master || (env.BRANCH_NAME == 'master')) {
			dir('rps-tourney-deployment') {
			withPythonEnv('/usr/bin/python2.7') {
				pysh "pip install --upgrade setuptools"
				pysh "pip install --requirement requirements.txt"
				pysh "ansible-galaxy install --role-file=install_roles.yml --force"

				withCredentials([file(credentialsId: 'rps-tourney-ansible-vault-password', variable: 'vaultPasswordFile')]) {
				sshagent(['eddings-builds-ssh-private-key']) {
					vaultPasswordFileEscaped = vaultPasswordFile.replaceAll("/", "\\/")
					echo "vaultPasswordFile: ${vaultPasswordFile}"
					echo "vaultPasswordFileEscaped: ${vaultPasswordFileEscaped}"
					sh "sed --in-place 's/^vault_password_file = .*\$/vault_password_file = ${vaultPasswordFileEscaped}/g' ansible.cfg"
					sh "grep --quiet 'eddings.justdavis.com' ~/.ssh/known_hosts || ssh-keyscan -t rsa eddings.justdavis.com 2>/dev/null >> ~/.ssh/known_hosts"
					pysh "./ansible-playbook-wrapper site.yml --syntax-check"
					pysh "./ansible-playbook-wrapper site.yml"
				} }
			} }
		}
	}
}

/**
 * Runs Maven with the specified arguments.
 *
 * @param args the arguments to pass to <code>mvn</code>
 */
def mvn(args) {
	// This tool must be setup and named correctly in the Jenkins config.
	def mvnHome = tool 'maven-3'

	// Run the build, using Maven, with the appropriate config.
	configFileProvider(
			[
				configFile(fileId: 'justdavis:settings.xml', variable: 'MAVEN_SETTINGS'),
				configFile(fileId: 'justdavis:toolchains.xml', variable: 'MAVEN_TOOLCHAINS')
			]
	) {
		sh "${mvnHome}/bin/mvn --settings $MAVEN_SETTINGS --toolchains $MAVEN_TOOLCHAINS ${args}"
	}
}

/**
 * Runs Java with the specified arguments.
 *
 * @param args the arguments to pass to <code>java</code>
 */
def java(args) {
	// This tool must be setup and named correctly in the Jenkins config.
	def jdkHome = tool type: 'jdk', name: 'openjdk-8-jdk'

	sh "${jdkHome}/bin/java ${args}"
}
