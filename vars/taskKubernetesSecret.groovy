def call(){
  standardJob(
    [
      pipeline: {
        timeout(20){
          // TODO: Add a namespace selector, we currently only use one name space.
          def selectedSecret = ''
          def actionInput = input(
            id: 'action',
            message: 'Please select desired action:',
            ok: 'Select',
            parameters: [
              choice(
                choices: 'create\ndelete',
                description: 'Select "create" to create a new secret, select "delete" to destroy an existing secret.',
                name: 'action'
              )
            ],
            submitterParameter: 'submitter'
          )
          if ('create' != actionInput.action){
            def secretList = readJSON(
              text: execute(
                hideStdout: true,
                script: 'kubectl get secrets --output=json'
              ).stdout
            ).items
            def secretOptions = ''
            secretList.each {
              secretOptions += "${it.metadata.name}\n"
            }
            selectedSecret = input(
              id: 'selectedSecret',
              message: "Please select the secret you would like to ${actionInput.action}",
              ok: 'Select',
              parameters: [
                choice(
                  choices: secretOptions,
                  description: "Select the secret you would like to ${actionInput.action}",
                  name: 'secret'
                )
              ],
              submitterParameter: 'submitter'
            ).secret
          }else{
            selectedSecret = input(
              id: 'enterSecret',
              message: 'Please insert secret name',
              ok: 'Validate',
              parameters: [
                string(
                  defaultValue: '',
                  description: 'Secrets must follow the following regex: [a-z0-9-]{1-63}',
                  name: 'secret',
                  trim: true
                )
              ],
              submitterParameter: 'submitter'
            ).secret
          }
          def secretKeyValues = []
          if ('delete' != actionInput.action){
            def addAnotherKey = true
            while (false != addAnotherKey) {
              currentKeyValue = input(
                id: 'secret',
                message: 'Please enter a k/v for your secret.',
                ok: 'Validate',
                parameters: [
                  string(
                    defaultValue: '',
                    description: 'Define the key for your secret.',
                    name: 'key',
                    trim: true
                  ),
                  text(
                    defaultValue: '',
                    description: 'Define the value of your secret.',
                    name: 'value'
                  )
                ],
                submitterParameter: 'submitter'
              )
              writeFile(
                file: "tmp/${currentKeyValue.key}",
                text: currentKeyValue.value
              )
              secretKeyValues.add("tmp/${currentKeyValue.key}")
              addAnotherKey = input(
                id: 'continue',
                message: 'continue',
                ok: 'Validate',
                parameters: [
                  booleanParam(
                    defaultValue: false,
                    description: 'Do you wish to add another key/value to the secret ?',
                    name: 'continue'
                  )
                ],
                submitterParameter: 'submitter'
              ).continue
            }
          }
          def optionsString = ''
          if ('create' == actionInput.action){
            optionsString += 'generic '
          }
          optionsString += "${selectedSecret} "
          secretKeyValues.each {
            optionsString += "--from-file=${it} "
          }
          execute(
            script: "kubectl ${actionInput.action} secret ${optionsString}"
          )
        }
      }
    ]
  )
}
