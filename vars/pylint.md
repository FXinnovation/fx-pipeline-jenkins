# pylint()
Will execute pylint and return the output.

## Input
The function takes a Map as input that follows the following pattern:
```
{
  dockerImage: 'fxinnovation/pylint:latest',
  options:     '',
  filePattern: ''                            # Required
}
```

## Output
String
