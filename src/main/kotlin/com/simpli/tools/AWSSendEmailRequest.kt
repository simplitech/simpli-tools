/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpli.tools

import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.simpleemail.model.*
import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import java.io.StringWriter
import java.util.logging.Level
import java.util.logging.Logger
import org.apache.log4j.BasicConfigurator

/**
 *
 * @author ricardoprado
 */
open class AWSSendEmailRequest {
    protected var from = ""
    protected var name = from
    protected var to = ""
    protected var bcc = ""
    protected var ListBcc: List<String>? = null
    protected var subject = ""
    protected var host = ""
    protected var body: String = ""
    protected var attachment = ""
    protected var nameAttachment = ""
    private var region: Regions? = null

    constructor(region: Regions) {
        this.region = region
    }

    constructor() {
        this.region = Regions.US_WEST_2
    }

    fun send() {
        // Construct an object to contain the recipient address.
        val destination = Destination().withToAddresses(to)

        // Create the subject and body of the message.
        val subjectParam = Content().withData(subject)
        val textBody = Content().withData(body)
        val bodyParam = Body().withHtml(textBody)

        // Create a message with the specified subject and body.
        val message = Message().withSubject(subjectParam).withBody(bodyParam)

        // Assemble the email.
        val request = SendEmailRequest().withSource(from).withDestination(destination).withMessage(message)
        val ses = AwsSES()
        ses.setRegion(Region.getRegion(region!!))
        ses.sendEmail(request)
    }

    /**
     *
     * @param forClassLoader this.getClass()
     * @param model
     * @param templateFilename
     */
    fun setBodyFromTemplate(forClassLoader: Class<*>, model: Map<String, Any>, templateFilename: String) {
        try {
            val temp = getTemplateConfigInstance(forClassLoader).getTemplate(templateFilename)
            val out = StringWriter()
            temp.process(model, out)
            body = out.toString()
        } catch (ex: Exception) {
            Logger.getLogger(AWSSendEmailRequest::class.java.getName()).log(Level.SEVERE, ex.message, ex)
        }

    }

    companion object {
        private var templateConfig: Configuration? = null

        /**
         *
         * @param forClassLoader this.getClass()
         * @return
         */
        fun getTemplateConfigInstance(forClassLoader: Class<*>): Configuration {
            if (templateConfig == null) {
                BasicConfigurator.configure()
                templateConfig = Configuration(Configuration.VERSION_2_3_22)
                templateConfig!!.setClassForTemplateLoading(forClassLoader, "/mail-templates")
                templateConfig!!.setDefaultEncoding("UTF-8")
                templateConfig!!.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
                templateConfig!!.setLocalizedLookup(false)
            }

            return templateConfig as Configuration
        }
    }

}
