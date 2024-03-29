/* PrintMessageAction.java

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
package com.cubusmail.gwtui.client.actions.message;

import com.cubusmail.gwtui.client.actions.BaseGridAction;
import com.cubusmail.gwtui.client.model.GWTMessage;
import com.cubusmail.gwtui.client.services.GWTSessionManager;
import com.cubusmail.gwtui.client.util.ImageProvider;
import com.cubusmail.gwtui.client.util.PrintManager;
import com.cubusmail.gwtui.client.util.TextProvider;

/**
 * Print messages.
 * 
 * @author Juergen Schlierf
 */
public class PrintMessageAction extends BaseGridAction {

	public PrintMessageAction() {

		super( null );
		setText( TextProvider.get().actions_print_message_text() );
		setTooltipText( TextProvider.get().actions_print_message_tooltip() );
		setImageName( ImageProvider.PRINTER );
	}

	@Override
	public void execute() {

		GWTMessage message = GWTSessionManager.get().getCurrentMessage();
		PrintManager.print( message );
	}
}
