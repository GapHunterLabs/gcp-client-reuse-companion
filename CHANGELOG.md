<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# GCP Client Reuse Companion Changelog

## [Unreleased]

## [0.1.0]

### Added

- Warning icon on a Google Cloud client (StorageOptions,
  FirestoreOptions, BigQueryOptions, PubSubOptions, and similar) built
  via XxxOptions.getDefaultInstance().getService() inside a regular
  method instead of created once and reused -- matching Google Cloud's
  own client libraries best-practices guidance.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/gcp-client-reuse-companion/compare/0.1.0...HEAD
[0.1.0]: https://github.com/GapHunterLabs/gcp-client-reuse-companion/commits/0.1.0
