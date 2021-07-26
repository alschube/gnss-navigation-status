import multiprocessing
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder
from gnss_data_fetcher import DataFetcher
from handle_messages import MessageHandler

def run():
    print("Starting Main Backend ...........................")
    
    #running Data Fetcher on new Thread
    data_fetcher = DataFetcher()
    p1 = multiprocessing.Process(target=data_fetcher.run)
    p1.start()
    
    #running Message Handler on new Thread
    message_handler = MessageHandler()
    p2 = multiprocessing.Process(target=message_handler.run)
    p2.start()

if __name__ == '__main__':
    run()