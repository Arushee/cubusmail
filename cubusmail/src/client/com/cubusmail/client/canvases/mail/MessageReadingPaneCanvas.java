/* MessageReadingPane.java

   Copyright (c) 2009 Juergen Schlierf, All Rights Reserved
   
   This file is part of Cubusmail (http://code.google.com/p/cubusmail/).
	
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.
	
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
	
   You should have received a copy of the GNU Lesser General Public
   License along with Cubusmail. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cubusmail.client.canvases.mail;

import com.cubusmail.client.events.EventBroker;
import com.cubusmail.client.events.MessageLoadedListener;
import com.cubusmail.common.model.GWTMessage;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Reading pane for mail messages.
 * 
 * @author Juergen Schlierf
 */
public class MessageReadingPaneCanvas extends VLayout implements MessageLoadedListener {

	private MessageReadingPaneHeader header;
	private Canvas content;

	public MessageReadingPaneCanvas() {

		super();

		this.header = new MessageReadingPaneHeader();
		this.content = new Canvas();

		setMembers( this.header, this.content );

		EventBroker.get().addMessageLoadedListener( this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.client.events.MessageLoadedListener#onMessageLoaded(com
	 * .cubusmail.common.model.GWTMessage)
	 */
	public void onMessageLoaded( GWTMessage message ) {

		this.header.setMessage( message );
	}
}