package br.com.simpli.tools

import java.io.UnsupportedEncodingException
import java.math.BigInteger
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.util.Formatter
import java.security.Key
import java.security.spec.KeySpec
import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.digest.DigestUtils
import java.nio.charset.Charset

/**
 *
 * @author gil
 */
object SecurityUtils {

    @JvmOverloads
    fun encrypt(raw: String, key: String, encode: Boolean = false, urlSafe: Boolean = false): String? {
        try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = byteArrayOf(1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7)
            cipher.init(Cipher.ENCRYPT_MODE, generateKey(key), IvParameterSpec(iv))
            val b = raw.toByteArray(charset("UTF-8"))
            val ciphertext = cipher.doFinal(b, 0, b.size)
            val encoded = when (urlSafe) {
                true -> Base64.getUrlEncoder()
                false -> Base64.getEncoder()
            }.encode(ciphertext)
            var encryptedValue = String(encoded, charset("UTF-8"))

            if (encode) {
                encryptedValue = URLEncoder.encode(encryptedValue, "UTF-8")
            }

            return encryptedValue
        } catch (ex: Exception) {
            return null
        }

    }

    @JvmOverloads
    fun decrypt(encrypted: String, key: String, decode: Boolean = false, urlSafe: Boolean = false): String? {
        var tencrypted = encrypted
        try {
            if (decode) {
                tencrypted = URLDecoder.decode(tencrypted, "UTF-8")
            }

            val decoded = when (urlSafe) {
                true -> Base64.getUrlDecoder().decode(tencrypted.toByteArray())
                false -> Base64.getDecoder().decode(tencrypted.toByteArray())
            }
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val iv = byteArrayOf(1, 2, 3, 4, 5, 6, 6, 5, 4, 3, 2, 1, 7, 7, 7, 7)
            cipher.init(Cipher.DECRYPT_MODE, generateKey(key), IvParameterSpec(iv))
            val decryptedBytes = cipher.doFinal(decoded, 0, decoded.size)
            return String(decryptedBytes, charset("UTF-8"))
        } catch (ex: Exception) {
            return null
        }

    }

    fun encode(token: String, enc: String): String? {
        try {
            return URLEncoder.encode(token, enc)
        } catch (ex: UnsupportedEncodingException) {
            ex.printStackTrace()
            return null
        }

    }

    fun decode(token: String, enc: String): String? {
        try {
            return URLDecoder.decode(token, enc)
        } catch (ex: UnsupportedEncodingException) {
            ex.printStackTrace()
            return null
        }

    }

    fun generateRandomString(length: Int): String {
        val random = SecureRandom()

        return BigInteger(length * 5, random).toString(32)
    }

    fun randInt(min: Int, max: Int): Int {
        val random = SecureRandom()
        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return random.nextInt(max - min + 1) + min
    }

    fun generateHash(subject: Any, prime: Long): String {
        return sha1((subject.hashCode() * prime).toString() + "")
    }

    fun md5(input: String?): String? {

        var md5: String? = null

        if (null == input) return null

        try {

            //Create MessageDigest object for MD5
            val digest = MessageDigest.getInstance("MD5")

            //Update input string in message digest
            digest.update(input.toByteArray(), 0, input.length)

            //Converts message digest value in base 16 (hex)
            md5 = BigInteger(1, digest.digest()).toString(16)

        } catch (e: NoSuchAlgorithmException) {
        }

        return md5
    }

    fun sha1(subject: String): String {
        var sha1 = ""
        try {
            val crypt = MessageDigest.getInstance("SHA-1")
            crypt.reset()
            crypt.update(subject.toByteArray(charset("UTF-8")))
            sha1 = byteToHex(crypt.digest())
        } catch (e: NoSuchAlgorithmException) {

        } catch (e: UnsupportedEncodingException) {

        }

        return sha1
    }

    fun sha256(subject: String): String {
        return DigestUtils.sha256Hex(subject)
    }

    private fun byteToHex(hash: ByteArray): String {
        val formatter = Formatter()
        for (b in hash) {
            formatter.format("%02x", b)
        }
        val result = formatter.toString()
        formatter.close()
        return result
    }

    @Throws(Exception::class)
    private fun generateKey(key: String): Key {
        val salt = byteArrayOf(-84, -119, 25, 56, -100, 100, -120, -45, 84, 67, 96, 10, 24, 111, 112, -119, 3)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        val spec = PBEKeySpec(key.toCharArray(), salt, 1024, 128)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

}
