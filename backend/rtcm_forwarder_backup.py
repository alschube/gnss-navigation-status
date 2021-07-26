import socket
import threading
import serial
from rtcm_receiver import RtcmReceiver
from position_transmitter import PositionTransmitter

class RtcmForwarder:
    rtcmEnabled = False
    threadsRunning = False
    
    position_transmitter = PositionTransmitter()
    rtcm_receiver = RtcmReceiver()
    
    thread3 = threading.Thread(target=position_transmitter.run)
    thread4 = threading.Thread(target=rtcm_receiver.run)

    def __init__(self):
        pass
    
    def setRtcmEnabled(self, bool):
        self.rtcmEnabled = bool
        print('RtcmForwarder: set rtcmEnabled to ', bool)
    
    def startThreads(self):
        print('RtcmForwarder: starting both threads......')
        self.thread3.start()
        self.thread4.start()
        
    def run(self):
        self.startThreads()
        self.rtcmEnabled = True
        while True:
            #check if the switch was activated and threads are not already running, then start
            if self.rtcmEnabled and self.threadsRunning == False:
                print('RtcmForwarder: running threads......')
                self.thread3.do_run = True
                self.thread4.do_run = True
                self.threadsRunning = True
                
            #check if the switch was disabled and threads are running, then stop  
            elif self.rtcmEnabled == False and self.threadsRunning == True:
                print('RtcmForwarder: stopping threads......')
                self.thread3.do_run = False
                self.thread4.do_run = False
                self.threadsRunning = False

if __name__ == '__main__':
    rtcm_forwarder = RtcmForwarder()
    rtcm_forwarder.run()