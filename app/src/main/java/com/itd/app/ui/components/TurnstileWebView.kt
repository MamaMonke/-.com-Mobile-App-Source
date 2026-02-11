package com.itd.app.ui.components

import android.annotation.SuppressLint
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val TURNSTILE_SITE_KEY = "0x4AAAAAACHhxczw6fJGwPBg"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TurnstileWebView(
    modifier: Modifier = Modifier,
    onTokenReceived: (String) -> Unit
) {
    val html = remember {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <script src="https://challenges.cloudflare.com/turnstile/v0/api.js?onload=onTurnstileLoad" async defer></script>
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    min-height: 65px;
                    background: transparent;
                }
                .cf-turnstile {
                    transform: scale(0.85);
                    transform-origin: center;
                }
            </style>
        </head>
        <body>
            <div id="turnstile-container"></div>
            <script>
                function onTurnstileLoad() {
                    turnstile.render('#turnstile-container', {
                        sitekey: '$TURNSTILE_SITE_KEY',
                        theme: 'dark',
                        callback: function(token) {
                            if (window.TurnstileBridge) {
                                window.TurnstileBridge.onToken(token);
                            }
                        },
                        'error-callback': function() {
                            if (window.TurnstileBridge) {
                                window.TurnstileBridge.onError('Turnstile error');
                            }
                        },
                        'expired-callback': function() {
                            if (window.TurnstileBridge) {
                                window.TurnstileBridge.onExpired();
                            }
                        }
                    });
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)

                    addJavascriptInterface(object {
                        @JavascriptInterface
                        fun onToken(token: String) {
                            onTokenReceived(token)
                        }

                        @JavascriptInterface
                        fun onError(error: String) {
                            // Turnstile failed - could log or retry
                        }

                        @JavascriptInterface
                        fun onExpired() {
                            // Token expired - could trigger refresh
                        }
                    }, "TurnstileBridge")

                    loadDataWithBaseURL(
                        "https://xn--d1ah4a.com",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        )
    }
}
