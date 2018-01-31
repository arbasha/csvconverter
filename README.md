# Instructions to Get started

MySQL DB Config:
- DB URL:localhost:3306
- CREATE DATABASE deal;
- username: root
- password: root

Application Setup:
1. mvn clean install
2. Go to the target folder
3. Run java -jar csv-converter-0.0.1-SNAPSHOT.jar
            or
4. Get the jar from https://github.com/arbasha/csvconverter/tree/master/target/csv-converter-0.0.1-SNAPSHOT.jar and execute it with java -jar command

# Home page - http://localhost:8080/

1. Get a csv file with testdata by clicking "Generate Test File" button
2. Upload the same file (either by Drag & Drop / by clicking Browse button
3. Spinning Loader Wheel indicates work in progress, Once the file processing gets complete, the "Blue color" Wheel indicates successful processing of file and "Red color" indicates failures.
4. Simultaneously, the table in home page will display the metrics behind that file upload.
5. "Search" box in top right naviagtion bar, can be used search a file by providing file name.  

![index](https://user-images.githubusercontent.com/22431218/35640474-4519a4a6-06e3-11e8-919d-e30da8754c1f.PNG)


![file_search](https://user-images.githubusercontent.com/22431218/35640911-9db4e430-06e4-11e8-8278-29fe9fc37c3d.PNG)
