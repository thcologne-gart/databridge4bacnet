package org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.obj.logBuffer.LogBuffer;
import com.serotonin.bacnet4j.type.AmbiguousValue;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.EncodedValue;
import com.serotonin.bacnet4j.type.constructed.BaseType;
import com.serotonin.bacnet4j.type.primitive.*;
import com.serotonin.bacnet4j.type.primitive.Double;
import com.serotonin.bacnet4j.type.primitive.Boolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public abstract class BacnetDatatypeParser {
    private static final Logger LOG = LoggerFactory.getLogger(BacnetDatatypeParser.class);
    public static Encodable parse(Object value, Class<? extends Encodable> targetClass) {
        if (targetClass == AmbiguousValue.class) {
            return null;
        } else if (targetClass == EncodedValue.class) {
            return null;
        } else if (Primitive.class.isAssignableFrom(targetClass)) {
            return parsePrimitive(value, (Class<? extends Primitive>) targetClass);
        } else if (BaseType.class.isAssignableFrom(targetClass)) {
            return parseBaseType(value, (Class<? extends BaseType>)targetClass);
        } else if (LogBuffer.class.isAssignableFrom(targetClass)) {
            return null;

        }
        return null;
    }

    private static Encodable parseBaseType(Object value, Class<? extends BaseType> targetClass) {
        return null;
    }
    private static Encodable parsePrimitive(Object value, Class<? extends Primitive> targetClass) {
        if (targetClass == Null.class) {
            return new Null();
        } else if (targetClass == Real.class) {
            return parseWithPrimitiveConstructor(value, targetClass);
        } else if (targetClass == Double.class) {
            return parseWithPrimitiveConstructor(value, targetClass);
        } else if (targetClass == SignedInteger.class) {
            return parseWithPrimitiveConstructor(value, targetClass);
        } else if (targetClass == UnsignedInteger.class) {
            return parseWithPrimitiveConstructor(value, targetClass);

        } else if (targetClass == CharacterString.class) {
            return new CharacterString(value.toString());
        } else if (targetClass == Boolean.class) {
            try{
                return parseBoolean(value);
            } catch (BACnetErrorException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private static Boolean parseBoolean(Object value) throws BACnetErrorException {
        if (value instanceof Character) {
            value = String.valueOf(value);
        }
        if (value instanceof String) {
            switch (((String) value).toUpperCase()) {
                case "TRUE":
                case "1":
                    return Boolean.TRUE;
                case "FALSE":
                case "0":
                    return Boolean.FALSE;
                default:
                    return null;
            }
        }
        if (value instanceof Number) {
            if (((Number) value).intValue() == 0) {
                return Boolean.FALSE;
            } else if (((Number) value).intValue() == 1) {
                return Boolean.TRUE;
            }
            return null;
        }
        if (value instanceof java.lang.Boolean) {
            if ((java.lang.Boolean) value) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return null;
    }


    private static Encodable parseWithPrimitiveConstructor(Object value, Class<? extends Primitive> targetClass) {
        // Double, SignedInteger, UnsignedInteger, Real
        if (value instanceof String) {
            try {
                value = java.lang.Double.parseDouble((String) value);
            }  catch (NumberFormatException ignored) {

            }
        }

        if (value instanceof Number) {
            for (Method m : Number.class.getDeclaredMethods()) {
                try {
                    Constructor<? extends Encodable> c = targetClass.getDeclaredConstructor(m.getReturnType());
                    return c.newInstance(m.invoke(value));
                } catch (NoSuchMethodException ignored) {

                } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return null;
    }
}
