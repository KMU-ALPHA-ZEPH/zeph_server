package zeph_server.course.service;

import org.springframework.stereotype.Component;
import zeph_server.course.dto.common.Point;

import java.util.List;

@Component
public class GpxWriter {

    public String writeRoute(String name, List<Point> points) {
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<gpx version=\"1.1\" creator=\"zeph\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
        sb.append("  <rte>\n");
        sb.append("    <name>").append(escape(name)).append("</name>\n");
        for (Point p : points) {
            sb.append("    <rtept lat=\"").append(p.lat())
                    .append("\" lon=\"").append(p.lng())
                    .append("\"/>\n");
        }
        sb.append("  </rte>\n");
        sb.append("</gpx>\n");
        return sb.toString();
    }

    private String escape(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
