package unus.model

import scala.math.min


object Helpers {
  object Levenshtein {
    def minimum(i1: Int, i2: Int, i3: Int): Int =min(min(i1, i2), i3)
    def lev(s1:String, s2:String): Int ={
      val dist=Array.tabulate(s2.length+1, s1.length+1){(j,i)=>if(j==0) i else if (i==0) j else 0}

      for(j<-1 to s2.length; i<-1 to s1.length)
        dist(j)(i)=if(s2(j-1)==s1(i-1)) dist(j-1)(i-1)
        else minimum(dist(j-1)(i)+1, dist(j)(i-1)+1, dist(j-1)(i-1)+1)

      dist(s2.length)(s1.length)
    }
  }

  def miss(s: Option[String], t: Option[String]): Int = {
    s match {
      case None => 1
      case Some(ss) =>
        t match {
          case None => 1
          case Some(tt) =>
            if (ss == "" || tt == "") {
              1
            } else {
              0
            }
        }
    }
  }

  def lev(s: Option[String], t: Option[String]): Int = {
    s match {
      case None => 21
      case Some(ss) =>
        t match {
          case None => 21
          case Some(tt) =>
            math.min (20, Levenshtein.lev(ss,tt) )
        }
    }
  }


  def hamm(s: Option[String], t: Option[String]): Int = {
    s match {
      case None => 11
      case Some(ss) =>
        t match {
          case None => 11
          case Some(tt) =>
            math.min (10, (ss zip tt) count (x => x._1 != x._2) )
        }
    }
  }

  def firstInitial(s: Option[String]): Option[String] = {
    s.map(_.headOption.getOrElse("").toString)
  }

  def cleanEmail(s: Option[String]): Option[String] = {
    s match {
      case None => None
      case Some(ss) => if(ss.split("@")(0).length > 1) {
        Some(ss)
      } else {
        None
      }
    }
  }

  def cleanPhone(ss: String): Option[String] = {
    if(
      ss.replaceAllLiterally("-","").toCharArray.distinct.length > 1 &&
        ss.replaceAllLiterally("-","") != "1234567890" &&
        ss.replaceAllLiterally("-","") != "0123456789") {
      Some(ss.replaceAllLiterally("-",""))
    } else {
      None
    }
  }
}
