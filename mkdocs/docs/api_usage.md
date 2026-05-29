# API Usage

This page describes how to use JSim from the supported languages and runtime layers.

C++ (native)
- Link against the native library built in `core/build` or via the top-level build system.
- Include headers from `core/include` and link `libjsim`.

Python
- Use the Python bindings (generated under `python/`).
- Example: import the bindings in `examples/python/simple_world_demo.py`.

Java
- Use the Java bindings in `jsim/java/` or the generated JAR/Javadoc artifacts.

Common patterns
- Create a world, add bodies/mechanisms, step simulation with a fixed timestep, and collect state for logging or telemetry.
- Use the `sensor_pipeline` components in `apps/sim-runtime/` for example integrations.

Gamepiece rotation activity
- Configure a `GamepieceZone` with `setExitParameters(velocity, rotation)` when the exit pose is static, or call `configure(...)` to supply rotation dynamically each simulation step.
- The zone's exit rotation is read back through `getExitRotation()` and refreshed by `StateManager` before gamepieces are spawned.
- For `OUTTAKE` and `SHOOT`, the simulation uses the zone rotation's Y component as pitch and the Z component as a heading offset when computing the launch vector.
- `INTAKE` still uses the zone for capture logic, but it does not apply the exit rotation to the pickup itself.
