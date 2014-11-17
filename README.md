COMP3150
========

Computer Networking Course Project

Right just so we all know how to use this git stuff, can we all edit this readme toshow all of our Names and ID's please.

Nicholas Mendez - 811002795
Kimberly Bridglal - 811000371
Jherez Taylor - 812003287
Inzamam Rahaman - 810006495


| Functionality | Expected Response From Client | Epected Response from Server|
|---------------|:----------------------------:|-----------------------------:|
|Add file to server | STOR filename filesize(bytes) | 200 File write sucessful|
|Retrieval File from server| RETR filename |125 Expect numberofbytes|
|Get directory contents | PWD | 200 filename1:filename2:filename3|

FTP Status codes that need to be handled by client:
(Suggestion: use enums to handle on client)
1. 403 - Invalid Username or password
2. 331 - Username accpeted, but we need password
3. 231 - Quiting service
4. 230 - User logged in, proceeding to service
5. 530 - Not logged in
6. 503 - Bad sequence of commands
7. 250 - Requested operation successful
8. 450 - Requested action not taken
9. 202 - Command not implemented
10. 553 - File does not exist

Note: all 2xx commands may be represtned by a single enum for success

Note: code to represent FTP Status codes completed in client; just write code to handle the codes

Code to perfrom heavy lifting of client completed.

To use:

Client client = new AuthenticatedClient(address, port, username, password);

Call services from client and handle responses in UI
Read the code in Client and AuthenticatedClient classes to understand how to call the methods
Read the Status enum and use common sense to see how to handle the Responses from the server
