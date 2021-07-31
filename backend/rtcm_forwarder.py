import socket
import multiprocessing
import serial
from rtcm_receiver import RtcmReceiver
from position_transmitter import PositionTransmitter

class RtcmForwarder:
    rtcmEnabled = False
    threadsRunning = False
    
    position_transmitter = PositionTransmitter()
    rtcm_receiver = RtcmReceiver()

    def __init__(self):
        pass
    
    def setRtcmEnabled(self, bool):
        self.rtcmEnabled = bool
        print('RtcmForwarder: set rtcmEnabled to ', bool)
    
    def startThreads(self):
        print('.')
        
    def run(self):
        #self.startThreads()
        p1 = multiprocessing.Process(target=self.position_transmitter.run)
        p2 = multiprocessing.Process(target=self.rtcm_receiver.run)
        print('RtcmForwarder: starting both threads......')
        p1.start()
        p2.start()
        
        #self.rtcmEnabled = True
        while True:
            #check if the switch was activated and threads are not already running, then start
            if self.rtcmEnabled and self.threadsRunning == False:
                print('RtcmForwarder: running threads......')
                self.p1.do_run = True
                self.p2.do_run = True
                self.threadsRunning = True
                
            #check if the switch was disabled and threads are running, then stop  
            elif self.rtcmEnabled == False and self.threadsRunning == True:
                print('RtcmForwarder: stopping threads......')
                self.p1.do_run = False
                self.p2.do_run = False
                self.threadsRunning = False

if __name__ == '__main__':
    rtcm_forwarder = RtcmForwarder()
    rtcm_forwarder.run()
