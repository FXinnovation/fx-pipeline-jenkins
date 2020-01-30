import com.fxinnovation.helper.ClosureHelper

def call(Map config = [:], Map closures = [:]){
  mapAttributeCheck(config, 'publish', Boolean, false)
  mapAttributeCheck(config, 'cookstyle', Map, [:])
  mapAttributeCheck(config, 'foodcritic', Map, [:])
  mapAttributeCheck(config, 'kitchen', Map, [:])

  closureHelper = new ClosureHelper(this, closures)

  closureHelper.executeWithinStage('preTest')

  stage('test'){
    cookstyle(config.cookstyle)
    foodcritic(config.foodcritic)
    kitchen.test(config.kitchen)
  }
  
  closureHelper.executeWithinStage('postTest')
  closureHelper.executeWithinStage('prePublish')

  if(config.publish) {
    println "Publish step is skipped!"
  }
  if(!closureHelper.isDefined) {
    println "“publish” closure is not defined. Publish step will be skipped!"
  }

  closureHelper.executeWithinStage('publish')
  closureHelper.executeWithinStage('postpublish')
}
