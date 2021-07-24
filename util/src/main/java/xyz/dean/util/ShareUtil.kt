@file:Suppress("unused")

package xyz.dean.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.net.URLEncoder

object ShareUtil {
    private const val SHARE_HOST_FACEBOOK = "https://www.facebook.com/sharer/sharer.php"
    private const val SHARE_HOST_TWITTER = "https://twitter.com/intent/tweet"
    private const val SHARE_HOST_LINE = "https://line.me/R"

    fun shareFacebookIntent(context: Context?, text: String, url: String?): Intent {
        val shareContent = "$text${if (url != null) " $url" else ""}"
        val actionIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, shareContent)
        val resInfos =
            context?.packageManager?.queryIntentActivities(actionIntent, 0) ?: emptyList()
        val appShare = resInfos.map { it.activityInfo.packageName }
            .filter {
                // There are different versions of Facebook.
                it.contains("com.facebook.katana") || it.contains("com.facebook.lite")
            }
            .map {
                Intent().setPackage(it)
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, shareContent)
            }
        val webShare = listOf(
            Intent(Intent.ACTION_VIEW, createUri(SHARE_HOST_FACEBOOK, param("u", shareContent)))
        )
        val chooserIntents = listOf(appShare, webShare).flatten()

        return Intent.createChooser(chooserIntents.first(), "Choose One to Share")
            .putExtra(Intent.EXTRA_INITIAL_INTENTS, chooserIntents.drop(1).toTypedArray())
    }

    fun shareTwitterIntent(text: String, url: String): Intent {
        val uri = createUri(
            SHARE_HOST_TWITTER,
            param("text", URLEncoder.encode(text, "UTF-8")),
            param("url", url)
        )
        return Intent(Intent.ACTION_VIEW, uri)
    }

    fun shareLineIntent(text: String, url: String): Intent {
        val content = URLEncoder.encode("$text $url", "UTF-8")
        val uri = createUri(
            "$SHARE_HOST_LINE/msg/text/",
            content
        )
        return Intent(Intent.ACTION_VIEW, uri)
    }

    private fun createUri(host: String, vararg params: String) =
        Uri.parse(
            host + if (params.isEmpty()) ""
            else params.joinToString(separator = "&", prefix = "?")
        )

    private fun param(param: String, value: String) = "$param=$value"

    class EmailBuilder {
        private val addresses: ArrayList<String> = ArrayList()
        private val ccAddresses: ArrayList<String> = ArrayList()
        private val bccAddresses: ArrayList<String> = ArrayList()
        private var subject: String? = null
        private var content: String? = null
        private val attachments: ArrayList<Uri> = ArrayList()

        fun addAddresses(vararg addresses: String) = apply {
            this.addresses.addAll(addresses)
        }

        fun addCcAddresses(vararg ccAddresses: String) = apply {
            this.ccAddresses.addAll(ccAddresses)
        }

        fun addBccAddress(vararg bccAddresses: String) = apply {
            this.bccAddresses.addAll(bccAddresses)
        }

        fun setSubject(subject: String) = apply {
            this.subject = subject
        }

        fun setContent(content: String) = apply {
            this.content = content
        }

        fun addAttachments(context: Context, authority: String, vararg attachments: File) = apply {
            attachments
                .filter { it.exists() }
                .map { FileProvider.getUriForFile(context, authority, it) }
                .forEach { this.attachments.add(it) }
        }

        fun buildEmailIntent(): Intent {
            val (action, type) = when (attachments.size) {
                0 -> Intent.ACTION_SENDTO to "text/plain"
                1 -> Intent.ACTION_SEND to "*/*"
                else -> Intent.ACTION_SEND_MULTIPLE to "*/*"
            }
            return Intent(action).apply {
                this.type = type
                if (action == Intent.ACTION_SENDTO)
                    data = Uri.parse("mailto:")
                if (addresses.size > 0)
                    putExtra(Intent.EXTRA_EMAIL, addresses.toTypedArray())
                if (ccAddresses.size > 0)
                    putExtra(Intent.EXTRA_CC, ccAddresses.toTypedArray())
                if (bccAddresses.size > 0)
                    putExtra(Intent.EXTRA_BCC, bccAddresses.toTypedArray())
                if (!subject.isNullOrEmpty())
                    putExtra(Intent.EXTRA_SUBJECT, subject)
                if (!content.isNullOrEmpty())
                    putExtra(Intent.EXTRA_TEXT, content)
                if (attachments.size > 0) {
                    if (action == Intent.ACTION_SEND)
                        putExtra(Intent.EXTRA_STREAM, attachments[0])
                    else
                        putExtra(Intent.EXTRA_STREAM, attachments)
                }
            }
        }
    }
}