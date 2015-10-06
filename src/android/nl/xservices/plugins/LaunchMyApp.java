package nl.xservices.plugins;

import android.content.Intent;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaActivity;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.IntentService;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.MediaStore;

import android.os.Bundle;
import android.webkit.WebView;

public class LaunchMyApp extends CordovaPlugin {

  private static final String ACTION_CHECKINTENT = "checkIntent";
  private static final String ACTION_CLEARINTENT = "clearIntent";

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if (ACTION_CLEARINTENT.equalsIgnoreCase(action)) {
      final Intent intent = ((CordovaActivity) this.webView.getContext()).getIntent();
      //intent.setData(null);
      return true;
    } else if (ACTION_CHECKINTENT.equalsIgnoreCase(action)) {
      final Intent intent = ((CordovaActivity) this.webView.getContext()).getIntent();
      final String intentString = intent.getDataString();
      String intentaction = intent.getAction(); String type = intent.getType();
       JSONObject parameter = new JSONObject();
       String hasintentdata = "no";
          if (Intent.ACTION_SEND.equals(intentaction) && type != null) {
        if ("text/plain".equals(type)) {
            	String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		    	if (sharedText != null) {hasintentdata = "yes";
		    		parameter.put("itype", "sharedtext");parameter.put("val", sharedText); 	//webView.loadUrl("javascript:handleNewIntent('sharedtext', '" + sharedText + "');");
		    		}
        } else if (type.startsWith("image/")) {
                 Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
    			if (imageUri != null) {hasintentdata = "yes";
    				parameter.put("itype", "singleimage");parameter.put("val", imageUri); //webView.loadUrl("javascript:handleNewIntent('singleimage', '" + imageUri + "');");
    				}
        } else if (type.startsWith("video/")) {
                Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
    			if (videoUri != null) {hasintentdata = "yes";
    				parameter.put("itype", "videUri");parameter.put("val", videoUri); //webView.loadUrl("javascript:handleNewIntent('singlevideo', '" + videoUri + "');");
    			}
        }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(intentaction) && type != null) {
        if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			    if (imageUris != null) {hasintentdata = "yes";
			    	parameter.put("itype", "multipleimages");parameter.put("val", imageUris); //webView.loadUrl("javascript:handleNewIntent('multipleimages', '" + imageUris + "');");
			    	}
        } else if (type.startsWith("video/")) {hasintentdata = "yes";
			    ArrayList<Uri> videoUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			    if (videoUris != null) {
			    	parameter.put("itype", "multiplevideos");parameter.put("val", videoUris); //webView.loadUrl("javascript:handleNewIntent('multiplevideos', '" + videoUris + "');");
			    }
        }
    } else {
    	hasintentdata = "no";
        // Handle other intents, such as being started from the home screen
        //callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, intent.getDataString()));
    }
    
      if (hasintentdata == "yes") {
        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, parameter));
      } else {
        callbackContext.error("App was not started via the launchmyapp URL scheme. Ignoring this errorcallback is the best approach.");
      }
      return true;
    } else {
      callbackContext.error("This plugin only responds to the " + ACTION_CHECKINTENT + " action.");
      return false;
    }
  }



  @Override
  public void onNewIntent(Intent intent) {
    
        String action = intent.getAction();
    String type = intent.getType();

    if (Intent.ACTION_SEND.equals(action) && type != null) {
        if ("text/plain".equals(type)) {
            	String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
		    	if (sharedText != null) {webView.loadUrl("javascript:handleNewIntent('sharedtext', '" + sharedText + "');");}
        } else if (type.startsWith("image/")) {
                 Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
    			if (imageUri != null) {webView.loadUrl("javascript:handleNewIntent('singleimage', '" + imageUri + "');");}
        } else if (type.startsWith("video/")) {
                Uri videoUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
    			if (videoUri != null) {webView.loadUrl("javascript:handleNewIntent('singlevideo', '" + videoUri + "');");}
        }
    } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
        if (type.startsWith("image/")) {
                ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			    if (imageUris != null) {webView.loadUrl("javascript:handleNewIntent('multipleimages', '" + imageUris + "');");}
        } else if (type.startsWith("video/")) {
			    ArrayList<Uri> videoUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
			    if (videoUris != null) {webView.loadUrl("javascript:handleNewIntent('multiplevideos', '" + videoUris + "');");}
        }
    } else {
        // Handle other intents, such as being started from the home screen
    }
  }

  // Taken from commons StringEscapeUtils
  private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote,
                                            boolean escapeForwardSlash) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("The Writer must not be null");
    }
    if (str == null) {
      return;
    }
    int sz;
    sz = str.length();
    for (int i = 0; i < sz; i++) {
      char ch = str.charAt(i);

      // handle unicode
      if (ch > 0xfff) {
        out.write("\\u" + hex(ch));
      } else if (ch > 0xff) {
        out.write("\\u0" + hex(ch));
      } else if (ch > 0x7f) {
        out.write("\\u00" + hex(ch));
      } else if (ch < 32) {
        switch (ch) {
          case '\b':
            out.write('\\');
            out.write('b');
            break;
          case '\n':
            out.write('\\');
            out.write('n');
            break;
          case '\t':
            out.write('\\');
            out.write('t');
            break;
          case '\f':
            out.write('\\');
            out.write('f');
            break;
          case '\r':
            out.write('\\');
            out.write('r');
            break;
          default:
            if (ch > 0xf) {
              out.write("\\u00" + hex(ch));
            } else {
              out.write("\\u000" + hex(ch));
            }
            break;
        }
      } else {
        switch (ch) {
          case '\'':
            if (escapeSingleQuote) {
              out.write('\\');
            }
            out.write('\'');
            break;
          case '"':
            out.write('\\');
            out.write('"');
            break;
          case '\\':
            out.write('\\');
            out.write('\\');
            break;
          case '/':
            if (escapeForwardSlash) {
              out.write('\\');
            }
            out.write('/');
            break;
          default:
            out.write(ch);
            break;
        }
      }
    }
  }

  private static String hex(char ch) {
    return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
  }
}
