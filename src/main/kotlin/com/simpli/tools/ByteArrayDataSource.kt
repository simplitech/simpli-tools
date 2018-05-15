package com.simpli.tools

import java.io.*
import javax.activation.*

class ByteArrayDataSource : DataSource {

    private var data: ByteArray? = null // data
    private var type: String? = null // content-type

    /* Create a DataSource from an input stream */
    constructor(`is`: InputStream, type: String) {
        this.type = type
        try {
            val os = ByteArrayOutputStream()
            var ch: Int = `is`.read()

            while (ch != -1) {
                os.write(ch)
                ch = `is`.read()
            }
            data = os.toByteArray()

        } catch (ioex: IOException) {
        }

    }

    /* Create a DataSource from a byte array */
    constructor(data: ByteArray, type: String) {
        this.data = data
        this.type = type
    }

    /* Create a DataSource from a String */
    constructor(data: String, type: String) {
        try {
            // Assumption that the string contains only ASCII
            // characters! Otherwise just pass a charset into this
            // constructor and use it in getBytes()
            this.data = data.toByteArray(charset("UTF-8"))
        } catch (uex: UnsupportedEncodingException) {
        }

        this.type = type
    }

    @Throws(IOException::class)
    override fun getInputStream(): InputStream {
        if (data == null) {
            throw IOException("no data")
        }
        return ByteArrayInputStream(data!!)
    }

    @Throws(IOException::class)
    override fun getOutputStream(): OutputStream {
        throw IOException("cannot do this")
    }

    override fun getContentType(): String? {
        return type
    }

    override fun getName(): String {
        return "Name"
    }
}
