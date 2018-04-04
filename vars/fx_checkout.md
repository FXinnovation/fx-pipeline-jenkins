# fx_checkout()
This function allows you to checkout the repo and to fetch some information about it. Returns an object with the information.

## Input
N/A

## Output
```
  scmInfo: {
    commitId: String
    branch:   String
    tag:      String
  }
```

## Example
```
foo = fx_checkout()
```
