import java.time.LocalDateTime

object DomainModelling {
  // define data structures for our business model
  case class Event private (
                             name: String,
                             startDate: LocalDateTime,
                             endDate: LocalDateTime,
                             availableTickets: List[Ticket]
                           )
  case class Ticket(eventName: String, id: Int, isExchanged: Boolean = false)

  object Event {
    def apply(
               name: String,
               startDate: LocalDateTime,
               endDate: LocalDateTime,
               availableTickets: List[Ticket]
             ): Option[Event] = {
      // validation logic for events is handled here, invalid parameters result in a None being returned
      if (
        (1 < availableTickets.size && availableTickets.size < 300) &&
          (startDate.isEqual(endDate) || startDate.isBefore(endDate))) {
        Some(new Event(name, startDate, endDate, availableTickets))
      } else {
        None
      }
    }
  }

}
