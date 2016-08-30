import com.iheart.sbtPlaySwagger.SwaggerMapping

logLevel in update := sbt.Level.Warn

enablePlugins(PlayScala, SwaggerPlugin)

name := "app"

scalaVersion := "2.11.7"

swaggerDomainNameSpaces := Seq("namespace1", "namespace2")

swaggerRoutesFile := "my-routes"

swaggerMappings := Seq(SwaggerMapping("java.time.LocalDate", "string", Some("date")),
                       SwaggerMapping("java.time.Duration", "integer"))

TaskKey[Unit]("check") := {
  val expected =
    s"""
      |{
      |   "paths":{
      |      "/tracks/{trackId}":{
      |         "get":{
      |            "tags":[
      |               "${swaggerRoutesFile.value}"
      |            ],
      |            "summary":"Get the track metadata",
      |            "responses":{
      |               "200":{
      |                  "schema":{
      |                     "$$ref":"#/definitions/namespace2.Track"
      |                  }
      |               }
      |            },
      |            "parameters":[
      |               {
      |                  "name":"path",
      |                  "type":"string",
      |                  "required":true,
      |                  "in":"query"
      |               },
      |               {
      |                  "name":"trackId",
      |                  "type":"asset",
      |                  "required":true,
      |                  "in":"path"
      |               }
      |            ]
      |         }
      |      }
      |   },
      |   "definitions":{
      |      "namespace1.Artist":{
      |         "properties":{
      |            "name":{
      |               "type":"string"
      |            },
      |            "age":{
      |               "type":"integer",
      |               "format":"int32"
      |            },
      |            "birthdate":{
      |               "type":"string",
      |               "format":"date"
      |            }
      |         },
      |         "required":[
      |            "name",
      |            "age",
      |            "birthdate"
      |         ]
      |      },
      |      "namespace2.Track":{
      |         "properties":{
      |            "name":{
      |               "type":"string"
      |            },
      |            "genre":{
      |               "type":"string"
      |            },
      |            "artist":{
      |               "$$ref":"#/definitions/namespace1.Artist"
      |            },
      |            "related":{
      |               "type":"array",
      |               "items":{
      |                  "$$ref":"#/definitions/namespace1.Artist"
      |               }
      |            },
      |            "numbers":{
      |               "type":"array",
      |               "items":{
      |                  "type":"integer",
      |                  "format":"int32"
      |               }
      |            },
      |            "length":{
      |               "type":"integer"
      |            }
      |         },
      |         "required":[
      |            "name",
      |            "artist",
      |            "related",
      |            "numbers",
      |            "length"
      |         ]
      |      }
      |   },
      |   "swagger":"2.0",
      |   "info":{
      |      "title":"Poweramp API",
      |      "description":"My API is the best"
      |   },
      |   "tags":[
      |      {
      |         "name":"${swaggerRoutesFile.value}"
      |      }
      |   ]
      |}
    """.stripMargin.split('\n').map(_.trim.filter(_ >= ' ')).mkString

  val result = IO.read(swaggerTarget.value / swaggerFileName.value)

  if (result != expected) {
    sys.error(
      s"""Swagger.json is off.
         |Result: $result
         |Expected: $expected
         |
       """.stripMargin)
  }
}
