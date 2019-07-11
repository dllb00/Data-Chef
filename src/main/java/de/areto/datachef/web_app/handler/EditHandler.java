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
            columns.add("Kostenstelle");
            columns.add("Datum");
            columns.add("Umsatz");
            
            List<String> cells1 = new ArrayList<>();
            cells1.add("1099");
            cells1.add("20.06.2019");
            cells1.add("36.98");
            
            List<String> cells2 = new ArrayList<>();
            cells2.add("1099");
            cells2.add("20.06.2019");
            cells2.add("122.60");
            
            List<String> cells3 = new ArrayList<>();
            cells3.add("2077");
            cells3.add("20.06.2019");
            cells3.add("2.40");
            
            List<String> cells4 = new ArrayList<>();
            cells4.add("2077");
            cells4.add("20.06.2019");
            cells4.add("152.80");
            
            List<String> cells5 = new ArrayList<>();
            cells5.add("2077");
            cells5.add("20.06.2019");
            cells5.add("12.50");
            
            List<List<String>> rows = new ArrayList<>();
            
            rows.add(cells1);
            rows.add(cells2);
            rows.add(cells3);
            rows.add(cells4);
            rows.add(cells5);
            
            
             Map<String, Object> results = new HashMap<>();
             results.put("active", "edit");
             results.put("mappings", query);
             results.put("columns", columns);
             results.put("rows", rows);
             
             return results;

        }

    }
}
