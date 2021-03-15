package com.fxinnovation.event_data

import com.fxinnovation.observer.EventDataInterface

class PipelineEventData implements EventDataInterface {
  private Boolean shouldLoginToDockerRegistry
  private String checkoutCredentialID
  private String checkoutDirectory
  private String checkoutRepositoryURL
  private String checkoutTag
  private String dockerDataBasepath
  private Boolean dockerDataIsCurrentDirectory
  private String dockerRegistry
  private String dockerRegistryCredentialId
  private String headerMessage
  private String preCommitDockerImageName

  PipelineEventData(
    Boolean shouldLoginToDockerRegistry,
    String checkoutCredentialID,
    String checkoutDirectory,
    String checkoutRepositoryURL,
    String checkoutTag,
    String dockerDataBasepath,
    Boolean dockerDataIsCurrentDirectory,
    String dockerRegistry,
    String dockerRegistryCredentialId,
    String headerMessage,
    String preCommitDockerImageName
  ) {
    this.shouldLoginToDockerRegistry = shouldLoginToDockerRegistry
    this.checkoutCredentialID = checkoutCredentialID
    this.checkoutDirectory = checkoutDirectory
    this.checkoutRepositoryURL = checkoutRepositoryURL
    this.checkoutTag = checkoutTag
    this.dockerDataBasepath = dockerDataBasepath
    this.dockerDataIsCurrentDirectory = dockerDataIsCurrentDirectory
    this.dockerRegistry = dockerRegistry
    this.dockerRegistryCredentialId = dockerRegistryCredentialId
    this.headerMessage = headerMessage
    this.preCommitDockerImageName = preCommitDockerImageName
  }

  Boolean getShouldLoginToDockerRegistry() {
    return shouldLoginToDockerRegistry
  }

  void shouldLoginToDockerRegistry(Boolean shouldLoginToDockerRegistry) {
    this.shouldLoginToDockerRegistry = shouldLoginToDockerRegistry
  }

  String getCheckoutCredentialID() {
    return checkoutCredentialID
  }

  void setCheckoutCredentialID(String checkoutCredentialID) {
    this.checkoutCredentialID = checkoutCredentialID
  }

  String getCheckoutDirectory() {
    return checkoutDirectory
  }

  void setCheckoutDirectory(String checkoutDirectory) {
    this.checkoutDirectory = checkoutDirectory
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

  String getDockerDataBasepath() {
    return dockerDataBasepath
  }

  void setDockerDataBasepath(String dockerDataBasepath) {
    this.dockerDataBasepath = dockerDataBasepath
  }

  Boolean dockerDataIsCurrentDirectory() {
    return dockerDataIsCurrentDirectory
  }

  void setDockerDataIsCurrentDirectory(Boolean dockerDataIsCurrentDirectory) {
    this.dockerDataIsCurrentDirectory = dockerDataIsCurrentDirectory
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

  String getHeaderMessage() {
    return headerMessage
  }

  void setHeaderMessage(String headerMessage) {
    this.headerMessage = headerMessage
  }

  String getPreCommitDockerImageName() {
    return preCommitDockerImageName
  }

  void setPreCommitDockerImageName(String preCommitDockerImageName) {
    this.preCommitDockerImageName = preCommitDockerImageName
  }
}
