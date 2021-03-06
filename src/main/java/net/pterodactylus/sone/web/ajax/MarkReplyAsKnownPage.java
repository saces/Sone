/*
 * Sone - MarkPostAsKnownPage.java - Copyright © 2010 David Roden
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

package net.pterodactylus.sone.web.ajax;

import net.pterodactylus.sone.data.Reply;
import net.pterodactylus.sone.web.WebInterface;
import net.pterodactylus.util.json.JsonObject;

/**
 * AJAX handler that marks a {@link Reply} as known.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class MarkReplyAsKnownPage extends JsonPage {

	/**
	 * Creates a new “mark reply as known” AJAX handler.
	 *
	 * @param webInterface
	 *            The Sone web interface
	 */
	public MarkReplyAsKnownPage(WebInterface webInterface) {
		super("markReplyAsKnown.ajax", webInterface);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected JsonObject createJsonObject(Request request) {
		String replyId = request.getHttpRequest().getParam("reply");
		Reply reply = webInterface.getCore().getReply(replyId, false);
		if (reply == null) {
			return createErrorJsonObject("invalid-reply-id");
		}
		webInterface.getCore().markReplyKnown(reply);
		return createSuccessJsonObject();
	}

}
