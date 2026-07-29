package com.android.tvapp.ui.common

import android.graphics.Typeface
import android.text.style.StyleSpan
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.core.text.HtmlCompat

fun String.htmlToAnnotatedString(): AnnotatedString {
    val spanned = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val full = buildAnnotatedString {
        append(spanned.toString())
        spanned.getSpans(0, spanned.length, StyleSpan::class.java).forEach { span ->
            val style = when (span.style) {
                Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                Typeface.BOLD_ITALIC -> SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
                else -> null
            }
            style?.let { addStyle(it, spanned.getSpanStart(span), spanned.getSpanEnd(span)) }
        }
    }
    return full.subSequence(0, full.text.trimEnd().length)
}
