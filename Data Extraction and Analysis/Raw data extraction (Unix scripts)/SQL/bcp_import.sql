LOAD DATA LOCAL INFILE 'finalLog.txt' INTO TABLE history 
FIELDS TERMINATED BY ' | '
(PROJECT_NAME, REVISION, AUTHOR, DATE, SOURCE_CONTROL)
