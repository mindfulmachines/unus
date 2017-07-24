package unus.db

case class Patient(
                    enterpriseId: String,
                    last: Option[String],
                    first: Option[String],
                    middle: Option[String],
                    suffix: Option[String],
                    dob: Option[String],
                    gender: Option[String],
                    ssn: Option[String],
                    address1: Option[String],
                    address2: Option[String],
                    zip: Option[String],
                    mothersMaidenName: Option[String],
                    mrn: Option[String],
                    city: Option[String],
                    state: Option[String],
                    phone: Option[String],
                    phone2: Option[String],
                    email: Option[String],
                    alias: Option[String]
                  )

