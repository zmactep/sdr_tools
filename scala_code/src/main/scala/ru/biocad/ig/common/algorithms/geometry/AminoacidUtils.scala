package ru.biocad.ig.common.algorithms.geometry

import ru.biocad.ig.common.structures.aminoacid.SimplifiedAminoAcid
import ru.biocad.ig.common.structures.geometry.GeometryVector
import scala.math.signum
/**
this file should contain helper methods related to full-atom aminoadif model reconstruction from
SimplifiedAminoacid representation.
It is temporary and should be moved to different classes and objects
*/
object AminoacidUtils{

  /**
  this returns local coordinate system for given positions of 4 consecutive alpha-carbons
  alpha-carbons are given as 4 GeometryVectors with their coordinates (x,y,z)
  center of local coordinate system is located in p2
  */
  def getLocalCoordinateSystem(
    p1 : GeometryVector,
    p2 : GeometryVector,
    p3 : GeometryVector,
    p4 : GeometryVector
  ) : (GeometryVector, GeometryVector, GeometryVector) = {

    val v1 = p3 - p2
    val vp = p4 - p2
    val x = (v1 ** vp).normalize
    val y = (vp ** x).normalize
    val z = (x ** y).normalize
    (x, y, z)
  }

  /**
  returns global coordinates for given axes and atom's local coordinates
  */
  def getGlobalCoordinates(localAxes : Seq[GeometryVector], localCoordinates : Seq[Double]) : GeometryVector = {
    (localAxes, localCoordinates).zipped.map(_ * _).reduceLeft(_ + _)
  }

  /**
  this returns distances between positions of 4 consecutive alpha-carbons
  */
  def getDistances(
    p1 : GeometryVector,
    p2 : GeometryVector,
    p3 : GeometryVector,
    p4 : GeometryVector
  ) : (Double, Double, Double) = {
    val d1 = (p3 - p1).length
    val d2 = (p4 - p2).length
    val v1 = p2 - p1
    val v2 = p3 - p2
    val v3 = p4 - p3
    val d3 = signum((v1**v2)*v3) * (v1 + v2 + v3).length
    (d1, d2, d3)
  }

}