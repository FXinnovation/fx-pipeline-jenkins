def call() {
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
Please enter the following numbers (without spaces, commas):
${numbers[firstNumber]} ${numbers[secondNumber]} ${numbers[thirdNumber]}""",
    ok: 'Approve',
    parameters: [
      string(
        defaultValue: '',
        description: '',
        name: 'Response',
        trim: true
      )
    ],
    submitterParameter: 'approver'
  )
  if ( foolProofInput.Response != "${firstNumber}${secondNumber}${thirdNumber}") {
    error("${foolProofInput.approver} did not put the digits for validation.")
  }
}
