package chronos

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.check.ws.WsFrameCheck

import scala.concurrent.duration._

class EventTest extends Simulation {

  val DEMO_URL = "https://chronos.omegasys.eu"
  val LOCAL_URL = "http://localhost:8081"
  val SOCKET = "ws://localhost:8081/websocket/channel/1";
  /**
   * Test Events
   */
  val NUMBER_OF_EVENTS = 50;
  val EVENT_INTERVAL = 20 // milliseconds
  val PAUSE = 1000 // milliseconds
  val STATUS_OK = 200

  val mc = ws.checkTextMessage("Message Check").check(jsonPath("$.id").ofType[Int].saveAs("KEY"))
//  val msgCheck1 = ws.checkTextMessage("Message Check").check(jsonPath("$.id").ofType[Int].is(72))
//  val msgCheck2 = ws.checkTextMessage("Message Check").check(jsonPath("$.id").ofType[Int].is(73))
//  val msgCheck3 = ws.checkTextMessage("Message Check").check(jsonPath("$.id").ofType[Int].is(74))

//  val msgCheckSequences = List.fill(NUMBER_OF_EVENTS)(msgCheck)

//  var i = 77;
//  def a(): WsFrameCheck = {
//    i = i + 1
//    ws.checkTextMessage("Check").check(jsonPath("$.id").count.is(i))
//  }
//

//  var b: WsFrameCheck = () => {
//    ws.checkTextMessage("Check").check(jsonPath("$.id").count.is(initial + i))
//  }

  val httpProtocol = http.baseUrl(LOCAL_URL)
//    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
//    .doNotTrackHeader("1")
//    .acceptLanguageHeader("en-US,en;q=0.5")
//    .acceptEncodingHeader("gzip, deflate")
//    .userAgentHeader("Gatling2")

  val scnEvent = scenario("Event Tests")
//    .repeat(REPEAT) {
    //      exec(
    //        http("Hello Chronos").get("/").check(status.is(STATUS_OK))
    //      )
    //      .pause(PAUSE seconds) // Note that Gatling has recorder real time pauses
    //      .exec(
    //        http("All Channels").get("/channel/all").asJson.check(status.is(STATUS_OK))
    //      )
//    .exec(
//      ws("WebSocket")
//        .wsName("Channel")
//        .connect(SOCKET)
//    )
    .repeat(NUMBER_OF_EVENTS, "i") {
      exec(
        http("Events Generated")
          .post("/user/event")
          .header("Content-Type", "application/json")
          .body(StringBody(
            """[{
                      "applicationId": "00000000-0000-0000-0000-000000000000",
                      "transactionId": null,
                      "eventId": 1,
                      "eventName": "Login",
                      "category": "USER_EVENT",
                      "partyId": "91429948",
                      "date": 1569796439557,
                      "eventData": {
                        "eventName": "Login",
                        "partyId": "91429948",
                        "sessionKey": "ABC1234567890",
                        "ip": "127.0.0.1",
                        "accountBalancePlayableBonus": 2440.00,
                        "accountBalanceReal": 16727.30,
                        "accountBalanceReleasedBonus": 696.41
                      }
                  }]"""
          ))
          .check(status.is(STATUS_OK))
      )
      .pause(EVENT_INTERVAL milliseconds) // 50 events in 1s
    }
//      .pause(PAUSE)
      .exec(
        ws("WebSocket").connect(SOCKET).await(1 seconds) ( //50 checks
          mc, mc, mc, mc, mc, mc, mc, mc, mc, mc,
          mc, mc, mc, mc, mc, mc, mc, mc, mc, mc,
          mc, mc, mc, mc, mc, mc, mc, mc, mc, mc,
          mc, mc, mc, mc, mc, mc, mc, mc, mc, mc,
          mc, mc, mc, mc, mc, mc, mc, mc, mc, mc
        )
          .onConnected(
            exec(session => {
              val key = session("KEY").as[String]
              println(s"Response body: \n$key")
              session
            })
          )
//        ws("WebSocket").connect(SOCKET).onConnected(

//          ws.checkTextMessage("checkName").check(regex(".*I'm still alive.*"))
//          .await(1 seconds) (msgCheck)
//        )
      )
      .pause(PAUSE milliseconds)
//      .exec(session => {
//        val key = session("KEY").as[String]
//        println(s"Response body: \n$key")
//        session
//      })
//      .pause(3)
//      .exec(session => {
//        val data = session("data").asOption[String]
//        println(s"Message Received: \n$data")
//        session
//      })
      .exec(ws("Close WebSocket").close)
//  }

  setUp(
    scnEvent.inject(atOnceUsers(1)).protocols(httpProtocol)
  )
}
