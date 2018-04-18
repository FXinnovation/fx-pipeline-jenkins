# foodcritic()
This call will run foodcritic on your current path. It also returns the result as a String.

## Input
The function takes a Map in entry that follows the following pattern:
```
{
  dockerImage: String,  # Defaults to 'fxinnovation/chefdk:lastest'
  options:     String   # Defaults to ''
}
```

## Output
The function outputs a String which is the output of foodcritic. It can be used for further manipulation.

## Example
foodcritic(
  options:     '--some option',
  dockerImage: 'your/imagehere:tag'
)
```
