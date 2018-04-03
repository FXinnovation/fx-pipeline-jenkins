# command()
Will execute an shell command and return the output, or throws an error with the output as the message

## Input
| Name | Type | Description |
|------|------|-------------|
|command|String|Command to execute|

## Output
| Type | Description |
|------|-------------|
|String| stdout of the executed command |

## Example
```
output = command('echo "foo"') # output will equal "foo"
```
