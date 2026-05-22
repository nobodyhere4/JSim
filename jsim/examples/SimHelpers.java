package examples;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsim.field.FieldDefinitionCatalog;
import jsim.field.FieldConfig;
import jsim.api.StateManager;

/**
 * Small helpers for examples to centralize common simulation setup.
 */
public final class SimHelpers {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private SimHelpers() {}

  public static void initializeField(int year) {
    JsonNode node = FieldDefinitionCatalog.loadFieldNode(year);
    try {
      FieldConfig cfg = MAPPER.treeToValue(node, FieldConfig.class);
      StateManager.getInstance().initializeField(cfg);
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize field for year " + year, e);
    }
  }

  /**
   * Silences WPILib joystick connection warnings if WPILib is present on the classpath.
   * This uses reflection so the examples package doesn't require a WPILib compile dependency.
   */
  public static void silenceJoystickWarnings() {
    try {
      Class<?> ds = Class.forName("edu.wpi.first.wpilibj.DriverStation");
      java.lang.reflect.Method m = ds.getMethod("silenceJoystickConnectionWarning", boolean.class);
      m.invoke(null, true);
    } catch (ClassNotFoundException e) {
      // WPILib not available; nothing to do.
    } catch (Exception e) {
      throw new RuntimeException("Failed to silence joystick warnings", e);
    }
  }
}
