def call(Map closures = [:], Map config = [:]){
  if (!closures.containsKey('preCookstyle')){
    closures.preCookstyle = {
      println 'bar'
    }
  }
  stage('pre-cookstyle'){
    closures.preCookstyle()
  }
  stage('cookstyle'){
    println 'toto'
    //cookstyle()
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
