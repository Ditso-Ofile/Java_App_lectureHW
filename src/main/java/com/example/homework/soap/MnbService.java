package com.example.homework.soap;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import soapclient.MNBArfolyamServiceSoap;
import soapclient.MNBArfolyamServiceSoapImpl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calls the SOAP web-service of the Hungarian National Bank (MNB) and parses the XML answer.
 *
 * getExchangeRates(...) returns an XML string like:
 * <MNBExchangeRates>
 *   <Day date="2026-10-01"><Rate unit="1" curr="EUR">389,45</Rate></Day>
 *   ...
 * </MNBExchangeRates>
 */
@Service
public class MnbService {

    /** One point of the result: a date and the HUF price of 1 unit of the currency. */
    public record RatePoint(String date, double rate, int unit) {}

    private MNBArfolyamServiceSoap port() {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        return impl.getCustomBindingMNBArfolyamServiceSoap();
    }

    /** Raw XML answer of the web-service. */
    public String getExchangeRatesXml(String start, String end, String currency) throws Exception {
        return port().getExchangeRates(start, end, currency);
    }

    /** Parsed answer, ordered by date ascending (MNB sends newest first). */
    public List<RatePoint> parse(String xml) throws Exception {
        List<RatePoint> points = new ArrayList<>();
        if (xml == null || xml.isBlank()) return points;

        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder b = f.newDocumentBuilder();
        Document doc = b.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        NodeList days = doc.getElementsByTagName("Day");
        for (int i = 0; i < days.getLength(); i++) {
            Element day = (Element) days.item(i);
            NodeList rates = day.getElementsByTagName("Rate");
            if (rates.getLength() == 0) continue;
            Element rate = (Element) rates.item(0);
            String value = rate.getTextContent().trim().replace(",", ".");
            int unit = 1;
            try { unit = Integer.parseInt(rate.getAttribute("unit")); } catch (NumberFormatException ignored) {}
            points.add(new RatePoint(day.getAttribute("date"), Double.parseDouble(value), unit));
        }
        Collections.reverse(points);
        return points;
    }
}
