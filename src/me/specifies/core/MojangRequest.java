package me.specifies.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MojangRequest {
	
	/**
	 * @author Specifies
	 */
	
	private JSONParser parser = new JSONParser();
	
	/**
	 * 
	 * @param username User to make the GET request with.
	 * @return JSONID User's UniqueID.
	 * @throws Exception
	 */
	public String getUUID(String username) throws Exception{
		
		URL requestSite = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		//Request properties
		con.setRequestMethod("GET");
		con.setRequestProperty("Content-Type", "aplication/json");
	
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The getUUID request returned the response code of: " + responseCode + "\n");
		
		//Return Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		Object getId = parser.parse(response.toString());
		JSONObject getIdJson = (JSONObject) getId;
		return (String) getIdJson.get("id");
		
	}
	/**
	 * @param UniqueID Player's UniqueID
	 * @return response A json string that can then be parsed for its data
	 * @throws Exception
	 */
	public String nameHistory(String UniqueID) throws Exception{
		//Strip the UUID from any dashes, as Mojang's API requires
		String uuid = stripUUID(UniqueID);
		
		URL requestSite = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		//Request properties
		con.setRequestMethod("GET");
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The nameHistory request returned the response code of: " + responseCode + "\n");
		
		//Return Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		while((inputline = in.readLine()) !=null) {
			response.append(inputline);
		}
		in.close();
		
		return response.toString();
	}
	/**
	 * @param UniqueID Player's UniqueID
	 * @return skinURL Link to the Player's skin
	 * @throws Exception
	 */
	public String getSkin(String UniqueID) throws Exception{
		String uuid = stripUUID(UniqueID);
		
		URL requestSite = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		//Request properties
		con.setRequestMethod("GET");
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The getSkin request returned the response code of " + responseCode + "\n");
		if(responseCode == 429) {
			System.out.print("Mojang's session servers have a rate limit on requests, you may only request the same profile once per minute. \n");
		}
		
		//Return Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		while((inputline = in.readLine()) !=null) {
			response.append(inputline);
		}
		in.close();
		
		//Get Base64 values to decode
		Object getToDecode = parser.parse(response.toString());
		JSONObject grabProperties = (JSONObject) getToDecode;
		JSONArray getValue = (JSONArray) grabProperties.get("properties");
		Object getValArray = (Object) getValue.get(0);
		JSONObject finalObject = (JSONObject) getValArray;
		String toDecode = (String) finalObject.get("value");
		
		//Decode
		String decodeValues = decodeBase64(toDecode);
		Object getSkin = parser.parse(decodeValues);
		JSONObject grabTextures = (JSONObject) getSkin;
		
		//Convert the textures JsonString into an object we can parse, again.
		Object textureValues = (Object) grabTextures.get("textures");
		JSONObject grabSkinProperties = (JSONObject) textureValues;
		Object textureValues2 = (Object) grabSkinProperties.get("SKIN");
		JSONObject getURL = (JSONObject) textureValues2;
	
		return (String) getURL.get("url");
	}
	/**
	 * @return ArrayList List of hashes of servers Mojang has blocked due to violation of EULA
	 * @throws Exception
	 */
	public ArrayList<String> getBlockedServerHashes() throws Exception{
		
		URL requestSite = new URL("https://sessionserver.mojang.com/blockedservers");
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		//Request properties
		con.setRequestMethod("GET");
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The getBlockedServerHashes request returned the response code of: " + responseCode + "\n"); 
		
		//Read Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		while((inputline = in.readLine()) !=null) {
			response.append(inputline);
		}
		in.close();
		
		String toFormat = response.toString();
		int codeToBreak = 0;
		String createString = "";
		ArrayList<String> appendHashes = new ArrayList<String>();
		for (int i = 0; i < toFormat.length(); i++){
		    char c = toFormat.charAt(i);
		    createString = createString + c;
		    codeToBreak = codeToBreak + 1;
		    //They're 40 character hashes, so we'll append when it reaches 40
		    if(codeToBreak == 40) {
		    	codeToBreak = 0;
		    	appendHashes.add(createString);
		    	createString = "";
		    }
		}
		
		return appendHashes;
	}
	/**
	 * @param userName Username to reference
	 * @param timestamp Timestamp to which it was owned or changed
	 * @return response A json string that can then be parsed for data
	 * @throws Exception
	 */
	public String getOriginalUser(String userName, String timestamp) throws Exception{
		//https://api.mojang.com/users/profiles/minecraft/<username>?at=<timestamp>
		URL requestSite = new URL("https://api.mojang.com/users/profiles/minecraft/" + userName + "?at=" + timestamp);
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		System.out.println("The getOriginalUser method will return a JSON string with information on the original user of the name you input, but it only works if there was a name change. \n");
		
		//Request properties
		con.setRequestMethod("GET");
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The getOriginalUser request returned the response code of: " + responseCode + "\n"); 
		
		//Read Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		while((inputline = in.readLine()) !=null) {
			response.append(inputline);
		}
		in.close();
		return response.toString();
	}
	/**
	 * @param query Get the status of a specific service
	 * @return output Status of various Mojang services GREEN(no issues), YELLOW(some issues), RED (service unavailable)
	 * @throws Exception
	 */
	public String getStatus(String query) throws Exception {
		System.out.print(query + "\n");
		String queryToLower = query.toLowerCase();
		System.out.print("Return status of various Mojang services. Queries: minecraft, session, account, auth, skins, authserver, sessionserver, api, textures, mojang \n");
		
		URL requestSite = new URL("https://status.mojang.com/check");
		HttpURLConnection con = (HttpURLConnection) requestSite.openConnection();
		
		//Request properties
		con.setRequestMethod("GET");
		
		//Response code
		int responseCode = con.getResponseCode();
		System.out.print("The getStatus request returned the response code of: " + responseCode + "\n");
		
		//Read Data
		//Read Data
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputline;
		StringBuffer response = new StringBuffer();
		while((inputline = in.readLine()) !=null) {
			response.append(inputline);
		}
		in.close();
		
		Object obj = parser.parse(response.toString());
		JSONArray array = (JSONArray) obj;

		String output = "";
		switch(queryToLower) {
		case "minecraft":
			  Object index = array.get(0);
			  JSONObject toParse = (JSONObject) index;
			  output = (String) toParse.get("minecraft.net");
			  System.out.print("The status of the service minecraft.net is: " + output);
		case "session":
			Object index1 = array.get(1);
			JSONObject toParse1 = (JSONObject) index1;
			output = (String) toParse1.get("session.minecraft.net");
			System.out.print("The status of the service session.minecraft.net is: " + output);
		case "account":
			Object index2 = array.get(2);
			JSONObject toParse2 = (JSONObject) index2;
			output = (String) toParse2.get("account.mojang.com");
			System.out.print("The status of the service account.mojang.com is: " + output);
		case "authserver":
			Object index3 = array.get(3);
			JSONObject toParse3 = (JSONObject) index3;
			output = (String) toParse3.get("authserver.mojang.com");
			System.out.print("The status of the service authserver.mojang.com is: " + output);
		case "sessionserver":
			Object index4 = array.get(4);
			JSONObject toParse4 = (JSONObject) index4;
			output = (String) toParse4.get("sessionserver.mojang.com");
			System.out.print("The status of the service sessionserver.mojang.com is: " + output);
		case "api":
			Object index5 = array.get(5);
			JSONObject toParse5 = (JSONObject) index5;
			output = (String) toParse5.get("api.mojang.com");
			System.out.print("The status of the service api.mojang.com is: " + output);
		case "textures":
			Object index6 = array.get(6);
			JSONObject toParse6 = (JSONObject) index6;
			output = (String) toParse6.get("textures.minecraft.net");
			System.out.print("The status of the service textures.minecraft.net is: " + output);
		case "mojang":
			Object index7 = array.get(7);
			JSONObject toParse7 = (JSONObject) index7;
			output = (String) toParse7.get("mojang.com");
			System.out.print("The status of the service mojang.com is: " + output);
		}
		
		return output;
	}
	
	/**
	 * @param toDecode Base64 charset to decode
	 * @return output Decoded Base64 string
	 */
	private String decodeBase64(String toDecode) {
		byte[] decodedString = Base64.getMimeDecoder().decode(toDecode);
		String output = new String(decodedString);
		return output;
	}
	/**
	 * StripUUID is to strip a player's UUID of '-', because Mojang's API requires it to be that way.
	 * @param UniqueID UniqueID to strip
	 * @return id UniqueID stripped of '-'
	 */
	private String stripUUID(String UniqueID) {
		return UniqueID.replace("-", "");
	}
	
}
