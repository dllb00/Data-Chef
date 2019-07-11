package de.areto.datachef.web_app.handler;

import de.areto.common.template.WebTemplateRenderer;
import de.areto.datachef.persistence.HibernateUtility;
import de.areto.datachef.web_app.common.RouteHandler;
import de.areto.datachef.web_app.common.TemplateRouteHandler;
import de.areto.datachef.web_app.common.WebRoute;
import spark.Request;
import spark.Response;
import spark.route.HttpMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

@WebRoute(
        path = "/manual",
        template = "manual.vm",
        requestType = HttpMethod.post
)
public class ManualMappingHandler extends RouteHandler {

    @Override
    public Object doWork(Request request, Response response) throws Exception {

        try (Session session = HibernateUtility.getSessionFactory().openSession()) {

            // long dbId, String mappingName, LocalDateTime executionDateTime, WorkerCargo.Status status, long runtime, long payloadSize
        	
			String selectedMapping = request.queryParams("selected_mapping");
			
            final String qRows = "select m.name from MappingWorkerCargo m";
            final String q2Rows = "select m.name from MappingColumn m, MappingWorkerCargo n where m.mapping = n.mapping and n.name = '" + selectedMapping + "'";
            
            final List<String> query = session.createQuery(qRows).list();
            final List<String> query2 = session.createQuery(q2Rows).list();


             Map<String, Object> results = new HashMap<>();
             results.put("active", "manual");
             results.put("mappings", query);
             results.put("columns", query2);
             results.put("mapping", selectedMapping);
             
             final String template = this.getRouteConfig().getTemplate();
             return new WebTemplateRenderer(template).render(results);

        }

    }
}
