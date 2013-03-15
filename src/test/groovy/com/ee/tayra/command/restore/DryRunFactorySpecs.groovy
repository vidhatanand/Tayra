package com.ee.tayra.command.restore

import java.io.PrintWriter;

import com.ee.tayra.command.restore.DryRunFactory;
import com.ee.tayra.command.restore.RestoreCmdDefaults;
import com.ee.tayra.io.DeafAndDumbReporter;
import com.ee.tayra.io.SelectiveOplogReplayer;

import spock.lang.Specification

class DryRunFactorySpecs extends Specification{

	private def config
	private def factory
	private PrintWriter ignoreConsole

	def setup() {
		config = new RestoreCmdDefaults()
		factory =  new DryRunFactory(config, ignoreConsole)
	}

	def createsEmptyListener() {
		expect: 'listener created is instance of EmptyProgressReporter'
			factory.createListener().class == DeafAndDumbReporter
	}

	def createsEmptyReporter() {
		expect: 'reporter created is instance of EmptyProgressReporter'
			factory.createReporter().class == DeafAndDumbReporter
	}

	def createsWriter() {
		expect: 'writer created is instance of SelectiveOplogReplayer'
			factory.createWriter().class == SelectiveOplogReplayer
	}
}