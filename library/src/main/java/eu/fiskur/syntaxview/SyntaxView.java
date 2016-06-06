package eu.fiskur.syntaxview;

import android.content.Context;
import android.graphics.Color;
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
import java.io.IOException;

public class SyntaxView extends RelativeLayout {
    private static final String TAG = SyntaxView.class.getSimpleName();
    private WebView webView;
    private ProgressBar progress;
    private SyntaxViewObserver observer = null;
    public static final String[] LANGUAGES =new String[]{"objectivec", "javascript", "json", "css", "sql", "python", "markdown", "java", "php", "gradle", "xml", "bash"};

    private String language = "xml";
    private File file = null;
    private boolean detectLanguage = true;

    private static final String SYNTAX_MARKUP_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "    <link rel=\"stylesheet\" href=\"./style.css\">\n" +
            "    <link rel=\"stylesheet\" href=\"./styles/%s.css\">\n" +
            "    <script src=\"./highlight.pack.js\"></script>\n" +
            "    <script>hljs.initHighlightingOnLoad();</script>\n" +
            "</head>\n" +
            "<body>\n" +
            "        <code id=\"codearea\" class=\"%s hljs\">\n" +
            "        </code>\n" +
            "    <script src=\"./client.js\"></script>\n" +
            "    <script src=\"./vkbeautify.js\"></script>\n" +
            "</body>\n" +
            "</html>\n";

    public SyntaxView(Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.syntax_view, this);
    }

    public SyntaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(getContext()).inflate(R.layout.syntax_view, this);
    }

    public SyntaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.syntax_view, this);
    }

    public void setObserver(SyntaxViewObserver observer){
        this.observer = observer;
    }

    public void setup(String theme){
        setup(theme, null);
    }

    public void setup(String theme, String language){
        this.language = language;
        webView = (WebView) findViewById(R.id.syntax_web_view);
        webView.loadUrl("about:blank");//clear all
        progress = (ProgressBar) findViewById(R.id.syntax_progress_bar);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new SyntaxChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");

        if(theme == null || theme.isEmpty()){
            theme = "monokai";
        }
        if(language == null || language.isEmpty()){
            language = "xml";
        }
        webView.loadDataWithBaseURL("file:///android_asset/", String.format(SYNTAX_MARKUP_TEMPLATE, theme, language), "text/html", "utf-8", null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

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
            if(observer != null){
                observer.error(message);
            }
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
        public void finishedImport() {
            post(new Runnable() {
                @Override
                public void run() {
                    progress.setVisibility(View.GONE);
                }
            });
        }
    }

    public void loadFile(final File file, boolean detectLanguage){
        this.file = file;
        this.detectLanguage = detectLanguage;
        if(detectLanguage){
            detectLanguage(file);
        }
        if(file.length() > 100000){
            error("File too big (100k max)");
        }else{
            progress.setVisibility(View.VISIBLE);
            new FileUtils().loadByLine(file, this);
        }
    }

    private void detectLanguage(File file){
        String filename = file.getName().toLowerCase();
        String language = "";
        if(filename.endsWith("java")){
            language = "java";
        }else if(filename.endsWith("xml") || filename.endsWith("kml")){
            language = "xml";
        }else if(filename.endsWith("js")){
            language = "javascript";
        }else if(filename.endsWith("json")){
            language = "json";
        }else if(filename.endsWith("css")){
            language = "css";
        }else if(filename.endsWith("sql")){
            language = "sql";
        }else if(filename.endsWith("py")){
            language = "python";
        }else if(filename.endsWith("md")){
            language = "markdown";
        }else if(filename.endsWith("php")){
            language = "php";
        }else if(filename.endsWith("gradle")){
            language = "gradle";
        }else if(filename.endsWith("sh")){
            language = "bash";
        }
        setLanguage(language);
    }

    public void setLanguage(String language){
        webView.loadUrl("javascript:setLanguage('" + language + "');");
    }

    public void loadUrl(String js){
        webView.loadUrl(js);
    }

    public void beautifyXML(){
        l("beautifyXML()");
        webView.loadUrl("javascript:beautifyXML();");
        error("beautifyXML() not implemented yet");
    }

    public void minifyXML(){
        l("minifyXML()");
        webView.loadUrl("javascript:minifyXML();");
        error("minifyXML() not implemented yet");
    }

    public void listLanguages(){
        l("listLanguages()");
        webView.loadUrl("javascript:listLanguages();");
    }

    public void setTheme(String theme){
        l("Load theme: " + theme);
        if(file != null) {
            webView.loadDataWithBaseURL("file:///android_asset/", String.format(SYNTAX_MARKUP_TEMPLATE, theme, language), "text/html", "utf-8", null);
            loadFile(file, detectLanguage);
        }
    }

    public String[] themes(){
        l("themes()");
        String[] themes = null;
        try {
            themes = getContext().getAssets().list("styles");
            for(int i = 0 ; i < themes.length ; i++){
                String theme = themes[i];
                themes[i] = theme.substring(0, theme.indexOf("."));
                l("theme: " + themes[i]);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error listing styles: " + e.toString());
        }

        return themes;
    }

    public void setLoadingColor(int color){
        progress.getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
    }
    private void error(String message){
        if(observer != null){
            observer.error(message);
        }
    }

    private void l(String message){
        Log.d(TAG, message);
    }

}
