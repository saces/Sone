/*
 * Sone - CoreListener.java - Copyright © 2010 David Roden
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

import java.util.EventListener;

import net.pterodactylus.sone.data.Post;
import net.pterodactylus.sone.data.Reply;
import net.pterodactylus.sone.data.Sone;

/**
 * Listener interface for objects that want to be notified on certain
 * {@link Core} events, such es discovery of new data.
 *
 * @author <a href="mailto:bombe@pterodactylus.net">David ‘Bombe’ Roden</a>
 */
public interface CoreListener extends EventListener {

	/**
	 * Notifies a listener that a Sone is now being rescued.
	 *
	 * @param sone
	 *            The Sone that is rescued
	 */
	public void rescuingSone(Sone sone);

	/**
	 * Notifies a listener that the Sone was rescued and can now be unlocked.
	 *
	 * @param sone
	 *            The Sone that was rescued
	 */
	public void rescuedSone(Sone sone);

	/**
	 * Notifies a listener that a new Sone has been discovered.
	 *
	 * @param sone
	 *            The new Sone
	 */
	public void newSoneFound(Sone sone);

	/**
	 * Notifies a listener that a new post has been found.
	 *
	 * @param post
	 *            The new post
	 */
	public void newPostFound(Post post);

	/**
	 * Notifies a listener that a new reply has been found.
	 *
	 * @param reply
	 *            The new reply
	 */
	public void newReplyFound(Reply reply);

	/**
	 * Notifies a listener that the given Sone is now marked as known.
	 *
	 * @param sone
	 *            The known Sone
	 */
	public void markSoneKnown(Sone sone);

	/**
	 * Notifies a listener that the given post is now marked as known.
	 *
	 * @param post
	 *            The known post
	 */
	public void markPostKnown(Post post);

	/**
	 * Notifies a listener that the given reply is now marked as known.
	 *
	 * @param reply
	 *            The known reply
	 */
	public void markReplyKnown(Reply reply);

	/**
	 * Notifies a listener that the given post was removed.
	 *
	 * @param post
	 *            The removed post
	 */
	public void postRemoved(Post post);

	/**
	 * Notifies a listener that the given reply was removed.
	 *
	 * @param reply
	 *            The removed reply
	 */
	public void replyRemoved(Reply reply);

	/**
	 * Notifies a listener when a Sone was locked.
	 *
	 * @param sone
	 *            The Sone that was locked
	 */
	public void soneLocked(Sone sone);

	/**
	 * Notifies a listener that a Sone was unlocked.
	 *
	 * @param sone
	 *            The Sone that was unlocked
	 */
	public void soneUnlocked(Sone sone);

}
