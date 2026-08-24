# GCP Client Reuse Companion

Gutter warning icon on a Google Cloud client library
(`StorageOptions`, `FirestoreOptions`, `BigQueryOptions`, `PubSubOptions`,
and similar) built via `XxxOptions.getDefaultInstance().getService()`
inside a regular method body — Google Cloud's own client libraries
best-practices documentation says: "you should reuse the same client
object for many requests when possible, instead of creating a new one
for every request", explaining that each client instance has its own
credential cache and that "creating too many in a small period of time
may incur rate limiting causing library requests to fail
authentication".

## Why it exists

`Storage storage = StorageOptions.getDefaultInstance().getService();`
compiles fine and returns a working client — call it once per request
handler and every single call quietly re-authenticates and spins up a
new credential cache, instead of reusing the one client instance the
application already has.

## Why built this way

- **100% static text/PSI analysis** — matches the options class name
  by simple text, so it works whether the real Google Cloud jar is on
  the classpath or not. Java and Kotlin.

## v0.1 scope — stated honestly, not exhaustively

Only flags the direct `.getDefaultInstance().getService()` chain — an
options instance assigned to an intermediate variable before
`.getService()` is called isn't specially traced. Never flags a call
inside a constructor or a field/property initializer (legitimate
"create once" locations). Matches by simple class name, not real type
resolution — an unrelated `getDefaultInstance()`/`getService()` pair
on some other type is a possible (rare) false positive.

## Usage

Open any Java/Kotlin file using a Google Cloud client library. A
client built inside a regular method shows a warning icon.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
