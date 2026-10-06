<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# GCP Client Reuse Companion Changelog

## [Unreleased]

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

## [0.1.1]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.1.0]

### Added

- Warning icon on a Google Cloud client (StorageOptions,
  FirestoreOptions, BigQueryOptions, PubSubOptions, and similar) built
  via XxxOptions.getDefaultInstance().getService() inside a regular
  method instead of created once and reused -- matching Google Cloud's
  own client libraries best-practices guidance.
- 100% static text/PSI analysis, Java and Kotlin, no network calls,
  no telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/gcp-client-reuse-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/gcp-client-reuse-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/gcp-client-reuse-companion/commits/0.1.0
