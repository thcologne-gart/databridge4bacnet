package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.serotonin.bacnet4j.type.primitive.Enumerated;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class MappingConfiguration {
    // the field name should match the protocol name used in the AID. Example:
    // base = bacnet:127.0.0.1

    public static final MappingConfiguration BACNET = new MappingConfiguration("BacnetMappingConfiguration");
    public static final MappingConfiguration AAS = new MappingConfiguration("AasMappingConfiguration");

    private static final Map<String, MappingConfiguration> idMap = new HashMap<>();
    private static final Map<String, String> protocolForIdMap = new HashMap<>();
    private static final Map<String, MappingConfiguration> protocolMap = new HashMap<>();

    private final String idShort;

    static {
        init(MappingConfiguration.class, idMap, protocolForIdMap, protocolMap);
    }

    private MappingConfiguration(String idShort) {
        this.idShort = idShort;
    }

    private static void init(final Class<?> clazz, final Map<String, MappingConfiguration> idMap,
                             Map<String, String> protocolForIdMap, Map<String, MappingConfiguration> protocolMap) {
        try {
            final Field[] fields = clazz.getFields();
            for (final Field field : fields) {
                if (Modifier.isPublic(field.getModifiers()) //
                        && Modifier.isStatic(field.getModifiers()) //
                        && Modifier.isFinal(field.getModifiers()) //
                        && field.getType() == clazz) {

                    protocolMap.put(field.getName().toLowerCase(), (MappingConfiguration) field.get(null));
                    protocolForIdMap.put(((MappingConfiguration) field.get(null)).getIdShort(), field.getName().toLowerCase());
                    idMap.put(((MappingConfiguration) field.get(null)).getIdShort(), (MappingConfiguration) field.get(null));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static MappingConfiguration forProtocolName(String protocolName) {
        return protocolMap.get(protocolName.toLowerCase());
    }
    public static MappingConfiguration forIdShort(String idShort) {
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
