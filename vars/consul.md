# consul()

## consul.command()
Allows you to run a consul command, returns the output of the command.

### Input
The function takes a Map as input, map look for the following objects:
```
  httpAddr:    String, # Defaults to http://consul:8500
  command:     String, # Required
  version:     String, # Defaults to latest
  dockerImage: String  # Defaults to consul
```
### Output
This function returns stdout of the command.

### Example
```
output = consul.command(
  command: 'kv get foo'
)
```

## consul.put()
Function that allows you to put a value into consul's kv

### Input
The function takes a Map as input, map look for the following objects:
```
  httpAddr:    String, # Defaults to http://consul:8500
  key:         String, # Required
  value:       String, # Required
```
### Output
N/A

### Example
```
consul.put(
  key:   'foo',
  value: 'bar'
)
```

## consul.get()
This function allows you to retrieve a value from consul's kv

### Input
The function take a Map as input, map looks for the following objects:
```
  httpAddr:    String, # Defaults to http://consul:8500
  key:         String, # Required
```
### Output
The function return a String which will be the output of the command.

### Example
```
output = consul.put(
  key:   'foo'
)
```
