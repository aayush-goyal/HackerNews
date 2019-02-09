package `in`.aayushgoyal.hackernews

import android.net.http.SslError
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.ProgressBar
import androidx.fragment.app.Fragment

class ArticleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        val progressBar: ProgressBar = view.findViewById(R.id.progress_bar_fragment_article)
        val webView: WebView = view.findViewById(R.id.webview_fragment_article)

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object: WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                progressBar.visibility = View.GONE
                webView.visibility = View.VISIBLE
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
                handler?.proceed()
            }
        }
        webView.loadUrl((arguments?.getSerializable("story") as Story).url)

        return view
    }
    
}
