// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

#pragma once

#include "frcsim/gamepiece/gamepiece.hpp"
#include "frcsim/gamepiece/season_2026_gamepiece_presets.hpp"

namespace frcsim {

/**
 * @brief Season-2026 convenience gamepiece wrapper.
 *
 * Creates `Gamepiece` instances pre-configured with the 2026 presets.
 */
class Season2026Gamepiece : public Gamepiece {
 public:
  Season2026Gamepiece()
      : Gamepiece(BallGamepiecePresets::season2026BallConfig(),
                  BallGamepiecePresets::season2026BallProperties()) {
    setTypeName("Ball2026");
  }

  Season2026Gamepiece(const Gamepiece::Config& cfg,
                      const Gamepiece::Properties& props)
      : Gamepiece(cfg, props) {
    setTypeName("Ball2026");
  }

  static Gamepiece::Properties defaultProperties() {
    return BallGamepiecePresets::season2026BallProperties();
  }

  static Gamepiece::Config defaultConfig() {
    return BallGamepiecePresets::season2026BallConfig();
  }
};

}  // namespace frcsim
