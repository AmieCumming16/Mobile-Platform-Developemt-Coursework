package com.example.cumming_amie_s1824920;
import androidx.appcompat.app.AppCompatActivity;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    // Amie Cumming S1824920
    // acummi205@caledonian.ac.uk
    private TextView inputDate;
    private TextView inputRoad;
    private Button routePlan;
    private String result = "";
    private String url1="";
    private ArrayList<String> listItems=new ArrayList<String>();
    private LinkedList<itemData> blist =  new LinkedList<itemData>();
    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    private ArrayAdapter<String> adapter;


    private Date start_date;
    private Date end_date;
    private Date target_date;
    final Calendar myCalendar= Calendar.getInstance();
    private SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm");
    private SimpleDateFormat calendarformatter = new SimpleDateFormat("dd MM yyyy");
    // Traffic Scotland Planned Roadworks XML link
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
        setContentView(R.layout.activity_main);
        Button routePlan = (Button) findViewById(R.id.routePlan);
        inputDate = (TextView) findViewById(R.id.inputDate);
        routePlan.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (inputDate.getText().length() > 1){

                    Intent i = new Intent(MainActivity.this,routePlan.class);
                    i.putExtra("selected_date", inputDate.getText().toString());
                    Log.d("TDATE", (inputDate.getText().toString()));
                    startActivity(i);
                }
            }
        });
        Log.e("MyTag","in onCreate");
        // Set up the raw links to the graphical components
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {

                try {
                    target_date = calendarformatter.parse(day + " " + (month + 1) + " " + (year));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d("TargetDate", String.valueOf(target_date));
                startProgress();

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                String myFormat="dd/MM/yy";
                SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat);
                inputDate.setText(dateFormat.format(myCalendar.getTime()));

            }

        };
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        android.widget.ListView listView = (ListView) findViewById(R.id.ListViewID);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Log.d("item clicked ", String.valueOf(arg2));
                Log.d("item clicked ", String.valueOf(blist.get(arg2)));
                Intent i = new Intent(MainActivity.this,incidentActivity.class);
                i.putExtra("selected_incident", blist.get(arg2));
                startActivity(i);
            }

        });
        Log.e("MyTag","after startButton");
        startProgress();
        Button startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener( new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                listItems.clear();
                                                blist.clear();
                                                adapter.notifyDataSetChanged();

                                                inputRoad = (TextView) findViewById(R.id.inputRoad);
                                                if(inputRoad.getText().toString().length()>1){LinkedList<itemData> alist = parseDataRoad(result);}
                                                else if(inputDate.getText().toString().length()>1){LinkedList<itemData> alist = parseDataDate(result);}
                                                else{LinkedList<itemData> alist = parseData(result);}
                                                Log.d("Date", inputDate.getText().toString());
                                                Log.d("Road", inputRoad.getText().toString());

                                            }
                                        }
        );
        adapter=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView.setAdapter(adapter);
        // Initializing a new String Array



        // Create an ArrayAdapter from List

        final ListView ListView = (ListView) findViewById(R.id.ListViewID);

    }
    private LinkedList<itemData> parseDataRoad(String dataToParse)
    {
        Log.e("ERROR", "Begin Parse");
        itemData widget = null;
        LinkedList <itemData> alist = null;
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
                        if(widget.getTitle().toLowerCase().contains(inputRoad.getText().toString().toLowerCase()+" ") || widget.getTitle().toLowerCase().equals(inputRoad.getText().toString().toLowerCase())) {
                            Log.e("MyTag", "widget is " + widget.toString());
                            alist.add(widget);
                            blist.add(widget);
                            listItems.add(widget.getTitle());
                            adapter.notifyDataSetChanged();
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
        }

        Log.e("MyTag","End document");
        Log.e("MyTag",alist.toString());



        return alist;

    }
    private LinkedList<itemData> parseDataDate(String dataToParse)
    {
        Log.e("ERROR", "Begin Parse");
        itemData widget = null;
        LinkedList <itemData> alist = null;
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
                        String[] incident_description = widget.getDescription().split("<br /");
                        String start_date = incident_description[0].split(": ")[1];
                        String end_date = incident_description[1].split(": ")[1];
                        if(target_date.after(formatter.parse(start_date)) && target_date.before(formatter.parse(end_date))) {
                            alist.add(widget);
                            blist.add(widget);
                            Log.d("startDate: ", String.valueOf(formatter.parse(start_date)));
                            Log.d("endDate: ", String.valueOf(formatter.parse(end_date)));
                            Log.d("targetDate: ", String.valueOf(target_date));
                            listItems.add(widget.getTitle());
                            adapter.notifyDataSetChanged();}
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
        Log.e("MyTag",alist.toString());



        return alist;

    }
    private LinkedList<itemData> parseData(String dataToParse)
    {
        Log.e("ERROR", "Begin Parse");
        itemData widget = null;
        LinkedList <itemData> alist = null;
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
                        Log.e("MyTag","widget is " + widget.toString());
                        alist.add(widget);
                        blist.add(widget);
                        listItems.add(widget.getTitle());
                        adapter.notifyDataSetChanged();
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
        }

        Log.e("MyTag","End document");
        Log.e("MyTag",alist.toString());

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
            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    Log.d("UI thread", "I am the UI thread");
                }
            });

        }

    }
}