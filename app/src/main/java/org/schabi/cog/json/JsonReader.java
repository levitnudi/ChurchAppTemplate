package org.schabi.cog.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.schabi.cog.bean.Profile;

import us.shandian.giga.util.TagName;

public class JsonReader {

	public static List<Profile> getHome(JSONObject jsonObject)
			throws JSONException {
		List<Profile> profiles = new ArrayList<Profile>();

		JSONArray jsonArray = jsonObject.getJSONArray(TagName.TAG_PRODUCTS);
		Profile profile;
		for (int i = 0; i < jsonArray.length(); i++) {
			profile = new Profile();
			JSONObject productObj = jsonArray.getJSONObject(i);
			profile.setId(productObj.getInt(TagName.KEY_ID));
			profile.setName(productObj.getString(TagName.KEY_NAME));
			profile.setImageUrl(productObj.getString(TagName.KEY_IMAGE_URL));

			profiles.add(profile);
		}
		return profiles;
	}
}
