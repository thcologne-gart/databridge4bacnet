package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class PropertyIdentifierProprietary extends PropertyIdentifier {
    private static final int MAX_INT_PROPERTY_IDENTIFIER = 491;

    private final int propertyIdentifier;
    public PropertyIdentifierProprietary(int propertyIdentifier) throws BACnetErrorException {
        super(propertyIdentifier);
        this.propertyIdentifier = propertyIdentifier;
    }

    public static boolean isProprietary(int propertyIdentifier) {return propertyIdentifier > PropertyIdentifierProprietary.MAX_INT_PROPERTY_IDENTIFIER;}
    @Override
    public String toString() {
        return Integer.toString(propertyIdentifier);
    }

    public static String nameForId(final int id) {
        return Integer.toString(id);
    }

    public static PropertyIdentifier forId(final int id) {
        try {
            return new PropertyIdentifierProprietary(id);
        } catch (BACnetErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public static PropertyIdentifier forName(final String name) {
        int intValue;
        try {
            intValue = Integer.parseInt(name);
            return new PropertyIdentifierProprietary(intValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Use for Proprietary Properties only. If Property Identifier is no number, please use PropertyIdentifier class");
        } catch (BACnetErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
