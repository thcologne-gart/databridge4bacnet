package org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.basyx.aas.manager.ConnectedAssetAdministrationShellManager;
import org.eclipse.basyx.aas.metamodel.connected.ConnectedAssetAdministrationShell;
import org.eclipse.basyx.submodel.metamodel.api.identifier.IdentifierType;
import org.eclipse.basyx.submodel.metamodel.api.reference.IKey;
import org.eclipse.basyx.submodel.metamodel.api.reference.IReference;
import org.eclipse.basyx.submodel.metamodel.api.reference.enums.KeyElements;
import org.eclipse.basyx.submodel.metamodel.api.submodelelement.ISubmodelElement;
import org.eclipse.basyx.submodel.metamodel.connected.ConnectedSubmodel;
import org.eclipse.basyx.submodel.metamodel.connected.submodelelement.ConnectedSubmodelElementCollection;
import org.eclipse.basyx.submodel.metamodel.map.identifier.Identifier;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.EventRouteElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.IRouteElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.elements.TimerRouteElement;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.registry.AIDRegistry;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.annotations.FileFactory;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.assetInterfaceDescription.AssetInterfaceDescription;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class JsonFileFactory {
    private static Logger logger = LoggerFactory.getLogger(JsonFileFactory.class);

    private static final String DEFAULT_FILE_NAME = "jsonFile";
    protected static final String FILE_ENDING = ".json";
    protected JsonArray content;
    private String fileName;
    private String directory;


    public void loadContentFromExistingFile() {
        try {
            this.content = (JsonArray) readJsonFile(this.getDirectory() + this.getFileName());
        } catch (FileNotFoundException e) {
            this.content = new JsonArray();
            logger.warn("File '" + this.getDirectory() + this.getFileName() + "' does not excist.");
        }
    }

    public ISubmodelElement getReferencedElement(IReference reference, AIDRegistry registry) {
        ConnectedAssetAdministrationShellManager manager = new ConnectedAssetAdministrationShellManager(registry);

        ConnectedAssetAdministrationShell aas = null;
        ConnectedSubmodel submodel = null;
        ISubmodelElement currentElement = null;

        Identifier keyIdentifier;
        for (IKey key : reference.getKeys()) {
            if (key.getType().equals(KeyElements.ASSETADMINISTRATIONSHELL)) {
                keyIdentifier = new Identifier(IdentifierType.fromString(key.getIdType().toString()), key.getValue());
                aas = manager.retrieveAAS(keyIdentifier);
            } else if (key.getType().equals(KeyElements.SUBMODEL)) {
                if (aas == null) {
                    throw new RuntimeException("No AAS defined in Reference");
                }
                keyIdentifier = new Identifier(IdentifierType.fromString(key.getIdType().toString()), key.getValue());
                submodel = (ConnectedSubmodel) aas.getSubmodel(keyIdentifier);
            } else if (key.getType().equals(KeyElements.SUBMODELELEMENTCOLLECTION)) {
                if (currentElement == null) {
                    currentElement = submodel.getSubmodelElement(key.getValue());
                } else {
                    currentElement = ((ConnectedSubmodelElementCollection) currentElement).getSubmodelElement(key.getValue());
                }
            }
        }
        return currentElement;
    }
    public JsonFileFactory(String fileName, String directory) {
        this(fileName, new JsonArray(), directory);
    }

    public JsonFileFactory() {
        this(DEFAULT_FILE_NAME, new JsonArray(), System.getProperty("user.dir"));
    }

    public JsonFileFactory(String fileName, JsonArray content, String directory) {
        setFileName(fileName);
        setDirectory(directory);
        this.content = content;

    }
    protected IRouteElement buildRouteElement(String datasource, List<String> transformers, List<String> datasinks, JsonObject trigger, TimerConsumerFileFactory timerConsumerFileFactory) {
        if (trigger == null) {
            return new TimerRouteElement(datasource, transformers, datasinks, "timer1");
        } else if (trigger.get("trigger").getAsString().equals("timer")) {
            if (((JsonObject) trigger.get("triggerData")).has("period")) {
                String timerName = timerConsumerFileFactory.addTimer(((JsonObject) trigger.get("triggerData")).get("period").getAsInt());
                return new TimerRouteElement(datasource, transformers, datasinks, timerName);
            }
            throw new RuntimeException("Trigger of type 'timer' does not contain key 'period'");
        } else if (trigger.get("trigger").getAsString().equals("event")) {
            return new EventRouteElement(datasource, transformers, datasinks);
        } else if (trigger.get("trigger").getAsString().equals("request")) {
            throw new RuntimeException("trigger not implemented yet");
        }
        return new TimerRouteElement(datasource, transformers, datasinks, "timer1");
    }
    public String getFileEnding() {return this.FILE_ENDING;}

    public String getFileName() {return this.fileName;}
    public void setFileName(String fileName) {
        this.fileName = validateFileName(fileName);
    }
    public String getDirectory() {return this.directory;}

    public void setDirectory(String directory) {
        String separator = System.getProperty("file.separator");
        if (!directory.endsWith(separator)) {
            directory += separator;
        }
        this.directory = directory;

        File f = new File(directory);
        if (!f.isDirectory()) {
            logger.warn("Directory '" + directory + " does not excist. Create before writing file.");
        }
    }
    public JsonElement getContent() {return this.content;}
    private String validateFileName(String fileName) {
        if (fileName.endsWith(FILE_ENDING)) {
            return fileName;
        }
        return fileName + FILE_ENDING;
    }

    private String formatJsonString(String jsonString) {
        if (jsonString.contains("\n")) {
            logger.info("Returned input String because it contains newlines");
            return jsonString;
        }
        StringBuilder sb = new StringBuilder();
        String tabs = "";
        char c;
        for (int i = 0; i < jsonString.length(); i++) {
            c = jsonString.charAt(i);
            if (c == '[' || c == '{') {
                tabs += "\t";
                sb.append(c);
                if (jsonString.charAt(i+1) != ']' && jsonString.charAt(i+1) != '}') {
                    sb.append("\n").append(tabs);
                }

            } else if (c == ']' || c == '}') {
                tabs = tabs.substring(1);
                if (jsonString.charAt(i-1) != '[' && jsonString.charAt(i-1) != '{') {
                    sb.append("\n").append(tabs);
                }
                sb.append(c);
            } else if (c == ',') {
                sb.append(c).append("\n").append(tabs);
            } else if (c == ':' && jsonString.charAt(i-1) == '"') {
                sb.append(c).append(" ");
            }
            else {
                sb.append(c);
            }
        }
        return sb.toString();
    }


    public void createFile() throws IOException {
        createFile(false, false);
    }

    public void createFile(boolean formatJson, boolean override) throws IOException {
        String separator = System.getProperty("file.separator");
        String filePath = this.directory.endsWith(separator) ? this.directory + this.fileName : this.directory + separator + this.fileName;
        File f = new File(filePath);
        if(!override && f.exists() && !f.isDirectory()) {
            throw new IllegalArgumentException("Cannot create file '" + filePath + "' because it exists already");
        }
        FileWriter myWriter = new FileWriter(filePath);

        if (formatJson) {
            myWriter.write(formatJsonString(content.toString()));
        } else {
            myWriter.write(content.toString());
        }
        myWriter.close();
        logger.info("File '" + filePath + "' created");
    }

    public String readFile(String filePath) {
        String content = "";
        try {
            File myObj = new File(filePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                content += myReader.nextLine() + "\n";
            }
            myReader.close();

            if (!content.equals("")) {
                content = content.substring(0, content.length()-1);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.toString());
        }
        return content;
    }

    public static JsonElement readJsonFile (String filePath) throws FileNotFoundException {
        if (!filePath.endsWith(".json")) {
            throw new IllegalArgumentException("Path does not lead to a JSON-file");
        }
        Gson gson = new Gson();
        Reader reader = new FileReader(filePath);
        return gson.fromJson(reader, JsonElement.class);

    }


    public static String getDefaultFileName() {return DEFAULT_FILE_NAME;}

}