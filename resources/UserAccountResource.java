package resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import model.User;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import service.UserService;
import utils.LocandaConstants;

@Path("/userAccount/")
@Component
@Scope("prototype")
public class UserAccountResource {
	@Autowired
	private UserService userService = null;
	@Autowired
	private SolrServer solrServerUser = null;

	@PostConstruct
	public void init() {
		List<User> users = null;

		users = this.getUserService().findAll();
		try {
			this.getSolrServerUser().addBeans(users);
			this.getSolrServerUser().commit();
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@GET
	@Path("/search/{start}/{rows}")
	@Produces({ MediaType.APPLICATION_JSON })
	public List<User> search(@PathParam("start") Integer start, @PathParam("rows") Integer rows, @QueryParam("term") String term) {
		List<User> users = new ArrayList<User>();
		users.add((User) RequestContextHolder.currentRequestAttributes().getAttribute("user", RequestAttributes.SCOPE_SESSION));
		return users;
	}

	@PUT
	@Path("{id}")
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public User update(User user) {
		// Validate info
		boolean isValid = true;
		if (user.getEmail().length() > LocandaConstants.STRING_MAX_LENGTH) {
			isValid = false;
		}
		if (user.getName().length() > LocandaConstants.STRING_MAX_LENGTH) {
			isValid = false;
		}
		if (user.getPhone().length() > LocandaConstants.STRING_MAX_LENGTH) {
			isValid = false;
		}
		if (user.getSurname().length() > LocandaConstants.STRING_MAX_LENGTH) {
			isValid = false;
		}
		if (user.getPassword().length() > LocandaConstants.STRING_MAX_LENGTH) {
			isValid = false;
		}
		if (!isValid) {
			return null;
		}

		// Find user by email
		User uDB = this.getUserService().findUserByEmail(user.getEmail());

		if (uDB != null) {
			this.getUserService().updateUser(user);
		}

		try {
			this.getSolrServerUser().addBean(user);
			this.getSolrServerUser().commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

		return user;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public SolrServer getSolrServerUser() {
		return solrServerUser;
	}

	public void setSolrServerUser(SolrServer solrServerUser) {
		this.solrServerUser = solrServerUser;
	}

}