package ru.biocad.ig.common.structures.geometry

import ru.biocad.ig.common.structures.aminoacid.SimplifiedAminoacid
import ru.biocad.ig.common.algorithms.geometry.AminoacidUtils
import ru.biocad.ig.alascan.constants.{AminoacidLibrary, SidechainInfo}
import util.Random.nextInt

trait LatticeBasicMove {
  def isValid() : Boolean = ???
  def makeMove(structure : Seq[SimplifiedAminoacid], position : Int) : Seq[SimplifiedAminoacid] = ???
}
/***/
class RotamerMove(val rotamerLibrary: AminoacidLibrary[SidechainInfo]) extends LatticeBasicMove {
  def moveRotamer(structure : Seq[SimplifiedAminoacid], i : Int ) : SimplifiedAminoacid = {
    val v1 = i match {
      case 0 => structure(i + 1).ca - structure(i).ca
      case _ => structure(i).ca - structure(i - 1).ca
    }
    val v2 = i match {
      case i if i == structure.size - 1 => structure(i - 1).ca - structure(i - 2).ca
      case _ => structure(i + 1).ca - structure(i).ca
    }
    val v3 = i match {
      case i if i == structure.size - 1 => structure(i).ca - structure(i - 1).ca
      case i if i == structure.size - 2 => structure(i).ca - structure(i - 1).ca
      case _ => structure(i + 2).ca - structure(i + 1).ca
    }
    val aa = structure(i)
    val (d1, d2, d3) = AminoacidUtils.getDistances(v1, v2, v3)
    val (x, y, z) = AminoacidUtils.getLocalCoordinateSystem(v1, v2, v3)
    val sidechainInfo = rotamerLibrary.restoreAminoacidInfo(aa.name, d1, d2, d3)
    sidechainInfo.changeRotamerToRandom(aa)
  }

  override def makeMove(structure : Seq[SimplifiedAminoacid], position : Int) : Seq[SimplifiedAminoacid] = {
    println("in RotamerMove")
    structure.zipWithIndex.map({
      case (el, i) => {
        if (i == position)
        moveRotamer(structure, position)
        else el
      }
    })
  }
}

/**there should be several kinds of bond makeMoves,
this class takes number of bonds to makeMove, starting from zero*/
class BondMove(val basicVectors :  Array[GeometryVector], val numberOfBonds : Int) extends LatticeBasicMove {
  def prepareMove(moveVector : GeometryVector,
      structure : Seq[SimplifiedAminoacid],
      position : Int) : Seq[SimplifiedAminoacid] = {
    println("in BondMove")
    structure.zipWithIndex.map({case ( el, i) => {
      if (i >= position && i < position + numberOfBonds - 1)
      el.move(moveVector)
      else el
    }})

  }

  def getRandomVector() : GeometryVector = {
    basicVectors(nextInt(basicVectors.size))
  }

  override def makeMove(structure : Seq[SimplifiedAminoacid],
    position : Int) : Seq[SimplifiedAminoacid] = prepareMove(getRandomVector(), structure, position)
}

//direction == 1.0 means towards N-terminus, direction = -1 means tovards C
class DisplacementMove(val basicVectors :  Array[GeometryVector], val direction : Int) extends LatticeBasicMove {
  override def makeMove(structure : Seq[SimplifiedAminoacid], position : Int) : Seq[SimplifiedAminoacid] = {
    structure.zipWithIndex.map({case (el, i) => {
      if (i < position)
      el.move(Vector3d(1, 1, 1))
      else el
    }})
  }
}
