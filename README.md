# WIFIlogger

LOGIN PAGE:
When opening the app we can notice that a first registration is needed, if we try to login without been registered an error message will be displayed, the email and password are needed not only for security but the email is used in the Firebase database as a refence too.
Once registered the app will automatically log in in the app, if you just logged out from the previous section you will be able to enter the same data from the previous session and login, if you will try to register again with the same email an error message will be displayed.

 MENU PAGE:
After you successfully logged in a new page will be prompt, this page will allow you 4 different commands:
-	Start scan will allow the app the scan all the surrounding WIFI connection available, after the scan is completed a message will be displayed with the amount of WIFI scanned.
-	Get data will send the user to a new page that will display all the data that the user scanned.
-	Log out will simply redirect the user to the login page and in the next section when the user will reopen the app, she/he will need to insert the same data that she/he used to register previously.
-	Delete account will delete the current data that the user used to register and redirect the user to the login page, the user will need to register again using a new or an already used email and password.

LIST DATA PAGE:

This last page has the task of display all the data related to the previous scan that the user made with the user data (date, phone id and GPS location), and at the bottom of the page there is a delete data btn that has the task of deleting all the data related to the user scan that are stored in the Firebase database.

