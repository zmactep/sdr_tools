package ru.biocad.ig.common.io.pdb

import scala.collection.mutable.ArrayBuffer

//helper class, contains all information about atomic structure for given PDB
class PDBStructure {
  val parse_array : ArrayBuffer[PDBAtomInfo] = ArrayBuffer.empty[PDBAtomInfo]

  def readFile(filename : String) : Unit = {
    var pdbReader: PDBReader = new PDBReader(filename)

    while (pdbReader.hasNext) {
      parse_array += pdbReader.next().get
    }
  }

  override def toString : String = parse_array.mkString(", ")
  //TODO: should add method to add hydrogen atoms
  //TODO: should add method to remove water
}

//TODO: should extend all logic of pdb file reading, the following class is temporary solution for collecting atoms to aminoacids
class PDBAminoAcidCollection(val basic_structure : PDBStructure) {
  val aminoacids = basic_structure.parse_array.groupBy(_.chainID).map(x=>(x._1, x._2.groupBy(_.resSeq))).toMap
  val chains = aminoacids.keys
}
