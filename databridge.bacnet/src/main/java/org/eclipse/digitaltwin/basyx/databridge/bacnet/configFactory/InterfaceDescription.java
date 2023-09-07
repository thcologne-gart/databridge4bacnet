package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.serotonin.bacnet4j.type.primitive.Enumerated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class InterfaceDescription {
    // the field name should match the protocol name used in the AID. Example:
    // base = bacnet:127.0.0.1

    public static final InterfaceDescription BACNET = new InterfaceDescription("BACnetInterface");
    public static final InterfaceDescription OPCUA = new InterfaceDescription("UAInterface");
    public static final InterfaceDescription AAS = new InterfaceDescription("AasInterface");
    private static final Map<String, InterfaceDescription> idMap = new HashMap<>();
    private static final Map<String, String> protocolForIdMap = new HashMap<>();
    private static final Map<String, InterfaceDescription> protocolMap = new HashMap<>();

    private final String idShort;

    static {
        init(InterfaceDescription.class, idMap, protocolForIdMap, protocolMap);
    }

    private InterfaceDescription(String idShort) {
        this.idShort = idShort;
    }

    private static void init(final Class<?> clazz, final Map<String, InterfaceDescription> idMap,
                             Map<String, String> protocolForIdMap, Map<String, InterfaceDescription> protocolMap) {
        try {
            final Field[] fields = clazz.getFields();
            for (final Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) //
                        && Modifier.isStatic(field.getModifiers()) //
                        && Modifier.isFinal(field.getModifiers()) //
                        && field.getType() == clazz) {

                    protocolMap.put(field.getName().toLowerCase(), (InterfaceDescription) field.get(null));
                    protocolForIdMap.put(((InterfaceDescription) field.get(null)).getIdShort(), field.getName().toLowerCase());
                    idMap.put(((InterfaceDescription) field.get(null)).getIdShort(), (InterfaceDescription) field.get(null));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static InterfaceDescription forProtocolName(String protocolName) {
        return protocolMap.get(protocolName.toLowerCase());
    }
    public static InterfaceDescription forIdShort(String idShort) {
        return idMap.get(idShort);
    }

    public String getProtocolName() {
        return protocolForIdMap.get(idShort);
    }

    public String getIdShort() {
        return idShort;
    }

    public String toString() {
        return idShort;
    }

}
