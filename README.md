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


- http://localhost:8080/

1. Get a csv file with testdata by clicking "Generate Test File" button
2. Upload the same file (either by Drag & Drop / by clicking Browse button
3. Spinning Loader Wheel indicates work in progress, Once the file processing gets complete, the "Blue color" Wheel indicates successful processing of file and "Red color" indicates failures.
4. Simultaneously, the table in home page will display the metrics behind that file upload.
5. "Search" box in top right naviagtion bar, can be used search a file by providing file name.  
