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

import edu.pitt.dbmi.fhir.resource.mapper.util.DateFormatters;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
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

    private static final int DATE = 0;
    private static final int PATIENT = 1;
    private static final int ENCOUNTER = 2;

    public static List<DiagnosticReport> getDiagnosticReports(final Path file, final Pattern delimiter) {
        List<DiagnosticReport> diagnosticReports = new LinkedList<>();

        try (BufferedReader reader = Files.newBufferedReader(file, Charset.defaultCharset())) {
            reader.readLine(); // skip header
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                diagnosticReports.add(getDiagnosticReport(delimiter.split(line.trim())));
            }
        } catch (IOException | ParseException exception) {
            exception.printStackTrace(System.err);
        }

        return diagnosticReports;
    }

    public static DiagnosticReport getDiagnosticReport(String[] fields) throws ParseException {
        DiagnosticReport diagnosticReport = new DiagnosticReport();
        diagnosticReport.setEffective(new DateTimeType(DateFormatters.MM_DD_YYYY_HHMMSS_AM.parse(fields[DATE])));
        diagnosticReport.setSubject(getSubject(fields));
        diagnosticReport.setEncounter(getEncounter(fields));
        diagnosticReport.setStatus(DiagnosticReportStatus.FINAL);
        diagnosticReport.addCategory(new CodeableConcept(new Coding("http://terminology.hl7.org/CodeSystem/v2-0074", "IMG", "Diagnostic Imaging")));

        return diagnosticReport;
    }

    private static Reference getEncounter(String[] fields) {
        return (new Reference())
                .setReference(fields[ENCOUNTER]);
    }

    private static Reference getSubject(String[] fields) {
        return new Reference()
                .setReference(fields[PATIENT]);
    }

}
