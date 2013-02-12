/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.command

import com.ee.tayra.domain.operation.Operations
import com.ee.tayra.io.OplogReplayer
import com.ee.tayra.io.RestoreProgressReporter
import com.ee.tayra.io.SelectiveOplogReplayer
import com.mongodb.Mongo
import com.mongodb.ServerAddress
import java.io.PrintWriter;

class DefaultRestoreFactory extends RestoreFactory{

	private final PrintWriter console;
	private final def criteria;
	private final Mongo mongo;
	private final def listeningReporter
	private final def authenticator

	public DefaultRestoreFactory(Config config) {
		this.criteria = config.criteria
		this.console = config.console
		this.authenticator = config.authenticator

		ServerAddress server = new ServerAddress(config.destMongoDB, config.port)
		this.mongo = new Mongo(server)
		getAuthenticator(mongo).authenticate(config.username, config.password)

		listeningReporter = new RestoreProgressReporter(new FileWriter
				(config.exceptionFile), console)
	}

	@Override
	public def createWriter() {
		new SelectiveOplogReplayer(criteria, new OplogReplayer(new Operations(mongo)))
	}

	@Override
	public def createListener() {
		listeningReporter
	}

	@Override
	public def createReporter() {
		listeningReporter
	}

	def getAuthenticator(mongo) {
		authenticator == null ? new MongoAuthenticator(mongo) : authenticator
	}
}