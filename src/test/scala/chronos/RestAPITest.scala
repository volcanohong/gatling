package chronos

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import scala.concurrent.duration._

class RestAPITest extends Simulation {

  val DEMO_URL = "https://chronos.omegasys.eu"
  val LOCAL_URL = "http://localhost:8081"


  /**
   * Test Demo APIs
   */
  val REPEAT = 2
  val STATUS_OK = 200
  val PAUSE = 1 // secnods
  val USERS = 10;

  val httpProtocol = http.baseUrl(DEMO_URL)

  val scn = scenario("API Tests").repeat(REPEAT) {
        exec(
          http("Hello Chronos").get("/").check(status.is(STATUS_OK))
        )
        .pause(PAUSE seconds) // Note that Gatling has recorder real time pauses
        .exec(
          http("All Channels").get("/channel/all").asJson.check(status.is(STATUS_OK))
        )
        .pause(PAUSE seconds)
        //get raw message   /raw-message
        .exec(
          http("All Packets").get("/packet/all").asJson.check(status.is(STATUS_OK))
        )
        .exec(
          http("All DataSource").get("/data-source/all").asJson.check(status.is(STATUS_OK))
        )
        .pause(PAUSE seconds)
        .exec(
          http("My Channels").get("/channel-subscribe/7346").asJson.check(status.is(STATUS_OK))
        )
        //get /channel-store/20
        .exec(
          http("CORE4 Event Channel").get("/channel-store/20").asJson.check(status.is(STATUS_OK))
        )
        .exec(
          http("Login Event Packet").get("/packet/28").asJson.check(status.is(STATUS_OK))
        )
        // /raw-message?channels=20
        .exec(
          http("Get History Message").get("/raw-message")
            .queryParam("channels", "20")
            .asJson.check(status.is(STATUS_OK))
        )
  }

  //  val scn = scenario("Try")
  //    .repeat(1) {
  //      exec(http("GET /").get("/"))
  //    }

  //    .exec(http("request_10") // Here's an example of a POST request
  //      .post("/computers")
  //      .formParam("""name""", """Beautiful Computer""") // Note the triple double quotes: used in Scala for protecting a whole chain of characters (no need for backslash)
  //      .formParam("""introduced""", """2012-05-30""")
  //      .formParam("""discontinued""", """""")
  //      .formParam("""company""", """37"""))

  setUp(
    scn.inject(atOnceUsers(USERS)).protocols(httpProtocol)
  )
}
