
import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object RandomGenerator {
  def randomString(length: Int): String = {
    val SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
    val salt = new StringBuilder
    val rnd = new scala.util.Random
    while (salt.length < length) { // length of the random string.
      val index = (rnd.nextFloat() * SALTCHARS.length).asInstanceOf[Int]
      salt.append(SALTCHARS.charAt(index))
    }
    val saltStr = salt.toString
    saltStr
  }
  def randomactivitieRequest(): String =
    """{"id" : 1, "title":"""".stripMargin + RandomGenerator.randomString(30) +"""",
                                                                                   |"dueDate": "2021-12-09T21:32:42.474Z" , "completed": true}""".stripMargin
}

class Activitiesimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("https://fakerestapi.azurewebsites.net/")


  val post = scenario("Post activitie")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomactivitieRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post activitie")
        .post("/api/v1/Activities/")
        .body(StringBody("${postrequest}")).asJson
    )

  val get = scenario("Get activitie")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomactivitieRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post activitie")
        .post("/api/v1/Activities/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("activitieId"))
    )
    .exitHereIfFailed
    .exec(
      http("Get activitie")
        .get("/api/v1/Activities/${activitieId}")
    )

  val put = scenario("Put activitie")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomactivitieRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post activitie")
        .post("/api/v1/Activities/")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("activitieId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomactivitieRequest())
      sessionPutUpdate
    })
    .exec(
      http("Put activitie")
        .put("/api/v1/Activities/${activitieId}")
        .body(StringBody("${putrequest}")).asJson
    )

  val delete = scenario("Delete activitie")
    .exec(sessionPost => {
      val sessionPostUpdate = sessionPost.set("postrequest", RandomGenerator.randomactivitieRequest())
      sessionPostUpdate
    })
    .exec(
      http("Post activitie")
        .post("/api/v1/Activities")
        .body(StringBody("${postrequest}")).asJson
        .check(jsonPath("$.id").saveAs("activitieId"))
    )
    .exitHereIfFailed
    .exec(sessionPut => {
      val sessionPutUpdate = sessionPut.set("putrequest", RandomGenerator.randomactivitieRequest())
      sessionPutUpdate
    })
    .exec(
      http("Delete activitie")
        .delete("/api/v1/Activities/${activitieId}")
    )

  setUp(post.inject(rampUsers(35).during(20.seconds)).protocols(httpProtocol),
    get.inject(rampUsers(35).during(20.seconds)).protocols(httpProtocol),
    put.inject(rampUsers(35).during(20.seconds)).protocols(httpProtocol),
    delete.inject(rampUsers(35).during(20.seconds)).protocols(httpProtocol))

}