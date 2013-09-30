package org.openremote.modeler.lutron;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openremote.modeler.server.lutron.importmodel.Area;
import org.openremote.modeler.server.lutron.importmodel.Button;
import org.openremote.modeler.server.lutron.importmodel.ControlStation;
import org.openremote.modeler.server.lutron.importmodel.Device;
import org.openremote.modeler.server.lutron.importmodel.Output;
import org.openremote.modeler.server.lutron.importmodel.Project;
import org.openremote.modeler.server.lutron.importmodel.Room;

public class LutronHomeworksImporter {

  @SuppressWarnings("unchecked")
  public static Project importXMLConfiguration(InputStream configurationStream) throws ImportException {
    Document protocolDoc = null;
    SAXReader reader = new SAXReader();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);
    
    Project project = null;
    
    try {
      protocolDoc = reader.read(configurationStream);
      Element projectElement = protocolDoc.getRootElement();
      if (projectElement.element("ProjectName") == null) {
        throw new ImportException("Invalid file format", null);
      }
      project = new Project(projectElement.elementText("ProjectName"));
      Iterator<Element> areaIterator = projectElement.elementIterator("Area");
      while (areaIterator.hasNext()) {
        Element areaElement = areaIterator.next();
        System.out.println("Area " + areaElement.elementText("Name"));
        Area area = new Area(areaElement.elementText("Name"));
        project.addArea(area);
        Iterator<Element> roomIterator = areaElement.elementIterator("Room");
        while (roomIterator.hasNext()) {
          Element roomElement = roomIterator.next();
          System.out.println("  Room " + roomElement.elementText("Name"));
          Room room = new Room(roomElement.elementText("Name"));
          area.addRoom(room);
          Iterator<Element> outputIterator = roomElement.element("Outputs").elementIterator("Output");
          while (outputIterator.hasNext()) {
            Element outputElement = outputIterator.next();
            String outputType = outputElement.elementText("Type");
            Output output = new Output(outputElement.elementText("Name"), outputType, outputElement.elementText("Address"));
            room.addOutput(output);
          }
          Iterator<Element> inputIterator = roomElement.element("Inputs").elementIterator("ControlStation");
          while (inputIterator.hasNext()) {
            Element controlStationElement = inputIterator.next();
            ControlStation controlStation = new ControlStation(controlStationElement.elementText("Name"));
            room.addInput(controlStation);
            Iterator<Element> deviceIterator = controlStationElement.element("Devices").elementIterator("Device");
            while (deviceIterator.hasNext()) {
              Element deviceElement = deviceIterator.next();
              Device device = new Device(deviceElement.elementText("Type"), deviceElement.elementText("Address"), deviceElement.elementText("WebEnabled").equalsIgnoreCase("True"), deviceElement.elementText("WebKeypadName"));
              controlStation.addDevice(device);
              Iterator<Element> buttonIterator = deviceElement.element("Buttons").elementIterator("Button");
              while (buttonIterator.hasNext()) {
                Element buttonElement = buttonIterator.next();
                Button button = new Button(buttonElement.elementText("Name"), Integer.parseInt(buttonElement.elementText("Number")));
                device.addButton(button);
              }
            }
          }
        }
      }
    } catch (DocumentException e) {
      throw new ImportException("Error parsing file", e);
    }

    return project;
  }

}