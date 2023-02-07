package com.example.mynodemcucar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
    private final String port = "80";
    boolean ipSet = false;
    private ESP8266Connector esp8266Connector;
    private String IP;
    private ImageButton up;
    private ImageButton down;
    private Button setIP;
    private ImageButton right;
    private ImageButton left;
    private EditText text;
    private Button group;
    private Button showIp;
    private ImageButton stop;
    private Button searchIP;
    private ProgressBar bar;
    private ExecutorService es;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bar = findViewById(R.id.progressBar);
        up = findViewById(R.id.button_up);
        down = findViewById(R.id.button_down);
        right = findViewById(R.id.button_right);
        left = findViewById(R.id.button_left);
        text = findViewById(R.id.edit_ip);
        setIP = findViewById(R.id.setIp);
        group = findViewById(R.id.group_name);
        showIp = findViewById(R.id.ip);
        stop = findViewById(R.id.stop_btn);
        searchIP = findViewById(R.id.search_ip);


        searchIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipSet)
                    Toast.makeText(getBaseContext(), "IP entered is correct...", Toast.LENGTH_SHORT).show();
                ipSet = false;
                text.setText("");
                bar.setVisibility(View.VISIBLE);
                Toast.makeText(getBaseContext(), "Searching for IP", Toast.LENGTH_SHORT).show();

                es = Executors.newFixedThreadPool(5);

                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        IP = getNodemcuIP(1, 50);

                    }
                });
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        IP = getNodemcuIP(51, 100);
                    }
                });
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        IP = getNodemcuIP(101, 150);
                    }
                });
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        IP = getNodemcuIP(151, 200);
                    }
                });
                es.execute(new Runnable() {
                    @Override
                    public void run() {
                        IP = getNodemcuIP(201, 255);
                    }
                });

                if (ipSet) {
                    es.shutdown();
                    bar.setVisibility(View.GONE);
                    Log.e("taskStatus", "done");
                    Toast.makeText(getBaseContext(), "Searching Completed", Toast.LENGTH_LONG).show();
                }
            }


        });


        setIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IP = text.getText().toString().trim();
                checkIP(IP);
                closeKeyboard();
            }
        });

        group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipSet)
                    esp8266Connector.showGroup();
                else {
                    Toast.makeText(getBaseContext(), "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
            }
        });

        showIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ipSet)
                    esp8266Connector.showIPAdd();
                else {
                    Toast.makeText(getBaseContext(), "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
            }
        });


        up.setOnTouchListener(this);
        down.setOnTouchListener(this);
        left.setOnTouchListener(this);
        right.setOnTouchListener(this);
        stop.setOnTouchListener(this);

    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.button_up:
                if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == true) {

                    esp8266Connector.moveForward();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == false) {
                    Toast.makeText(this, "Type a valid IP address", Toast.LENGTH_SHORT).show();
                } else if (event.getAction() == MotionEvent.ACTION_UP && ipSet == true) {

                    esp8266Connector.stopMoving();
                }

                break;
            case R.id.button_down:
                if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == true) {

                    esp8266Connector.moveBackward();
                } else if (event.getAction() == MotionEvent.ACTION_UP && ipSet == true) {

                    esp8266Connector.stopMoving();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == false) {
                    Toast.makeText(this, "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_right:
                if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == true) {

                    esp8266Connector.turnRight();
                } else if (event.getAction() == MotionEvent.ACTION_UP && ipSet == true) {

                    esp8266Connector.stopMoving();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == false) {
                    Toast.makeText(this, "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.button_left:
                if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == true) {

                    esp8266Connector.turnLeft();
                } else if (event.getAction() == MotionEvent.ACTION_UP && ipSet == true) {

                    esp8266Connector.stopMoving();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == false) {
                    Toast.makeText(this, "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.stop_btn:
                if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet) {

                    esp8266Connector.stopMoving();
                } else if (event.getAction() == MotionEvent.ACTION_UP && ipSet == true) {

                    esp8266Connector.stopMoving();
                } else if (event.getAction() == MotionEvent.ACTION_DOWN && ipSet == false) {
                    Toast.makeText(this, "Type a valid IP address", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    public void onStop() {
        esp8266Connector.stopMoving();
        esp8266Connector.clearRequestQueue();
        es.shutdown();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        esp8266Connector.stopMoving();
        esp8266Connector.clearRequestQueue();
        es.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("IP Address", text.getText().toString());
        myEdit.commit();
        super.onPause();
    }

    @Override
    protected void onResume() {
        SharedPreferences sh = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String s1 = sh.getString("IP Address", "");
        text.setText(s1);
        super.onResume();
    }

    public void onBackPressed() {

        bar.setVisibility(View.GONE);
        esp8266Connector.clearRequestQueue();
        if (es != null)
            es.shutdown();

    }

    private void closeKeyboard() {

        View view = this.getCurrentFocus();
        if (view != null) {

            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }

    public String getMyIPAddress() {
        String myIP = null;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String mAdd = addr.getHostAddress().toUpperCase();
                        if (mAdd.length() < 20) {
                            myIP = mAdd;
                            return myIP;
                        }
                    }
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return myIP;


    }


    public String getNodemcuIP(int start, int stop) {
        String myDeviceIP = getMyIPAddress();

        String subnet = myDeviceIP.substring(0, myDeviceIP.lastIndexOf("."));
        String currentHost = "";
        for (int i = start; i < stop && !ipSet; i++) {
            currentHost = subnet + "." + i;
            Log.e("testing", currentHost);
            Process p1 = null;
            try {
                p1 = Runtime.getRuntime().exec("ping -c 1 " + currentHost);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            int returnVal = 0;
            try {
                returnVal = p1.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean reachable = (returnVal == 0);
            if (reachable) {
                Log.e("ip check is reachable", "?");
                checkIP(currentHost);

            }

        }
        return currentHost;
    }

    public void checkIP(String foundIP) {
        esp8266Connector = new ESP8266Connector(getBaseContext(), foundIP, port);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setVisibility(View.VISIBLE);
                esp8266Connector.sendRequest("S", new VolleyCallback() {
                    @Override
                    public void onSuccess(String success) {
                        if (success.equals("stop")) {
                            Log.e("found", "found");
                            ipSet = true;
                            text.setText(foundIP);
                            setIP.setText("Change Ip");
                            bar.setVisibility(View.GONE);

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("not found", "not found");
                        Toast.makeText(getBaseContext(), "Wrong IP Address", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }




}