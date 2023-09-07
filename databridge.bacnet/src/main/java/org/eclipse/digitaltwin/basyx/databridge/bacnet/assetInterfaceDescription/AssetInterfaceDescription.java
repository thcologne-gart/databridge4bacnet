package org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription;

import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.SubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.submodelelement.dataelement.property.Property;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.JsonFileFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetInterfaceDescription extends SubmodelElementCollection implements ISubmodelElementCollection{
    private static Logger logger = LoggerFactory.getLogger(AssetInterfaceDescription.class);

    private final SubmodelElementCollection smc;
    private final String protocol;
    private final String ipAddress;


    public AssetInterfaceDescription(SubmodelElementCollection submodelElementCollection) {
        this.smc = submodelElementCollection;
        this.protocol = identifyProtocol();
        this.ipAddress = identifyIpAddress();
    }

    public ISubmodelElementCollection getEndpointMetadata() {
        return (ISubmodelElementCollection) smc.getSubmodelElement("EndpointMetadata");
    }
    public String getBase() {
        return this.getEndpointMetadata().getSubmodelElement("base").getValue().toString();
    }
    public String getContentType() {
        return this.getEndpointMetadata().getSubmodelElement("contentType").getValue().toString();
    }


    public String getProtocol() {return this.protocol;}
    public String getIpAddress() {return this.ipAddress;}

    private String identifyProtocol() {
        String base = this.getBase();
        if (!base.contains(":")) {
            throw new RuntimeException("Base ' "+ base + "' does not contain the protocol name");
        }
        return base.split(":")[0];
    }

    private String identifyIpAddress() {
        String base = this.getBase();
        if (base.contains("://")) {
            for (String t : base.split("://")) {
                for (String s : t.split(":")) {
                    if (s.split("\\.").length == 4 && s.length() > 6 && s.length() < 15) {
                        return s;
                    }
                }
            }
        }
        for (String s : base.split(":")) {
            if (s.split("\\.").length == 4 && s.length() > 6 && s.length() < 15) {
                return s;
            }
        }
        throw new RuntimeException("Base '"+ base + "' does not contain the ip-address");
    }


    public ISubmodelElementCollection getSecurityDefinitions() {
        if (!this.getInterfaceMetadata().getSubmodelElements().containsKey("securityDefinitions")) {
            logger.warn("No securityDefinitions");
            return null;
        }
        return (ISubmodelElementCollection) this.getInterfaceMetadata().getSubmodelElement("securityDefinitions");
    }
    public ISubmodelElementCollection getInterfaceMetadata() {
        return (ISubmodelElementCollection) smc.getSubmodelElement("InterfaceMetadata");
    }

    public ISubmodelElementCollection getPropertiesSmc() {
        return (ISubmodelElementCollection) this.getInterfaceMetadata().getSubmodelElement("Properties");
    }
    public ISubmodelElementCollection getProperty(String idShort) {
        return (ISubmodelElementCollection) getPropertiesSmc().getSubmodelElement(idShort);
    }
    public ISubmodelElementCollection getOperationsSmc() {
        return (ISubmodelElementCollection) this.getInterfaceMetadata().getSubmodelElement("Operations");
    }
    public ISubmodelElementCollection getEventsSmc() {
        return (ISubmodelElementCollection) this.getInterfaceMetadata().getSubmodelElement("Events");
    }
}
