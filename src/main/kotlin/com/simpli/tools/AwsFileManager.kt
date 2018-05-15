package com.simpli.tools

import com.amazonaws.HttpMethod
import com.amazonaws.auth.PropertiesCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.ec2.util.S3UploadPolicy
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 * @author gil
 */
class AwsFileManager @JvmOverloads constructor(bucketName: String, endpoint: String = "https://s3-sa-east-1.amazonaws.com/") {

    private var bucketName: String? = null
    private var endpoint: String? = null
    private var properties: InputStream? = null
    private var credentials: PropertiesCredentials? = null
    private var s3Client: AmazonS3? = null

    init {
        try {
            this.bucketName = bucketName
            this.endpoint = endpoint
            properties = AwsFileManager::class.java.getResourceAsStream("/AwsCredentials.properties")
            credentials = PropertiesCredentials(properties)
            s3Client = AmazonS3Client(credentials)
        } catch (ex: IOException) {
            Logger.getLogger(AwsFileManager::class.java.name).log(Level.SEVERE, null, ex)
        }

    }

    fun initiateMultipartUpload(folder: String, filename: String): String {
        val path = folder + "/" + filename
        val multipartRequest = InitiateMultipartUploadRequest(bucketName, path)
        val multipartResult = s3Client!!.initiateMultipartUpload(multipartRequest)
        return multipartResult.getUploadId()
    }

    fun getPresignedUrlForFile(filename: String, contentType: String?, httpMethod: HttpMethod): String {
        val expiration = java.util.Date()
        var msec = expiration.time
        msec += (24000 * 60 * 60).toLong()
        expiration.time = msec
        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, filename)
        generatePresignedUrlRequest.setMethod(httpMethod)
        if (contentType != null) {
            generatePresignedUrlRequest.setContentType(contentType)
        }
        generatePresignedUrlRequest.setExpiration(expiration)
        val s = s3Client!!.generatePresignedUrl(generatePresignedUrlRequest)
        return s.toExternalForm()
    }

    @JvmOverloads
    fun getPresignedUrl(folder: String, filename: String, contentType: String?, httpMethod: HttpMethod = HttpMethod.PUT): String {
        val path = folder + "/" + filename
        val expiration = java.util.Date()
        var msec = expiration.time
        msec += (24000 * 60 * 60).toLong()
        expiration.time = msec
        val generatePresignedUrlRequest = GeneratePresignedUrlRequest(bucketName, path)
        generatePresignedUrlRequest.setMethod(httpMethod)
        if (contentType != null) {
            generatePresignedUrlRequest.setContentType(contentType)
        }
        generatePresignedUrlRequest.setExpiration(expiration)
        val s = s3Client!!.generatePresignedUrl(generatePresignedUrlRequest)
        return s.toExternalForm()
    }

    fun getPresignedUrl(folder: String, filename: String): String {
        return this.getPresignedUrl(folder, filename, null)
    }

    fun getUploadCredentials(folder: String, filename: String): Array<String> {
        val path = folder + "/" + filename
        val policy = S3UploadPolicy(credentials!!.awsAccessKeyId, credentials!!.awsSecretKey, bucketName, path, 10)
        return arrayOf(policy.getPolicyString(), policy.getPolicySignature())
    }

    fun upload(folder: String, filename: String, input: InputStream): String {
        val path = folder + "/" + filename
        val objectMetadata = ObjectMetadata()
        val putObjectRequest = PutObjectRequest(bucketName, path, input, objectMetadata)
        s3Client!!.putObject(putObjectRequest)
        return endpoint + bucketName + "/" + path
    }

    fun upload(folder: String, filename: String, file: ByteArray): String {
        val stream = ByteArrayInputStream(file)
        return upload(folder, filename, stream)
    }

    fun listFiles(folder: String): List<String> {
        val saida = ArrayList<String>()

        val objetosNaPasta = s3Client!!.listObjects(bucketName, folder)
        val objectSummaries = objetosNaPasta.getObjectSummaries()
        for (obj in objectSummaries) {
            if (obj.getSize() > 0) {
                saida.add(obj.getKey())
            }
        }

        return saida
    }

}
