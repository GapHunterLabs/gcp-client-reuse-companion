package dev.gaphunter.gcpclientreusecompanion.detect

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class KotlinClientBuildFinderTest : BasePlatformTestCase() {

    fun `test a client built inside a regular function is flagged`() {
        val file = myFixture.configureByText(
            "FileService.kt",
            """
            class FileService {
                fun upload() {
                    val storage = StorageOptions.getDefaultInstance().getService()
                }
            }
            """.trimIndent(),
        )
        assertEquals(1, KotlinClientBuildFinder.findAll(file).size)
    }

    fun `test a client built as a class property is not flagged`() {
        val file = myFixture.configureByText(
            "FileService.kt",
            """
            class FileService {
                val storage = StorageOptions.getDefaultInstance().getService()
            }
            """.trimIndent(),
        )
        assertTrue(KotlinClientBuildFinder.findAll(file).isEmpty())
    }

    fun `test an unrelated getDefaultInstance getService pair is never flagged`() {
        val file = myFixture.configureByText(
            "FileService.kt",
            """
            class FileService {
                fun upload() {
                    val locale = LocaleOptions.getDefaultInstance().getService()
                }
            }
            """.trimIndent(),
        )
        assertTrue(KotlinClientBuildFinder.findAll(file).isEmpty())
    }
}
