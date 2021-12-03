DumpTracker, UpdateTracker, CreateTracker and FRS are Java example using Jagosi API to access GForge's SOAP interface.

DumpTracker:
dumps elements of a tracker.
Results are dump in <tracker_name>.csv

Mandatory options:
-U : url of the soap interface on the server
-u login_name : user's login
-p password : user's password
-g project_name : project's name
-t tracker_name : tracker's name

DumpTracker can be called with DumpTracker.bat batch (Windows OS) or DumpTracker.sh (Linux OS).

Ex:
DumpTracker.bat -U https://acos.alcatel-lucent.com/soap/ -u myCSL -p mypassword -g projecta -t "Support"

UpdateTracker:
updates elements of a tracker.
Data must be in <tracker_name>_update.csv file.
First line must be exact names of the fields. It can be easiest to dump the tracker before to have a template.
Mandatory fiels in the CSV file are: artifact_id, state, priority, assignedTo, summary, details

UpdateTracker can be called with UpdateTracker.bat (Windows OS) or UpdateTracker.sh (Linux OS).

Mandatory options:
-U : url of the soap interface on the server
-u login_name : user's login
-p password : user's password
-g project_name : project's name
-t tracker_name : tracker's name

UpdateTracker can be called with UpdateTracker.bat (Windows OS) or UpdateTracker.sh (Linux OS).

Ex:
UpdateTracker.bat -U https://acos.alcatel-lucent.com/soap/ -u myCSL -p mypassword -g projecta -t "Support"

CreateTracker:
creates elements in a tracker.
Data must be in <tracker_name>_create.csv file.
First line must be exact names of the fields. It can be easiest to dump the tracker before to have a template.
Mandatory fiels in the CSV file are: summary, details
Default values (if not set):
- state: null (ANY)
- priority: 5
- assignedTo: Nobody

Mandatory options:
-U : url of the soap interface on the server
-u login_name : user's login
-p password : user's password
-g project_name : project's name
-t tracker_name : tracker's name

CreateTracker can be called with CreateTracker.bat (Windows OS) or CreateTracker.sh (Linux OS).

Ex:
CreateTracker.bat -U https://acos.alcatel-lucent.com/soap/ -u myCSL -p mypassword -g cssforge -t "Test campains"

FRS:
outputs FRS's informations, creates packages and releases, uploads files.

Mandatory options:
-U : url of the soap interface on the server
-u login_name : user's login
-p password : user's password
-g project_name : project's name

Optional options:
-d : display FRS's informations
-k package_name : package's name of the uploaded file; if not set, project_name (from -g option) is used
-r version : version's name
-n file_name : file's name of the uploaded file
-f file : path of the file to upload

FRS can be called with FRS.bat (Windows OS) or FRS.sh (Linux OS).

Ex:
FRS.bat -U https://acos.alcatel-lucent.com/soap/ -u myCSL -p mypassword -g factory-test -r 1.1 -k toto1.1 -n toto -f /tmp/toto.tar
