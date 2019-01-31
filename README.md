# Android remote control app
Android app to controll a raspberry in headless mode displaying a dashboard (browser + kiosk)
This is an android application to controll a Raspberry Pi 3 displaying a dashboard in headless mode. 
The goal was to be able to shutdown and restart the Raspberry without having to plug-in a keyboard or to use SSH from another 
computer and to be able to change the dashboard page displayed.

## Using
```
-Android Studio
-Java Secure Channel library for SSH
```

## Details

This application simply send a command via SSH to the raspberry. You can configure the SSH information inside the application 
without needing to change the code, this configuration is saved in a SharedPreferences file in the device. 

## Images

<p align="center">
  <img src="https://raw.githubusercontent.com/Aelly/android_remote_control/master/Example1.png" width="350" title="APP">
  <img src="https://raw.githubusercontent.com/Aelly/android_remote_control/master/Example2.png" width="350" title="SSH Config">
</p>
