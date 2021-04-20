import threading
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder
from gnss_data_fetcher import DataFetcher

def run():
    print("Starting Main Backend ...........................")
    data_fetcher = DataFetcher()
    thread1 = threading.Thread(target=data_fetcher.run)
    thread1.start()
    #data_fetcher.run()
    
    #thread2 = threading.Thread(target=listen_to_messages)
    #thread2.start()

def listen_to_messages():
    print("test")

if __name__ == '__main__':
    run()