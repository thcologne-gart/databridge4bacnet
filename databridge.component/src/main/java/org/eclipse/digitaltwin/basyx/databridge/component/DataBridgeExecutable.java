/*******************************************************************************
 * Copyright (C) 2023 the Eclipse BaSyx Authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * SPDX-License-Identifier: MIT
 ******************************************************************************/
package org.eclipse.digitaltwin.basyx.databridge.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.eclipse.digitaltwin.basyx.databridge.bacnet.ConfigManager;
import org.eclipse.digitaltwin.basyx.databridge.core.component.DataBridgeComponent;
import org.eclipse.digitaltwin.basyx.databridge.core.configuration.route.core.RoutesConfiguration;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.BacnetCommunicator;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.bacnet.RemoteDeviceHandler;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.FileBuilder;
import org.eclipse.digitaltwin.basyx.databridge.bacnet.configFactory.JsonFileFactory;

import java.io.FileNotFoundException;

public class DataBridgeExecutable {

	private static final String DEFAULT_CONFIG_PATH = System.getProperty("user.dir") + System.getProperty("file.separator") + "config";
	private static DataBridgeComponent dataBridgeComponent;


	public static void main(String[] args) throws Exception {
		String configPath = getConfigPath(args);

		ConfigManager.init(configPath);
		FileBuilder.assureFilesExist(configPath);


		JsonArray devices = getDeviceIdsFromJsonFile(configPath);
		BacnetCommunicator.start();

		if (devices.size() != 0) {
			RemoteDeviceHandler.searchDevices(BacnetCommunicator.getLocalDevice(), devices);
		}
		System.out.println(configPath);
		RoutesConfigurationLoader routesConfigurationLoader = new RoutesConfigurationLoader(configPath);

		RoutesConfiguration config = routesConfigurationLoader.create();

		dataBridgeComponent = new DataBridgeComponent(config);
		dataBridgeComponent.startComponent();
	}

	private static String getConfigPath(String[] args) {
		if (args.length == 0) {
			return DEFAULT_CONFIG_PATH;
		} else {
			return args[0];
		}
	}

	private static JsonArray getDeviceIdsFromJsonFile(String configPath) {
		JsonArray deviceIds = new JsonArray();
		JsonArray endpoints;
		try {
			endpoints = (JsonArray) JsonFileFactory.readJsonFile(configPath + "/bacnetconsumer.json");
		} catch (FileNotFoundException e) {
			return deviceIds;
		}

		JsonElement deviceId;
		for (JsonElement endpoint : endpoints) {
			deviceId = ((JsonObject) endpoint).get("deviceId");
			if (!deviceIds.contains(deviceId)) {
				deviceIds.add(deviceId);
			}
		}
		return deviceIds;
	}

	private static boolean buildRoutesWanted(String configPath) {
		JsonObject configs;
		try {
			configs = (JsonObject) JsonFileFactory.readJsonFile(configPath + "/databridgeConfig.json");
		} catch (FileNotFoundException e) {
			return false;
		}

		if (configs.has("buildRoutes")) {
			return configs.get("buildRoutes").getAsBoolean();
		} else {
			// default value in case "buildRoutes" was not provided
			return false;
		}

	}

	private static boolean registryProvided(String configPath) {
		JsonObject configs;
		try {
			configs = (JsonObject) JsonFileFactory.readJsonFile(configPath + "/databridgeConfig.json");
		} catch (FileNotFoundException e) {
			return false;
		}
		return configs.has("urlRegistry");
	}

	public static DataBridgeComponent getDataBridgeComponent() {
		return dataBridgeComponent;
	}
}
