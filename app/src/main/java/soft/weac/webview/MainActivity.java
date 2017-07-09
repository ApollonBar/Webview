package soft.weac.webview;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText webUrlStr;
    private Button goUrl;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        load();
    }
    private void init(){
        webView= (WebView) findViewById(R.id.webView1);
        webUrlStr = (EditText) findViewById(R.id.web_url_input);
        goUrl = (Button) findViewById(R.id.web_url_goto);
    }

    private void load(){
        goUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://" + webUrlStr.getText().toString();
                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
                if(URLUtil.isNetworkUrl(url)&&URLUtil.isValidUrl(url)){
                    webView.loadUrl(url);
                    webView.setWebViewClient(new WebViewClient(){
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {

                            //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                            view.loadUrl(url);
                            return true;
                        }
                        //WebViewClient帮助WebView去处理一些页面控制和请求

                    });
                    //启用支持Javascript
                    WebSettings settings = webView.getSettings();
                    settings.setJavaScriptEnabled(true);
                    //webView加载页面优先使用缓存加载
                    settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                    webView.setWebChromeClient(new WebChromeClient(){
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            //newProgress 1-100之间的整数
                            if (newProgress==100){
                                //网页加载完毕，关闭ProgressDialog
                                closeDialog();
                            }
                            else {
                                //网页正在加载，打开ProgressDialog
                                openDialog(newProgress);
                            }
                        }
                    });
                }else{
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("警告")
                            .setMessage("不是有效的网址")
                            .create()
                            .show();
                }
            }
        });
    }

    private void closeDialog() {
        if (dialog!=null && dialog.isShowing()){
            dialog.dismiss();
            dialog=null;
        }

    }

    private void openDialog(int newProgress) {
        if (dialog==null){
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setTitle("Loading");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(newProgress);
            dialog.show();
        }
        else {
            dialog.setProgress(newProgress);
        }

    }

    //改写物理按键--返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            //Toast.makeText(this,webView.getUrl(),Toast.LENGTH_SHORT).show();

            /*由于我们已通过WebView去覆盖UI的加载方式，使网页显示在WebView上，而不是调用其他浏览器，
            所以WebView会自动帮我们记录历史记录，我们只需要加一步判断，判断WebView能不能返回到上一级*/
            if (webView.canGoBack()){
                webView.goBack();//返回上一页面
                return true;
                //返回页面会刷新是因为网站做重定向或者是转发造成的影响
            }
            else
            {
                System.exit(0);//退出程序
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
