import java.time.LocalDate


// define data structures for our business model
case class Event (
                   id: Long,
                   name: String,
                   startDate: LocalDate,
                   endDate: LocalDate,
                   availableTickets: Int,
                   soldTickets: Int,
                   exchangedTickets: Int
                 )
// Ticket is initialised as "not sold"
case class Ticket(eventId: Long, id: Long, eventTicketNumber: Int, sold: String = "Not sold")


object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  // Define Tables using Slick's API
  class EventTable(tag: Tag) extends Table[Event](tag, Some("event_system"), "Events") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc, O.Unique)
    def name = column[String]("event_name")
    def startDate = column[LocalDate]("start_date")
    def endDate = column[LocalDate]("end_date")
    def availableTickets = column[Int]("available_tickets")
    def soldTickets = column[Int]("sold_tickets")
    def exchangedTickets = column[Int]("exchanged_tickets")

    override def * =  (id, name, startDate, endDate, availableTickets, soldTickets, exchangedTickets) <> (Event.tupled, Event.unapply)
  }

  class TicketTable(tag: Tag) extends Table[Ticket](tag, Some("event_system"), "Tickets") {
    def eventId = column[Long]("event_id")
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def eventTicketNumber = column[Int]("event_ticket_number")
    def sold = column[String]("sold")

    override def * = (eventId, id, eventTicketNumber, sold) <> (Ticket.tupled, Ticket.unapply)
  }

  // API "entry points"
  lazy val eventTable = TableQuery[EventTable]
  lazy val ticketTable = TableQuery[TicketTable]
}

