/* UIFactory.java

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
package com.cubusmail.client.util;

import java.util.List;

import com.cubusmail.client.actions.IGWTAction;
import com.cubusmail.client.widgets.CubusButton;
import com.cubusmail.client.widgets.CubusImgButton;
import com.cubusmail.common.model.AddressFolder;
import com.cubusmail.common.model.GWTConstants;
import com.cubusmail.common.model.GWTMailFolder;
import com.cubusmail.common.model.GWTMailbox;
import com.cubusmail.common.model.IGWTFolder;
import com.cubusmail.common.model.ImageProvider;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.menu.MenuButton;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tree.TreeNode;

/**
 * Factory for UI elements.
 * 
 * @author Juergen Schlierf
 */
public abstract class UIFactory {

	/**
	 * Create buttons.
	 * 
	 * @param action
	 * @return
	 */
	public static Button createButton( final IGWTAction action ) {

		Button button = new Button();
		if ( action.getText() != null ) {
			button.setTitle( action.getText() );
		}
		if ( action.getIcon() != null ) {
			button.setIcon( action.getIcon() );
		}
		if ( action.getTooltip() != null ) {
			button.setTooltip( action.getTooltip() );
		}

		button.addClickHandler( new ClickHandler() {

			public void onClick( ClickEvent event ) {

				action.execute();
			}
		} );
		return button;
	}

	/**
	 * Create image buttons.
	 * 
	 * @param action
	 * @return
	 */
	public static Button createToolbarButton( final IGWTAction action, boolean imageOnly ) {

		Button button = new CubusButton();
		if ( !imageOnly && action.getText() != null ) {
			button.setTitle( action.getText() );
			button.setAutoFit( true );
		}
		else {
			button.setTitle( "" );
			button.setWidth( 24 );
			button.setShowDown( true );
			button.setShowOverCanvas( true );
		}
		button.setIcon( action.getIcon() );
		if ( action.getTooltip() != null ) {
			button.setTooltip( action.getTooltip() );
		}
		button.setBorder( "0px" );
		button.addClickHandler( new ClickHandler() {

			public void onClick( ClickEvent event ) {

				action.execute();
			}
		} );
		return button;
	}

	/**
	 * @param action
	 * @return
	 */
	public static ImgButton createImgButton( final IGWTAction action ) {

		ImgButton button = new CubusImgButton();
		button.setSrc( action.getIcon() );
		button.setShowDisabled( true );
		button.setShowDisabledIcon( false );
		button.setShowRollOver( false );
		button.setShowDown( false );
		button.setImageType( ImageStyle.CENTER );
		button.setSize( 18 );
		button.addClickHandler( new ClickHandler() {

			public void onClick( ClickEvent event ) {

				action.execute();
			}
		} );

		return button;
	}

	/**
	 * @param action
	 * @return
	 */
	public static IButton createIButton( final IGWTAction action ) {

		IButton button = new IButton();
		button.setShowDisabledIcon( false );
		button.setShowRollOverIcon( false );
		button.setIcon( action.getIcon() );
		button.setWidth( 25 );
		button.addClickHandler( new ClickHandler() {

			public void onClick( ClickEvent event ) {

				action.execute();
			}
		} );

		return button;
	}

	/**
	 * @param action
	 * @return
	 */
	public static MenuButton createMenuButton( final IGWTAction action ) {

		MenuButton button = new MenuButton( action.getText() );
		button.setIconOrientation( "" );
		button.setBorder( "0px" );
		if ( action.getIcon() != null ) {
			button.setIcon( action.getIcon() );
		}
		button.addClickHandler( new ClickHandler() {

			public void onClick( ClickEvent event ) {

				action.execute();
			}
		} );

		return button;
	}

	public static TreeNode[] createAddressFolderTreeNodes( List<AddressFolder> folderList ) {

		TreeNode[] nodes = new TreeNode[folderList.size()];
		for (int i = 0; i < folderList.size(); i++) {
			AddressFolder folder = folderList.get( i );
			nodes[i] = new TreeNode( folder.getName() );
			nodes[i].setAttribute( "icon", ImageProvider.ADDRESS_FOLDER );
			nodes[i].setID( folder.getName() );
			nodes[i].setAttribute( GWTConstants.ADDRESS_FOLDER, folder );
		}

		return nodes;
	}

	/**
	 * @param mailFolder
	 * @return
	 */
	public static TreeNode createTreeNode( IGWTFolder mailFolder ) {

		TreeNode node = new TreeNode( mailFolder.getName() );
		node.setAttribute( "icon", getFolderIcon( mailFolder ) );
		node.setID( mailFolder.getId() );
		node.setCanAcceptDrop( true );
		if ( mailFolder.isMoveSupported() ) {
			node.setCanDrag( true );
		}
		else {
			node.setCanDrag( false );
		}
		GWTUtil.setGwtFolder( node, mailFolder );

		if ( mailFolder.getSubfolders() != null && mailFolder.getSubfolders().length > 0 ) {
			TreeNode[] nodes = new TreeNode[mailFolder.getSubfolders().length];
			for (int i = 0; i < mailFolder.getSubfolders().length; i++) {
				nodes[i] = createTreeNode( mailFolder.getSubfolders()[i] );
				nodes[i].setAttribute( GWTConstants.PARAM_PARENT_FOLDER, node );
			}
			node.setChildren( nodes );
		}

		return node;
	}

	/**
	 * @param folder
	 * @return
	 */
	private static String getFolderIcon( IGWTFolder folder ) {

		if ( folder instanceof GWTMailbox ) {
			return ImageProvider.MAIL_FOLDER_MAILBOX;
		}
		else {
			GWTMailFolder mailFolder = (GWTMailFolder) folder;
			if ( mailFolder.isInbox() ) {
				return ImageProvider.MAIL_FOLDER_INBOX;
			}
			else if ( mailFolder.isDraft() ) {
				return ImageProvider.MAIL_FOLDER_DRAFT;
			}
			else if ( mailFolder.isSent() ) {
				return ImageProvider.MAIL_FOLDER_SENT;
			}
			else if ( mailFolder.isTrash() ) {
				return ImageProvider.MAIL_FOLDER_TRASH_FULL;
			}
			else {
				return ImageProvider.MAIL_FOLDER;
			}
		}
	}

	/**
	 * @param action
	 * @return
	 */
	public static MenuItem createMenuItem( final IGWTAction action ) {

		MenuItem menuItem = new MenuItem( action.getText() );
		if ( action.getIcon() != null ) {
			menuItem.setIcon( action.getIcon() );
		}
		menuItem.addClickHandler( new com.smartgwt.client.widgets.menu.events.ClickHandler() {

			public void onClick( MenuItemClickEvent event ) {

				action.execute();
			}
		} );

		return menuItem;
	}
}
