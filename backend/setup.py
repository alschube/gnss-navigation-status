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

message_list = None

def createMessages():
    enable_rtcm=''
"""
CFG-MSGOUT-UBX_NAV_SAT_UART1
Ram layer config message:
b5 62 06 8a 09 00 00 01 00 00 07 00 91 20 01 53 48 

BBR layer config message:
b5 62 06 8a 09 00 00 02 00 00 07 00 91 20 01 54 50 

Flash layer config message:
b5 62 06 8a 09 00 00 04 00 00 07 00 91 20 01 56 60

CFG-MSGOUT-UBX_NAV_SAT_UART1
Ram layer config message:
b5 62 06 8a 09 00 00 01 00 00 16 00 91 20 01 62 93 

BBR layer config message:
b5 62 06 8a 09 00 00 02 00 00 16 00 91 20 01 63 9b 

Flash layer config message:
b5 62 06 8a 09 00 00 04 00 00 16 00 91 20 01 65 ab

CFG-MSGOUT-UBX_RXM_RTCM_UART1
Ram layer config message:
b5 62 06 8a 09 00 00 01 00 00 69 02 91 20 01 b7 3a 

BBR layer config message:
b5 62 06 8a 09 00 00 02 00 00 69 02 91 20 01 b8 42 

Flash layer config message:
b5 62 06 8a 09 00 00 04 00 00 69 02 91 20 01 ba 52 


"""


def run():
    

if __name__ == '__main__':
    run()