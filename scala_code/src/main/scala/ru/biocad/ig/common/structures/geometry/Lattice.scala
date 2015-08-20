package ru.biocad.ig.common.structures.geometry

import ru.biocad.ig.common.structures.aminoacid._
import ru.biocad.ig.alascan.constants._

object Lattice {
  /** helper methods*/
  //this returns true if they can, false otherwise - quite simple
  def can_form_H_bond(aminoacids: Seq[SimplifiedAminoAcid], i : Int, j : Int) : Boolean = {
    val r_ij = aminoacids(j).ca - aminoacids(i).ca
    val b_i_b_i_1 = aminoacids(i - 1).ca - aminoacids(i).ca //TODO: check if i == 0
    val b_j_b_j_1 = aminoacids(j - 1).ca - aminoacids(j).ca
    i - j >= 3 && LatticeConstants.H_bond_distance_condition(r_ij.length) &&
      (b_i_b_i_1*r_ij).abs <= LatticeConstants.H_bond_a_max &&
      (b_j_b_j_1*r_ij).abs <= LatticeConstants.H_bond_a_max
    //LatticeConstants
  }
  /** energy methods*/
  def get_E_CA_trace(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    ???
  }

  def get_E_H_bond(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    var E = 0.0
    (1 to aminoacids.size).foreach( i => {
      (i to aminoacids.size).foreach(j => {
        if (can_form_H_bond(aminoacids, i, j)) {
          E += LatticeConstants.E_H
          //FIX: consider cooperativity
          //FIX: consider condition - each aminoacid can form no more than 2 H-bonds (proline - no more than 1 bond)
          //FIX: maximize
        }
      })
    }
    )
    E
  }

  def get_E_rot(aminoacids : Seq[SimplifiedAminoAcid]): Double = {
    ???
  }

  def get_E_SG_local(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    ???
  }

  def get_E_one(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    ???
  }

  def get_E_pair(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    ???
  }

  def get_E_tem(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    ???
  }

  //TODO: we have pair of chains, that means we somehow should utilize that when we compute total energy
  def get_E(aminoacids : Seq[SimplifiedAminoAcid]) : Double = {
    get_E_CA_trace(aminoacids) +
    get_E_H_bond(aminoacids) +
    get_E_rot(aminoacids) +
    get_E_SG_local(aminoacids) +
    get_E_one(aminoacids) +
    get_E_pair(aminoacids) +
    get_E_tem(aminoacids)
  }

}