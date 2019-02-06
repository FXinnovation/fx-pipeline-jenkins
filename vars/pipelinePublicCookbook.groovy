def call(Map closures = [:], Map config = [:]){
  if (!closures.containsKey('preCookstyle')){
    closures.preCookstyle = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('postCookstyle')){
    closures.postCookstyle = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('preFoodcritic')){
    closures.preFoodcritic = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('postFoodcritic')){
    closures.postFoodcritic = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('preKitchen')){
    closures.preKitchen = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('postKitchen')){
    closures.postKitchen = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('prePublish')){
    closures.prePublish = {
      println 'nothing was specified'
    }
  }
  if (!closures.containsKey('postPublish')){
    closures.postPublish = {
      println 'nothing was specified'
    }
  }
  if (!config.containsKey('publish') || !(config.publish instanceof Boolean)){
    config.publish = false
  }
  if (!config.containsKey('stoveCredentialsId')){
    error ('stoveCredentialsId parameter is mandatory.')
  }

  stage('pre-cookstyle'){
    closures.preCookstyle()
  }
  stage('cookstyle'){
    cookstyle()
  }
  stage('post-cookstyle'){
    closures.postCookstyle()
  }
  stage('pre-foodcritic'){
    closures.preFoodcritic()
  }
  stage('foodcritic'){
    foodcritic()
  }
  stage('post-foodcritic'){
    closures.postFoodcritic()
  }
  stage('pre-kitchen'){
    closures.preKitchen()
  }
  stage('kitchen'){
    kitchen()
  }
  stage('post-kitchen'){
    closures.postKitchen()
  }
  stage('pre-publish'){
    closures.prePublish()
  }
  stage('publish'){
    if (config.publish){
      stove(
        credentialsId: config.stoveCredentialsId
      )
    }else{
      println "Publish step is skipped"
    }
  }
  stage('post-publish'){
    closures.postPublish()
  }
}
