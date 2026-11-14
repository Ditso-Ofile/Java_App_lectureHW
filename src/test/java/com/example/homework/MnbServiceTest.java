package com.example.homework;

import com.example.homework.soap.MnbService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Unit test of the MNB XML parser (no network needed). */
class MnbServiceTest {

    @Test @DisplayName("MNB XML answer is parsed and sorted by date ascending")
    void testParse() throws Exception {
        String xml = "<MNBExchangeRates>"
                + "<Day date=\"2026-10-02\"><Rate unit=\"1\" curr=\"EUR\">390,10</Rate></Day>"
                + "<Day date=\"2026-10-01\"><Rate unit=\"1\" curr=\"EUR\">389,45</Rate></Day>"
                + "</MNBExchangeRates>";
        List<MnbService.RatePoint> points = new MnbService().parse(xml);
        assertEquals(2, points.size());
        assertEquals("2026-10-01", points.get(0).date());
        assertEquals(389.45, points.get(0).rate(), 1e-9);
        assertEquals(390.10, points.get(1).rate(), 1e-9);
    }

    @Test @DisplayName("Empty answer gives an empty list")
    void testParseEmpty() throws Exception {
        assertTrue(new MnbService().parse("<MNBExchangeRates/>").isEmpty());
        assertTrue(new MnbService().parse("").isEmpty());
    }
}
