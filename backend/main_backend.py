import threading
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder
from gnss_data_fetcher import DataFetcher
from handle_messages import MessageHandler

def run():
    print("Starting Main Backend ...........................")
    
    #running Data Fetcher on new Thread
    data_fetcher = DataFetcher()
    thread1 = threading.Thread(target=data_fetcher.run)
    thread1.start()
    
    #running Message Handler on new Thread
    message_handler = MessageHandler()
    thread2 = threading.Thread(target=message_handler.run)
    thread2.start()
    

if __name__ == '__main__':
    run()