# SQL Table Name Parser

[![Build Status](https://travis-ci.org/mnadeem/sql-table-name-parser.svg?branch=master)](https://travis-ci.org/mnadeem/sql-table-name-parser)
[![Coverage Status](https://coveralls.io/repos/github/mnadeem/sql-table-name-parser/badge.svg)](https://coveralls.io/github/mnadeem/sql-table-name-parser)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mnadeem/sql-table-name-parser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mnadeem/sql-table-name-parser)
[![Dependency Status](https://www.versioneye.com/user/projects/57c0512d968d6400395168f8/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/57c0512d968d6400395168f8)

Regular Expressions are not full proof solution for extracting table names from SQL queries... As tons of things has to be considered, which would be trickier to express in RegX, and would break out in one or other cases....

Then what?? Full proof sql parsers like [JSQL parser](https://github.com/JSQLParser/JSqlParser) , [ZQL Library](http://zql.sourceforge.net/), [SQL Parser](http://www.sqlparser.com/)??

Well if you just need to extract table names from SQLs, full blown SQL parsers would be over kill, further most of the parser does not support all the dialects, You may end up modifying the grammer files for you need, just to extract table names.

This small library would help you do that very easily and concisely.

# How To Use It?

Just Use the following Syntax

    new TableNameParser(sql).tables()


## License

SQL Table Name Parser is licensed under **Apache Software License, Version 2.0**.

## News

* Version **0.0.3** released on 11/03/2016.
* Version **0.0.2** released on 08/27/2016.


## Maven Repository

SQL Table Name Parser is deployed at sonatypes open source maven repository. You may use the following repository configuration (if you are interested in snapshots)

```xml
<repositories>
     <repository>
         <id>dexecutor-snapshots</id>
         <snapshots>
             <enabled>true</enabled>
         </snapshots>
         <url>https://oss.sonatype.org/content/groups/public/</url>
     </repository>
</repositories>
```
This repositories releases will be synched to maven central on a regular basis. Snapshots remain at sonatype.

Alternatively you can  pull it from the central maven repository, just add these to your pom.xml file:
```xml
<dependency>
    <groupId>com.github.mnadeem</groupId>
    <artifactId>sql-table-name-parser</artifactId>
    <version>0.0.3</version>
</dependency>
```

## BUILDING from the sources

As it is maven project, buidling is just a matter of executing the following in your console:

	mvn package

This will produce the sql-table-name-parser-VERSION.jar file under the target directory.

## Support
If you need help using this library feel free to drop an email or create an issue in github.com (preferred)

## Contributions
To help development you are encouraged to provide 
* Suggestion/feedback/Issue
* pull requests for new features

[![View My profile on LinkedIn](https://static.licdn.com/scds/common/u/img/webpromo/btn_viewmy_160x33.png)](https://in.linkedin.com/pub/nadeem-mohammad/17/411/21)
	
	

