#include "frcsim/api.h"

#include "frcsim/physics_world.hpp"

extern "C" {

PhysicsWorld_t* frcsim_create_world() {
  return new PhysicsWorld_t;
}

void frcsim_destroy_world(PhysicsWorld_t* w) {
  delete w;
}

RigidBody_t* frcsim_create_body(PhysicsWorld_t* w, double mass_kg) {
  if (!w) return nullptr;
  return &w->createBody(mass_kg);
}

Gamepiece_t* frcsim_create_gamepiece(PhysicsWorld_t* w,
                                    const frcsim::Gamepiece::Config* config,
                                    const frcsim::Gamepiece::Properties* props) {
  if (!w) return nullptr;
  if (config && props) {
    return static_cast<Gamepiece_t*>(&w->createBall(*config, *props));
  }
  if (config) {
    return static_cast<Gamepiece_t*>(&w->createBall(*config, frcsim::Gamepiece::Properties()));
  }
  if (props) {
    return static_cast<Gamepiece_t*>(&w->createBall(frcsim::Gamepiece::Config(), *props));
  }
  return static_cast<Gamepiece_t*>(&w->createBall(frcsim::Gamepiece::Config(), frcsim::Gamepiece::Properties()));
}

void frcsim_step_world(PhysicsWorld_t* w, double dt_s) {
  if (!w) return;
  // Use configured fixed_dt_s if dt_s <= 0
  if (dt_s > 0.0) {
    auto cfg = w->config();
    cfg.fixed_dt_s = dt_s;
    w->config() = cfg;
  }
  w->step();
}

void frcsim_set_body_box_geometry(RigidBody_t* body, double dim_x, double dim_y, double dim_z) {
  if (!body) return;
  frcsim::RigidBody::AerodynamicGeometry g;
  g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kBox;
  g.box_dimensions_m = frcsim::Vector3(dim_x, dim_y, dim_z);
  body->setAerodynamicGeometry(g);
}

void frcsim_set_body_sphere_geometry(RigidBody_t* body, double radius) {
  if (!body) return;
  frcsim::RigidBody::AerodynamicGeometry g;
  g.shape = frcsim::RigidBody::AerodynamicGeometry::Shape::kSphere;
  g.radius_m = radius;
  body->setAerodynamicGeometry(g);
}

void frcsim_set_body_position(RigidBody_t* body, double x, double y, double z) {
  if (!body) return;
  body->setPosition(x, y, z);
}

void frcsim_get_gamepiece_state(Gamepiece_t* gamepiece, double* px, double* py, double* pz,
                                double* vx, double* vy, double* vz) {
  if (!gamepiece) return;
  const auto& state = gamepiece->state();
  if (px) *px = state.position_m.x;
  if (py) *py = state.position_m.y;
  if (pz) *pz = state.position_m.z;
  if (vx) *vx = state.velocity_mps.x;
  if (vy) *vy = state.velocity_mps.y;
  if (vz) *vz = state.velocity_mps.z;
}

void frcsim_gamepiece_outtake(Gamepiece_t* gamepiece, double px, double py, double pz,
                              double vx, double vy, double vz) {
  if (!gamepiece) return;
  frcsim::Vector3 pos(px, py, pz);
  frcsim::Vector3 vel(vx, vy, vz);
  gamepiece->outtake(pos, vel);
}

}
