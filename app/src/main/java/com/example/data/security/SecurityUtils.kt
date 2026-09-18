package com.example.data.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID

object SecurityUtils {

    /**
     * Validates Pakistani CNIC number.
     * Format: XXXXX-XXXXXXX-X or 13 consecutive digits.
     */
    fun isValidCnic(cnicRaw: String): Boolean {
        val digits = cnicRaw.filter { it.isDigit() }
        if (digits.length != 13) return false

        // First digit must be 1 to 7 (valid Pakistan province / ICT / FATA / AJK codes)
        val firstDigit = digits[0].digitToIntOrNull() ?: return false
        if (firstDigit !in 1..7) return false

        return true
    }

    /**
     * Formats raw digits into CNIC standard display XXXXX-XXXXXXX-X
     */
    fun formatCnic(digitsOnly: String): String {
        val clean = digitsOnly.filter { it.isDigit() }.take(13)
        val sb = StringBuilder()
        for (i in clean.indices) {
            sb.append(clean[i])
            if (i == 4 || i == 11) {
                if (i != clean.lastIndex) {
                    sb.append("-")
                }
            }
        }
        return sb.toString()
    }

    /**
     * Masks CNIC for privacy and compliance (e.g. 42101-*******-5).
     * Never logs or displays unmasked CNIC.
     */
    fun maskCnic(cnic: String): String {
        val digits = cnic.filter { it.isDigit() }
        if (digits.length != 13) return "•••••-•••••••-•"
        val prefix = digits.substring(0, 5)
        val suffix = digits.substring(12)
        return "$prefix-*******-$suffix"
    }

    /**
     * Validates Pakistani mobile phone numbers.
     * Valid formats: 03001234567, +923001234567, 923001234567, 0300-1234567
     */
    fun isValidPakistaniMobile(mobile: String): Boolean {
        val normalized = normalizePakistaniMobile(mobile)
        return normalized.matches(Regex("^03[0-9]{9}$"))
    }

    /**
     * Normalizes phone number to standard 11-digit local format: 03XXXXXXXXX
     */
    fun normalizePakistaniMobile(mobile: String): String {
        var clean = mobile.replace(Regex("[^0-9+]"), "")
        if (clean.startsWith("+92")) {
            clean = "0" + clean.substring(3)
        } else if (clean.startsWith("92") && clean.length == 12) {
            clean = "0" + clean.substring(2)
        }
        return clean
    }

    /**
     * Generates a cryptographically strong random salt
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return saltBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Hashes PIN with salt using SHA-256 (Never store raw PIN)
     */
    fun hashPin(pin: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val combined = "$salt:$pin:PayWavePK-SecurityV1"
        val hash = md.digest(combined.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    /**
     * Generates an Idempotency Key for mutation transactions
     * Prevents double-spend and replay attacks
     */
    fun generateIdempotencyKey(): String {
        val ts = System.currentTimeMillis()
        val uuid = UUID.randomUUID().toString().replace("-", "").take(16)
        return "PW-IDEM-$ts-$uuid"
    }

    /**
     * Generates an official PayWave PK Transaction Reference Number
     */
    fun generateTransactionRef(prefix: String = "PW"): String {
        val ts = System.currentTimeMillis().toString().takeLast(8)
        val rand = (1000..9999).random()
        return "$prefix-$ts$rand"
    }

    /**
     * Calculates cryptographic hash for immutable ledger entry chain
     */
    fun calculateLedgerHash(
        previousHash: String,
        entryId: String,
        amount: Double,
        type: String,
        timestamp: Long
    ): String {
        val md = MessageDigest.getInstance("SHA-256")
        val payload = "$previousHash|$entryId|$amount|$type|$timestamp|PAYWAVE-IMMUTABLE-LEDGER"
        val digest = md.digest(payload.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }
}
