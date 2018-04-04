# kitchen()
This call will run kitchen on your current path. It also returns the result as a String.

## Input
The function takes a Map in entry that follows the following pattern:
```
{
  debug:         Boolean, # Defaults to false
  dockerImage:   String,  # Defaults to 'fxinnovation/chefdk:lastest'
  options:       String,  # Defaults to '-D --force-default-config'
  dockerOptions: String   # Defaults to '-v /tmp:/tmp'
}
```

## Output
The function outputs a String which is the output of kitchen. It can be used for further manipulation.

## Example
kitchen(
  debug:         false,
  options:       '--some option',
  dockerImage:   'your/imagehere:tag',
  dockerOptions: '--some-other-option'
)
```
