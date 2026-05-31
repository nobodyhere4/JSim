// Minimal C API wrapper for basic world/ball/body operations.
#pragma once

#include "frcsim/physics_world.hpp"

extern "C" {

using PhysicsWorld_t = frcsim::PhysicsWorld;
using RigidBody_t = frcsim::RigidBody;
using Gamepiece_t = frcsim::Gamepiece;

PhysicsWorld_t* frcsim_create_world();
void frcsim_destroy_world(PhysicsWorld_t* w);

RigidBody_t* frcsim_create_body(PhysicsWorld_t* w, double mass_kg);
// Create a gamepiece (generic). For now this materializes a ball-based
// gamepiece instance; later this can instantiate non-ball types.
Gamepiece_t* frcsim_create_gamepiece(PhysicsWorld_t* w,
                                    const frcsim::Gamepiece::Config* config,
                                    const frcsim::Gamepiece::Properties* props);


void frcsim_step_world(PhysicsWorld_t* w, double dt_s);

void frcsim_set_body_box_geometry(RigidBody_t* body, double dim_x, double dim_y,
                                  double dim_z);
void frcsim_set_body_sphere_geometry(RigidBody_t* body, double radius);
void frcsim_set_body_position(RigidBody_t* body, double x, double y, double z);

// Gamepiece-oriented accessors
void frcsim_get_gamepiece_state(Gamepiece_t* gamepiece, double* px, double* py, double* pz,
                                double* vx, double* vy, double* vz);
void frcsim_gamepiece_outtake(Gamepiece_t* gamepiece, double px, double py, double pz,
                              double vx, double vy, double vz);

// Backwards-compatible Ball API removed in favor of gamepiece APIs.

}
