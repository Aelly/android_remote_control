package com.aelly.fr.raspberry_controllapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private ImageButton restartIBtn;
    private ImageButton shutdownIBtn;
    private Button Btn_layout1;
    private Button Btn_layout2;
    private Button Btn_configSSH;

    private EditText ET_User;
    private EditText ET_Password;
    private EditText ET_Host;
    private EditText ET_Port;
    private Button Btn_Submit;
    private Button Btn_Cancel;

    public static final String SHARED_PREFS_SHH = "sharedPrefsSSH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        restartIBtn = findViewById(R.id.restartIBtn);
        shutdownIBtn = findViewById(R.id.shutdownIBtn);

        Btn_layout1 = findViewById(R.id.firstLayoutBtn);
        Btn_layout2 = findViewById(R.id.secondLayoutBtn);

        Btn_configSSH = findViewById(R.id.Btn_configSSH);

        Btn_Cancel = findViewById(R.id.btnCancel);

        new CommandBackground("echo test").execute(1);

        Btn_layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommandBackground("export DISPLAY=:0 && chromium-browser --kiosk --incognito --noerrdialogs Documents/raspberry-assistant/api/index.html").execute(1);
            }
        });
        Btn_layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CommandBackground("export DISPLAY=:0 && chromium-browser https://aelly.github.io").execute(1);
            }
        });
        restartIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to restart ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new CommandBackground("sudo reboot").execute(1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });
        shutdownIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom)
                        .setTitle("Confirm")
                        .setMessage("Are you sure you want to shutdown ?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new CommandBackground("sudo shutdown -h now").execute(1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });


        Btn_configSSH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(R.layout.pop_up_saved_ssh);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                Btn_Submit = alertDialog.findViewById(R.id.btnSubmit);
                Btn_Cancel = alertDialog.findViewById(R.id.btnCancel);
                ET_User = alertDialog.findViewById(R.id.ET_User);
                ET_Password = alertDialog.findViewById(R.id.ET_Password);
                ET_Host = alertDialog.findViewById(R.id.ET_Host);
                ET_Port = alertDialog.findViewById(R.id.ET_Port);
                Btn_Submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveDataSSHConfig(ET_User.getText().toString(), ET_Password.getText().toString(), ET_Host.getText().toString(), Integer.parseInt((ET_Port.getText().toString())));
                        alertDialog.cancel();
                    }
                });
                Btn_Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    public void saveDataSSHConfig(String user, String password, String host, int port){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SHH, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("User", user);
        editor.putString("Password", password);
        editor.putString("Host", host);
        editor.putInt("Port",port);

        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }

    private class CommandBackground extends AsyncTask<Integer, Void, Void>{
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_SHH, MODE_PRIVATE);

        private String user = sharedPreferences.getString("User", "");
        private String password = sharedPreferences.getString("Password", "");
        private String host = sharedPreferences.getString("Host","");
        private int port = sharedPreferences.getInt("Port",0);

        private String command;

        public CommandBackground(String cmd){
            this.command = cmd;
        }

        protected Void doInBackground(Integer... params) {
            try{
                String ret = executeRemoteCommand(user,password,host,port);
                //System.out.println("Return: " + ret + " Fin return");
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public String executeRemoteCommand(String username,String password,String hostname,int port)
                throws Exception {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, hostname, port);
            session.setPassword(password);


            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);

            session.connect();

            // SSH Channel
            ChannelExec channelssh = (ChannelExec)
                    session.openChannel("exec");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            channelssh.setOutputStream(System.out);

            // Execute command
            //channelssh.setCommand("ls > /home/pi/test.txt");
            channelssh.setCommand(command);
            channelssh.connect();
            channelssh.disconnect();

            return baos.toString();
        }
    }


}
