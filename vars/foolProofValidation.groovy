Map call(List forbiddenApprovers = []) {
  numbers = [
    'zero',
    'one',
    'two',
    'three',
    'four',
    'five',
    'six',
    'seven',
    'eight',
    'nine'
  ]
  Random random = new Random()
  firstNumber = random.nextInt(10)
  secondNumber = random.nextInt(10)
  thirdNumber = random.nextInt(10)
  foolProofInput = input(
    message: """
Please enter the following numbers (without spaces, commas, etc) as digits:\n\n
${numbers[firstNumber]} ${numbers[secondNumber]} ${numbers[thirdNumber]}""",
    ok: 'Approve',
    parameters: [
      string(
        defaultValue: '',
        description: 'Example: `one two three` becomes `123`',
        name: 'Response',
        trim: true
      )
    ],
    submitterParameter: 'approver'
  )
  if ( foolProofInput.Response != "${firstNumber}${secondNumber}${thirdNumber}" ) {
    error("${foolProofInput.approver} did not put the digits for validation.")
  }

  if( forbiddenApprovers.contains(foolProofInput.approver )) {
    error("${foolProofInput.approver} is not an authorized approver.")
  }

  return foolProofInput
}
