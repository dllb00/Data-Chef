package de.areto.datachef.web_app.handler;

import de.areto.datachef.web_app.common.RouteHandler;
import de.areto.datachef.web_app.common.WebRoute;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

@WebRoute(path = "/upload", requestType = HttpMethod.post)
@Slf4j
public class UploadFileHandler extends RouteHandler {

	File uploadDir = new File("upload");

	@Override
	public Object doWork(Request request, Response response) {

		try {
			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

			InputStream input = request.raw().getPart("uploaded_file").getInputStream(); 
			String targetFileName = getFileName(request.raw().getPart("uploaded_file"));
			Files.copy(input, Paths.get(uploadDir.toPath().toString(), targetFileName), StandardCopyOption.REPLACE_EXISTING);

			return "";
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static String getFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}
}
