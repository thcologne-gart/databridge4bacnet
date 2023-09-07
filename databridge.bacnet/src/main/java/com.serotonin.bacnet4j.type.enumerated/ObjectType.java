/*
    Overrides original class for public constructor
 */
package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import java.util.Collections;

public class ObjectType extends Enumerated {
    public static final ObjectType analogInput = new ObjectType(0);
    public static final ObjectType analogOutput = new ObjectType(1);
    public static final ObjectType analogValue = new ObjectType(2);
    public static final ObjectType binaryInput = new ObjectType(3);
    public static final ObjectType binaryOutput = new ObjectType(4);
    public static final ObjectType binaryValue = new ObjectType(5);
    public static final ObjectType calendar = new ObjectType(6);
    public static final ObjectType command = new ObjectType(7);
    public static final ObjectType device = new ObjectType(8);
    public static final ObjectType eventEnrollment = new ObjectType(9);
    public static final ObjectType file = new ObjectType(10);
    public static final ObjectType group = new ObjectType(11);
    public static final ObjectType loop = new ObjectType(12);
    public static final ObjectType multiStateInput = new ObjectType(13);
    public static final ObjectType multiStateOutput = new ObjectType(14);
    public static final ObjectType notificationClass = new ObjectType(15);
    public static final ObjectType program = new ObjectType(16);
    public static final ObjectType schedule = new ObjectType(17);
    public static final ObjectType averaging = new ObjectType(18);
    public static final ObjectType multiStateValue = new ObjectType(19);
    public static final ObjectType trendLog = new ObjectType(20);
    public static final ObjectType lifeSafetyPoint = new ObjectType(21);
    public static final ObjectType lifeSafetyZone = new ObjectType(22);
    public static final ObjectType accumulator = new ObjectType(23);
    public static final ObjectType pulseConverter = new ObjectType(24);
    public static final ObjectType eventLog = new ObjectType(25);
    public static final ObjectType globalGroup = new ObjectType(26);
    public static final ObjectType trendLogMultiple = new ObjectType(27);
    public static final ObjectType loadControl = new ObjectType(28);
    public static final ObjectType structuredView = new ObjectType(29);
    public static final ObjectType accessDoor = new ObjectType(30);
    public static final ObjectType timer = new ObjectType(31);
    public static final ObjectType accessCredential = new ObjectType(32);
    public static final ObjectType accessPoint = new ObjectType(33);
    public static final ObjectType accessRights = new ObjectType(34);
    public static final ObjectType accessUser = new ObjectType(35);
    public static final ObjectType accessZone = new ObjectType(36);
    public static final ObjectType credentialDataInput = new ObjectType(37);
    public static final ObjectType networkSecurity = new ObjectType(38);
    public static final ObjectType bitstringValue = new ObjectType(39);
    public static final ObjectType characterstringValue = new ObjectType(40);
    public static final ObjectType datePatternValue = new ObjectType(41);
    public static final ObjectType dateValue = new ObjectType(42);
    public static final ObjectType datetimePatternValue = new ObjectType(43);
    public static final ObjectType datetimeValue = new ObjectType(44);
    public static final ObjectType integerValue = new ObjectType(45);
    public static final ObjectType largeAnalogValue = new ObjectType(46);
    public static final ObjectType octetstringValue = new ObjectType(47);
    public static final ObjectType positiveIntegerValue = new ObjectType(48);
    public static final ObjectType timePatternValue = new ObjectType(49);
    public static final ObjectType timeValue = new ObjectType(50);
    public static final ObjectType notificationForwarder = new ObjectType(51);
    public static final ObjectType alertEnrollment = new ObjectType(52);
    public static final ObjectType channel = new ObjectType(53);
    public static final ObjectType lightingOutput = new ObjectType(54);
    public static final ObjectType binaryLightingOutput = new ObjectType(55);
    public static final ObjectType networkPort = new ObjectType(56);
    public static final ObjectType elevatorGroup = new ObjectType(57);
    public static final ObjectType escalator = new ObjectType(58);
    public static final ObjectType lift = new ObjectType(59);

    private static final Map<Integer, Enumerated> idMap = new HashMap<>();
    private static final Map<String, Enumerated> nameMap = new HashMap<>();
    private static final Map<Integer, String> prettyMap = new HashMap<>();

    static {
        Enumerated.init(MethodHandles.lookup().lookupClass(), idMap, nameMap, prettyMap);
    }

    public static ObjectType forId(final int id) {
        ObjectType e = (ObjectType) idMap.get(id);
        if (e == null)
            e = new ObjectType(id);
        return e;
    }

    public static String nameForId(final int id) {
        return prettyMap.get(id);
    }

    public static ObjectType forName(final String name) {
        return (ObjectType) Enumerated.forName(nameMap, name);
    }

    public static int size() {
        return idMap.size();
    }

    public ObjectType(final int value) {
        super(value);
    }

    public ObjectType(final ByteQueue queue) throws BACnetErrorException {
        super(queue);
    }

    /**
     * Returns a unmodifiable map.
     *
     * @return unmodifiable map
     */
    public static Map<Integer, String> getPrettyMap() {
        return Collections.unmodifiableMap(prettyMap);
    }

    /**
     * Returns a unmodifiable nameMap.
     *
     * @return unmodifiable map
     */
    public static Map<String, Enumerated> getNameMap() {
        return Collections.unmodifiableMap(nameMap);
    }

    @Override
    public String toString() {
        return super.toString(prettyMap);
    }

}
