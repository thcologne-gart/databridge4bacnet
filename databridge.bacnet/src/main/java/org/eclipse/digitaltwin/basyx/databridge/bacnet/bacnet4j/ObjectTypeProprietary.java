package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;

public class ObjectTypeProprietary extends ObjectType {
    private static final int MAX_INT_OBJECT_TYPE = 59;
    private final int objectType;
    public ObjectTypeProprietary(int objectType) throws BACnetErrorException {
        super(objectType);
        this.objectType = objectType;
    }

    public static boolean isProprietary(int propertyIdentifier) {return propertyIdentifier > ObjectTypeProprietary.MAX_INT_OBJECT_TYPE;}

    @Override
    public String toString() {
        return Integer.toString(objectType);
    }

    public static String nameForId(final int id) {
        return Integer.toString(id);
    }

    public static ObjectType forId(final int id) {
        try {
            return new ObjectTypeProprietary(id);
        } catch (BACnetErrorException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObjectType forName(final String name) {
        int intValue;
        try {
            intValue = Integer.parseInt(name);
            return new ObjectTypeProprietary(intValue);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Use for Proprietary Objects only. If Object Type is no Number, please use ObjectType class");
        } catch (BACnetErrorException e) {
            throw new RuntimeException(e);
        }
    }


}
