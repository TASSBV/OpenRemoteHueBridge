/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.modeler.server;

import java.util.ArrayList;

import org.openremote.modeler.client.rpc.SwitchRPCService;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.SwitchSensorRef;
import org.openremote.modeler.domain.CommandRefItem;
import org.openremote.modeler.service.DeviceCommandService;
import org.openremote.modeler.service.SensorService;
import org.openremote.modeler.service.SwitchService;
import org.openremote.modeler.service.UserService;
import org.openremote.modeler.shared.dto.DTOReference;
import org.openremote.modeler.shared.dto.DeviceCommandDTO;
import org.openremote.modeler.shared.dto.SwitchDTO;
import org.openremote.modeler.shared.dto.SwitchDetailsDTO;
import org.openremote.modeler.shared.dto.SwitchWithInfoDTO;
import org.openremote.modeler.logging.LogFacade;
import org.openremote.modeler.exception.PersistenceException;
import org.openremote.modeler.dao.GenericDAO;

/**
 * TODO :
 *
 *   The server side implementation of the RPC service <code>SwitchRPCService</code>.
 *
 *   Tasks to do :
 *
 *     - MODELER-313 -- return null reference in DTO if no sensor associated with switch
 *     - DTO transformation should logically go into the domain object implementation
 *     - This class should not access DB directly but to delegate to a REST API on Beehive
 *       that is responsible for persistent switch operations.
 *     - review the database load semantics, especially with regards to dependent objects
 *       in Switch, requires unit testing
 *
 *
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 * @author <a href = "mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SwitchController extends BaseGWTSpringController implements SwitchRPCService {

  private static LogFacade persistenceLog = LogFacade.getInstance(LogFacade.Category.PERSISTENCE);

   private SwitchService switchService;
   private SensorService sensorService;
   private DeviceCommandService deviceCommandService;
   
   private UserService userService;
   
   @Override
   public void delete(long id) {
      switchService.delete(id);
   }

   public void setSwitchService(SwitchService switchService) {
      this.switchService = switchService;
   }

   public void setSensorService(SensorService sensorService) {
    this.sensorService = sensorService;
  }

  public void setDeviceCommandService(DeviceCommandService deviceCommandService) {
    this.deviceCommandService = deviceCommandService;
  }

  public void setUserService(UserService userService) {
      this.userService = userService;
   }



  /**
   * Loads persistent switch state including dependent object states (optional associated
   * switch sensor, and mandatory switch 'on' and 'off' commands from the database and transforms
   * them into data transfer object graph that can be serialized to the client. In effect, this
   * method disconnects the persistent switch entities from client side processing.
   *
   * TODO :
   *    - the persistent entity transformation to data transfer objects logically belongs into
   *      the domain classes -- this avoids some unneeded data shuffling that helps maintain the
   *      domain object API independent of serialization requirements and restricts the domain
   *      object use to a smaller set of classes which helps with later refactoring
   *
   * @param   id    the persistent switch entity identifier (primary key)
   *
   * @return  a data transfer object that contains the switch state including associated sensor
   *          and command state in a serializable object graph
   */
  @Override public SwitchDetailsDTO loadSwitchDetails(long id)
  {
    Switch sw;

    try
    {
      // database load, see Switch class annotations for database access patterns

      sw = loadSwitch(id);

      // resolve read data into data transfer objects...

      DTOReference sensor     = resolveSensor(sw);
      DTOReference onCommand  = resolveOnCommand(sw);
      DTOReference offCommand = resolveOffCommand(sw);

      String onCommandDisplayName = sw.getSwitchCommandOnRef().getDeviceCommand().getDisplayName();
      String offCommandDisplayName = sw.getSwitchCommandOffRef().getDeviceCommand().getDisplayName();

      long switchID = sw.getOid();

      String switchName = sw.getName();


      return new SwitchDetailsDTO(
          switchID, switchName,
          sensor,
          onCommand, onCommandDisplayName,
          offCommand, offCommandDisplayName
      );
    }

    catch (PersistenceException e)
    {
      // TODO : 
      //    the requested switch ID was not found or could not be read from the database
      //    for some reason -- rethrowing as runtime error for now until can review proper
      //    error handling mechanism / implementation
      //                                                                    [JPL]

      throw new Error("Switch ID " + id + " could not be loaded : " + e.getMessage());
    }
  }


   
   @Override
   public ArrayList<SwitchWithInfoDTO> loadAllSwitchWithInfosDTO() {
     ArrayList<SwitchWithInfoDTO> dtos = new ArrayList<SwitchWithInfoDTO>();
     for (Switch sw : switchService.loadAll()) {
       dtos.add(createSwitchWithInfoDTO(sw));
     }
     return dtos;    
   }

  public static SwitchWithInfoDTO createSwitchWithInfoDTO(Switch aSwitch) {
    return new SwitchWithInfoDTO(aSwitch.getOid(), aSwitch.getDisplayName(),
                  (aSwitch.getSwitchCommandOnRef() != null)?aSwitch.getSwitchCommandOnRef().getDisplayName():null,
                  (aSwitch.getSwitchCommandOffRef() != null)?aSwitch.getSwitchCommandOffRef().getDisplayName():null,
                  (aSwitch.getSwitchSensorRef() != null)?aSwitch.getSwitchSensorRef().getDisplayName():null,
                  aSwitch.getDevice().getDisplayName());
  }
  
  public static SwitchDTO createSwitchDTO(Switch aSwitch) {
    SwitchDTO switchDTO = new SwitchDTO(aSwitch.getOid(), aSwitch.getDisplayName());
    DeviceCommand dc = aSwitch.getSwitchCommandOnRef().getDeviceCommand();
    switchDTO.setOnCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
    dc = aSwitch.getSwitchCommandOffRef().getDeviceCommand();
    switchDTO.setOffCommand(new DeviceCommandDTO(dc.getOid(), dc.getDisplayName(), dc.getProtocol().getType()));
    return switchDTO;
  }

  @Override
   public void updateSwitchWithDTO(SwitchDetailsDTO switchDTO) {
     Switch sw = switchService.loadById(switchDTO.getOid());
     sw.setName(switchDTO.getName());
     
     if (sw.getSwitchSensorRef().getSensor().getOid() != switchDTO.getSensor().getId()) {
       Sensor sensor = sensorService.loadById(switchDTO.getSensor().getId());
       sw.getSwitchSensorRef().setSensor(sensor);
     }
     
     if (sw.getSwitchCommandOnRef().getDeviceCommand().getOid() != switchDTO.getOnCommand().getId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOnCommand().getId());
       sw.getSwitchCommandOnRef().setDeviceCommand(dc);
     }
     
     if (sw.getSwitchCommandOffRef().getDeviceCommand().getOid() != switchDTO.getOffCommand().getId()) {
       DeviceCommand dc = deviceCommandService.loadById(switchDTO.getOffCommand().getId());
       sw.getSwitchCommandOffRef().setDeviceCommand(dc);
     }

     switchService.update(sw);
   }

   @Override
   public void saveNewSwitch(SwitchDetailsDTO switchDTO, long deviceId) {
     Sensor sensor = sensorService.loadById(switchDTO.getSensor().getId());
     DeviceCommand onCommand = deviceCommandService.loadById(switchDTO.getOnCommand().getId());
     DeviceCommand offCommand = deviceCommandService.loadById(switchDTO.getOffCommand().getId());
     
     Switch sw = new Switch(onCommand, offCommand, sensor);
     sw.setName(switchDTO.getName());
     sw.setAccount(userService.getAccount());
     
     switchService.save(sw);
   }



  // Private Instance Methods ---------------------------------------------------------------------

  /**
   * Loads a switch definition from a database, including dependent objects such
   * as an associated sensor and associated 'on' and 'off' commands.
   *
   * @see org.openremote.modeler.domain.Switch
   *
   * @param   id    switch identifier (primary key)
   *
   * @return  persistent switch entity
   *
   * @throws  PersistenceException    if the database load operation fails
   */
  private Switch loadSwitch(long id) throws PersistenceException
  {
    try
    {
      return switchService.loadById(id);
    }

    catch (GenericDAO.DatabaseError e)
    {
      throw new PersistenceException("Unable to load switch ID {0} : {1}", id, e.getMessage());
    }
  }
  
  /**
   * Attempts to resolve the associated sensor for this switch into a data transfer object
   * reference. Mainly this is used in preparation for a data transfer object graph that
   * disconnects the persistent switch entity from the client side processing.
   *
   * TODO :
   *   - if no sensor has been associated, currently still returns an empty DTOReference,
   *     should return null instead (see MODELER-313)
   *
   * @param sw  a persistent switch entity
   *
   * @return  a data transfer object reference for a persistent sensor instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client
   */
  private DTOReference resolveSensor(Switch sw)
  {
    SwitchSensorRef ref = sw.getSwitchSensorRef();

    DTOReference sensor;

    // defensive check -- switch may not always have a sensor associated to it...

    if (ref == null)
    {
      // TODO : should return null reference directly, see MODELER-313
      
      sensor = new DTOReference();
    }

    else
    {
      Sensor switchSensor = ref.getSensor();

      sensor = new DTOReference(switchSensor.getOid());
    }

    return sensor;
  }


  /**
   * Returns a data transfer object reference for a 'off' command for the given switch. This is
   * mainly used in preparation for a data transfer object graph that disconnects the persistent
   * switch entity from the client side processing.
   *
   *
   * @param   sw    a persistent switch entity
   *
   * @return  A data transfer object reference for a persistent command instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client. <p>
   *
   *          Note that in case of an error in resolving the dependent 'off' command reference,
   *          an empty DTO reference will be returned.
   */
  private DTOReference resolveOffCommand(Switch sw)
  {
    DTOReference ref = resolveSwitchCommandReference(sw.getSwitchCommandOffRef());

    // This is a defensive check -- switches *should* always have an off command, so if we don't
    // find one, it is most likely a database integrity/constraint issue.

    if (ref == null)
    {
      persistenceLog.error(
          "Switch ID {0} -- ''{1}'' in device ''{2}'' does not have an associated 'off' command. " +
          "(Account ID : {3}, Users : {4})",
          sw.getOid(), sw.getDisplayName(), sw.getDevice().getDisplayName(),
          sw.getAccount(), sw.getAccount().getUsers()
      );

      return new DTOReference();
    }

    return ref;
  }

  /**
   * Returns a data transfer object reference for an 'on' command for the given switch. This is
   * mainly used in preparation for a data transfer object graph that disconnects the persistent
   * switch entity from the client side processing.
   *
   *
   * @param   sw    a persistent switch entity
   *
   * @return  A data transfer object reference for a persistent command instance that can be
   *          used to build the DTO object graph for all switch state to be sent to the client. <p>
   *
   *          Note that in case of an error in resolving the dependent 'on' command reference,
   *          an empty DTO reference will be returned.
   */
  private DTOReference resolveOnCommand(Switch sw)
  {
    DTOReference ref = resolveSwitchCommandReference(sw.getSwitchCommandOnRef());

    // Above may return 'null' in case of errors -- log the error and return an empty DTO reference
    // instead.

    if (ref == null)
    {
      persistenceLog.error(
          "BUG: Switch ID {0} -- ''{1}'' in device ''{2}'' does not have an associated 'on' " +
          "command. (Account ID : {3}, Users : {4})",
          sw.getOid(), sw.getDisplayName(), sw.getDevice().getDisplayName(),
          sw.getAccount(), sw.getAccount().getUsers()
      );

      return new DTOReference();
    }

    return ref;
  }


  /**
   * Defensively resolves associated command references for a switch. The implementation
   * assumes most worse case scenarios with regards to data integrity issues and lack of
   * database constraints (hence, defensive)
   *
   * @param ref   the switch command reference to resolve into a data transfer object reference
   *
   * @return  Returns a data transfer object reference to a switch's 'on' or 'off' command that
   *          can be used to build a DTO object graph to disconnect the persistent switch entity
   *          from client side processing.  <p>
   *
   *          Note that may return a null reference in case of database integrity or constraint
   *          errors.
   */
  private DTOReference resolveSwitchCommandReference(CommandRefItem ref)
  {
    // defensive check -- this may indicate a database integrity issue, both 'on'
    // and 'off' commands should always be associated with a switch

    if (ref == null)
    {
      persistenceLog.error(
          "Switch command could not be resolved -- command reference not found."
      );

      return null;
    }

    DeviceCommand cmd = ref.getDeviceCommand();

    // defensive check -- this would be a DB constraint issue, there's a reference to
    // command but the command itself was not found...

    if (cmd == null)
    {
      persistenceLog.error(
          "Command for switch reference could not be resolved, command not found. " +
          "(Device : ''{0}'')", ref.getDisplayName()
      );

      return null;
    }

    return new DTOReference(cmd.getOid());
  }



}
