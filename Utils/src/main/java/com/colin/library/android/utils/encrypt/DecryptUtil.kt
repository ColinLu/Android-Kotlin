package com.colin.library.android.utils.encrypt

import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Create:2025-05-08 22:13
 *
 * Des   :解密工具类，提供AES解密功能
 */
class DecryptUtil {
    companion object {
        /** AES解密转换模式：CBC模式 + PKCS5填充 */
        const val TRANSFORMATION_AES = "AES/CBC/PKCS5Padding"
        private const val TRANSFORMATION_AES_PKCS7 = "AES/CBC/PKCS7Padding"

        /**
         * AES解密（指定IV）
         *
         * @param data 待解密的Base64编码字符串
         * @param key 解密密钥
         * @param iv 初始化向量
         * @param transformation 加密转换模式，默认AES/CBC/PKCS5Padding
         * @return 解密后的明文字符串，失败返回空字符串
         */
        @JvmStatic
        fun aes(
            data: String, key: String, iv: String, transformation: String = TRANSFORMATION_AES
        ): String {
            return try {
                val cipher = Cipher.getInstance(transformation)
                val rawKey = key.toByteArray(StandardCharsets.UTF_8)
                val rawIV = iv.toByteArray(StandardCharsets.UTF_8)
                val secretKey = SecretKeySpec(rawKey, "AES")
                val ivParam = IvParameterSpec(rawIV)
                cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParam)
                val decoded = Base64.decode(data, Base64.DEFAULT)
                String(cipher.doFinal(decoded), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
        }

        /**
         * AES解密（IV嵌入密文中）
         *
         * 数据格式：前16字节为IV，剩余部分为密文
         *
         * @param data 待解密的Base64编码字符串
         * @param key 解密密钥（会通过SHA-256哈希生成AES-128密钥）
         * @return 解密后的明文字符串，失败返回null
         */
        @JvmStatic
        fun aes(data: String, key: String): String? {
            try {
                // Base64 解码
                val decodedData = Base64.decode(data, Base64.DEFAULT)
                // 提取 IV 和密文（前16字节为IV）
                val iv = decodedData.copyOfRange(0, 16)
                val ciphertext = decodedData.copyOfRange(16, decodedData.size)
                // 生成 AES-128 密钥（对key进行SHA-256哈希，取前16字节）
                val sha256 = MessageDigest.getInstance("SHA-256")
                val hashKey = sha256.digest(key.toByteArray())
                val aesKey = hashKey.copyOfRange(0, 16)
                // 创建 SecretKeySpec 和 IvParameterSpec
                val secretKeySpec = SecretKeySpec(aesKey, "AES")
                val ivParameterSpec = IvParameterSpec(iv)
                // 创建 Cipher 实例并初始化为解密模式
                val cipher = Cipher.getInstance(TRANSFORMATION_AES)
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
                // 解密数据
                val decryptedData = cipher.doFinal(ciphertext)
                // 转换为字符串返回
                return String(decryptedData)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        /**
         * AES解密（ByteArray版本，带偏移量处理）
         *
         * @param data 待解密的Base64编码字节数组
         * @param key 解密密钥字节数组
         * @param offset 结果数据的起始偏移量，默认14
         * @return 解密后的字节数组，失败返回null
         */
        @JvmStatic
        fun aes(data: ByteArray, key: ByteArray, offset: Int = 14): ByteArray? {
            try {
                // Base64 解码
                val decodedData = Base64.decode(data, Base64.DEFAULT)
                // 提取 IV 和密文（前16字节为IV）
                val iv = decodedData.copyOfRange(0, 16)
                val ciphertext = decodedData.copyOfRange(16, decodedData.size)
                // 生成 AES-128 密钥
                val sha256 = MessageDigest.getInstance("SHA-256")
                val hashKey = sha256.digest(key)
                val aesKey = hashKey.copyOfRange(0, 16)
                // 创建 SecretKeySpec 和 IvParameterSpec
                val secretKeySpec = SecretKeySpec(aesKey, "AES")
                val ivParameterSpec = IvParameterSpec(iv)
                // 创建 Cipher 实例并初始化解密模式
                val cipher = Cipher.getInstance(TRANSFORMATION_AES)
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
                // 解密数据
                val result = cipher.doFinal(ciphertext)
                // 根据偏移量截取结果并再次Base64解码
                return if (result.size <= offset) null
                else Base64.decode(result.copyOfRange(offset, result.size), Base64.DEFAULT)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }
}
