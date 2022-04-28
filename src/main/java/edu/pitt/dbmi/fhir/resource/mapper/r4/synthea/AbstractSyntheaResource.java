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
package edu.pitt.dbmi.fhir.resource.mapper.r4.synthea;

import java.math.BigDecimal;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

/**
 *
 * Apr 25, 2022 4:14:54 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public abstract class AbstractSyntheaResource {

    protected static final String SNOMED_URI = "http://snomed.info/sct";
    protected static final String LOINC_URI = "http://loinc.org";
    protected static final String RXNORM_URI = "http://www.nlm.nih.gov/research/umls/rxnorm";
    protected static final String CVX_URI = "http://hl7.org/fhir/sid/cvx";
    protected static final String DISCHARGE_URI = "http://www.nubc.org/patient-discharge";
    protected static final String SHR_EXT = "http://standardhealthrecord.org/fhir/StructureDefinition/";
    protected static final String SYNTHEA_EXT = "http://synthetichealth.github.io/synthea/";
    protected static final String UNITSOFMEASURE_URI = "http://unitsofmeasure.org";
    protected static final String DICOM_DCM_URI = "http://dicom.nema.org/resources/ontology/DCM";
    protected static final String MEDIA_TYPE_URI = "http://terminology.hl7.org/CodeSystem/media-type";
    protected static final String SYNTHEA_IDENTIFIER = "https://github.com/synthetichealth/synthea";

    static Type toFHIRType(String type, String value, String unit) {
        switch (type) {
            case "numeric":
                return new Quantity().setValue(BigDecimal.valueOf(Double.parseDouble(value)))
                        .setCode(unit).setSystem(UNITSOFMEASURE_URI)
                        .setUnit(unit);
            case "text":
                return new StringType((String) value);
            default:
                return null;
        }
    }

    protected static CodeableConcept mapCodeToCodeableConcept(Code from, String system) {
        CodeableConcept to = new CodeableConcept();
        if (system != null) {
            system = getSystemURI(system);
        }
        from.setSystem(getSystemURI(from.getSystem()));

        if (from.getDisplay() != null) {
            to.setText(from.getDisplay());
        }

        Coding coding = new Coding();
        coding.setCode(from.getCode());
        coding.setDisplay(from.getDisplay());
        if (from.getSystem() == null) {
            coding.setSystem(system);
        } else {
            coding.setSystem(from.getSystem());
        }

        to.addCoding(coding);

        return to;
    }

    protected static CodeableConcept mapCodingToCodeableConcept(Coding coding) {
        return (new CodeableConcept())
                .addCoding(coding)
                .setText(coding.getDisplay());
    }

    protected static String getSystemURI(String system) {
        switch (system) {
            case "SNOMED-CT":
                system = SNOMED_URI;
                break;
            case "LOINC":
                system = LOINC_URI;
                break;
            case "RxNorm":
                system = RXNORM_URI;
                break;
            case "CVX":
                system = CVX_URI;
                break;
            case "DICOM-DCM":
                system = DICOM_DCM_URI;
                break;
            default:
                break;
        }

        return system;
    }

}
