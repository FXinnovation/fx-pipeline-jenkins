# public-common-pipeline-jenkins

This repository contains a set of jenkins pipeline helpers.
Documentation for each helper is located next the library and will be available in Jenkins once the library is loaded.


### Run tests on linux

Testing needs gradle.
For convenience, use https://scm.dazzlingwrench.fxinnovation.com/fxinnovation-public/docker-gradle

```bash
gradle test
```

### Refactoring to do

* Everything prefixed `fx` should go into its own repository
* All deprecated calls should be changed
* Everything not prefixed `standard` or `pipeline` should go into classes or listeners
