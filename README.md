# Remote access to Oulu TOLDB database.


This application is barebone and likely contains bugs.


## HOW TO:
### Option 1 (easy) : Download ready to use JAR binaries. This method requires Java Runtime Environment. https://github.com/tuokri/javadbc/releases/download/v0.2-alpha/toldb-remote-v0_2.zip


1. Download the ZIP package to your desired directory. For example /home/username/toldb-remote/


2. Extract the ZIP.


3. Open your preferred console program.


4. Navigate to the directory. ```cd home/username/toldb-remote```


5. Run the application. ```java -jar toldb-remote.jar```


6. The application will ask you for your credentials and guide you through the process.


**Video instructions :** https://www.youtube.com/watch?v=kLJ5XBx9mqU


### Option 2 (manual work) : Build from source.


Acquire dependencies and build.


Windows: ```javac -cp .;ORACLE_DRIVER.jar;JSCH.jar;DBUTILS.jar; *.java```


Linux/Unix: ```javac -cp .:ORACLE_DRIVER.jar:JSCH.jar:DBUTILS.jar: *.java```


#### Dependencies:
- **Oracle JDBC Driver** for Oracle Database access.
- **JSch** for SSH and SCP. http://www.jcraft.com/jsch/
- **Apache Commons DbUtils** for database utilites. https://commons.apache.org/proper/commons-dbutils/
