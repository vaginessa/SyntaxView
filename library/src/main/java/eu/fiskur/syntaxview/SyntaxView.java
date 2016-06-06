package eu.fiskur.syntaxview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.File;

public class SyntaxView extends RelativeLayout {
    private static final String TAG = SyntaxView.class.getSimpleName();
    private WebView webView;
    private ProgressBar progress;

    private static final String syntaxMarkup = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "    <link rel=\"stylesheet\" href=\"./style.css\">\n" +
            "    <link rel=\"stylesheet\" href=\"./styles/androidstudio.css\">\n" +
            "    <script src=\"./highlight.pack.js\"></script>\n" +
            "    <script>hljs.initHighlightingOnLoad();</script>\n" +
            "</head>\n" +
            "<body>\n" +
            "        <code id=\"codearea\" class=\"xml hljs\">\n" +
            "        </code>\n" +
            "    <script src=\"./client.js\"></script>\n" +
            "</body>\n" +
            "</html>\n";

    public SyntaxView(Context context) {
        super(context);
    }

    public SyntaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SyntaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        LayoutInflater.from(getContext()).inflate(R.layout.syntax_view, this);
        webView = (WebView) findViewById(R.id.syntax_web_view);
        progress = (ProgressBar) findViewById(R.id.syntax_progress_bar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebChromeClient(new SyntaxChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
        webView.loadDataWithBaseURL("file:///android_asset/", syntaxMarkup, "text/html", "utf-8", null);
    }

    private class SyntaxChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            String message = consoleMessage.message() + " -- line " + consoleMessage.lineNumber();
            switch (consoleMessage.messageLevel()) {
                case ERROR:
                    logErrorMessage(message);
                    break;
                default:
                    logInfoMessage(message);
                    break;
            }
            return true;
        }

        private void logInfoMessage(String message) {
            Log.i(TAG, message);
        }

        private void logErrorMessage(String message) {
            Log.e(TAG, message);
        }
    }

    class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showToast(String message) {
            l("showToast: " + message);
        }

        @JavascriptInterface
        public void finishedImport(){
            if(progress != null){
                progress.setVisibility(View.GONE);
            }
        }
    }

    public void loadFile(final File file){

        if(file.length() > 100000){
            l("File too big (100k max)");
        }else{
            new FileUtils().loadByLine(file, this);
            /*
            post(new Runnable() {
                @Override
                public void run() {
                    FileHelper.loadByLine(file, SyntaxView.this);
                }
            });
            */
        }
    }

    public void loadUrl(String js){
        webView.loadUrl(js);
    }

    private void l(String message){
        Log.d(TAG, message);
    }

}
