/*******************************************************************************
 *
 *  Copyright 2011 - Sardegna Ricerche, Distretto ICT, Pula, Italy
 *
 * Licensed under the EUPL, Version 1.1.
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *  http://www.osor.eu/eupl
 *
 * Unless required by applicable law or agreed to in  writing, software distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations under the Licence.
 * In case of controversy the competent court is the Court of Cagliari (Italy).
 *******************************************************************************/
package action;

import java.util.Map;

import model.Structure;
import model.User;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.beans.factory.annotation.Autowired;

import service.StructureService;

import com.opensymphony.xwork2.ActionSupport;

@ParentPackage(value = "default")
public class HomeAction extends ActionSupport implements SessionAware {
	private Map<String, Object> session = null;
	@Autowired
	private StructureService structureService = null;

	private String structureId;

	@Actions(value = { @Action(value = "/home", results = { @Result(name = "logged", location = "/WEB-INF/jsp/homeLogged.jsp"),
			@Result(name = "notLogged", location = "/WEB-INF/jsp/homeNotLogged.jsp") }) })
	public String execute() {
		String ret = "notLogged";

		User user = (User) this.getSession().get("user");
		if (user != null) {
//			if (structureId != null) {
//				Structure structure = this.getStructureService().findStructureById(new Integer(structureId));
//				if (structure == null) {
//					this.getSession().put("user", null);
//				} else {
//					user.setStructure(structure);
//					this.getSession().put("user", user);
//				}
//			}

			ret = "logged";
		}
		return ret;
	}

	@Actions(value = { @Action(value = "/goLogin", results = { @Result(name = "logged", location = "/WEB-INF/jsp/homeLogged.jsp"),
			@Result(name = "notLogged", location = "/WEB-INF/jsp/login.jsp") }) })
	public String goLogin() {
		String ret = "notLogged";

		if (this.getSession().get("user") != null) {
			ret = "logged";
		}
		return ret;
	}

	public Map<String, Object> getSession() {
		return session;
	}

	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

	public StructureService getStructureService() {
		return structureService;
	}

	public void setStructureService(StructureService structureService) {
		this.structureService = structureService;
	}

	public String getStructureId() {
		return structureId;
	}

	public void setStructureId(String structureId) {
		this.structureId = structureId;
	}
}