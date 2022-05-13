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
 * Apr 26, 2022 2:40:26 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 * @see http://www.hl7.org/implement/standards/fhir/v3/ActEncounterCode/vs.html
 */
public enum EncounterTypes {

    WELLNESS("AMB"),
    AMBULATORY("AMB"),
    OUTPATIENT("AMB"),
    INPATIENT("IMP"),
    EMERGENCY("EMER"),
    URGENTCARE("AMB"),
    HOSPICE("HH"),
    HOME("HH"),
    SNF("IMP"),
    VIRTUAL("VR");

    private final String code;

    EncounterTypes(String code) {
        this.code = code;
    }

    public static EncounterTypes fromString(String value) {
        if (value.isEmpty()) {
            return EncounterTypes.AMBULATORY;
        } else {
            switch (value) {
                case "super":
                    return EncounterTypes.INPATIENT;
                default:
                    return EncounterTypes.valueOf(value.toUpperCase());
            }
        }
    }

    public String code() {
        return this.code;
    }

}
