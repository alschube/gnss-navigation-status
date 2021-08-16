# This project was developed in the context of my bachelor thesis.
# It serves as a backend for my app to provide centimeter accurate positioning.
#
# The required data is received by the receiver and then passed on to the
# Raspberry Pi via the UART to be further processed in the backend and sent on to the frontend.
#
# It also serves as an interface for the frontend to communicate with the receiver to configure it.
#
# The Ublox Python package was used for the implementation.
# This package was also extended by some additional functions, which where not provided by u-blox
#------------------------------------------------------------------------
# Written by Aline Schubert, 2021

import multiprocessing
import sys
from gnss_configurator import GnssConfigurator
from rtcm_forwarder import RtcmForwarder
from gnss_data_fetcher import DataFetcher
from handle_messages import MessageHandler

"""
This starts the backend and all its subprocesses

Attributes
----------
data_fetcher : DataFetcher
    the data fetcher instance
message_handler : MessageHandler
    the message handler instance
"""
data_fetcher = DataFetcher()
message_handler = MessageHandler()
message_handler.setMsgHandlerInst(data_fetcher)

def run():
    """
    this starts the data fetcher and the message handler
    
    Raises
    ------
    KeyboardInterrupt
        if Strg + C is pressed the backend including all subprocesses will be shutdown
    """
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