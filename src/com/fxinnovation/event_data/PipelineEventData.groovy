package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class PipelineEventData implements EventDataInterface {
  private String checkoutDirectory
  private String checkoutCredentialID
  private String checkoutRepositoryURL
  private String checkoutTag
  private Boolean shouldLoginToDockerRegistry
  private String dockerRegistry
  private String dockerRegistryCredentialId

  PipelineEventData(String checkoutDirectory, String checkoutCredentialID, String checkoutRepositoryURL, String checkoutTag, Boolean shouldLoginToDockerRegistry, String dockerRegistry, String dockerRegistryCredentialId) {
    this.checkoutDirectory = checkoutDirectory
    this.checkoutCredentialID = checkoutCredentialID
    this.checkoutRepositoryURL = checkoutRepositoryURL
    this.checkoutTag = checkoutTag
    this.shouldLoginToDockerRegistry = shouldLoginToDockerRegistry
    this.dockerRegistry = dockerRegistry
    this.dockerRegistryCredentialId = dockerRegistryCredentialId
  }

  String getCheckoutDirectory() {
    return checkoutDirectory
  }

  void setCheckoutDirectory(String checkoutDirectory) {
    this.checkoutDirectory = checkoutDirectory
  }

  String getCheckoutCredentialID() {
    return checkoutCredentialID
  }

  void setCheckoutCredentialID(String checkoutCredentialID) {
    this.checkoutCredentialID = checkoutCredentialID
  }

  String getCheckoutRepositoryURL() {
    return checkoutRepositoryURL
  }

  void setCheckoutRepositoryURL(String checkoutRepositoryURL) {
    this.checkoutRepositoryURL = checkoutRepositoryURL
  }

  String getCheckoutTag() {
    return checkoutTag
  }

  void setCheckoutTag(String checkoutTag) {
    this.checkoutTag = checkoutTag
  }

  Boolean shouldLoginToDockerRegistry() {
    return shouldLoginToDockerRegistry
  }

  void setShouldLoginToDockerRegistry(Boolean shouldLoginToDockerRegistry) {
    this.shouldLoginToDockerRegistry = shouldLoginToDockerRegistry
  }

  String getDockerRegistry() {
    return dockerRegistry
  }

  void setDockerRegistry(String dockerRegistry) {
    this.dockerRegistry = dockerRegistry
  }

  String getDockerRegistryCredentialId() {
    return dockerRegistryCredentialId
  }

  void setDockerRegistryCredentialId(String dockerRegistryCredentialId) {
    this.dockerRegistryCredentialId = dockerRegistryCredentialId
  }
}
