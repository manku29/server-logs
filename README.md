# server-logs
Our custom-build server logs different events to a file named logfile.txt.  Every event has 2 entries in the file - one entry when the event was started and another when the event was finished. The entries in the file have no specific order (a finish event could occur before a start event for a given id) Every line in the file is a JSON object containing the following event data: 
