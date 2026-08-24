package dev.gaphunter.gcpclientreusecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class JavaClientBuildFinderTest : BasePlatformTestCase() {

    fun `test a client built inside a regular method is flagged`() {
        val file = myFixture.configureByText(
            "FileService.java",
            """
            class FileService {
                void upload() {
                    Storage storage = StorageOptions.getDefaultInstance().getService();
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, JavaClientBuildFinder.findAll(file).size)
    }

    fun `test a client built inside a constructor is not flagged`() {
        val file = myFixture.configureByText(
            "FileService.java",
            """
            class FileService {
                private final Storage storage;
                FileService() {
                    storage = StorageOptions.getDefaultInstance().getService();
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaClientBuildFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated getDefaultInstance getService pair is never flagged`() {
        val file = myFixture.configureByText(
            "FileService.java",
            """
            class FileService {
                void upload() {
                    Locale locale = LocaleOptions.getDefaultInstance().getService();
                }
            }
            """.trimIndent(),
        )
        assertTrue(JavaClientBuildFinder.findAll(file).isEmpty())
    }
}
