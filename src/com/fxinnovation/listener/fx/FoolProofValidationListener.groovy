package com.fxinnovation.listener.fx

import com.fxinnovation.event.TerraformEvents
import com.fxinnovation.event_data.TerraformEventData
import com.fxinnovation.observer.EventDataInterface
import com.fxinnovation.observer.EventSubscriber

class FoolProofValidationListener extends EventSubscriber {
  private Script context

    FoolProofValidationListener(Script context) {
    this.context = context
  }

  @Override
  List<String> getSubscribedEvents() {
    return [
      TerraformEvents.FOOL_PROOF_VALIDATION
    ]
  }

  /**
   * @param TerraformEventData eventData
   * @return TerraformEventData
   */
  EventDataInterface run(EventDataInterface eventData = null) {
    this.doRun()
    return eventData
  }

  private TerraformEventData doRun() {
    def numbers = [
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
    timeout(activity: true, time: 20) {
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
    }
  }
}
