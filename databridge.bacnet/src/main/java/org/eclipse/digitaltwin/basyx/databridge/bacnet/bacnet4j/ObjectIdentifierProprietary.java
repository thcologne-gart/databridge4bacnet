package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet4j;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class ObjectIdentifierProprietary extends ObjectIdentifier {

    public ObjectIdentifierProprietary(final int objectType, final int instanceNumber) throws BACnetErrorException {
        super(new ObjectTypeProprietary(objectType), instanceNumber);
    }
}
