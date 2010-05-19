package com.cubusmail.client.canvases.addressbook;

import com.cubusmail.common.model.Address;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

public class AddressEditAddressForm extends AddressEditAbstractForm {

	private TextItem streetItem;
	private TextItem zipItem;
	private TextItem cityItem;
	private TextItem stateItem;
	private TextItem countryItem;

	public AddressEditAddressForm() {

		super();
		this.streetItem = new TextItem( "streetItem" );
		this.streetItem.setHint( "Street" );
		this.streetItem.setShowHintInField( true );
		this.streetItem.setShowTitle( false );
		this.streetItem.setColSpan( 2 );
		this.zipItem = new TextItem( "zipItem" );
		this.zipItem.setHint( "Zip" );
		this.zipItem.setShowHintInField( true );
		this.zipItem.setShowTitle( false );
		this.cityItem = new TextItem( "cityItem" );
		this.cityItem.setShowTitle( false );
		this.cityItem.setHint( "City" );
		this.cityItem.setShowHintInField( true );
		this.stateItem = new TextItem( "stateItem" );
		this.stateItem.setHint( "State" );
		this.stateItem.setShowHintInField( true );
		this.stateItem.setShowTitle( false );
		this.countryItem = new TextItem( "countryItem" );
		this.countryItem.setHint( "Country" );
		this.countryItem.setShowHintInField( true );
		this.countryItem.setShowTitle( false );

		setItems( this.typeSelectionItem, this.streetItem, this.removeItem, this.addItem, new SpacerItem(),
				this.cityItem, new SpacerItem(), new SpacerItem(), new SpacerItem(), this.stateItem, this.zipItem,
				new SpacerItem(), new SpacerItem(), this.countryItem );
	}

	@Override
	public <T> T getValue() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue( Object value ) {

		Address address = (Address) value;
		if ( value == null ) {
			if ( this.streetItem.getValue() != null ) {
				this.streetItem.clearValue();
			}
			if ( this.zipItem.getValue() != null ) {
				this.zipItem.clearValue();
			}
			if ( this.cityItem.getValue() != null ) {
				this.cityItem.clearValue();
			}
			if ( this.stateItem.getValue() != null ) {
				this.stateItem.clearValue();
			}
			if ( this.countryItem.getValue() != null ) {
				this.countryItem.clearValue();
			}
		}
		else if ( getType() == AddressEditFormTypeEnum.PRVATE_ADDRESS ) {
			this.streetItem.setValue( address.getPrivateStreet() );
			this.zipItem.setValue( address.getPrivateZipcode() );
			this.cityItem.setValue( address.getPrivateCity() );
			this.stateItem.setValue( address.getPrivateState() );
			this.countryItem.setValue( address.getPrivateCountry() );
		}
		else {
			this.streetItem.setValue( address.getWorkStreet() );
			this.zipItem.setValue( address.getWorkZipcode() );
			this.cityItem.setValue( address.getWorkCity() );
			this.stateItem.setValue( address.getWorkState() );
			this.countryItem.setValue( address.getWorkCountry() );
		}
	}
}
