[![Build Status](https://travis-ci.org/diyfr/spark-archetype.svg?branch=master)](https://travis-ci.org/diyfr/spark-archetype)   

## Custom Spark Archetype :
* Spark Java a lightweight server  
* SQL2o a light library for easy query database with postgresl connection  
* Google Guice (lightweight dependency injection)  
* Gson  
* Lombok  
* Custom properties loader (Yaml UTF8 format or classic properties file)   
* Swagger model exposition  
* Dockerfile and Drone configuration for CI  
* Logback configuration

Result a jar less than 10 Mo  ;)


### Use template (Maven Archetype)
Clone this repository  
```bash
git clone https://github.com/diyfr/spark-archetype  
```
Change directory, build and install template in your local repository  
```bash
cd spark-archetype
mvn clean install
```
Optionnal : refresh your local repository  
```bash
mvn archetype:update-local-catalog
```
In your workspace (not in spark-archetype folder) use template for build your project.  
replace <value> by your values 

```bash
mvn archetype:generate \
-B \
-DarchetypeGroupId=fr.diyfr \
-DarchetypeArtifactId=spark-archetype \
-DarchetypeVersion=1.0-SNAPSHOT \
-DgroupId=<YOUR_GROUP_ID> \
-DartifactId=<YOUR_ARTIFACT_ID> \
-Dpackage=<YOUR_PACKAGE> \
-DarchetypeCatalog=local 
```
Or use Interactive (Debug) mode  
```bash  
mvn archetype:generate -DarchetypeCatalog=local -X  
```  

A new folder was created with <YOUR_ARTIFACT_ID> name , containing the source code of your project.  


### Inside  

- Sample class in `domain`  subpackage for using annotation  
- Sample controller for Swagger API description. Files in `src/main/resources`  
- Use internal or external properties (YAML or properties format) see sample in `src/main/resources/application.yml`  
- Sql2o database connection in `config` subpackage, this code use postgres. Update application.yml ;)  see `SampleRepository.java` in `repository` subpackage.  
Use repository : inject repository in your business class  
```java
    @Inject
    private SampleRepository sampleRepository;
```
- Helper subpackage with JSONHelper static method dataToJson , HttpException an sample custom exception and Sql2oColumnMapping for using @Column annotation with Sql2o (see `SampleRepository.java` and `Sample.java`)
- Update your Swagger model
- Update your registry in .drone.yml
- Update pom.xml with your developper informations
- configure logback configuration.

Build and launch !
 
```bash
mvn clean package  
cd target  
java -jar  <GROUP_ID>.<ARTIFACT_ID>.jar  
```


### Upgrade SQL2o to 1.6.0-RC3
For use ArrayList as query parameter (PostgreSql IN, ANY ...)  
Edit pom.xm, replace <sql2o-plugin-version
```xml
        <sql2o-plugin-version>1.6.0-RC3</sql2o-plugin-version>
```
For PostgreSql you need an extension (Sql2o 1.6+)  
```xml
        <dependency>
            <groupId>org.sql2o.extensions</groupId>
            <artifactId>sql2o-postgres</artifactId>
            <version>${sql2o-plugin-version}</version>
        </dependency>
```

Edit <package>/config/Database.java

replace
```java
  sql2o = new Sql2o(url, user, pass);
```
by
```java
  sql2o = new Sql2o(url, user, pass, new PostgresQuirks());
```
Add import `import org.sql2o.quirks.PostgresQuirks` 


Notes:  
 (https://logback.qos.ch/manual/layouts.html#conversionWord)[https://logback.qos.ch/manual/layouts.html#conversionWord]
