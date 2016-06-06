package eu.fiskur.syntaxview;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class FileUtils {
    public void loadByLine(File file, SyntaxView syntaxView){
        syntaxView.loadUrl("javascript:clearAll();");
        try {
            FileInputStream fis = new FileInputStream (file);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    receiveString = receiveString.replaceAll(Pattern.quote("<"),"&lt;");
                    receiveString = receiveString.replaceAll(Pattern.quote(">"),"&gt;");
                    receiveString = receiveString.replaceAll(Pattern.quote(" "),"&nbsp;");
                    syntaxView.loadUrl("javascript:addLine('" + URLEncoder.encode(receiveString, "UTF-8") + "');");
                }

                fis.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        syntaxView.loadUrl("javascript:finishedImport();");
    }
}
