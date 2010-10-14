/*
 * FreenetSone - SoneInserter.java - Copyright © 2010 David Roden
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.pterodactylus.sone.core;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.freenet.StringBucket;
import net.pterodactylus.util.logging.Logging;
import net.pterodactylus.util.service.AbstractService;
import net.pterodactylus.util.template.DefaultTemplateFactory;
import net.pterodactylus.util.template.ReflectionAccessor;
import net.pterodactylus.util.template.Template;
import freenet.client.async.ManifestElement;
import freenet.keys.FreenetURI;

/**
 * A Sone inserter is responsible for inserting a Sone if it has changed.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class SoneInserter extends AbstractService {

	/** The logger. */
	private static final Logger logger = Logging.getLogger(SoneInserter.class);

	/** The template factory used to create the templates. */
	private static final DefaultTemplateFactory templateFactory = new DefaultTemplateFactory();

	static {
		templateFactory.addAccessor(Object.class, new ReflectionAccessor());
	}

	/** The Freenet interface. */
	private final FreenetInterface freenetInterface;

	/** The Sone to insert. */
	private final Sone sone;

	/**
	 * Creates a new Sone inserter.
	 *
	 * @param freenetInterface
	 *            The freenet interface
	 * @param sone
	 *            The Sone to insert
	 */
	public SoneInserter(FreenetInterface freenetInterface, Sone sone) {
		super("Sone Inserter for “" + sone.getName() + "”");
		this.freenetInterface = freenetInterface;
		this.sone = sone;
	}

	//
	// SERVICE METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void serviceRun() {
		long modificationCounter = 0;
		while (!shouldStop()) {
			InsertInformation insertInformation = null;
			synchronized (sone) {
				modificationCounter = sone.getModificationCounter();
				if (modificationCounter > 0) {
					insertInformation = new InsertInformation(sone.getRequestUri(), sone.getInsertUri());
				}
			}
			if (insertInformation != null) {
				logger.log(Level.INFO, "Inserting Sone “%s”…", new Object[] { sone.getName() });

				boolean success = false;
				try {
					FreenetURI finalUri = freenetInterface.insertDirectory(insertInformation.getInsertUri().setKeyType("USK").setDocName("Sone-" + sone.getName()).setSuggestedEdition(0), insertInformation.generateManifestEntries(), "index.html");
					success = true;
					logger.log(Level.INFO, "Inserted Sone “%s” at %s.", new Object[] { sone.getName(), finalUri });
				} catch (SoneException se1) {
					logger.log(Level.WARNING, "Could not insert Sone “" + sone.getName() + "”!", se1);
				}

				/*
				 * reset modification counter if Sone has not been modified
				 * while it was inserted.
				 */
				if (success) {
					synchronized (sone) {
						if (sone.getModificationCounter() == modificationCounter) {
							sone.setModificationCounter(0);
						}
					}
				}
			}
			logger.log(Level.FINEST, "Waiting 60 seconds before checking Sone “" + sone.getName() + "” again.");
			sleep(60 * 1000);
		}
	}

	/**
	 * Container for information that are required to insert a Sone. This
	 * container merely exists to copy all relevant data without holding a lock
	 * on the {@link Sone} object for too long.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	private class InsertInformation {

		/** The request URI of the Sone. */
		private final FreenetURI requestUri;

		/** The insert URI of the Sone. */
		private final FreenetURI insertUri;

		/**
		 * Creates a new insert information container.
		 *
		 * @param requestUri
		 *            The request URI of the Sone
		 * @param insertUri
		 *            The insert URI of the Sone
		 */
		public InsertInformation(FreenetURI requestUri, FreenetURI insertUri) {
			this.requestUri = requestUri;
			this.insertUri = insertUri;
		}

		//
		// ACCESSORS
		//

		/**
		 * Returns the request URI of the Sone.
		 *
		 * @return The request URI of the Sone
		 */
		@SuppressWarnings("unused")
		public FreenetURI getRequestUri() {
			return requestUri;
		}

		/**
		 * Returns the insert URI of the Sone.
		 *
		 * @return The insert URI of the Sone
		 */
		public FreenetURI getInsertUri() {
			return insertUri;
		}

		//
		// ACTIONS
		//

		/**
		 * Generates all manifest entries required to insert this Sone.
		 *
		 * @return The manifest entries for the Sone insert
		 */
		@SuppressWarnings("synthetic-access")
		public HashMap<String, Object> generateManifestEntries() {
			HashMap<String, Object> manifestEntries = new HashMap<String, Object>();

			Charset utf8Charset = Charset.forName("UTF-8");

			/* first, create an index.html. */
			Template indexTemplate = templateFactory.createTemplate(new InputStreamReader(getClass().getResourceAsStream("/templates/insert/index.html"), utf8Charset));
			indexTemplate.set("currentSone", sone);
			StringWriter indexWriter = new StringWriter();
			indexTemplate.render(indexWriter);
			StringBucket indexBucket = new StringBucket(indexWriter.toString(), utf8Charset);
			ManifestElement indexManifestElement = new ManifestElement("index.html", indexBucket, "text/html; charset=utf-8", indexBucket.size());
			manifestEntries.put("index.html", indexManifestElement);

			return manifestEntries;
		}

		//
		// PRIVATE METHODS
		//

	}

}