package test.alphabet

//import DefaultJsonProtocol._
import scopt.OptionParser
import java.io.{File}
import scala.io.Source
import com.typesafe.scalalogging.slf4j.LazyLogging


import ru.biocad.ig.common.algorithms.MonteCarloRunner
import ru.biocad.ig.common.console.ScriptRunner

/** Selects method of data processing from command line parameters
  * should have several ways to invoke:
  * 1. input file in pdb format with structure to refine/fold
  * 2. input file in pdb to perform alanine scanning
  * 3. input file in fasta or as a sequence of aminoacids - to compare with known folding
  */
object MCTest extends LazyLogging {

  private case class Config(inputFile : File = new File("2OSL.pdb"),
      outputFile : File = new File("result.pdb"),
      mcTimeUnits : Int = 200,
      mode : String = "fold",
      sequence : String = "GARFIELD",//"RMAQLEAKVEELLSKNWNLENEVARLKKLVGER",//
      //TODO: add option - refine/fold/alascan - default refine
      postprocess : Boolean = true,
      debug : Boolean = false)

  private def getParser = new OptionParser[Config]("sdr_tools") {
      opt[Int]('n', "mcTimeUnits") action {(s, c) =>
        c.copy(mcTimeUnits = s)} text "number of iterations (time units -  each time unit contains several move attempts) to call MC"
      opt[File]('o', "outputFile") valueName("<file>") action {(s, c) =>
        c.copy(outputFile = s)} text "output file in PDB format"
      cmd("refine") action {(_, c) =>
        c.copy(mode = "refine") } text("starts structure refinement procedure for given input PDB file.") children(
          opt[File]('i', "inputFile") valueName("<file>") action {(s, c) =>
            c.copy(inputFile = s)} text "input file in PDB format"
        )
      cmd("fold") action {(_, c) =>
        c.copy(mode = "fold")} text("folds protein with given aminoacid sequence.") children(
          opt[String]('s', "sequence") action {(s, c) =>
            c.copy(sequence = s)} text("aminoacid sequence to process")
        )
      cmd("scan") action {(_, c) =>
        c.copy(mode = "scan") } text("performs alanine scanning for given input pdb file") children(
          opt[File]('i', "inputFile") valueName("<file>") action {(s, c) =>
            c.copy(inputFile = s)} text "input file in PDB format"
        )

      opt[Unit]("postprocess") action {(_, c) => c.copy(postprocess = true)} text "call postprocessing script"
      opt[Unit]("debug") action {(_, c) => c.copy(debug = true)} text "enable debug output"
      help("help") text "this message"
  }

  def main(args : Array[String]) : Unit = {
    val parser = getParser
    parser.parse(args, Config()) match {
      case Some(config) =>
        try {
          config.mode match {
            case "fold" => {
              MonteCarloRunner.fold(config.sequence, config.mcTimeUnits, config.outputFile)
            }
            case "refine" => {
              MonteCarloRunner.refine(config.inputFile, config.mcTimeUnits, config.outputFile)
            }
            case "scan" => {
              MonteCarloRunner.scan(config.inputFile, config.mcTimeUnits, config.outputFile)
            }
          }
          if (config.postprocess) {
            //next goes postprocessing
            val postpocessingCommandsSource = Source.fromURL(getClass.getResource("/postprocessing.txt"))
            (new ScriptRunner(postpocessingCommandsSource)).run(
              Map("processed_pdb" -> config.outputFile.toString,
                  "prepared_mae" -> "prepared.mae"))
            postpocessingCommandsSource.close()
          }

        } catch {
          case e : Exception =>
            //logger.error(s"Fatal error: ${e.getMessage}")
            e.printStackTrace()
            if (config.debug) {
              //e.printStackTrace()
            }
        }
      case None => parser.showUsage
    }
  }
}
