# fxinnovation-common-pipeline-library
This repository contains some Jenkins pipeline library that are loaded in fx's Jenkins. 
It provides functions and helpers to write Jenkinsfiles more quickly and more dynamically.

Documentation for each function is described in a separate files next to the function.

### General good practices

- Do not install softwares in groovy files.
Directly install the needed software on the CI server or use containers.
