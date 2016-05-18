package com.jboss.devstudio.core.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.izforge.izpack.installer.Unpacker;
import com.jboss.devstudio.core.installer.bean.P2IU;

import junit.framework.TestCase;

public class GsonSerializationTest extends TestCase {

	public void testIUsSerialization() {
		// TODO Auto-generated method stub
		Gson gs = new Gson();
		Collection<P2IU> col = new ArrayList<P2IU>();
		col.addAll(Arrays.asList(new P2IU[] { 
				new P2IU("id1", "label1", "description1", "devstudio"),
				new P2IU("id1", "label1", "description1", "devstudio"),
				new P2IU("id1", "label1", "description1", "devstudio") }));
		System.out.println(gs.toJson(col));
	}
	
	public void testIULoadFromFile() throws FileNotFoundException {
		
		InputStream features = new FileInputStream(new File(CommonTestData.PROJECT_ROOT,"src/config/resources/DevstudioFeaturesSpec.json"));
		JsonParser parser = new JsonParser();
		JsonArray array = parser.parse(new InputStreamReader(features)).getAsJsonArray();
		Gson gson = new Gson();
		String jarLocation = P2DirectorStarterListener.findPathJar(Unpacker.class);
		ArrayList<P2IU> ius = new ArrayList<P2IU>();
		for (JsonElement jsonElement : array) {
			P2IU iu = gson.fromJson(jsonElement, P2IU.class);
			if(!iu.getPath().startsWith("http://") && !iu.getPath().startsWith("https://")) {
				iu.setPath("jar:file://" + jarLocation + "!/" + iu.getPath());
			} 
			ius.add(iu);
		}
	}
}
