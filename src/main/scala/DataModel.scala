import java.time.LocalDate


// define data structures for our business model
case class Event (name: String, startDate: LocalDate, endDate: LocalDate, availableTickets: Int)
case class Ticket(eventName: String, id: Int, eventTicketNumber: Int)


object SlickTables {
  import slick.jdbc.PostgresProfile.api._

  // Define Tables using Slick's API
  class EventTable(tag: Tag) extends Table[Event](tag, Some("events"), "Events") {
    def name = column[String]("event_name", O.PrimaryKey)
    def startDate = column[LocalDate]("start_date")
    def endDate = column[LocalDate]("end_date")
    def availableTickets = column[Int]("available_tickets")

    override def * =  (name, startDate, endDate, availableTickets) <> (Event.tupled, Event.unapply)
  }

  class TicketTable(tag: Tag) extends Table[Ticket](tag, Some("tickets"), "Tickets") {
    def eventName = column[String]("event_name")
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def eventTicketNumber = column[Int]("event_ticket_number")

    override def * = (eventName, id, eventTicketNumber) <> (Ticket.tupled, Ticket.unapply)
  }

  // API "entry points"
  lazy val eventTable = TableQuery[EventTable]
  lazy val ticketTable = TableQuery[TicketTable]
}

