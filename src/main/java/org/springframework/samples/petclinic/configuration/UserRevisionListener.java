package org.springframework.samples.petclinic.configuration;

import org.hibernate.envers.RevisionListener;
import org.springframework.samples.petclinic.model.UserRevEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserRevisionListener implements RevisionListener {
    @Override
	public void newRevision(Object revisionEntity) {
	UserRevEntity revision = (UserRevEntity) revisionEntity;
	String username = "";
	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	Object principal = auth.getPrincipal();
	if (principal != null && principal instanceof UserDetails)
		username = ((UserDetails) principal).getUsername();
	else if (principal != null)
		username = principal.toString();
	revision.setUsername(username);
}

}
