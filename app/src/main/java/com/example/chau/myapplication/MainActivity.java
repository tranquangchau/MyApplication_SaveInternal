package com.example.chau.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

public class MainActivity extends Activity {

    WebView myWebView;
    RelativeLayout fullscreen;
    TextView txt_link;
    Button btn_save,btn_open;
    Integer i1=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load_webview();
        btn_save =(Button) findViewById(R.id.button);
        btn_open =(Button) findViewById(R.id.button_open);
        txt_link =(TextView) findViewById(R.id.textView);
        fullscreen =(RelativeLayout) findViewById(R.id.fullscreen);

        fullscreen.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Log.d("i1", String.valueOf(i1));
                if(i1!=0){
                    myWebView.setVisibility(View.VISIBLE);
                    i1=0;
                    fullscreen.setBackgroundResource(0);
                }
                else {
                    myWebView.setVisibility(View.GONE);
                    Resources res = getResources(); //resource handle
                    Drawable drawable = res.getDrawable(R.drawable.two_way); //new Image that was added to the res folder
                    fullscreen.setBackground(drawable);
                    i1=1;
                }
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_link.setText(m1);
                save_filetoInternal();
            }
        });
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read_fileInternal("MyFile.html");
                read_listfileInternal();
            }
        });


    }
    String m1="";
    private void load_webview() {
        myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.addJavascriptInterface(new LoadListener(), "HTMLOUT");

        myWebView.loadUrl("http://www.03way.com/webs/list.php");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Check the URL here - if it is a file,
                // then initiate the download
                Log.d("url", url); //http://03way.com/webs/data/index.php?name=181114.txt
                Log.d("getUrl", myWebView.getUrl());//http://www.03way.com/webs/list.php
                m1 = url;
                Log.d("m1", m1);
                txt_link.setText(m1);

                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
    }

    /**
     * Javascript listing webview
     */
    class LoadListener{
        /**
         * get all sourcode code html when request url ok
         * @param html
         * http://stackoverflow.com/a/19519373
         */
        public void processHTML(String html)
        {
            Log.d("result_sourcode", html);
            sourcode=html;
        }
        public void myFunction(String value)
        {
            Log.d("result__clcik", value);
            read_fileInternal(value);
            //sourcode=html;
        }
    }
    String sourcode="";
    public void save_filetoInternal(){
        // Create a file in the Internal Storage

        //var tabId = id.split("_").pop()
        String[] m2=m1.split("name=");
        Log.d("asdf_bienconlai",m2[1]);
        //String fileName = "fil1.html";
        String fileName = m2[1];
        String content = sourcode;

        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("save internal","ok");
    }
    //read file save internal
    public void read_fileInternal(String name_file){
        Log.d("read_filename", name_file);
        BufferedReader input = null;
        File file = null;
        try {
            file = new File(getFilesDir(), name_file); // Pass getFilesDir() and "MyFile" to read file

            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }

            Log.d("read", buffer.toString());
            myWebView.loadDataWithBaseURL("", buffer.toString(), "text/html", "UTF-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void read_listfileInternal(){
        //Log.d("list_file", String.valueOf(getFilesDir().getAbsolutePath()));// /data/data/com.example.chau.myapplication/files
        //Log.d("list_file", String.valueOf(getFilesDir().getAbsoluteFile()));//data/data/com.example.chau.myapplication/files
        Log.d("list_file", String.valueOf(getFilesDir()));//data/data/com.example.chau.myapplication/files
        File[] cachedFiles =getFilesDir().listFiles();
        //cachedFiles[1].getName().toString(); //MyFile.html
        //read_fileInternal(cachedFiles[1].getName().toString());

        String content= "";
        for (Integer i=0;i<cachedFiles.length;i++){
          //  content="<b>onclick='myFunction("+content+cachedFiles[i].getName()+")'"+content+cachedFiles[i].getName()+"</b><br>";
            String now=cachedFiles[i].getName();
            Log.d("now_data",now);
            //content ="<p onclick='HTMLOUT.myFunction(&#39;asdf&#39;)'>Click me</p>"+content;
            content ="<p onclick='HTMLOUT.myFunction(&#39;"+now+"&#39;)'>"+now+"</p>"+content;
        }

        String summary = "<html><body>List file<br><br> "+content+"</body></html>";
        myWebView.loadData(summary, "text/html", null);




    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (myWebView.canGoBack()) {
                        myWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
