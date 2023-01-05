import slick.jdbc.PostgresProfile.api._

object DatabaseConnection {
  // load db connection info from application.conf file
  val db = Database.forConfig("postgres")
}
