# ReportPortal Integration Guide for Java TestNG Maven Project

## Overview

ReportPortal is an open-source test automation reporting tool that provides:
- real-time execution dashboards
- test result aggregation
- logs, screenshots, attachments
- launch history and trends
- filtering by suite, test case, environment, build, etc.

This guide explains how to integrate ReportPortal with a Java Maven project using TestNG, and how to avoid storing sensitive information in source-controlled files.

---

## 1. What is ReportPortal?

ReportPortal is designed for automated testing and CI/CD pipelines. It helps teams:
- track test execution progress
- analyze failed tests quickly
- review logs and attachments
- correlate failures with builds and environments
- centralize results across multiple test runs

For Java automation projects, ReportPortal commonly integrates with:
- TestNG
- JUnit
- Maven / Gradle
- Jenkins / GitHub Actions / GitLab CI
- Logback / SLF4J / Log4j

---

## 2. Required Maven Dependencies

Use the ReportPortal TestNG agent and Logback integration.

Example `pom.xml` dependencies:

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <java.version>21</java.version>
    <maven.compiler.source>${java.version}</maven.compiler.source>
    <maven.compiler.target>${java.version}</maven.compiler.target>

    <reportportal.testng.version>5.6.8</reportportal.testng.version>
    <reportportal.logback.version>5.4.0</reportportal.logback.version>
    <logback.version>1.6.6</logback.version>
</properties>

<dependencies>
    <dependency>
        <groupId>com.epam.reportportal</groupId>
        <artifactId>agent-java-testng</artifactId>
        <version>${reportportal.testng.version}</version>
    </dependency>

    <dependency>
        <groupId>com.epam.reportportal</groupId>
        <artifactId>logger-java-logback</artifactId>
        <version>${reportportal.logback.version}</version>
    </dependency>

    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>${logback.version}</version>
    </dependency>

    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.11.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

This gives you:
- TestNG listener support for ReportPortal
- Logback integration for sending logs to ReportPortal

---

## 3. Maven Surefire Configuration

If your project uses TestNG suite XML, configure Surefire like this:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.5.4</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>src/test/resources/testng.xml</suiteXmlFile>
                </suiteXmlFiles>
            </configuration>
        </plugin>
    </plugins>
</build>
```

This ensures your TestNG suite file runs during Maven test execution.

---

## 4. Using `reportportal.properties`

The typical local setup is:

```properties
rp.endpoint=http://localhost:8082
rp.api.key=YOUR_API_KEY
rp.project=default_personal
rp.launch=Java launch
rp.description=TestNG run of SampleJavaProject
rp.attributes=build:local;env:qa
rp.enable=true
rp.reporting.async=true
```

Place this file in:

```text
src/test/resources/reportportal.properties
```

This file is convenient for local runs and quick setup.

### Example
```properties
# reportportal.properties
rp.endpoint = http://localhost:8082
rp.api.key = my-rp-api-key
rp.project = default_personal
rp.launch = Java launch
rp.description = TestNG run of SampleJavaProject
rp.attributes = build:local;env:qa
rp.enable = true
rp.reporting.async = true
```

### Important
Do not commit real API keys to GitHub, GitLab, or Bitbucket.

Use:
- environment variables
- Jenkins credentials
- GitHub Actions secrets
- secure CI/CD variable stores

---

## 5. Alternative: Pass ReportPortal Settings as System Properties

You can avoid `reportportal.properties` entirely and pass values directly via Maven command line.

### Local Maven execution
```bash
mvn test \
  -Drp.endpoint=http://localhost:8082 \
  -Drp.api.key=YOUR_REPORT_PORTAL_API_KEY \
  -Drp.project=default_personal \
  -Drp.launch="Java launch" \
  -Drp.description="TestNG run of SampleJavaProject" \
  -Drp.attributes="build:local;env:qa" \
  -Drp.enable=true
```

### Windows PowerShell
```powershell
mvn test `
  -Drp.endpoint="http://localhost:8082" `
  -Drp.api.key="YOUR_REPORT_PORTAL_API_KEY" `
  -Drp.project="default_personal" `
  -Drp.launch="Java launch" `
  -Drp.description="TestNG run of SampleJavaProject" `
  -Drp.attributes="build:local;env:qa" `
  -Drp.enable=true
```

### Why this is better for CI/CD
- no secret stored in repository
- values can be injected from secure variables
- easier to manage across multiple environments

---

## 6. Jenkins Integration Without a Properties File

Use Jenkins Credentials and pass values as environment variables.

### Jenkins pipeline example
```groovy
pipeline {
    agent any

    environment {
        RP_ENDPOINT = 'http://localhost:8082'
        RP_PROJECT = 'default_personal'
        RP_LAUNCH = 'Java launch'
        RP_DESCRIPTION = 'TestNG run of SampleJavaProject'
        RP_ATTRIBUTES = 'build:jenkins;env:qa'
        RP_API_KEY = credentials('reportportal-api-key')
    }

    stages {
        stage('Run TestNG Tests') {
            steps {
                bat '''
                    mvn test ^
                      -Drp.endpoint="%RP_ENDPOINT%" ^
                      -Drp.api.key="%RP_API_KEY%" ^
                      -Drp.project="%RP_PROJECT%" ^
                      -Drp.launch="%RP_LAUNCH%" ^
                      -Drp.description="%RP_DESCRIPTION%" ^
                      -Drp.attributes="%RP_ATTRIBUTES%" ^
                      -Drp.enable=true
                '''
            }
        }
    }
}
```

### Linux/macOS Jenkins pipeline
```groovy
pipeline {
    agent any

    environment {
        RP_ENDPOINT = 'http://localhost:8082'
        RP_PROJECT = 'default_personal'
        RP_LAUNCH = 'Java launch'
        RP_DESCRIPTION = 'TestNG run of SampleJavaProject'
        RP_ATTRIBUTES = 'build:jenkins;env:qa'
        RP_API_KEY = credentials('reportportal-api-key')
    }

    stages {
        stage('Run TestNG Tests') {
            steps {
                sh '''
                    mvn test \
                      -Drp.endpoint="$RP_ENDPOINT" \
                      -Drp.api.key="$RP_API_KEY" \
                      -Drp.project="$RP_PROJECT" \
                      -Drp.launch="$RP_LAUNCH" \
                      -Drp.description="$RP_DESCRIPTION" \
                      -Drp.attributes="$RP_ATTRIBUTES" \
                      -Drp.enable=true
                '''
            }
        }
    }
}
```

### Jenkins credential setup
1. Go to Jenkins
2. Manage Jenkins
3. Manage Credentials
4. Add a secret text credential
5. Name it: `reportportal-api-key`
6. Paste your ReportPortal API key

---

## 7. GitHub Actions Integration Without a Properties File

### Example workflow
```yaml
name: TestNG ReportPortal

on:
  push:
  pull_request:

jobs:
  test:
    runs-on: ubuntu-latest

    env:
      RP_ENDPOINT: ${{ secrets.RP_ENDPOINT }}
      RP_API_KEY: ${{ secrets.RP_API_KEY }}
      RP_PROJECT: default_personal
      RP_LAUNCH: Java launch
      RP_DESCRIPTION: TestNG run of SampleJavaProject
      RP_ATTRIBUTES: build:github;env:qa

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '21'
          cache: maven

      - name: Run tests
        run: |
          mvn test \
            -Drp.endpoint="$RP_ENDPOINT" \
            -Drp.api.key="$RP_API_KEY" \
            -Drp.project="$RP_PROJECT" \
            -Drp.launch="$RP_LAUNCH" \
            -Drp.description="$RP_DESCRIPTION" \
            -Drp.attributes="$RP_ATTRIBUTES" \
            -Drp.enable=true
```

### GitHub secrets to add
- `RP_ENDPOINT`
- `RP_API_KEY`

---

## 8. Common ReportPortal Properties

Here are the most common properties:

| Property | Description |
|---|---|
| `rp.endpoint` | ReportPortal server URL |
| `rp.api.key` | API key for authentication |
| `rp.project` | Project name in ReportPortal |
| `rp.launch` | Launch name |
| `rp.description` | Description for launch |
| `rp.attributes` | Attributes like `build:local;env:qa` |
| `rp.enable` | Enable or disable ReportPortal |
| `rp.reporting.async` | Async reporting mode |
| `rp.rerun` | Optional rerun support |
| `rp.launch.uuid` | Optional existing launch ID |

---

## 9. Recommended Best Practices

### Do this
- use secure secrets in CI/CD
- keep local properties only for development
- use `rp.attributes` for build and environment tracking
- separate test runs by launch name or build number
- attach logs/screenshots where needed

### Avoid this
- storing API keys in Git
- hardcoding server URLs in code
- using the same launch name for all builds without tags
- sharing a single project across unrelated environments without clear attributes

---

## 10. Troubleshooting

### Issue: Tests run but nothing appears in ReportPortal
Check:
- `rp.endpoint` is correct
- `rp.api.key` is valid
- `rp.project` exists
- `rp.enable=true`
- Maven is running with the correct system properties

### Issue: Authentication fails
Verify:
- API key is not expired
- project name is correct
- server URL is reachable

### Issue: Logs not visible
Ensure:
- Logback dependency is present
- the ReportPortal Logback integration is included
- application/test logs are actually emitted

### Issue: Build blocks or runs slow
Use:
- `rp.reporting.async=true`
- avoid excessive attachments in every step
- reduce unnecessary logging for large test suites

---

## 11. Summary

There are 3 common ways to integrate ReportPortal:

1. `reportportal.properties`
   - good for local or quick setup

2. Maven system properties
   - best for CI/CD and security

3. Jenkins/GitHub Actions environment variables
   - best for team pipelines and secret management

For production and team usage, the recommended approach is:
- store `RP_API_KEY` in CI/CD secrets
- pass `-Drp.*` properties at runtime
- keep source files free from sensitive data

---

## 12. Example Final Maven Command

```bash
mvn test \
  -Drp.endpoint=http://localhost:8082 \
  -Drp.api.key=YOUR_REPORT_PORTAL_API_KEY \
  -Drp.project=default_personal \
  -Drp.launch="Java launch" \
  -Drp.description="TestNG run of SampleJavaProject" \
  -Drp.attributes="build:local;env:qa" \
  -Drp.enable=true \
  -Drp.reporting.async=true
```

This is the most secure and portable method for running TestNG tests with ReportPortal.

---

## 13. Final Notes

ReportPortal works best when your test framework and CI/CD runtime are aligned. For a TestNG + Maven + Java project, the standard integration pattern is:
- add `agent-java-testng`
- add `logger-java-logback`
- pass ReportPortal values externally
- keep secrets in CI/CD credentials
- run tests through Maven Surefire