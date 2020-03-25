# Distributed Library Management System (CORBA Version)

The project has 2 main modules:
● Client : This is the module used by the users, i.e., students and managers to access the information in the servers. It communicates with the server using CORBA. The client inputs are converted into a string and sent to the server using a the CORBA connection.
● Server : The server contains all the book details. Based on the request obtained, a reply is sent back to the client. Communication between servers is done by using UDP communication.

Communication:
The modules communicate with each other using 2 protocols :
● CORBA : The Client and server interact with each other using CORBA, client is the CORBA
client. The message sent from the client to corba is a string containing all the information that is required to process the request.
● UDP : The servers communicate with each other using the unreliable UDP protocol. The protocol is made reliable by using checksums that uses the MD5 and UTF-8 encoding .
