package de.areto.datachef.web_app.handler;

import de.areto.datachef.config.SinkConfig;
import de.areto.datachef.web_app.common.RouteHandler;
import de.areto.datachef.web_app.common.WebRoute;
import lombok.extern.slf4j.Slf4j;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;

import org.aeonbits.owner.ConfigCache;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

@WebRoute(path = "/upload", requestType = HttpMethod.post)
@Slf4j
public class UploadFileHandler extends RouteHandler {

	private final SinkConfig sinkConfig = ConfigCache.getOrCreate(SinkConfig.class);

	File uploadDir = new File(sinkConfig.uploadPath().replace("./", ""));

	@Override
	public Object doWork(Request request, Response response) {

		try {
			request.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));

			InputStream input = request.raw().getPart("uploaded_file").getInputStream();
			String targetFileName = getFileName(request.raw().getPart("uploaded_file"));
			boolean isCsv = targetFileName.toLowerCase().contains(".csv");
			boolean isExcel = targetFileName.toLowerCase().contains(".xl");

			String selectedMapping = request.queryParams("selected_mapping");

			log.debug("targetFileName: " + targetFileName + " / selectedMapping: " + selectedMapping);
			LocalDateTime localDate = LocalDateTime.now();
			String localDateString = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(localDate);
			log.debug("localDateString: " + localDateString);
			
			if (isCsv) {
				targetFileName = selectedMapping + "." + localDateString + ".csv";
				log.debug("new targetFileName: " + targetFileName + " / selectedMapping: " + selectedMapping);
			}

			if (isExcel) {
				try {
					DecimalFormat decimalFormat = new DecimalFormat("0.#####################");
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					log.debug("excel case: " + targetFileName);
					Workbook wb = WorkbookFactory.create(input);
					for (int i = 0; i < wb.getNumberOfSheets(); i++) {
						log.debug("worksheet: " + wb.getSheetAt(i).getSheetName());
						targetFileName = selectedMapping + "." + i + "." + localDateString + ".csv";
						log.debug("new targetFileName: " + targetFileName + " / selectedMapping: " + selectedMapping);
						File outfile = Paths.get(uploadDir.toPath().toString(), targetFileName).toFile();
						BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
						
						Sheet sheet = wb.getSheetAt(i);
						for(int j = 0; j < sheet.getLastRowNum(); j++) {
							Row row = sheet.getRow(j);
							if (row == null) {
					            writer.write('\n');
					            continue;
					        } 
														
							Iterator<Cell> cellIterator = row.cellIterator();
				            StringBuffer sb = new StringBuffer();
				            while (cellIterator.hasNext()) {
				                Cell cell = cellIterator.next();
				                //if (sb.length() != 0) {
				                    
				                //}
	
				                switch (cell.getCellType()) {
				                case STRING:
				                    sb.append(cell.getStringCellValue());
				                    break;
				                case NUMERIC:
				                	if(DateUtil.isCellDateFormatted(cell)) {
				                		sb.append(dateFormat.format(cell.getDateCellValue()));
				                	} else {
				                		sb.append(decimalFormat.format(cell.getNumericCellValue()));
				                	}
				                    break;
				                case BOOLEAN:
				                    sb.append(cell.getBooleanCellValue());
				                    break;
				                default:
				                }
				                
				                if(cellIterator.hasNext()) {
				                	sb.append(";");
				                }
				                
				            }
				            writer.write(sb.toString() + '\n');
						}
						writer.close();
						log.debug("bytes written: " + outfile.length());
						
					}
			
				} catch (Exception ex) {
					log.error(ex.getMessage());
					ex.printStackTrace();
				} 

			} else {

				Files.copy(input, Paths.get(uploadDir.toPath().toString(), targetFileName),
						StandardCopyOption.REPLACE_EXISTING);
				
			}

			response.redirect("/upload");
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
