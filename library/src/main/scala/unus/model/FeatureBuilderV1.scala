package unus.model

import unus.stage.PairedPatients
import org.apache.spark.rdd.RDD

class FeatureBuilderV1 extends FeatureBuilder {
  import Helpers._

  val name: String = "FeatureBuilderV1"

  def generateFeatures(data: RDD[PairedPatients]): RDD[Features] = {
    data.map(r => {
      val p1phones = r.patient1.phone.map(_.split("\\^\\^").flatMap(cleanPhone).toSet).getOrElse(Set())
      val p2phones = r.patient2.phone.map(_.split("\\^\\^").flatMap(cleanPhone).toSet).getOrElse(Set())

      val p1names = (r.patient1.first.toList ::: r.patient1.last.toList ::: r.patient1.alias.toList.filter(rr => ! Set("JOHN DOE","JANE DOE" ,"PATIENTNOTFOUND").contains(rr)).flatMap(_.split(' ').toList))
        .distinct

      val p2names = (r.patient2.first.toList ::: r.patient2.last.toList ::: r.patient2.alias.toList.filter(rr => ! Set("JOHN DOE","JANE DOE" ,"PATIENTNOTFOUND").contains(rr)).flatMap(_.split(' ').toList))
        .distinct

      Features(r.labeled,
        r.labeled.valid,
        Array(
          lev(r.patient1.last, r.patient2.last),
          //miss(r.patient1.last, r.patient2.last),

          lev(r.patient1.first, r.patient2.first),
          //miss(r.patient1.first, r.patient2.first),

          //lev(r.patient1.last.flatMap(RefinedSoundexAlgorithm.compute), r.patient2.last.flatMap(RefinedSoundexAlgorithm.compute)),
          //lev(r.patient1.last.flatMap(RefinedSoundexAlgorithm.compute), r.patient2.last.flatMap(RefinedSoundexAlgorithm.compute)),
          //miss(r.patient1.last, r.patient2.last),

          //lev(r.patient1.first.flatMap(RefinedSoundexAlgorithm.compute), r.patient2.first.flatMap(RefinedSoundexAlgorithm.compute)),

          hamm(firstInitial(r.patient1.middle), firstInitial(r.patient2.middle)),
          //miss(r.patient1.middle, r.patient2.middle),

          lev(r.patient1.suffix, r.patient2.suffix),
          //miss(r.patient1.suffix, r.patient2.suffix),

          //lev(r.patient1.mothersMaidenName, r.patient2.mothersMaidenName),

          lev(r.patient1.dob, r.patient2.dob),
          //miss(r.patient1.dob, r.patient2.dob),

          hamm(firstInitial(r.patient1.gender).filter(_ != "U"), firstInitial(r.patient2.gender).filter(_ != "U")),
          //miss(r.patient1.gender, r.patient2.gender),

          if(firstInitial(r.patient1.gender).getOrElse("") == "F" || firstInitial(r.patient2.gender).getOrElse("") == "F") 1.0 else 0.0,

          hamm(r.patient1.ssn, r.patient2.ssn),
          //miss(r.patient1.ssn, r.patient2.ssn),

          lev(r.patient1.address1, r.patient2.address1),
          //miss(r.patient1.address1, r.patient2.address1),

          lev(r.patient1.address2, r.patient2.address2),
          //miss(r.patient1.address2, r.patient2.address2),

          hamm(r.patient1.zip, r.patient2.zip),
          //miss(r.patient1.zip, r.patient2.zip),

          lev(r.patient1.city, r.patient2.city),
          //miss(r.patient1.city, r.patient2.city),

          hamm(r.patient1.state, r.patient2.state),
          //miss(r.patient1.state, r.patient2.state),

          hamm(r.patient1.zip, r.patient2.zip),
          //miss(r.patient1.zip, r.patient2.zip),

          lev(cleanEmail(r.patient1.email), cleanEmail(r.patient2.email)),
          //miss(cleanEmail(r.patient1.email), cleanEmail(r.patient2.email)),

          lev(cleanPhone(r.patient1.phone.getOrElse("")), cleanPhone(r.patient2.phone.getOrElse(""))),
          //miss(cleanEmail(r.patient1.phone), cleanEmail(r.patient2.phone)),

          p1phones.intersect(p2phones).size/p1phones.union(p2phones).size.toDouble,
          //if (p1phones.isEmpty || p2phones.isEmpty) 1 else 0,

          math.abs(r.patient1.mrn.map(_.toInt).getOrElse(0) - r.patient2.mrn.map(_.toInt).getOrElse(0))/(r.patient1.mrn.map(_.toInt).getOrElse(1) + r.patient2.mrn.map(_.toInt).getOrElse(1)).toDouble,
          math.abs(r.patient1.mrn.map(_.toInt).getOrElse(0) - r.patient2.mrn.map(_.toInt).getOrElse(0))

          //p1names.intersect(p2names).size/p1names.union(p2names).size.toDouble
          //miss(r.patient1.mrn, r.patient2.mrn)
        )
      )
    }
    )
  }
}
