// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

#pragma once

#include "frcsim/gamepiece/ball_physics.hpp"

namespace frcsim {

/**
 * @brief Generic runtime gamepiece abstraction.
 *
 * For now this type is a thin specialization of BallPhysicsSim3D to provide
 * a single top-level gamepiece abstraction that can be extended later for
 * other hitbox families (boxes, custom CAD meshes, etc.).
 */
class Gamepiece : public BallPhysicsSim3D {
 public:
  using BallPhysicsSim3D::BallPhysicsSim3D;
};

}  // namespace frcsim
