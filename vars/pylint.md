# pylint()
Will execute pylint and return the output.

## Input
The function takes a Map as input that follows the following pattern:
```
[
  dockerImage: 'fxinnovation/pythonlinters:latest',
  options:     '',
  filePattern: '',                            # Required
  linterOptionsRepo: 'https://bitbucket.org/fxadmin/public-common-configuration-linters.git',
  linterOptionsRepoCredentialsId: '',         # Required
  linterOptionsRepoBranchPattern: '*/master'
]
```

## Output
String
