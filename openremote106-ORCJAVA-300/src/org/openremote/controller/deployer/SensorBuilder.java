/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.deployer;

import org.jdom.Element;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.model.sensor.Sensor;

/**
 * Generic interface for implementations to provide mapping from XML document instance
 * to sensor object model. Uses JDOM based API.
 *
 * @see org.openremote.controller.model.xml.Version20SensorBuilder
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface SensorBuilder<T extends ModelBuilder>
{
  /**
   * Build the sensor instances from given XML element.
   *
   * @param   sensorElement  the XML element to parse a single sensor from
   *
   * @return  sensor instance
   *
   * @throws  InitializationException
   *              if the XML mapping process encounters an irrecovable error
   */
  Sensor build(Element sensorElement) throws InitializationException;


  /**
   * Injects a reference of the model builder this sensor sub-builder is associated with. <p>
   *
   * It is up to the aggregating model builder to ensure this reference is set appropriately.
   *
   * @param modelBuilder    reference of the model builder this sensor builder is associated with
   */
  void setModelBuilder(T modelBuilder);


}
