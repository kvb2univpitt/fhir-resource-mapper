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
package edu.pitt.dbmi.fhir.resource.mapper.util;

import edu.pitt.dbmi.fhir.resource.mapper.r4.standards.CodingSystemURIs;
import java.math.BigDecimal;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Type;

/**
 *
 * May 13, 2022 4:06:50 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class FhirUtils {

    private FhirUtils() {
    }

    public static Type toFHIRType(String type, String value, String unit) {
        switch (type) {
            case "numeric":
                return new Quantity().setValue(BigDecimal.valueOf(Double.parseDouble(value)))
                        .setCode(unit).setSystem(CodingSystemURIs.UNITSOFMEASURE_URI)
                        .setUnit(unit);
            case "text":
                return new StringType((String) value);
            default:
                return null;
        }
    }

    public static CodeableConcept mapCodingToCodeableConcept(Coding coding, Coding... codings) {
        CodeableConcept codeableConcept = new CodeableConcept()
                .addCoding(coding)
                .setText(coding.getDisplay());
        for (Coding c : codings) {
            codeableConcept.addCoding(c);
        }

        return codeableConcept;
    }

}
