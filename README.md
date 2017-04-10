# Remote access to Oulu TOLDB database.


## HOW TO:
### Option 1 (easy) : Download ready to use JAR package.


### Option 2 (manual work) : Build from source.


Acquire dependencies and build.


Windows: ```javac -cp .;ORACLE_DRIVER.jar;JSCH.jar;DBUTILS.jar *.java```


Linux/Unix: ```javac -cp .:ORACLE_DRIVER.jar:JSCH.jar:DBUTILS.jar *.java```


#### Dependencies:
- **Oracle JDBC Driver** for Oracle Database access.
- **JSch** for SSH and port forwarding. http://www.jcraft.com/jsch/
- **Apache Commons DbUtils** for database utilites. https://commons.apache.org/proper/commons-dbutils/
