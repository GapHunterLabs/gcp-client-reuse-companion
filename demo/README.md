# Demo data — GCP Client Reuse Companion

For capturing the real Marketplace screenshot:

1. `./gradlew runIde`
2. Open `demo/FileService.java` as a scratch/standalone file (or drop
   it into any sandbox project) inside the sandbox IDE.
3. The `StorageOptions.getDefaultInstance().getService()` call inside
   `uploadUnsafely` shows the warning — hover it for the tooltip.
   `uploadSafely`'s constructor-assigned instance stays clean, for
   contrast.
4. Enter Full Screen (`View > Appearance > Enter Full Screen`), capture
   with `Win+Shift+S`, save directly to `docs/screenshots/` in this
   repo.
