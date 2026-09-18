package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PayWave PK", appName)
  }

  @Test
  fun `cnic validation tests`() {
    // Valid 13-digit Pakistani CNICs (starts with province code 1-7)
    assertTrue(SecurityUtils.isValidCnic("42101-1234567-1"))
    assertTrue(SecurityUtils.isValidCnic("35202-9876543-2"))
    assertTrue(SecurityUtils.isValidCnic("6110112345671"))

    // Invalid CNICs
    assertFalse(SecurityUtils.isValidCnic("02101-1234567-1")) // Starts with 0
    assertFalse(SecurityUtils.isValidCnic("92101-1234567-1")) // Starts with 9
    assertFalse(SecurityUtils.isValidCnic("12345")) // Too short
  }

  @Test
  fun `cnic masking protects privacy`() {
    val masked = SecurityUtils.maskCnic("42101-1234567-1")
    assertEquals("42101-*******-1", masked)
    assertFalse(masked.contains("1234567"))
  }

  @Test
  fun `pakistani mobile normalization tests`() {
    assertEquals("03001234567", SecurityUtils.normalizePakistaniMobile("+923001234567"))
    assertEquals("03001234567", SecurityUtils.normalizePakistaniMobile("923001234567"))
    assertEquals("03001234567", SecurityUtils.normalizePakistaniMobile("0300-1234567"))
    assertTrue(SecurityUtils.isValidPakistaniMobile("03001234567"))
    assertFalse(SecurityUtils.isValidPakistaniMobile("04001234567"))
  }

  @Test
  fun `immutable ledger hash chain computation`() {
    val prevHash = "0000000000000000000000000000000000000000000000000000000000000000"
    val hash1 = SecurityUtils.calculateLedgerHash(
      previousHash = prevHash,
      entryId = "ENTRY-1",
      amount = 5000.0,
      type = "CREDIT",
      timestamp = 100000L
    )
    val hash2 = SecurityUtils.calculateLedgerHash(
      previousHash = hash1,
      entryId = "ENTRY-2",
      amount = 1200.0,
      type = "DEBIT",
      timestamp = 100050L
    )

    assertEquals(64, hash1.length)
    assertEquals(64, hash2.length)
    assertNotEquals(hash1, hash2)
  }
}
