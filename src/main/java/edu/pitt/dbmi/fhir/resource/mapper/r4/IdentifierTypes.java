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
package edu.pitt.dbmi.fhir.resource.mapper.r4;

import org.hl7.fhir.r4.model.Coding;

/**
 *
 * Apr 25, 2022 10:07:40 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class IdentifierTypes {

    public static final Coding MEDICAL_RECORD_NUMBER = (new Coding())
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("MR")
            .setDisplay("Medical record number");

    public static final Coding SOCIAL_SECURITY_NUMBER = (new Coding())
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("SS")
            .setDisplay("Social Security number");

    public static final Coding DRIVERS_LICENSE_NUMBER = (new Coding())
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("DL")
            .setDisplay("Driver's license number");

    public static final Coding PASSPORT_NUMBER = (new Coding())
            .setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
            .setCode("PPN")
            .setDisplay("Passport number");

    private IdentifierTypes() {
    }

}
