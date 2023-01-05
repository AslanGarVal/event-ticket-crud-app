import java.time.LocalDate
import java.util.concurrent.{ExecutorService, Executors}
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.util.{Failure, Success}

// create execution context for Futures
object PrivateExecutionContext {
  val executor: ExecutorService = Executors.newFixedThreadPool(4)
  implicit val executionContext: ExecutionContextExecutorService = ExecutionContext.fromExecutorService(executor)
}

object Main {
  import slick.jdbc.PostgresProfile.api._
  import PrivateExecutionContext._


  // define insert, update, and delete methods

  def insertEvent(event: Event): Unit = {
    // validate data and throw exception
    if (
      (event.startDate.isBefore(event.endDate) || event.startDate.isEqual(event.endDate)) &&
        (1 <= event.availableTickets && event.availableTickets <= 300)
    ) {
      val insertQueryDescription = SlickTables.eventTable += event
      val futureId: Future[Int] = DatabaseConnection.db.run(insertQueryDescription)

      futureId.onComplete {
        case Failure(exception) => println(s"Error inserting to database: $exception")
        case Success(id) => println(s"Inserted new event with id: $id")
      }
    }
    else {
      throw new RuntimeException("Invalid event attempted to be inserted")
    }
  }
  /*
    Since we need to fetch events from the database to display them, update them, or delete them
    and Slick's API returns a Future with our result (which we wrap in an Option for error handling),
    we abstract this fetching behaviour and our read, update, delete methods will be generalisations
    of this abstraction
  * */

  def fetchEventAndThen(f: Option[Event] => Unit): Long => Unit = (eventId: Long) => {
    val fetchEvent: Future[Option[Event]] = DatabaseConnection.db.run(
      SlickTables.eventTable.filter(_.id === eventId).result.headOption
    )

    fetchEvent.onComplete {
      case Failure(exception) => println(s"Error fetching record with id $eventId: $exception")
      case Success(eventOption) => f(eventOption)
    }

    Thread.sleep(3000)
  }

  val readEvent: Long => Unit = fetchEventAndThen {
    case Some(event) => println(
      s"""Event found with id: ${event.id},
         |name: ${event.name},
         |start date: ${event.startDate},
         |end date: ${event.endDate},
         |available tickets: ${event.availableTickets},
         |sold tickets: ${event.soldTickets},
         |exchanged tickets: ${event.exchangedTickets}
         |""".stripMargin)
    case None => println(s"No event found")
  }

  val updateEvent: Event => Unit = (eventUpdate: Event) => fetchEventAndThen {
    case Some(foundEvent) =>
      println(foundEvent)
      // update data only if date and ticket availability conditions are fulfilled
      val dateCondition = (eventUpdate.startDate.isAfter(foundEvent.startDate) || eventUpdate.startDate.isEqual(foundEvent.startDate)) &&
        eventUpdate.endDate.isAfter(foundEvent.startDate)

      val ticketCondition = eventUpdate.availableTickets > foundEvent.soldTickets

      if (dateCondition && ticketCondition) {
        DatabaseConnection.db.run(SlickTables.eventTable.filter(_.id === foundEvent.id).update(eventUpdate))
      } else {
        throw new RuntimeException(s"Invalid update")
      }

    case None => println(s"No event found")
  } (eventUpdate.id)


  val deleteEvent: Long => Unit = fetchEventAndThen {
      case Some(foundEvent) =>
        // update data only if date and ticket availability conditions are fulfilled
        if (LocalDate.now().isAfter(foundEvent.endDate) || foundEvent.soldTickets < 1) {
          DatabaseConnection.db.run(SlickTables.eventTable.filter(_.id === foundEvent.id).delete)
        } else {
          throw new RuntimeException(s"Event cannot be deleted")
        }

      case None => println(s"No event found")
  }


  def insertTicket(ticket: Ticket): Unit = {
    // get event from ticket and update event
    fetchEventAndThen {
      case Some(event) =>
        val eventUpdate: Event = Event(
          event.id,
          event.name,
          event.startDate,
          event.endDate,
          event.availableTickets,
          event.soldTickets + 1,
          event.exchangedTickets
        )

        updateEvent(eventUpdate)
      case None => println("No event found")
    } (ticket.eventId)


    // then insert into ticket table
    DatabaseConnection.db.run(SlickTables.ticketTable += ticket)
  }

  def updateTicket(ticketUpdate: Ticket): Unit = {
    // get event from ticket and update event
    fetchEventAndThen {
      case Some(event) =>
        val eventUpdate: Event = Event(
          event.id,
          event.name,
          event.startDate,
          event.endDate,
          event.availableTickets,
          event.soldTickets,
          event.exchangedTickets + 1
        )

        println(eventUpdate)

        updateEvent(eventUpdate)
      case None => println("No event found")
    } (ticketUpdate.eventId)

    // then update ticket
    DatabaseConnection.db.run(SlickTables.ticketTable.filter(_.id === ticketUpdate.id).update(ticketUpdate))
  }


  def main(args: Array[String]): Unit = {
    val eventInsert = Event(
      id = 1L,
      name = "Convencion de calvos",
      startDate = LocalDate.of(1999, 2, 12),
      endDate = LocalDate.of(1999, 2, 14),
      availableTickets = 100,
      soldTickets = 50,
      exchangedTickets = 25
    )

    val ticketInsert = Ticket(
      eventId = 1L,
      id = 1L,
      eventTicketNumber = 65
    )

    // create event demo
    // insertEvent(eventInsert)
    // Thread.sleep(10000)

    // read
    readEvent(1L)


    // update event demo
    updateEvent(eventInsert.copy(name = "Convencion de pelones", availableTickets = 250))

    // buy ticket demo
    insertTicket(ticketInsert)

    // exchange ticket demo
    updateTicket(ticketInsert.copy(sold = "potato"))
    Thread.sleep(10000)

  }

}

