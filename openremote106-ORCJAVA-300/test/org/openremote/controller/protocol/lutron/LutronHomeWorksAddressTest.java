package org.openremote.controller.protocol.lutron;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author <a href="mailto:eric@openremote.org">Eric Bariaux</a>
 *
 */
public class LutronHomeWorksAddressTest {

	@Test public void testValidAddressCreations() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("1:6:1:3:2");
    new LutronHomeWorksAddress("[1:6:1:3:2]");
    new LutronHomeWorksAddress("[1.6.1.3.2]");
    new LutronHomeWorksAddress("[1/6/1/3/2]");
    new LutronHomeWorksAddress("[1-6-1-3-2]");
    new LutronHomeWorksAddress("[1\\6\\1\\3\\2]");
    new LutronHomeWorksAddress("[01:06-01/03\\02]");
    new LutronHomeWorksAddress("[1:6:1]");
    new LutronHomeWorksAddress("[1:6:1:2]");
    new LutronHomeWorksAddress("   [1:  6:1:3 :2]");
    new LutronHomeWorksAddress("[1:6\n:1:3:2]");
    new LutronHomeWorksAddress("[This 1: is 6\n: a 1: lutron 3: address 2]");
	}
	
	@Test public void testValidAddressCreationsForDifferentAddressTypes() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:0:3:2]"); // RPM Dimmer/Switch
    new LutronHomeWorksAddress("[1:4:1:4:3]"); // D48 Dimmer/Switch
    new LutronHomeWorksAddress("[1:5:3:6:8]"); // H48 Dimmer/Switch
    new LutronHomeWorksAddress("[15:6:31]"); // Keypad/Sivoia Control/CCO/CCI/TEL-9
    new LutronHomeWorksAddress("[16:5:1]"); // GRAFIK Eye Main Unit
    new LutronHomeWorksAddress("[3:4:1:8]"); // GRAFIK Eye Main Unit Single Zone
    new LutronHomeWorksAddress("[3:8:1:63]"); // RF Dimmer/Switch
    new LutronHomeWorksAddress("[5:8:2:27]"); // RF Keypad
    new LutronHomeWorksAddress("[8:8:3:1]"); // RF Repeater
	}
	
	@Test public void testNormalizedAddressFormat() throws InvalidLutronHomeWorksAddressException {
    Assert.assertEquals("[01:06:01:03:02]", new LutronHomeWorksAddress("[1:6:1:3:2]").toString());
    Assert.assertEquals("[01:04:01:02]", new LutronHomeWorksAddress("1:4:1:2").toString());
	}
	
	@Test public void testAddressEquality() throws InvalidLutronHomeWorksAddressException {
    Assert.assertEquals(new LutronHomeWorksAddress("[1:6:1:3:2]"), new LutronHomeWorksAddress("[1:6:1:3:2]"));
    Assert.assertEquals(new LutronHomeWorksAddress("01.6.01.3/2]"), new LutronHomeWorksAddress("[1:6:1:3:2]"));
    Assert.assertEquals(new LutronHomeWorksAddress("[1:6:1:3:2]").hashCode(), new LutronHomeWorksAddress("[1:6:1:3:2]").hashCode());
    Assert.assertEquals(new LutronHomeWorksAddress("01.6.01.3/2]").hashCode(), new LutronHomeWorksAddress("[1:6:1:3:2]").hashCode());
	}
	
  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testGarbageAddress() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("hjfhsdkh!!676JKD");
  }
	
	@Test(expected=InvalidLutronHomeWorksAddressException.class)
	public void testUnbalancedSquareBrackets() throws InvalidLutronHomeWorksAddressException {
		new LutronHomeWorksAddress("[1:5:3");
	}

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testTooFewComponents() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:3]");
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testTooManyComponents() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:5:3:4:3:1]");
  }
  
  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testProcessorNumberTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[0:1:0:3:2]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testProcessorNumberTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[17:1:0:3:2]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRPMRouterTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:16:3:2]");
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRPMModuleTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:0:0:2]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRPMModuleTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:0:9:2]");    
  }
  
  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRPMOutputTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:0:1:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRPMOutputTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:1:0:4:5]");    
  }
  
  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testD48RouterTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:0:4:3]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testD48RouterTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:5:4:3]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testD48BusTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:0:3]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testD48BusTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:13:3]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testH48DimmerTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:4:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testH48DimmerTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:4:9]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testKeypadKeypadTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testKeypadKeypadTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:33]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testGrafikEyeSingleZoneGrafikEyeTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:0:1]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testGrafikEyeSingleZoneGrafikEyeTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:9:1]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testGrafikEyeSingleZoneOutputTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testGrafikEyeSingleZoneOutputTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:4:1:9]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testLink8InvalidDeviceType() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:4:1]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFDimmerDimmerTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:1:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFDimmerDimmerTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:1:65]");    
  }
  
  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFKeypadKeypadTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:2:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFKeypadKeypadTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:2:33]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFRepeaterRepeaterTooSmall() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:3:0]");    
  }

  @Test(expected=InvalidLutronHomeWorksAddressException.class)
  public void testRFRepeaterRepeaterTooBig() throws InvalidLutronHomeWorksAddressException {
    new LutronHomeWorksAddress("[1:8:3:5]");    
  }

  @Test public void testAddressTypeDetection() throws InvalidLutronHomeWorksAddressException {
    Assert.assertTrue(new LutronHomeWorksAddress("[1:8:2:13]").isValidKeypadAddress()); // RF Keypad
    Assert.assertTrue(new LutronHomeWorksAddress("[1:5:31]").isValidKeypadAddress()); // Wired keypad
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:1:1]").isValidKeypadAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:1:1:1:1]").isValidKeypadAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:5:4:1:1]").isValidKeypadAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:1:1]").isValidKeypadAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:3:3]").isValidKeypadAddress());

    Assert.assertTrue(new LutronHomeWorksAddress("[1:1:1:1:1]").isValidDimmerAddress()); // RPM Dimmer
    Assert.assertTrue(new LutronHomeWorksAddress("[1:4:1:7:1]").isValidDimmerAddress()); // D48 Dimmer
    Assert.assertTrue(new LutronHomeWorksAddress("[1:4:1:1:7]").isValidDimmerAddress()); // H48 Dimmer
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:1:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:2:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:3:1]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:11]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:1:1]").isValidDimmerAddress());
    
    Assert.assertTrue(new LutronHomeWorksAddress("[1:4:7]").isValidGrafikEyeAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:1:1:1:1]").isValidGrafikEyeAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:6:1]").isValidGrafikEyeAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:1:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:2:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:3:1]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:1:4]").isValidGrafikEyeAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:11]").isValidGrafikEyeAddress());

    Assert.assertTrue(new LutronHomeWorksAddress("[1:4:1:4]").isValidGrafikEyeSingleZoneAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:7]").isValidGrafikEyeSingleZoneAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:1:1:1:1]").isValidGrafikEyeSingleZoneAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:1:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:2:7]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:8:3:1]").isValidDimmerAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:11]").isValidGrafikEyeSingleZoneAddress());
    Assert.assertFalse(new LutronHomeWorksAddress("[1:4:1:11:1]").isValidGrafikEyeSingleZoneAddress());
  }
}
