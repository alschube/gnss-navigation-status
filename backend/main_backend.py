import multiprocessing
import sys
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder
from gnss_data_fetcher import DataFetcher
from handle_messages import MessageHandler


data_fetcher = DataFetcher()
message_handler = MessageHandler()
message_handler.setMsgHandlerInst(data_fetcher)

def run():
    print("Starting Main Backend ...........................")
    
    #running Data Fetcher on new Thread
    process1 = multiprocessing.Process(target=data_fetcher.run)
    process1.start()
    
    #running Message Handler on new Thread
    process2 = multiprocessing.Process(target=message_handler.run)
    process2.start()
    
    while True:
        try:
            pass
        except (KeyboardInterrupt):
            print('Shutting down Main Backend ...........................')
            data_fetcher.FINISH = True
            message_handler.FINISH = True
            sys.exit()

if __name__ == '__main__':
    run()