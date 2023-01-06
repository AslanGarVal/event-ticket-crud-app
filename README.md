# event-ticket-crud-app
A CRUD app modelling an event-ticket handling system which persists data into a Postgres DB

## Process

The code is composed of three main modules: 

1. _DataModel_: The representation of the problem in data structures and their respective mappings to the tables in which the data will be persisted. 
2. _DatabaseConnection_: The connection to the external database from the application.conf file provided 
3. _Main_: The API for interacting with the database is defined, the main method demos the implemented API

### DataModel

The two main business entities (events and tickets) are implemented as Scala case classes. 

#### Event
The Event case class includes the attributes specified 
by the requirements plus an ID field (since the event name can be changed). 

#### Ticket 
The Ticket case class consists of an event id (to match the ticket to its corresponding event), a unique id for the ticket itself, 
a second ticket id relating to the event itself, and a sold status (currently modelled as a string).

#### SlickTables
The SlickTables object contains two classes that extend Slick's Table class with Event and Ticket as type parameter.
In both classes, methods are defined to map each field of the respective case class into a column of the corresponding table. 
The '*' method is overridden to define the mapping of a whole case class to a row of its corresponding table. 

### DatabaseConnection
The DatabaseConnection object simply contains the Database instance that is generated from reading the application.conf file provided which includes all the
connection info required to communicate with the external Postgres database.

### Main
The Main module contains the main methods for writing and reading case classes and table rows. 
Events are allowed to be created into the database table, read from the database table, deleted or updated into the database table; data to be 
created/updated/deleted is validated before doing so and an exception is thrown when not validated. 

Ticket buying and redemption is achieved through the insert and update methods for the Ticket case class.

The main method of this module shows a quick demonstration of the above implemented methods.

## Testing

The database against which the system is run is deployed by a Postgres image of a Docker container. 
To run the database, place yourself on this repo's main directory and use the following commands: 

```docker-compose up```

to initialise the database, the schema, and the empty tables to be used; and to fire up the Docker instance. The definition of the schema and tables is found 
in the directory ```db/init-scripts.sql```.

```docker exec -it pruebatecnica_db_1 psql -U postgres```

to access the Docker instance and run a querying SQL engine against the database. 


The Scala project can be compiled via sbt and the resulting jar file can be run with the Main module as main class. 
Alternatively, if using IntelliJ, the Main module can be executed from the IDE without compiling into a jar file. 
