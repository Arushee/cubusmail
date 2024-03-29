/* DeleteFolderAction.java

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
package com.cubusmail.client.actions.folder;

import com.cubusmail.client.events.EventBroker;
import com.cubusmail.client.exceptions.GWTExceptionHandler;
import com.cubusmail.client.util.GWTUtil;
import com.cubusmail.client.util.ServiceProvider;
import com.cubusmail.client.util.TextProvider;
import com.cubusmail.common.exceptions.folder.GWTMailFolderException;
import com.cubusmail.common.model.IGWTFolder;
import com.cubusmail.common.model.ImageProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;

/**
 * Delete mail folder.
 * 
 * @author schlierf
 */
public class DeleteFolderAction extends GWTFolderAction implements AsyncCallback<Void> {

	/**
	 * 
	 */
	public DeleteFolderAction() {

		super();
		setText( TextProvider.get().actions_deletefolder_text() );
		setIcon( ImageProvider.MAIL_FOLDER_DELETE );
		setTooltip( TextProvider.get().actions_deletefolder_tooltip() );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cubusmail.gwtui.client.actions.GWTAction#execute()
	 */
	public void execute() {

		SC.ask( TextProvider.get().actions_deletefolder_text(), TextProvider.get().actions_deletefolder_warning(),
				new BooleanCallback() {

					public void execute( Boolean value ) {

						if ( value ) {
							IGWTFolder folder = GWTUtil.getGwtFolder( getSelectedTreeNode() );
							if ( folder != null ) {
								deleteFolder( folder.getId() );
							}
						}
					}
				} );
	}

	private void deleteFolder( String folderId ) {

		ServiceProvider.getMailboxService().deleteFolder( folderId, this );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable
	 * )
	 */
	public void onFailure( Throwable caught ) {

		GWTExceptionHandler.handleException( caught );
		GWTMailFolderException e = (GWTMailFolderException) caught;

		SC.warn( TextProvider.get().exception_folder_delete( e.getFolderName() ), new BooleanCallback() {

			public void execute( Boolean value ) {

				EventBroker.get().fireFoldersReload();
			}
		} );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
	 */
	public void onSuccess( Void result ) {

		// PanelRegistry.LEFT_PANEL.unmask();
		// getSelectedTreeNode().remove();
		this.tree.getData().remove( getSelectedTreeNode() );
	}
}
