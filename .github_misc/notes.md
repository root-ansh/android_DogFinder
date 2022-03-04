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

sequential tasks :  lint> test > build
```yaml
name: validate commit on master
on:
  workflow_dispatch:
jobs:
  build:
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

      - name: Check  lint via gradle  -- gradle lint
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: lint

      - name: Run unit tests via -- gradle test 
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: test

      - name: Build code via -- gralde build
        uses: gradle/gradle-build-action@v2.1.3
        with:
          arguments: build

```