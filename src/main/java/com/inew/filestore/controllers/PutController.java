package com.inew.filestore.controllers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Tibor Kulcsar
 * <p>
 * Date: 4/10/2018
 * @since
 */
@Controller
@RequestMapping(value = "/put/**/*", method = RequestMethod.PUT)
public class PutController
{
	/*private final Log log = LogFactory.getLog(getClass());

	@Autowired
	private UrlParser urlParser;

	@RequestMapping
	public void put(HttpServletRequest request, HttpServletResponse response) {

		try {
			InputStream inputStream = request.getInputStream();

			if (inputStream != null) {
				String filePath = urlParser.getFilePath(request.getRequestURI());
				FileOutputStream outputStream = new FileOutputStream(new File(filePath));

				byte[] buffer = new byte[1024];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.flush();

				log.info("Put file " + filePath);
			}
		} catch (FileNotFoundException e) {
			log.error(e.toString(), e);
		} catch (IOException e) {
			log.error(e.toString(), e);
		} catch (UrlParseException e) {
			log.error(e.toString(), e);
		}
	}*/
}
