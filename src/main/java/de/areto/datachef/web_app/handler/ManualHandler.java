package de.areto.datachef.web_app.handler;

import de.areto.datachef.persistence.HibernateUtility;
import de.areto.datachef.web_app.common.TemplateRouteHandler;
import de.areto.datachef.web_app.common.WebRoute;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

@WebRoute(
        path = "/manual",
        template = "manual.vm"
)
public class ManualHandler extends TemplateRouteHandler {
    @Override
    public Map<String, Object> createContext(Request request, Response response) throws Exception {

        try (Session session = HibernateUtility.getSessionFactory().openSession()) {


            // long dbId, String mappingName, LocalDateTime executionDateTime, WorkerCargo.Status status, long runtime, long payloadSize

            final String qRows = "select m.name from MappingWorkerCargo m";
            final List<String> query = session.createQuery(qRows).list();

             Map<String, Object> results = new HashMap<>();
             results.put("active", "manual");
             results.put("mappings", query);
             
             return results;

        }

    }
}
