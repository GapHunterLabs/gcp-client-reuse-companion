// Demo data for GCP Client Reuse Companion -- used with
// `./gradlew runIde` to capture the real Marketplace screenshot. Open
// this file, the warning should appear on the getDefaultInstance()
// line inside uploadUnsafely.

class FileService {

    private final Storage storage;

    FileService() {
        // Built once, in the constructor -- NOT flagged.
        this.storage = StorageOptions.getDefaultInstance().getService();
    }

    void uploadUnsafely(byte[] bytes) {
        // A new client built here on every call -- FLAGGED. Each
        // instance re-authenticates and spins up its own credential
        // cache.
        Storage storage = StorageOptions.getDefaultInstance().getService();
        storage.create(BlobInfo.newBuilder("bucket", "object").build(), bytes);
    }

    void uploadSafely(byte[] bytes) {
        // Reuses the instance built once in the constructor -- NOT
        // flagged.
        storage.create(BlobInfo.newBuilder("bucket", "object").build(), bytes);
    }
}
