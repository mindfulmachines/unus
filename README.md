# Ūnus

Ūnus is a machine learning based patient matching engine built for the [ONC Patient Matching Challenge][1]. 

Detailed write up are available here:

http://mindfulmachines.io/blog/2017/7/23/onc-patient-matching-challenge-part1

http://mindfulmachines.io/blog/2017/7/23/onc-patient-matching-challenge-part-2


## Technology
The technologies used to build the matching engine are:
* Scala
* Play
* Spark
* PostgreSQL
* Docker

## Installing
Clone this repository:

`git clone https://github.com/mindfulmachines/unus.git`

Install [Java 8][2]

Install [SBT][3]

Install [Docker][4]

## Running
Place the `FInalDataset.csv` file in the `data/` folder.

Configure a docker volume for PostgreSQL:

`docker volume create postgresql`

Start the postgres server:

`docker-compose up`

Optionally configure the database url by editing `conf/application.conf`. Specifically if you're using Docker Machine 
(Windows 7) the database url is probably `jdbc:postgresql://192.168.99.100/postgres`.

Start the play application:

`sbt "runMain PlayApp"`

Go to `localhost:9000` to view the UI. The first time it may take a while for the UI to be available as data is loaded into the DB. You can also check `localhost:4040` for how Spark is progressing.

[1]: https://www.patientmatchingchallenge.com
[2]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[3]: http://www.scala-sbt.org/download.html
[4]: https://docs.docker.com/engine/installation/#supported-platforms
