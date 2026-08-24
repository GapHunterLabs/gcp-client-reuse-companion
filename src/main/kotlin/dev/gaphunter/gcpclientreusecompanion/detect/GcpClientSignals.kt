package dev.gaphunter.gcpclientreusecompanion.detect

/**
 * Google Cloud client library "Options" class names this plugin
 * recognizes -- matched by simple name only, so it works whether the
 * real Google Cloud jar is on the classpath or not. Not exhaustive:
 * the most commonly used services only, all following the same
 * documented `XxxOptions.getDefaultInstance().getService()` shape
 * (google-cloud-java, "google-cloud-storage overview": "Storage
 * storage = StorageOptions.getDefaultInstance().getService();").
 */
object GcpClientSignals {
    val OPTIONS_CLASS_NAMES = setOf(
        "StorageOptions",
        "FirestoreOptions",
        "BigQueryOptions",
        "PubSubOptions",
        "DatastoreOptions",
        "SpannerOptions",
        "TranslateOptions",
        "LanguageServiceOptions",
        "ResourceManagerOptions",
        "DnsOptions",
    )
}
