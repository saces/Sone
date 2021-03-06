/*
 * Sone - ReplyAccessor.java - Copyright © 2010 David Roden
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

package net.pterodactylus.sone.template;

import java.io.IOException;
import java.io.StringReader;

import net.pterodactylus.sone.core.Core;
import net.pterodactylus.sone.data.Reply;
import net.pterodactylus.sone.data.Sone;
import net.pterodactylus.sone.text.FreenetLinkParser;
import net.pterodactylus.util.template.Accessor;
import net.pterodactylus.util.template.DataProvider;
import net.pterodactylus.util.template.ReflectionAccessor;
import net.pterodactylus.util.template.TemplateFactory;

/**
 * {@link Accessor} implementation that adds a couple of properties to
 * {@link Reply}s.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public class ReplyAccessor extends ReflectionAccessor {

	/** Parser for Freenet links. */
	private final FreenetLinkParser linkParser;

	/** The core. */
	private final Core core;

	/**
	 * Creates a new reply accessor.
	 *
	 * @param core
	 *            The core
	 * @param templateFactory
	 *            The template factory for the text parser
	 */
	public ReplyAccessor(Core core, TemplateFactory templateFactory) {
		this.core = core;
		linkParser = new FreenetLinkParser(templateFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object get(DataProvider dataProvider, Object object, String member) {
		Reply reply = (Reply) object;
		if ("likes".equals(member)) {
			return core.getLikes(reply);
		} else if (member.equals("liked")) {
			Sone currentSone = (Sone) dataProvider.getData("currentSone");
			return (currentSone != null) && (currentSone.isLikedReplyId(reply.getId()));
		} else if (member.equals("new")) {
			return core.isNewReply(reply.getId(), false);
		} else if (member.equals("text")) {
			String text = reply.getText();
			try {
				return linkParser.parse(new StringReader(text));
			} catch (IOException ioe1) {
				/* ignore. */
			}
		}
		return super.get(dataProvider, object, member);
	}

}
