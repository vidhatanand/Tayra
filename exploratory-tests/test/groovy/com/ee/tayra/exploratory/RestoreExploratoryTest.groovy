package com.ee.tayra.exploratory

import com.ee.tayra.command.restore.Restore
import com.mongodb.MongoClient

class RestoreExploratoryTest extends RequiresExploratoryTestSupport {

  private def context
  static StringBuilder result

  def setupSpec() {
    takeBackup()
  }

  def setup() {
    result = new StringBuilder()
    context = new Binding()
  }

  def cleanup() {
    deleteDataFrom(tgt)
  }

  def restoresOnNonAuthenticatedStandaloneWhenCredentialsAreGiven() {
    given: 'arguments for restore contains -d, --port, -f, -u, -p options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '-u', USERNAME, '-p', PASSWORD])

    when: 'restore runs with above args'
      new Restore(context).run()

    then: 'the target Node should contain all documents'
      assertTargetNodeContainsAllDocuments()
  }

  def ignoresDestinationPortArgsWithDryRunOption () {
    given: 'arguments for restore contains -d, --port, -f, --dry-run options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--dry-run'])

    when: 'restore runs with above args'
      new Restore(context).run()

    then: 'the target Node should not contain any document'
      !assertTargetNodeContainsAllDocuments()
  }

  def excludesDocumentsForGivenNamespacesWithDryRun() {
    given: 'arguments for restore contains -d, --port, -f, --sExclude, --sNs, --dry-run options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=Tayra,EELab.thing', '--dry-run'])

    when: 'restore runs with above args'
      result << restoreAndCaptureConsoleOutput(context)

    then: 'the target Node should not contain document having ns as Tayra and EElab.thing'
      !result.toString().contains('"ns" : "Tayra.')
      !result.toString().contains('"ns" : "EELab.thing"')
      result.toString().contains('"ns" : "EELab.profile"')
      result.toString().contains('"ns" : "DL.')
  }

  def excludesDocumentsForGivenNamespacesWhenRestoring() {
    given: 'arguments for restore contains -d, --port, -f, --sExclude, --sNs options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sExclude', '--sNs=DL,EELab.thing'])

    when: 'backup runs with above args'
      new Restore(context).run()

    then: 'the target Node should not contain documents having ns as DL and EELab.thing'
      !tgt.getDatabaseNames().contains("DL")
      tgt.getDB("EELab").getCollection("thing").count() == 0
  }

  def allDocumentsAreRestoredWhenNoValueIsGivenforSNs() {
    given:'arguments contains -d, --port, -f, --sNs options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sNs='])

    when: 'Restore runs'
      new Restore(context).run()

    then: 'the target node should contain all documents'
      assertTargetNodeContainsAllDocuments()
  }

  def allDocumentsAreRestoredWhenNoValueIsGivenforSUntil() {
    given: 'arguments for restore contains -d, --port, -f, --sUntil options'
      context.setVariable('args', ['-d', tgtHOST, "--port=$tgtPORT", '-f', backupFile, '--sUntil='])

    when: 'restore runs'
      new Restore(context).run()

    then: 'the target Node should contain all documents'
      assertTargetNodeContainsAllDocuments()
  }

  private restoreAndCaptureConsoleOutput(def context) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    // IMPORTANT: Save the old System.out!
    PrintStream old = System.out;
    // use your special stream
    System.setOut(ps);

    new Restore(context).run()

    // Put things back
    System.out.flush();
    System.setOut(old);
    baos.toString()
  }
}
