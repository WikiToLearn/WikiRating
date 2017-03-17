/**
 * 
 */
package org.wikitolearn.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author alessandro
 *
 */
@RestController
public class MaintenanceController {

	private static final Logger LOG = LoggerFactory.getLogger(MaintenanceController.class);

	@RequestMapping(value = "${maintenance.uri}", method = RequestMethod.POST, produces = "application/json")
	@Secured("ROLE_ADMIN")
	public String maintenanceMode(@RequestParam(value = "active") String active) {
		JSONObject response = new JSONObject();
		int mode = Integer.parseInt(active);

		try {
			if (mode == 0) {
				response.put("status", "success");
				response.put("message", "Application is live now.");
				File f = new File("maintenance.lock");
				if (f.exists()) {
					f.delete();
					LOG.info("Deleted maintenance lock file.");
				}
				LOG.info("Application is live now.");
			} else if (mode == 1) {
				try {
					response.put("status", "success");
					response.put("message", "Application is in maintenance mode now.");
					Properties props = new Properties();
					props.setProperty("maintenance.active", "true");
					;
					File lockFile = new File("maintenance.lock");
					OutputStream out = new FileOutputStream(lockFile);
					DefaultPropertiesPersister p = new DefaultPropertiesPersister();
					p.store(props, out, "Maintenance mode lock file");
					LOG.info("Application is in maintenance mode now.");
				} catch (Exception e) {
					LOG.error("Something went wrong.", e.getMessage());
				}

			} else {
				response.put("status", "error");
				response.put("message", "Parameter value not supported");
			}
		} catch (JSONException e) {
			LOG.error("Something went wrong using JSON API.", e.getMessage());
		}
		return response.toString();
	}
}
