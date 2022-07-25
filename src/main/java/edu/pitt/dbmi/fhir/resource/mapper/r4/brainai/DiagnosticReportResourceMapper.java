/*
 * Copyright (C) 2022 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.fhir.resource.mapper.r4.brainai;

import edu.pitt.dbmi.fhir.resource.mapper.r4.standards.CodingSystemURIs;
import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DiagnosticReport;
import org.hl7.fhir.r4.model.DiagnosticReport.DiagnosticReportStatus;
import org.hl7.fhir.r4.model.Reference;

/**
 * Map Cerner exported diagnostic reports (imaging) to DiagnosticReport
 * resource.
 *
 * Jul 21, 2022 1:22:01 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class DiagnosticReportResourceMapper {

    private static final int ISSUE_DATE = 0;
    private static final int EFFECTIVE_DATE = 1;
    private static final int PATIENT = 2;
    private static final int ENCOUNTER = 3;
    private static final int OBSERVATION = 4;
    private static final int OBSERVATION_DISPLAY = 5;
    private static final int CATEGORY_CODE = 6;
    private static final int CATEGORY_DISPLAY = 7;
    private static final int CODING_CODE = 8;
    private static final int CODING_DISPLAY = 9;

    public static List<DiagnosticReport> getDiagnosticReports(final Path file, final Pattern delimiter) {
        List<DiagnosticReport> diagnosticReports = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            getDiagnosticReports(reader.lines().skip(1).collect(Collectors.toList()), delimiter, diagnosticReports);
        } catch (IOException | ParseException exception) {
            exception.printStackTrace(System.err);
        }

        return diagnosticReports;
    }

    public static void getDiagnosticReports(final List<String> lines, final Pattern delimiter, List<DiagnosticReport> diagnosticReports) throws ParseException {
        Map<String, List<ReferenceData>> encounterGroupOfObservations = new HashMap<>();
        Map<String, DiagnosticReport> encounterDiagnosticReports = new HashMap<>();
        for (String line : lines) {
            String[] fields = delimiter.split(line);

            String key = fields[ENCOUNTER].trim();

            // get group of observations
            List<ReferenceData> observations = encounterGroupOfObservations.get(key);
            if (observations == null) {
                observations = new LinkedList<>();
                encounterGroupOfObservations.put(key, observations);
            }
            observations.add(new ReferenceData(fields[OBSERVATION], fields[OBSERVATION_DISPLAY]));

            // get diagnostic report
            if (!encounterDiagnosticReports.containsKey(key)) {
                encounterDiagnosticReports.put(key, getDiagnosticReport(fields));
            }
        }

        // add observations to diagnostic reports
        encounterDiagnosticReports.forEach((key, diagnosticReport) -> {
            if (encounterGroupOfObservations.containsKey(key)) {
                encounterGroupOfObservations.get(key)
                        .forEach(observation -> {
                            diagnosticReport.addResult()
                                    .setReference(observation.reference)
                                    .setDisplay(observation.display);
                        });
            }

            diagnosticReports.add(diagnosticReport);
        });
    }

    private static DiagnosticReport getDiagnosticReport(String[] fields) throws ParseException {
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setEffective(getEffectiveDate(fields));
        diagnosticReport.setIssued(getIssuedDate(fields));
        diagnosticReport.setSubject(getSubject(fields));
        diagnosticReport.setEncounter(getEncounter(fields));
        diagnosticReport.setStatus(DiagnosticReportStatus.FINAL);
        diagnosticReport.addCategory(getCategory(fields));
        diagnosticReport.setCode(getCode(fields));

        return diagnosticReport;
    }

//    public static List<DiagnosticReport> getDiagnosticReports(final Path file, final Pattern delimiter) {
//        List<DiagnosticReport> diagnosticReports = new LinkedList<>();
//
//        try ( BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
//            reader.readLine(); // skip header
//            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
//                diagnosticReports.add(getDiagnosticReport(delimiter.split(line.trim())));
//            }
//        } catch (IOException | ParseException exception) {
//            exception.printStackTrace(System.err);
//        }
//
//        return diagnosticReports;
//    }
//    public static DiagnosticReport getDiagnosticReport(String[] fields) throws ParseException {
//        DiagnosticReport diagnosticReport = new DiagnosticReport();
//        diagnosticReport.setEffective(getEffectiveDate(fields));
//        diagnosticReport.setIssued(getIssuedDate(fields));
//        diagnosticReport.setSubject(getSubject(fields));
//        diagnosticReport.setEncounter(getEncounter(fields));
//        diagnosticReport.setStatus(DiagnosticReportStatus.FINAL);
//        diagnosticReport.addCategory(getCategory(fields));
//        diagnosticReport.setCode(getCode(fields));
//
//        return diagnosticReport;
//    }
    private static DateTimeType getEffectiveDate(String[] fields) throws ParseException {
        return new DateTimeType(DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[EFFECTIVE_DATE]));
    }

    private static Date getIssuedDate(String[] fields) throws ParseException {
        return DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[ISSUE_DATE]);
    }

    private static CodeableConcept getCategory(String[] fields) {
        Coding category = (new Coding())
                .setCode(fields[CATEGORY_CODE])
                .setSystem(CodingSystemURIs.DIAGNOSTIC_REPORT_CODE_SYSTEM)
                .setDisplay(fields[CATEGORY_DISPLAY]);

        return new CodeableConcept(category);
    }

    private static CodeableConcept getCode(String[] fields) {
        Coding category = (new Coding())
                .setCode(fields[CODING_CODE])
                .setSystem(CodingSystemURIs.LOINC_URI)
                .setDisplay(fields[CODING_DISPLAY]);

        return new CodeableConcept(category);
    }

//    private static CodeableConcept getCategory() {
//        Coding category = (new Coding())
//                .setCode("IMG")
//                .setSystem("http://terminology.hl7.org/CodeSystem/v2-0074")
//                .setDisplay("Diagnostic Imaging");
//
//        return new CodeableConcept(category);
//    }
//    private static CodeableConcept getCode() {
//        return new CodeableConcept(new Coding(CodingSystemURIs.LOINC_URI, "55112-7", "Summary"));
//    }
    private static Reference getEncounter(String[] fields) {
        return (new Reference())
                .setReference(fields[ENCOUNTER]);
    }

    private static Reference getSubject(String[] fields) {
        return new Reference()
                .setReference(fields[PATIENT]);
    }

    private static class ReferenceData {

        private final String reference;
        private final String display;

        public ReferenceData(String reference, String display) {
            this.reference = reference;
            this.display = display;
        }

        public String getReference() {
            return reference;
        }

        public String getDisplay() {
            return display;
        }

    }

}
