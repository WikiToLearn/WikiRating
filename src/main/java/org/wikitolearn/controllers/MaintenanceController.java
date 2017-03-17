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
	
	/**
	 * Secured endpoint to enable or disable maintenance mode. When maintenance mode is enabled only GET requests are
	 * allowed. To change this behavior see org.wikitolearn.filter.MaintenanceFilter class.
	 * @param active String Requested parameter that indicates the maintenance mode to set. Binary values are accepted.
	 * @return response String The JSONObject casted to String with the response
	 */
	@RequestMapping(value = "${maintenance.uri}", method = RequestMethod.POST, produces = "application/json")
	public String maintenanceMode(@RequestParam(value = "active") String active) {
		JSONObject response = new JSONObject();
		int mode = Integer.parseInt(active);

		try {
			if (mode == 0) {
				response.put("status", "success");
				response.put("message", "Application is live now.");
				// Delete maintenance lock file if it exists
				File f = new File("maintenance.lock");
				if (f.exists()) {
					f.delete();
					LOG.debug("Deleted maintenance lock file.");
				}
				LOG.info("Application is live now.");
			} else if (mode == 1) {
				try {
					response.put("status", "success");
					response.put("message", "Application is in maintenance mode now.");
					// Create maintenance lock file with a maintenance.active property setted to true
					Properties props = new Properties();
					props.setProperty("maintenance.active", "true");
					File lockFile = new File("maintenance.lock");
					OutputStream out = new FileOutputStream(lockFile);
					DefaultPropertiesPersister p = new DefaultPropertiesPersister();
					p.store(props, out, "Maintenance mode lock file");
					
					LOG.debug("Created maintenance lock file.");
					LOG.info("Application is in maintenance mode now.");
				} catch (Exception e) {
					LOG.error("Something went wrong.", e.getMessage());
				}

			} else {
				// The active parameter value is not supported
				response.put("status", "error");
				response.put("message", "Parameter value not supported");
			}
		} catch (JSONException e) {
			LOG.error("Something went wrong using JSON API.", e.getMessage());
		}
		return response.toString();
	}
}
