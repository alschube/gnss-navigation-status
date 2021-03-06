# GNSS Navigation Status
## Table of Contents
1. [Overview](#overview)
    - [1.1. What does this programm do?](#1-what-does-this-programm-do)
    - [1.2. Requirements](#2-requirements)
2. [Installation guide (Backend)](#installation-guide-backend)
    - [2.1. Cloning the Repository](#1-cloning-the-repository)
    - [2.2. Interface Configuration](#2-interface-configuration)
    - [2.3. Python Version](#3-python-version)
    - [2.4. Additional Python Package](#4-additional-python-packages)
    - [2.5. Receiver Firmware](#5-receiver-firmware)
    - [2.6. Receiver Configuration](#6-receiver-configuration)
    - [2.7. Starting and Stopping](#7-starting-and-stopping)
3. [Quick Start Guide (Frontend)](#quick-start-guide-frontend)
4. [RTCM and NTRIP Configuration](#rtcm-and-ntrip-configuration)
5. [Links](#links)

***

## Overview

### __1. What does this programm do?__

This project was developed in the context of my bachelor's thesis for the Applied Computer Science degree programme at Heilbronn University.

The required data, such as position and Satellites are received by the u-blox ZED-F9R pHAT and then passed on to the Raspberry Pi via UART to be further processed in the backend and sent on to the frontend application (Android).

The backend also serves as an interface for the frontend to communicate with the receiver to configure it. You can configure if which satellite systems you want to use to calculate your position or if you want to use rtcm data.

The Ublox Python Package by SparkFun Electronics was used for the implementation and extended with some additional functions, which were not provided.

In order to use RTCM correction data, a connection to an RTCM provider is established via NTRIP Client and the correction data received is sent back to the pHAT, which then computes a centimetre-precise position.

### __2. Requirements__

To run this programm you'll need at least the following hardware:

Frontend:
* Android Smartphone (Minimum API Level 30: Android 11.0)

Backend:
* Raspberry Pi 4 - 4GB Model
* SparkFun GPS-RTK Dead Reckoning pHAT for Raspberry Pi
* 15W USB-C power supply
* Micro SD card with Raspberry Pi OS installed
* keyboard and mouse
* GNSS Multi-Band Antenna (SMA)
* Interface Cable SMA to U.FL

Other: (Only if you wish to use rtcm)
* Credentials with a RTCM provider such as SAPOS
* NTRIP Client such as RTKNAVI

## Installation guide (Backend)

If you need help with setting up your raspberry pi for the first time, please check out this [tutorial](https://projects.raspberrypi.org/en/projects/raspberry-pi-setting-up)

Once your raspberry pi is ready, follow these steps to set up the backend.


### __1. Cloning the Repository__
***
If you do not have git installed, install it first
```shell
$ sudo apt update
$ sudo apt install git
```

Go to the directory where you want to clone the repository to
```shell
$ cd desiredFolder/
```
Clone it via
```shell
$ git clone https://github.com/alschube/gnss-navigation-status.git
```

### __2. Interface Configuration__
***
Ensure that GPIO Remote is activated.

You can do this via GUI under Settings > Interfaces (see picture below)
![Raspberry Pi Config](https://gpiozero.readthedocs.io/en/stable/_images/raspi-config.png)

or you can do it on the command line and enable  Remote GPIO. 
``` shell
$ sudo raspi-config 
```

### __3. Python Version__
***

__Make sure you have at least Python Version 3.7 installed. If so, you can skip this step and go to the [next chapter](#4-additional-python-packages).__

Otherwise follow the instructions below to install Python 3.8.4, which i worked with.

The first step is to install some required packages:
```shell
$ sudo apt install libffi-dev libbz2-dev liblzma-dev libsqlite3-dev libncurses5-
dev libgdbm-dev zlib1g-dev libreadline-dev libssl-dev tk-dev build-essential libncursesw5-dev libc6-dev openssl
```

To download version 3.8.4, type the following command:
``` shell
$ wget https://www.python.org/ftp/python/3.8.4/Python-3.8.4.tar.xz
``` 

These are unpacked with:
``` shell
$ tar xf Python-3.8.4.tar.x
``` 

Navigate to the unzipped folder:
``` shell
$ cd Python-3.8.4
``` 

And prepares the configuration. This process can take a few minutes.
``` shell
$ ./configure
``` 

The installation file is created via:
``` shell
$ make -j -l 4
``` 

After all, you install them with:
``` shell
$ sudo make altinstall
``` 

In order not to have to enter python3.8 or pip3.8 all the time, you can add aliases.
``` shell
$ echo "alias python3=python3.8" >> ~/.bashrc
$ echo "alias pip3=pip3.8" >> ~/.bashrc
``` 

Use the following command to load the new settings so that they take effect.
``` shell
$ source ~/.bashrc
``` 

### __4. Additional Python Packages__
***
In order to run the programm you also need some additional python packages to install:
``` shell
$ pip3 install serial
$ pip3 install spidev
```

### __5. Receiver Firmware__
__If you got a new ZED-F9R you probably need to update the firmware version.__
Sometimes they are delivered with a test firmware version only and need to be updated as it was in my case.

To do so, download the latest firmware version from [ZED-F9R Module Firmware](https://www.u-blox.com/en/product/zed-f9r-module?field_file_category=223&field_file_lifecycle=All&field_file_legacy_single=0&edit-submit-product-information=Go#tab-documentation-resources). You'll find it under Documentation & Resources > Firmware Update > ZED-F9R HPS 1.20 firmware



After downloading you need to install it via u-center software.
(Unless you haven't already installed, download from [here](https://www.u-blox.com/en/product/u-center) and install it)

To install the firmware, open u-center, select the COM-Port where the receiver is connected (directly connect it via usb-c to your pc) and once the connection is established you can click Tools > Firmware Update

![Firmware Update](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/firmware-update.png?token=AN4NRRNP7FZYPT6ZFUQI65TBLL4JE)

Select the downloaded firmware file, the baudrate (typically 115200), please check 'Enter safeboot before update', 'send training sequence' and 'use chip erase' options.

![Firmware Update Window](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/u-center-firmware.png?token=AN4NRRK5XMGVJTOHIMA6O7LBLL4PW)

Click Send and the firmware will be uploaded.



### __6. Receiver Configuration__
***
The Receiver needs to be configured once to enable all required messages and protocols.


To do so, the provided setup.py script is sending several messages one after the other to the receiver over the UART1 of the Pi.


The script can be found in the backend folder.

Simply run it by opening cmd in the backend folder and typing:
```shell
$ python setup.py
```

### __7. Starting and Stopping__
***

To finally run the backend, go to the backend folder and run it via:
```shell
$ python main_backend.py
```

The program can be interrupted by pressing the key combination Ctrl + C twice.

## Quick Start Guide (Frontend)

* To set up the frontend simply install the __apk that is found under \gnss-navigation-status\frontend\app\release__ or under Releases (Version 1.0)

* When you first open the app you have to configure the ip address of your raspberry pi in the settings tab. After clicking the connect Button a connection should be established.

* In order to configure which satellite systems should be used you can check or uncheck them under the Settings Tab

* In order to use RTCM correction you have to run an ntrip client and connect to a base station (see [RTCM Chapter](#rtcm-and-ntrip-configuration)) and you need to activate it in the app in the Settings Tab

## RTCM and NTRIP Configuration

If you want to use RTCM corrections you have to install an ntrip client on your raspberry pi or laptop. I used rtknavi from the rtklib which can be downloaded from http://www.rtklib.com/

By clicking on the "I" on the top you can create a new input stream.

![Input Streams](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/Screenshot%202021-08-20%20124812.png?token=AN4NRRP5MW5L5CLGIS7QPHLBLL4R6)

First you have to set the _'Transmit NMEA GPGGA to Base Station'_ to _'Latitude/Longitude'_ and provide your current position as accurate as possible (e.g. the position calculated by the app).
Also check _'(2) Base Station'_, select _'NTRIP Client'_ as type and click on the first '...' Button.

![NTRIP Client Options](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/Screenshot%202021-08-20%20124836.png?token=AN4NRRJBJMMYIJWRX54NUJ3BLL4TQ)

There you have to enter your credentials and the Mountpoint you wish to use.
If you click on _'Ntrip...'_ you can select the desired Mountpoint from a list, copy and paste it into the input field.

After that you need to output the received RTCM data. For that you have to click on the "L" Button in the upper right corner.

![Log Streams](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/Screenshot%202021-08-20%20124906.png?token=AN4NRRJLSGCLOYDHNXH3AKTBLL4US)

Check _'(7) Base Station'_ and _'TCP Client'_ as type. Open the Options by clicking on the '...' Button right beside it.
There you have to type your raspberry pi's ip address and the port of the TCP Server (the backend) 8766.

![TCP Client Options](https://raw.githubusercontent.com/alschube/Bachelorarbeit/dev/LaTeX%20Vorlage/Bilder/Screenshot%202021-08-20%20124917.png?token=AN4NRRKMK7X56V4HBM7EZ2DBLL4VW)

You can confirm and go back to the main window.
If you click start, the Ntrip client tries to communicate with the base station and transmitts your position in order to get correction data. This can take up to some minutes. If you see bars in the diagram in the center below, it is working.

You can now activate rtcm in the app (if not already done) and see how the calculated position gets more and more accurate.

## Links

[Github - Qwiic Ublox Gps Py](https://github.com/sparkfun/Qwiic_Ublox_Gps_Py/)

[SparkFun Hookup Guide](https://learn.sparkfun.com/tutorials/sparkfun-gps-rtk-dead-reckoning-zed-f9r-hookup-guide/introduction)

[RTKLIB Manual](http://www.rtklib.com/prog/manual_2.4.2.pdf)

[ZED-F9R Integrationmanual](https://cdn.sparkfun.com/assets/learn_tutorials/1/1/7/2/ZED-F9R_Integrationmanual__UBX-20039643_.pdf)

[ZED-F9R Module Overview and Resources](https://www.u-blox.com/en/product/zed-f9r-module)
