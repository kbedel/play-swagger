package com.iheart.sbtPlaySwagger

case class SwaggerMapping(fromType: String, toType: String, toFormat: Option[String] = None) {
  def toJson: String = {
    val format = toFormat match {
      case Some(x) ⇒ s""", "format": "$x""""
      case _       ⇒ ""
    }

    s"""{ "fromType": "$fromType", "toType": "$toType" $format }"""
  }
}
