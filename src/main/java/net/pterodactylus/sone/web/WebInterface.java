/*
 * FreenetSone - WebInterface.java - Copyright © 2010 David Roden
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

package net.pterodactylus.sone.web;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.pterodactylus.sone.main.SonePlugin;
import net.pterodactylus.sone.web.page.CSSPage;
import net.pterodactylus.sone.web.page.PageToadlet;
import net.pterodactylus.sone.web.page.PageToadletFactory;
import net.pterodactylus.util.logging.Logging;
import net.pterodactylus.util.service.AbstractService;
import net.pterodactylus.util.template.Template;
import net.pterodactylus.util.template.TemplateFactory;
import freenet.clients.http.LinkEnabledCallback;
import freenet.clients.http.SessionManager;
import freenet.clients.http.ToadletContainer;
import freenet.clients.http.ToadletContext;
import freenet.l10n.BaseL10n;

/**
 * Bundles functionality that a web interface of a Freenet plugin needs, e.g.
 * references to l10n helpers.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class WebInterface extends AbstractService {

	/** The logger. */
	private static final Logger logger = Logging.getLogger(WebInterface.class);

	/** The Sone plugin. */
	private final SonePlugin sonePlugin;

	/** The registered toadlets. */
	private final List<PageToadlet> pageToadlets = new ArrayList<PageToadlet>();

	/**
	 * Creates a new web interface.
	 *
	 * @param sonePlugin
	 *            The Sone plugin
	 */
	public WebInterface(SonePlugin sonePlugin) {
		this.sonePlugin = sonePlugin;
	}

	//
	// ACCESSORS
	//

	/**
	 * Returns the l10n helper of the node.
	 *
	 * @return The node’s l10n helper
	 */
	public BaseL10n l10n() {
		return sonePlugin.l10n().getBase();
	}

	/**
	 * Returns the session manager of the node.
	 *
	 * @return The node’s session manager
	 */
	public SessionManager sessionManager() {
		try {
			return sonePlugin.pluginRespirator().getSessionManager(new URI("/"));
		} catch (URISyntaxException use1) {
			logger.log(Level.SEVERE, "Could not get Session Manager!", use1);
			return null;
		}
	}

	//
	// SERVICE METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void serviceStart() {
		registerToadlets();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void serviceStop() {
		unregisterToadlets();
	}

	//
	// PRIVATE METHODS
	//

	/**
	 * Register all toadlets.
	 */
	private void registerToadlets() {
		TemplateFactory templateFactory = new SoneTemplateFactory(l10n());

		Template loginTemplate = templateFactory.createTemplate(createReader("/templates/login.html"));
		loginTemplate.set("formPassword", sonePlugin.pluginRespirator().getToadletContainer().getFormPassword());

		PageToadletFactory pageToadletFactory = new PageToadletFactory(sonePlugin.pluginRespirator().getHLSimpleClient(), "/Sone/");
		pageToadlets.add(pageToadletFactory.createPageToadlet(new LoginPage(loginTemplate, this), "Login"));
		pageToadlets.add(pageToadletFactory.createPageToadlet(new CSSPage("css/", "/static/css/")));

		ToadletContainer toadletContainer = sonePlugin.pluginRespirator().getToadletContainer();
		toadletContainer.getPageMaker().addNavigationCategory("/Sone/", "Navigation.Menu.Name", "Navigation.Menu.Tooltip", sonePlugin);
		for (PageToadlet toadlet : pageToadlets) {
			String menuName = toadlet.getMenuName();
			if (menuName != null) {
				toadletContainer.register(toadlet, "Navigation.Menu.Name", toadlet.path(), true, "Navigation.Menu.Item." + menuName + ".Name", "Navigation.Menu.Item." + menuName + ".Tooltip", false, new AlwaysEnabledCallback());
			} else {
				toadletContainer.register(toadlet, null, toadlet.path(), true, false);
			}
		}
	}

	/**
	 * Unregisters all toadlets.
	 */
	private void unregisterToadlets() {
		ToadletContainer toadletContainer = sonePlugin.pluginRespirator().getToadletContainer();
		for (PageToadlet pageToadlet : pageToadlets) {
			toadletContainer.unregister(pageToadlet);
		}
		toadletContainer.getPageMaker().removeNavigationCategory("Navigation.Menu.Name");
	}

	/**
	 * Creates a {@link Reader} from the {@link InputStream} for the resource
	 * with the given name.
	 *
	 * @param resourceName
	 *            The name of the resource
	 * @return A {@link Reader} for the resource
	 */
	private Reader createReader(String resourceName) {
		try {
			return new InputStreamReader(getClass().getResourceAsStream(resourceName), "UTF-8");
		} catch (UnsupportedEncodingException uee1) {
			return null;
		}
	}

	/**
	 * {@link LinkEnabledCallback} implementation that always returns
	 * {@code true} when {@link LinkEnabledCallback#isEnabled(ToadletContext)}
	 * is called.
	 *
	 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
	 */
	public class AlwaysEnabledCallback implements LinkEnabledCallback {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isEnabled(ToadletContext toadletContext) {
			return true;
		}
	}

}