# packer()
This files contains functions to help you use Packer with Jenkinsfiles

## packer.validate()
This function will execute a `packer validate` with speicfied options and returns the output

## Input
The input is a Map that follows the following structure
```
syntaxOnly,   Boolean # Defaults to false
except,       String  # Defaults to false
only,         String  # Defaults to false
vars,         Array   # Defaults to []
varFile,      String  # Defaults to false
dockerImage,  String  # Defaults to 'fxinnovation/packer:latest'
templateFile, String  # Required 
```
## Output
This function returns a String that will be the output of the command.

## Example
```
packer.validate(
  templateFile: ./configuration.json,
  syntaxOnly:   true,
  vars:         [
    'foo=bar',
    'duck=coincoin'
  ]
)
```

## packer.build()
This function will execute a `packer build` with speicfied options and returns the output

## Input
The input is a Map that follows the following structure
```
color,           String,  # Defaults to 'true'
debug,           Boolean, # Defaults to false
except,          String,  # Defaults to false
only,            String,  # Defaults to false
force,           Boolean, # Defaults to false
machineReadable, Boolean, # Defaults to false
onError,         String,  # Defaults to 'abort'
parallel,        String,  # Defaults to 'true'
vars,            Array    # Defaults to []
varFile,         String,  # Defaults to false
dockerImage,     String,  # Defaults to 'fxinnovation/packer:latest'
TemplateFile,    String   # Required    
```
## Output
This function returns a String that will be the output of the command.

## Example
```
packer.build(
  templateFile: ./configuration.json,
  vars:         [
    'foo=bar',
    'duck=coincoin'
  ]
)
```
