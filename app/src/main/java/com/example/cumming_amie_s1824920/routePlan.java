package com.example.cumming_amie_s1824920;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


public class routePlan extends FragmentActivity implements OnMapReadyCallback
{

    // Amie Cumming S1824920
    // acummi205@caledonian.ac.uk

    private String result = "";
    private String url1="";
    private LinkedList<itemData> alist;
    private ArrayList<String> listItems=new ArrayList<String>();
    private LinkedList<itemData> blist =  new LinkedList<itemData>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;
    private Date target_date;
    final Calendar myCalendar= Calendar.getInstance();
    private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm");
    private SimpleDateFormat calendarformatter = new SimpleDateFormat("dd/MM/yy");
    // Traffic Scotland Planned Roadworks XML link
    private GoogleMap mMap;
    private String
            urlSource="https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    // Create a List from String Array elements
    List<String> urls = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        urls.add("https://trafficscotland.org/rss/feeds/roadworks.aspx");
        urls.add("https://trafficscotland.org/rss/feeds/plannedroadworks.aspx");
        urls.add("https://trafficscotland.org/rss/feeds/currentincidents.aspx");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_plan);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String temp_target_date = getIntent().getStringExtra("selected_date");
        Log.d("TargetDate", String.valueOf(temp_target_date));
        try {
            target_date = calendarformatter.parse(temp_target_date);
            Log.d("TargetDate", String.valueOf(target_date));

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("TargetDate", String.valueOf(target_date));

        }
        startProgress();

        Button routePlan = (Button) findViewById(R.id.routePlan);


        Log.e("MyTag","after startButton");



        // Create an ArrayAdapter from List



    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng incident = new LatLng(55.8642, -4.2518);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(incident));
        googleMap.setMinZoomPreference(10);
    }
    private void  allIncidents(){
        for (itemData i:blist) {
            LatLng incident = new LatLng(Double.parseDouble(i.getxPosition()), Double.parseDouble(i.getyPosition()));
            mMap.addMarker(new MarkerOptions()
                    .position(incident)
                    .title(i.getTitle())
            .snippet(i.getDescription().split("<br />")[0]+" "+i.getDescription().split("<br />")[1]));

            Log.d("MARKER", i.getTitle());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(incident));
        }
    }

    private LinkedList<itemData> parseDataDate(String dataToParse)
    {
        Log.e("ERROR", "Begin Parse");
        itemData widget = null;
        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                // Found a start tag
                if(eventType == XmlPullParser.START_TAG)
                {
                    // Check which Tag we have
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        alist  = new LinkedList<itemData>();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        Log.e("MyTag","Item Start Tag found");
                        widget = new itemData();
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("title"))
                    {
                        if (widget!=null) {
                            // Now just get the associated text
                            String temp = xpp.nextText();
                            // Do something with text
                            Log.e("MyTag", "Title is " + temp);

                            widget.setTitle(temp);

                        }
                    }
                    else
                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("description"))
                        {
                            if (widget!=null) {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                                Log.e("MyTag", "Description is " + temp);
                                widget.setDescription(temp);
                            }
                        }
                        else
                            // Check which Tag we have
                            if (xpp.getName().equalsIgnoreCase("point"))
                            {
                                // Now just get the associated text
                                String temp = xpp.nextText();
                                // Do something with text
                                Log.e("MyTag","Point is " + temp);
                                widget.setMapPosition(temp);
                            }

                }
                else
                if(eventType == XmlPullParser.END_TAG)
                {
                    if (xpp.getName().equalsIgnoreCase("item"))
                    {
                        Log.d("startDate: ", "HELLO");
                        String[] incident_description = widget.getDescription().split("<br /");
                        String start_date = incident_description[0].split(": ")[1];
                        String end_date = incident_description[1].split(": ")[1];
                        Log.d("startDate: ", start_date);
                        Log.d("endDate: ", end_date);
                        Log.d("endDate: ", String.valueOf(target_date));
                        Log.d("endDate: ", String.valueOf(target_date.after(formatter.parse(start_date)) && target_date.before(formatter.parse(end_date))));
                        if(target_date.after(formatter.parse(start_date)) && target_date.before(formatter.parse(end_date))) {
                            alist.add(widget);
                            blist.add(widget);
                            Log.d("startDate: ", String.valueOf(formatter.parse(start_date)));
                            Log.d("endDate: ", String.valueOf(formatter.parse(end_date)));
                            Log.d("targetDate: ", String.valueOf(target_date));
                            listItems.add(widget.getTitle());

                            Log.d("SIZE", String.valueOf(blist.size()));
                        }
                    }
                    else
                    if (xpp.getName().equalsIgnoreCase("channel"))
                    {
                        int size;
                        size = alist.size();
                        Log.e("MyTag","widgetcollection size is " + size);
                    }
                }


                // Get the next event
                eventType = xpp.next();

            } // End of while

            //return alist;
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Log.e("MyTag","End document");
        Log.e("MyTag",blist.toString());



        return alist;

    }

    public void startProgress()
    {
        // Run network access on a separate thread;
        new Thread(new Task(urls)).start();

    } //

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
    private class Task implements Runnable
    {
        private List<String> urls2;
        public Task(List<String> aurls)
        {
            urls2 = aurls;}

        @Override
        public void run()
        {
            for(String url:urls2) {
                URL aurl;
                URLConnection yc;
                BufferedReader in = null;
                String inputLine = "";
                Log.e("MyTag", "in run");
                try {
                    Log.e("MyTag", "in try");
                    aurl = new URL(url);
                    yc = aurl.openConnection();
                    in = new BufferedReader(new
                            InputStreamReader(yc.getInputStream()));
                    Log.e("MyTag", "after ready");
                    //
                    // Now read the data. Make sure that there are no specific headers
                    // in the data file that you need to ignore.
                    // The useful data that you need is in each of the item entries
                    //
                    while ((inputLine = in.readLine()) != null) {
                        result = result + inputLine;

                    }
                    in.close();
                } catch (IOException ae) {
                    Log.e("MyTag", "ioexception in run");
                }
                //
                // Now that you have the xml data you can parse it

                // Now update the TextView to display raw XML data
                // Probably not the best way to update TextView
                // but we are just getting started !
            }
            routePlan.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                    blist = parseDataDate(result);
                    allIncidents();
                }
            });

        }

    }
}