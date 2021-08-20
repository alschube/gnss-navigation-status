# gnss-navigation-status

## Table of Contents
1. [What does this programm do?](#what-does-this-programm-do?)
2. [Installation guide (Backend)](#installation-guide-(backend))
3. [Quick Start Guide (Frontend)](#quick-start-guide-(frontend))
4. [RTCM + NTRIP Configuration](#rtcm-+-ntrip-configuration)

## What does this programm do?

Short description

## Installation guide (Backend)

Follow these steps to set up the backend on your raspberry pi.


### __1. Cloning the Repository__
***
If you do not have git installed, install it first
```
$ sudo apt update
$ sudo apt install git
```

Go to the directory where you want to clone the repository
```
$ cd desiredFolder/
```
First clone the repository to your raspberry pi device via
```
$ git clone https://example.com
```

### __2. Interface Configuration__
***
Ensure that the device is set up correctly and that GPIO Remote is activated.
You can do this via GUI (see picture below)
![Raspberry Pi Config](https://gpiozero.readthedocs.io/en/stable/_images/raspi-config.png) or you can do it on the command line and enable  Remote GPIO. 
``` 
$ sudo raspi-config 
```

### __3. Python Version__
***

__Make sure you have at least Python Version 3.7 installed. If so, you can skip this step.__
Otherwise follow the instructions below to install Python 3.8.4.


The first step is to install some required packages:
``` 
$ sudo apt install libffi-dev libbz2-dev liblzma-dev libsqlite3-dev libncurses5-
dev libgdbm-dev zlib1g-dev libreadline-dev libssl-dev tk-dev build-essential libncursesw5-dev libc6-dev openssl
```

To download version 3.8.4, type the following command:
``` 
$ wget https://www.python.org/ftp/python/3.8.4/Python-3.8.4.tar.xz
``` 

These are unpacked with:
``` 
$ tar xf Python-3.8.4.tar.x
``` 

Navigate to the unzipped folder:
``` 
$ cd Python-3.8.4
``` 

And prepares the configuration. This process can take a few minutes.
``` 
$ ./configure
``` 

The installation file is created via:
``` 
$ make -j -l 4
``` 

After all, you install them with:
``` 
$ sudo make altinstall
``` 

In order not to have to enter python3.8 or pip3.8 all the time, you can add aliases.
``` 
$ echo "alias python3=python3.8" >> ~/.bashrc
$ echo "alias pip3=pip3.8" >> ~/.bashrc
``` 

Use the following command to load the new settings so that they take effect.
``` 
$ source ~/.bashrc
``` 

### __4. Additional Python Package__
***
In order to run the programm you also need some additional python package to install:
```
pip3 install serial
```

### __5. Receiver Configuration__
***
The Receiver needs to be configured once to enable all required messages and protocols.


To do so, the provided setup.py script is sending several messages one after the other to the receiver over the UART1 of the Pi.


The script can be found in the backend folder.

Simply run it by opening cmd in the backend folder and typing:
```
python setup.py
```

### __6. Starting and Stopping__
***

To finally run the backend, go to the backend folder and run it via:
```
python main_backend.py
```

The program can be interrupted by pressing the key combination Ctrl + C twice.

## Quick Start Guide (Frontend)

* To set up the frontend simply install the apk that is found in the frontend folder on your smartphone

* When you first open the app you have to configure the ip address of your raspberry pi in the settings tab. After clicking the connect Button a connection should be established.

* In order to configure which satellite systems should be used you can check or uncheck them under the Settings Tab

* In order to use RTCM correction you have to run an ntrip client and connect to a base station (see Chapter RTCM) and you need to activate it in the app in the Settings Tab

## RTCM + NTRIP

If you want to use RTCM corrections you have to install an ntrip client on your raspberry pi or laptop. I used rtknavi from the rtklib which can be downloaded from http://www.rtklib.com/

By clicking on the "I" on the top you can create a new input stream.

Check (2) Base Station, select NTRIP Client as type and click on the first '...' Button.

There you have to enter your credentials and the Mountpoint you wish to use.
If you click on Ntrip... you can select the desired Mountpoint from a list, copy and paste it into the input field.

After that you can click OK and you have set the to Transmit NMEA GPGGA to Base Station to "Latitude/Longitude" and provide your current position as accurate as possible (you can read the longitude and latitude that the app calculates).

After that you need to output the received RTCM data. For that you have to click on the "L" Button in the upper right corner.

Check (7) Base Station and TCP Client as Type. Open the Options by clicking on the '...' Button right beside it.
There you have to type your raspberry pi's ip address and the port of the TCP Server (the backend), it is 8766.

You can confirm and go back to main window.
If you click start, the ntrip client tries to communicate with the base station and transmitts your position in order to get correction data. This can take up to some minutes. If you see bars in the diagram in the center below, it is working.

You can now activate rtcm in the app (if not already done) and see how the calculated position gets more and more accurate.