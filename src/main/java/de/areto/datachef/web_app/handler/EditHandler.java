package de.areto.datachef.web_app.handler;

import de.areto.datachef.persistence.HibernateUtility;
import de.areto.datachef.web_app.common.TemplateRouteHandler;
import de.areto.datachef.web_app.common.WebRoute;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

@WebRoute(
        path = "/edit",
        template = "edit.vm"
)
public class EditHandler extends TemplateRouteHandler {
    @Override
    public Map<String, Object> createContext(Request request, Response response) throws Exception {

        try (Session session = HibernateUtility.getSessionFactory().openSession()) {


            // long dbId, String mappingName, LocalDateTime executionDateTime, WorkerCargo.Status status, long runtime, long payloadSize

            final String qRows = "select m.name from MappingWorkerCargo m";
            final List<String> query = session.createQuery(qRows).list();

            
            List<String> columns = new ArrayList<>();
            columns.add("c1");
            columns.add("c2");
            columns.add("c3");
            
            List<String> cells1 = new ArrayList<>();
            cells1.add("cell1");
            cells1.add("cell2");
            cells1.add("cell3");
            
            List<String> cells2 = new ArrayList<>();
            cells2.add("cell21");
            cells2.add("cell22");
            cells2.add("cell23");
            
            List<List<String>> rows = new ArrayList<>();
            
            rows.add(cells1);
            rows.add(cells2);
            
            
             Map<String, Object> results = new HashMap<>();
             results.put("active", "edit");
             results.put("mappings", query);
             results.put("columns", columns);
             results.put("rows", rows);
             
             return results;

        }

    }
}
