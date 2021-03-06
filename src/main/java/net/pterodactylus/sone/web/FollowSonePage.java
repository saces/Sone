/*
 * Sone - FollowSonePage.java - Copyright © 2010 David Roden
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

import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.web.page.Page.Request.Method;
import net.pterodactylus.util.template.Template;

/**
 * This page lets the user follow another Sone.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class FollowSonePage extends SoneTemplatePage {

	/**
	 * @param template
	 *            The template to render
	 * @param webInterface
	 *            The Sone web interface
	 */
	public FollowSonePage(Template template, WebInterface webInterface) {
		super("followSone.html", template, "Page.FollowSone.Title", webInterface, true);
	}

	//
	// TEMPLATEPAGE METHODS
	//

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processTemplate(Request request, Template template) throws RedirectException {
		super.processTemplate(request, template);
		if (request.getMethod() == Method.POST) {
			String soneId = request.getHttpRequest().getPartAsStringFailsafe("sone", 44);
			String returnPage = request.getHttpRequest().getPartAsStringFailsafe("returnPage", 256);
			Sone currentSone = getCurrentSone(request.getToadletContext());
			currentSone.addFriend(soneId);
			webInterface.getCore().saveSone(currentSone);
			throw new RedirectException(returnPage);
		}
	}

}
