/**
 * 
 */
package org.wikitolearn.wikirating.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.wikitolearn.wikirating.exception.UpdateGraphException;
import org.wikitolearn.wikirating.model.api.ApiResponse;
import org.wikitolearn.wikirating.model.api.ApiResponseError;
import org.wikitolearn.wikirating.model.api.ApiResponseFail;
import org.wikitolearn.wikirating.model.api.ApiResponseSuccess;
import org.wikitolearn.wikirating.service.MaintenanceService;

/**
 * 
 * @author aletundo
 *
 */
@RestController
public class MaintenanceController {

	private static final Logger LOG = LoggerFactory.getLogger(MaintenanceController.class);

	@Autowired
	private MaintenanceService maintenanceService;

	/**
	 * Secured endpoint to enable or disable read only mode. When read only mode
	 * is enabled only GET requests are allowed. To change this behavior @see
	 * org.wikitolearn.wikirating.wikirating.filter.MaintenanceFilter.
	 * 
	 * @param active
	 *            String requested parameter that toggle the re. Binary values
	 *            are accepted.
	 * @return a JSON object with the response
	 */
	@RequestMapping(value = "${maintenance.readonlymode.uri}", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	@ResponseStatus(HttpStatus.ACCEPTED)
	public ApiResponse toggleReadOnlyMode(@RequestParam(value = "active") String active) {
		int mode = Integer.parseInt(active);
		ApiResponse body;
		if (mode == 0) {
			// Delete maintenance lock file if it exists
			File f = new File("maintenance.lock");
			if (f.exists()) {
				f.delete();
				LOG.debug("Deleted maintenance lock file.");
			}
			LOG.info("Application is live now.");
			body = new ApiResponseSuccess("Application is live now", Instant.now().toEpochMilli());
		} else if (mode == 1) {
			try {
				// Create maintenance lock file with a maintenance.active
				// property set to true
				Properties props = new Properties();
				props.setProperty("maintenance.active", "true");
				File lockFile = new File("maintenance.lock");
				OutputStream out = new FileOutputStream(lockFile);
				DefaultPropertiesPersister p = new DefaultPropertiesPersister();
				p.store(props, out, "Maintenance mode lock file");

				LOG.debug("Created maintenance lock file.");
				LOG.info("Application is in maintenance mode now.");
				body = new ApiResponseSuccess("Application is in maintenance mode now", Instant.now().toEpochMilli());
			} catch (Exception e) {
				LOG.error("Something went wrong. {}", e.getMessage());
				Map<String, Object> data = new HashMap<>();
				data.put("error", HttpStatus.NOT_FOUND.name());
				data.put("exception", e.getClass().getCanonicalName());
				data.put("stacktrace", e.getStackTrace());
				
				return new ApiResponseError(data, e.getMessage(), HttpStatus.NOT_FOUND.value(), Instant.now().toEpochMilli());
			}

		} else {
			// The active parameter value is not supported
			body = new ApiResponseFail("Parameter value not supported", Instant.now().toEpochMilli());
		}
		return body;
	}

	/**
	 * Secured endpoint that handles initialization request for the given
	 * language
	 *
	 * @return true if the initialization is completed without errors, false
	 *         otherwise
	 */
	@RequestMapping(value = "${maintenance.init.uri}", method = RequestMethod.POST, produces = "application/json")
	public boolean initialize() {
	    return maintenanceService.initializeGraph();
	}

	@RequestMapping(value = "${maintenance.fetch.uri}", method = RequestMethod.POST, produces = "application/json")
	public boolean fetch() {
	    try {
            return maintenanceService.updateGraph();
        }catch (UpdateGraphException e){
	        return false;
        }

	}

	/**
	 * 
	 */
	@RequestMapping(value = "${maintenance.wipe.uri}", method = RequestMethod.DELETE, produces = "application/json")
	public void wipeDatabase() {
		// TODO
	}
}
