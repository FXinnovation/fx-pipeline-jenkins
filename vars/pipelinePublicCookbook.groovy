def call(Map config = [:], Map closures = [:]){
  if (!closures.preCookstyle){
    closures.preCookstyle = {
      println 'preCookstyle stage is not defined, doing nothing'
    }
  }

  stage('pre-cookstyle'){
    closures.preCookstyle()
  }
  stage('cookstyle'){
    cookstyle()
  }
  //stage('post-cookstyle'){
  //  closures.postCookstyle()
  //}
  //stage('pre-foodcritic'){
  //  closures.preFoodcritic()
  //}
  //stage('foodcritic'){
  //  foodcritic()
  //}
  //stage('post-foodcritic'){
  //  closures.postFoodcritic()
  //}
  //stage('pre-kitchen'){
  //  closures.preKitchen()
  //}
  //stage('kitchen'){
  //  kitchen()
  //}
  //stage('post-kitchen'){
  //  closures.postKitchen()
  //}
  //stage('pre-publish'){
  //  closures.prePublish()
  //}
  //stage('publish'){
  //  if (config.publish){
  //    stove(
  //      credentialsId: config.stoveCredentialsId
  //    )
  //  }else{
  //    println "Publish step is skipped"
  //  }
  //}
  //stage('post-publish'){
  //  closures.postPublish()
  //}
}
