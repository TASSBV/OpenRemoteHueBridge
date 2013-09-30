/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.action;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openremote.controller.protocol.huebridge.HueBridgeCommandBuilder;
import org.openremote.controller.utils.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;


/**
 *     {@link BridgeFinder} is used to find active Hue Bridges in the local network.
 *     It can return a list of {@link Bridge}. It is also used to authenticate with a bridge.
 *      @author TASS Technology Solutions - www.tass.nl
 */
public class BridgeFinder {

    private static String BRIDGEFILE= "../webapps/controller/WEB-INF/classes/huebridge.json";

    private static Logger logger = Logger.getLogger(HueBridgeCommandBuilder.HUEBRIDGE_PROTOCOL_LOG_CATEGORY);

    /**
     * @return an ArrayList of {@link Bridge} found in the network
     */
    public ArrayList<Bridge> getBridges() throws IOException, JSONException {
        String json = discoverLocalBridge();
        return convertJsonToBridge(json);
    }

    /**
     * Call to the meethue website to get all local Hue bridges in the network
     * @return JSON string with information about the detected Hue bridges
     */
    private String discoverLocalBridge() throws IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpUriRequest request = new HttpGet("http://www.meethue.com/api/nupnp");
        String resp = null;
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        resp = client.execute(request, responseHandler);
        return resp;
    }

    /**
     * Create objects of the data from discoverLocalBridge()
     * @param json, the json string with all local brides from {@link #discoverLocalBridge()}
     * @return Arraylist of {@link Bridge} objects
     */
    private ArrayList<Bridge> convertJsonToBridge(String json) throws JSONException {
        ArrayList<Bridge> bridges = new ArrayList<Bridge>();
        JSONArray arr = new JSONArray(json);
        for (int i = 0; i < arr.length(); i++) {
            JSONObject jsonBridge = arr.getJSONObject(i);
            bridges.add(new Bridge(
                    jsonBridge.getString("id"),
                    jsonBridge.getString("internalipaddress"),
                    jsonBridge.getString("macaddress"))
            );
        }
        return bridges;
    }

    /**
     * Start authenticating with the chosen Hue Bridge, and save it to disk if everything is ok
     * @param id Hue Bridge ID
     * @param ip Internal ip address of the local Hue Bridge
     * @param mac Mac address of the local Hue Bridge
     */
    public String setChosenBridge(String id, String ip, String mac) throws MalformedURLException, UnsupportedEncodingException, JSONException, URISyntaxException {
        Bridge chosenBridge = new Bridge(id, ip, mac);

        String username = authWithBridge(ip);
        if (username != "") {
            chosenBridge.setUsername(username);
            saveUsernameToDisk(chosenBridge);
        } else {
            logger.info("Username is empty, not saving to disk");
        }
        return username;
    }

    /** Save the connected bridge to disk
     * @param bridge, {@link Bridge} object that needs to be saved
     */
    private void saveUsernameToDisk(Bridge bridge) throws JSONException {
        try {

            String content = bridge.bridgeToJson().toString();
            File file = new File(BRIDGEFILE);

            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
            System.out.println("Bridge saved to Disk");

        } catch (IOException e) {
            logger.error("IOException when saving hue bridge info to disk", e);
        }
    }

    /**
     * Authenticate with the chosen bridge
     * @param ip, the ip address of the bridge you want to authenticate with
     * @return the username of the hue bridge, that has been placed on the white list
     */
    private String authWithBridge(String ip) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {

        URI uri = new URL("http://" + ip + "/api").toURI();

        DefaultHttpClient client = new DefaultHttpClient();
        HttpUriRequest request = new HttpPost(uri);

        JSONObject obj = new JSONObject();
        try {
            obj.put("devicetype", "OpenRemoteController");
        } catch (JSONException e) {
            logger.error("JSONExceptionn when creating json object", e);
        }

        StringEntity data = new StringEntity(obj.toString(), "UTF-8");

        ((HttpPost) request).setEntity(data);
        String resp = "";
        HttpResponse response = null;
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        request.addHeader("User-Agent", "OpenRemoteController");
        request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);

        request.addHeader("Content-Type", "applicaion/json");

        try {
            response = client.execute(request);
            resp = responseHandler.handleResponse(response);
        } catch (ClientProtocolException e) {
            logger.error("ClientProtocolException when executing HTTP method", e);
        } catch (IOException e) {
            logger.error("IOException when executing HTTP method", e);
        } finally {
            try {
                if ((response != null) && (response.getEntity() != null)) {
                    response.getEntity().consumeContent();
                }
            } catch (IOException ignored) {
            }
            client.getConnectionManager().shutdown();
        }

        JSONObject jsonResponse = null;
        String jsonUsername = "";
        try {
            jsonResponse = new JSONArray(resp.toString()).getJSONObject(0).getJSONObject("success");
            jsonUsername = jsonResponse.getString("username");
        } catch (JSONException e) {
            logger.error("JSONException when reading returned json", e);
        }
        return jsonUsername;
    }

    public Bridge getSavedBridge(){
        Bridge bridge;
        File file = new File(BRIDGEFILE);
        StringBuilder json = new StringBuilder();
        try {
        if (!file.exists()) {
            return null;
        }
        FileReader fr = new FileReader(file.getAbsolutePath());
        BufferedReader br = new BufferedReader(fr);
        String currentLine = null;

            while ((currentLine = br.readLine()) != null) {
                json.append(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        try {
            JSONObject jo = new JSONObject(json.toString()) ;
            bridge = new Bridge(jo.getString("id"),jo.getString("internalip"),jo.getString("macaddres"));
            bridge.setUsername(jo.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
           return bridge;



    }


}
