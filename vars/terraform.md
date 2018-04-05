# terraform()
This file contains methods to help you use Terraform with Jenkinsfiles

## terraform.init()
This function will execute a `terraform init` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
backend        String   # Defaults to 'true'
backendConfig  String   # Defaults to false
fromModule     String   # Defaults to false
get            String   # Defaults to 'true'
getPlugins     String   # Defaults to 'true'
lock           String   # Defaults to 'true'
lockTimeout    String   # Defaults to '0s'
noColor        Boolean  # Defaults to false
pluginDirs     []String # Defaults to []
reconfigure    Boolean  # Defaults to false
upgrade        String   # Defaults to 'false'
verifyPlugins  String   # Defaults to 'true'
directory      String   # Defaults to './'
dockerImage    String   # Defaults to 'fxinnovation/terraform:latest'
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.init(
  get:       'false',
  directory: './some-dir/',
  noColor:   true
)
```

## terraform.validate()
This function will execute a `terraform validate` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
checkVariables String    # Defaults to 'true'
noColor        Boolean   # Defaults to false
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
directory      String    # Defaults to './'
vars           []String  # Defaults to []
varFile        String    # Defaults to false
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.validate(
  noColor:        true,
  directory:      './some-dir/',
  checkVariables: false
)
```

## terraform.plan()
This function will execute a `terraform plan` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
noColor        Boolean   # Defaults to false
vars           []String  # Defaults to []
varFile        String    # Defaults to false
directory      String    # Defaults to './'
out            String    # Defaults to false
destroy        Boolean   # Defaults to false
lock           String    # Defaults to 'true'
lockTimeout    String    # Defaults to '0s'
moduleDepth    String    # Defaults to '-1'
parallelism    String    # Defaults to '10'
refresh        String    # Defaults to 'true'
state          String    # Defaults to false
targets        []String  # Defaults to []
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.plan(
  noColor:        true,
  directory:      './some-dir/',
  state:          './terraform.tfstate',
  out:            './plan.terraform'
)
```

## terraform.apply()
This function will execute a `terraform apply` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
noColor        Boolean   # Defaults to false
vars           []String  # Defaults to []
varFile        String    # Defaults to false
directory      String    # Defaults to './'
plan           String    # Defaults to false
destroy        Boolean   # Defaults to false
lock           String    # Defaults to 'true'
lockTimeout    String    # Defaults to '0s'
parallelism    String    # Defaults to '10'
refresh        String    # Defaults to 'true'
state          String    # Defaults to false
stateOut       String    # Defaults to false
targets        []String  # Defaults to []
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
backup         String    # Defaults to false
```

#### Notes

* If you specifiy `plan`, the `directory` input will be ignored. Please take a look at terraform documentation for more information.

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.apply(
  noColor:        true,
  state:          './terraform.tfstate',
  stateOut:       './plan.terraform'
)
```

## terraform.taint()
This function will execute a `terraform destroy` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
allowMissing   Boolean   # Defaults to false
noColor        Boolean   # Defaults to false
lock           String    # Defaults to 'true'
lockTimeout    String    # Defaults to '0s'
module         String    # Defaults to false
state          String    # Defaults to false
stateOut       String    # Defaults to false
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
backup         String    # Defaults to false
resource       String    # Required
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.taint(
  noColor:        true,
  state:          './terraform.tfstate',
  stateOut:       './plan.terraform'
  resource:       'id_of_the_resource'
)
```

## terraform.untaint()
This function will execute a `terraform untaint` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
allowMissing   Boolean   # Defaults to false
noColor        Boolean   # Defaults to false
lock           String    # Defaults to 'true'
lockTimeout    String    # Defaults to '0s'
module         String    # Defaults to false
state          String    # Defaults to false
stateOut       String    # Defaults to false
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
backup         String    # Defaults to false
resource       String    # Required
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.untaint(
  noColor:        true,
  state:          './terraform.tfstate',
  stateOut:       './plan.terraform'
  resource:       'id_of_the_resource'
)
```

## terraform.destroy()
This function will execute a `terraform destroy` with speicfied options and returns the output

### Input
The input is a Map that follows the following structure
```
noColor        Boolean   # Defaults to false
vars           []String  # Defaults to []
varFile        String    # Defaults to false
directory      String    # Defaults to './'
destroy        Boolean   # Defaults to false
lock           String    # Defaults to 'true'
lockTimeout    String    # Defaults to '0s'
parallelism    String    # Defaults to '10'
refresh        String    # Defaults to 'true'
state          String    # Defaults to false
stateOut       String    # Defaults to false
targets        []String  # Defaults to []
dockerImage    String    # Defaults to 'fxinnovation/terraform:latest'
backup         String    # Defaults to false
```

### Output
This function returns a String that will be the output of the command.

### Example
```
output = terraform.destroy(
  noColor:        true,
  state:          './terraform.tfstate',
  stateOut:       './plan.terraform'
)
```
