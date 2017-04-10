# Remote access to Oulu TOLDB database.


This application is barebone and likely contains bugs.


## HOW TO:
### Option 1 (easy) : Download ready to use JAR package.


1. Download the JAR package to your desired directory. For example home/username/toldb-remote


2. Open your preferred console program.


3. Navigate to the directory. ```cd home/username/toldb-remote```


4. Run the application. ```java -jar toldb-remote```


5. The application will guide you through the process.


### Option 2 (manual work) : Build from source.


Acquire dependencies and build.


Windows: ```javac -cp .;ORACLE_DRIVER.jar;JSCH.jar;DBUTILS.jar *.java```


Linux/Unix: ```javac -cp .:ORACLE_DRIVER.jar:JSCH.jar:DBUTILS.jar *.java```


#### Dependencies:
- **Oracle JDBC Driver** for Oracle Database access.
- **JSch** for SSH and port forwarding. http://www.jcraft.com/jsch/
- **Apache Commons DbUtils** for database utilites. https://commons.apache.org/proper/commons-dbutils/
