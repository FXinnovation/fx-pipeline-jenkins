# public-common-pipeline-jenkins

This repository contains a set of jenkins pipeline helpers.
Documentation for each helper is located next the library and will be available in Jenkins once the library is loaded.


### Run tests on linux (easy & dirty version)

```bash
cd src && find ../test -mindepth 2 -type f -print -exec groovy {} . \;
cd ..
```
