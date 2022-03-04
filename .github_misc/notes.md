better gradle build solution
```
https://github.com/gradle/gradle-build-action 
```


possible names and events:
```yaml
name: github publish
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]
  schedule:
    #https://crontab.guru/
    - cron: '26 10 * * 1'

  # Allows you to run this workflow manually from the Actions tab
  workflow_dispatch:
```

another way to define branches:
```yaml
  push:
    branches:
      - 'master'

```

sequential tasks :  lint>get lint results > test > get test results > build > generate apk > upload generated apk 
```yaml
  lint_test_build_assemble:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - uses: actions/checkout@v2

      - name: set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: gradle

      - name: Check lint via marketplace gradle action (similar to gradle command `./gradlew lint`)
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: lint

      - name: Upload Lint results
        uses: actions/upload-artifact@v2
        with:
          name: lint_results
          path: app/build/reports/lint-results-debug.html

      - name: Run Tests ( marketplace gradle action similar to gradle command `./gradlew test`)
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: test

      - name: Upload Unit tests
        uses: actions/upload-artifact@v2
        with:
          name: unit-tests-results
          path: app/build/reports/tests/testDebugUnitTest/index.html


      - name: Build code ( marketplace gradle action similar to gradle command `./gradlew build`)
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: build

      - name: Generate apk ( marketplace gradle action similar to gradle command `./gradlew assembleDebug`)
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: assembleDebug

      - name: Upload APK via marketplace  action
        uses: actions/upload-artifact@v2
        with:
          name: app
          path: app/build/outputs/apk/debug/app-debug.apk
```

# deleting workflows
- simply remove from `.github/workflows` folder and they will not run. however the actions page 
  shows the history of past workflows. so for that we need to remove every previous workflow history
  
# impact of job step failure :
1. if the failure or success of step A does not affect the functioning of Step B:
  - run that function in parallel
2. if the failure or success of step A does effect the function of step B:
  - run that function in sequence
  - By default if any such step in sequence fails, the whole chain will fail and the job will be considered failed
  - if any step that can fail but should not affect the later chain, then later steps should be added in sequence with
    `if: always()` condition
    