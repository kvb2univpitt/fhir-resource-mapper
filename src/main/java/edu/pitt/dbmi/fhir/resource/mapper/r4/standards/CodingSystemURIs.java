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
package edu.pitt.dbmi.fhir.resource.mapper.r4.standards;

/**
 *
 * May 13, 2022 1:01:35 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class CodingSystemURIs {

    public static final String SNOMED_URI = "https://snomed.info/sct";
    public static final String LOINC_URI = "https://loinc.org";
    public static final String RXNORM_URI = "https://www.nlm.nih.gov/research/umls/rxnorm";
    public static final String CVX_URI = "http://hl7.org/fhir/sid/cvx";
    public static final String DISCHARGE_URI = "http://www.nubc.org/patient-discharge";
    public static final String SHR_EXT = "https://standardhealthrecord.org/fhir/StructureDefinition/";
    public static final String SYNTHEA_EXT = "https://synthetichealth.github.io/synthea/";
    public static final String UNITSOFMEASURE_URI = "https://unitsofmeasure.org";
    public static final String DICOM_DCM_URI = "https://dicom.nema.org/resources/ontology/DCM";
    public static final String MEDIA_TYPE_URI = "http://terminology.hl7.org/CodeSystem/media-type";

    public static final String DIAGNOSTIC_REPORT_CODE_SYSTEM = "http://terminology.hl7.org/CodeSystem/v2-0074";

    public static final String SYNTHEA_IDENTIFIER = "https://github.com/synthetichealth/synthea";

    private CodingSystemURIs() {
    }

    protected static String getSystemURI(String system) {
        String systemName = system.toLowerCase();
        switch (systemName) {
            case "snomed-ct":
                system = SNOMED_URI;
                break;
            case "loinc":
                system = LOINC_URI;
                break;
            case "rxnorm":
                system = RXNORM_URI;
                break;
            case "cvx":
                system = CVX_URI;
                break;
            case "dicom-dcm":
                system = DICOM_DCM_URI;
                break;
            default:
                break;
        }

        return system;
    }

}
