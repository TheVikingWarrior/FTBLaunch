package net.ftb.tracking.piwik;

import com.google.common.hash.Hashing;
import net.ftb.data.Constants;
import net.ftb.data.Settings;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;


public class PiwikTracker extends Thread {
    private final String thingToTrack, urlFrom;
    private static String extraParamaters = "";
    public PiwikTracker(String thingToTrack, String urlFrom) {
        this.thingToTrack = thingToTrack;
        this.urlFrom = urlFrom;
    }

    public static void addExtraPair(String key, String value){
        extraParamaters += PiwikUtils.addPair(key,value) + "&";
    }
    @Override
    public void run() {
        HttpURLConnection con = null;
        BufferedReader in = null;
        try {
            //TrackerUtils.setUserAgent("Java/" + System.getProperty("java.version") + " (" + System.getProperty("os.name") + "; " + System.getProperty("os.arch") + ")");
            //TODO make this not dependent on having a headed server!!
            if(Settings.getSettings().getGeneratedID() == null || Settings.getSettings().getGeneratedID().isEmpty() ) {
                Settings.getSettings().setGeneratedID(Hashing.md5().hashUnencodedChars(UUID.randomUUID().toString()).toString().substring(0, 16));
            }//TODO this needs to put bits, and the OS version in the UA data properly!!
            String s = "http://stats.feed-the-beast.com/piwik.php?action_name=" + PiwikUtils.urlEncode(thingToTrack) + "&url=" + PiwikUtils.urlEncode(urlFrom) + "3%20&idsite=6&%20rand=" + new Random().nextInt(999999) + "&%20h=18&%20m=14&%20s=3%20&rec=1&%20apiv=1&%20cookie=%20&%20urlref=http://feed-the-beast.com%20&_id=" + Settings.getSettings().getGeneratedID() + "%20&res=" + (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() + "�" + (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() + "&_cvar={�1?:['OS','" + System.getProperty("os.name") + "'],�2?:['Launcher Version','" + Constants.version + "']}&ua=Java/" + System.getProperty("java.version") + " (Windows NT 6.1)&";
            if(!extraParamaters.isEmpty())
                s += extraParamaters;
            con = (HttpURLConnection) new URL(s).openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(HeadlessException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(in != null) {
                    in.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
            if(con != null) {
                con.disconnect();
            }
        }
    }
}