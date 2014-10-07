//  
//  UpdateEmailTemplateRequest.java
//  reflection.io
//
//  Created by William Shakour on October 6, 2014.
//  Copyrights © 2014 SPACEHOPPER STUDIOS LTD. All rights reserved.
//  Copyrights © 2014 reflection.io. All rights reserved.
//
package io.reflection.app.api.admin.shared.call;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import io.reflection.app.api.shared.datatypes.Request;
import io.reflection.app.datatypes.shared.EmailTemplate;

public class UpdateEmailTemplateRequest extends Request {
	public EmailTemplate emailTemplate;

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		JsonElement jsonEmailTemplate = emailTemplate == null ? JsonNull.INSTANCE : emailTemplate.toJson();
		object.add("emailTemplate", jsonEmailTemplate);
		return object;
	}

	@Override
	public void fromJson(JsonObject jsonObject) {
		super.fromJson(jsonObject);
		if (jsonObject.has("emailTemplate")) {
			JsonElement jsonEmailTemplate = jsonObject.get("emailTemplate");
			if (jsonEmailTemplate != null) {
				emailTemplate = new EmailTemplate();
				emailTemplate.fromJson(jsonEmailTemplate.getAsJsonObject());
			}
		}
	}
}