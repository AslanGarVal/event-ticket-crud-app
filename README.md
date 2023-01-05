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
