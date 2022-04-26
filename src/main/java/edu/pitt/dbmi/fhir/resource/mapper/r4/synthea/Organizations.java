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

import edu.pitt.dbmi.fhir.resource.mapper.util.Delimiters;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Organization;

/**
 *
 * Apr 26, 2022 5:31:56 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public final class Organizations {

    protected static final String SYNTHEA_IDENTIFIER = "https://github.com/synthetichealth/synthea";

    public static final int Id = 0;
    public static final int NAME = 1;
    public static final int ADDRESS = 2;
    public static final int CITY = 3;
    public static final int STATE = 4;
    public static final int ZIP = 5;
    public static final int LAT = 6;
    public static final int LON = 7;
    public static final int PHONE = 8;
    public static final int REVENUE = 9;
    public static final int UTILIZATION = 10;

    private static final Map<String, Organization> organizations = new HashMap<>();

    static {
        loadOrganizations();
    }

    private Organizations() {
    }

    public static Organization getOrganization(String id) {
        return organizations.get(id);
    }

    private static void loadOrganizations() {
        Path file = Paths.get(Locations.class.getResource("/synthea/organizations.csv").getFile());
        try (Stream<String> stream = Files.lines(file)) {
            stream.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> {
                        String[] fields = Delimiters.COMMA_DELIM.split(line);

                        Organization organization = (new Organization())
                                .addAddress(getAddress(fields))
                                .addIdentifier(getIdentifier(fields));
                        organizations.put(fields[Id], organization);
                    });
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    private static Identifier getIdentifier(String[] fields) {
        return (new Identifier())
                .setValue(fields[Id])
                .setSystem(SYNTHEA_IDENTIFIER);
    }

    private static Address getAddress(String[] fields) {
        return (new Address())
                .addLine(fields[ADDRESS])
                .setCity(fields[CITY])
                .setState(fields[STATE])
                .setPostalCode(fields[ZIP]);
    }

}
