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


import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.service.Deployer;
import org.jdom.Namespace;

/**
 * Model builders are sequences of actions which construct the controller's object model (a.k.a.
 * strategy pattern). Different model builders may operate on differently structured XML document
 * instances. <p>
 *
 * This interface exposes the {@link org.openremote.controller.service.Deployer} side of the API
 * to construct the controller's object model. It is fairly opaque, one-way API to construct the
 * object model instances.  <p>
 *
 * The implementation of a model builder is expected not only to create the Java object instances
 * representing the object model, but also initialize, register and start all the created
 * resources as necessary. On returning from the {@link #buildModel()} method, the controller's
 * object model is expected to be running and fully functional.
 *
 * @see org.openremote.controller.deployer.ModelBuilder.SchemaVersion
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface ModelBuilder
{

  // Enums ----------------------------------------------------------------------------------------


  /**
   * Indicates a controller schema version which the deployer attempts to map to its object model.
   */
  public enum SchemaVersion
  {
    /**
     * Version 2.0 : this is the schema for controller.xml file
     */
    VERSION_2_0("2.0"),

    /**
     * Version 3.0 : this is the schema for openremote.xml file
     */
    VERSION_3_0("3.0");



    // Constants ------------------------------------------------------------------------------------


    /**
     * XML namespace definition for OpenRemote XML elements.
     */
    public final static Namespace OPENREMOTE_NAMESPACE = Namespace.getNamespace(
        "or",                            // prefix
        "http://www.openremote.org"      // namespace identifier
    );


    
    // Members ------------------------------------------------------------------------------------

    /**
     * Maps a string values to type-safe enum instances. <p>
     *
     * The string values should only be used in user interface (which may include user editable
     * configuration) to isolate users from actual enum names used in the codebase. The string
     * representations should not be used anywhere else in the codebase (always use the enums
     * instead).
     *
     * @param   value   string value to map to an enum instance
     *
     * @return  the enum instance corresponding to a given string value
     *
     * @throws  InitializationException if the given string could not be mapped to an enum
     */
    public static SchemaVersion toSchemaVersion(String value) throws InitializationException
    {
      if (value.equals(VERSION_2_0.toString()))
      {
        return VERSION_2_0;
      }

      else if (value.equals(VERSION_3_0.toString()))
      {
        return VERSION_3_0;
      }

      else
      {
        throw new InitializationException("Unrecognized schema version value ''{0}''", value);
      }
    }


    /**
     * Stores the string representation of this enum. This string value should only be used on
     * user interfaces to isolate the user from the enum definitions used within the codebase.
     */
    private String versionString;

    /**
     * Constructs an enum with a given string representation.
     *
     * @param version   version string for user interfaces
     */
    private SchemaVersion(String version)
    {
      this.versionString = version;
    }

    /**
     * Returns the version string intended for user interfaces. These string values should not
     * be used for other purposes in the codebase.
     *
     * @return  string representation of this enum instance for user interfaces
     */
    @Override public String toString()
    {
      return versionString;
    }

  }




  // Interface Definition -------------------------------------------------------------------------


  /**
   * Responsible for constructing the controller's object model. Implementation details
   * vary depending on the schema and source of defining artifacts.
   */
  void buildModel();


  /**
   * Model builder (schema) specific implementation to determine whether the controller
   * definition artifacts have changed in such a way that should result in redeploying the
   * object model.
   *
   * @see org.openremote.controller.service.Deployer.ControllerDefinitionWatch
   *
   * @return  true if the object model should be reloaded, false otherwise
   */
  boolean hasControllerDefinitionChanged();

  /**
   * When the ModelBuilder is updating the commandFactory based on new config properties, the
   * deployer is needed in case the commandBuilder is using it.
   * @param deployer
   */
  public void setDeployer(Deployer deployer);
  
}
