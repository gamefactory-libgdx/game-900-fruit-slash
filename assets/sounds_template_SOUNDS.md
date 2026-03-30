# SOUNDS.md — Audio Library (CC0 / Public Domain)

All files are CC0. No attribution required. Total: 111 music tracks + 87 SFX.

---

## HOW SOUNDS ARE USED

- **Music** — 3 files are pre-selected per archetype by the pipeline and renamed:
  - `sounds/music/music_menu.ogg`     — menu/title screen (loops)
  - `sounds/music/music_gameplay.ogg` — in-game gameplay (loops)
  - `sounds/music/music_game_over.ogg`— game over (plays once)
- **SFX** — all 87 files are available in `sounds/sfx/`. Use any of them.
  Load with `Sound` class via AssetManager. Never use `Music` for SFX.

---

## MUSIC — 111 tracks in `sounds/music/`

> **Agents: you do not choose music filenames directly.**
> The pipeline selects and renames tracks before you run.
> Just load `music_menu.ogg`, `music_gameplay.ogg`, `music_game_over.ogg`.

### Chiptune / Chip (10 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_chip_running_happy.ogg | Upbeat bouncy chiptune | Endless runners, jumpers |
| music_chip_paradise.ogg | Bright sunny chiptune | Flappy, casual menus |
| music_chip_hearts_parade.ogg | Playful marching chiptune | Crossy road, crowd games |
| music_chip_pixel_paradise.ogg | Energetic pixel chiptune | Casual menus, bubble games |
| music_chip_pixel_prowler.ogg | Punchy driving chiptune | Action menus, runners |
| music_chip_wanderlust.ogg | Dreamy wandering chiptune | Explore, adventure menus |
| music_chip_wanderer.ogg | Gentle flowing chiptune | Glide, adventure |
| music_chip_walk_woods.ogg | Peaceful nature chiptune | Fishing, calm menus |
| music_chip_phantom_party.ogg | Quirky upbeat chiptune | Whack-a-mole, party games |
| music_chip_dark_forest.ogg | Tense dark chiptune | Space menus, thriller |

### 8-bit / Chiptune Variety (7 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_chiptune_8-bit-mayhem.ogg | Frantic fast 8-bit | Intense gameplay, endless |
| music_chiptune_8-bit-perplexion.ogg | Puzzling 8-bit loop | Puzzle gameplay |
| music_chiptune_8-bit-puzzler.ogg | Classic puzzle 8-bit | Puzzle, minesweeper |
| music_chiptune_arcade-puzzler.ogg | Arcade puzzle hybrid | Memory, sliding puzzle |
| music_chiptune_frantic-gameplay.ogg | High-energy frantic | Fruit ninja, geometry dash |
| music_chiptune_insert-quarter.ogg | Classic arcade 8-bit | Retro arcade menu |
| music_chiptune_the-ice-cream-man.ogg | Whimsical lighthearted | Casual, coin-catch |

### Action / Intense (6 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_action_dystopic-mayhem.ogg | Dark cyberpunk intensity | Laser-dodge, sci-fi boss |
| music_action_intense1.ogg | Driving intense rock | Space shooter, laser-dodge |
| music_action_intense2.ogg | Hard electronic action | Geometry dash, gravity-flip |
| music_action_intense3.ogg | Aggressive electronic | Color-switch, intense gameplay |
| music_action_intense4.ogg | Dark pulsing action | Knife-hit, tense gameplay |
| music_action_intense5.ogg | Relentless driving action | Lane-runner, endless |

### Arcade / Casual (5 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_arcade_upbeat1.ogg | Upbeat fun arcade | Doodle-jump, endless-runner |
| music_arcade_upbeat2.ogg | Bright bouncy arcade | Crossy-road, casual |
| music_arcade_upbeat3.ogg | Punchy upbeat arcade | Whack-a-mole, action |
| music_arcade_casual1.ogg | Easygoing arcade | Jetpack, coin-catch |
| music_arcade_casual2.ogg | Light casual arcade | Archery, relaxed gameplay |

### BGM Packs (3 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_bgm_game_action.ogg | Action BGM | Fruit ninja, combat |
| music_bgm_game_adventure.ogg | Adventure BGM | Platformer, gravity-flip |
| music_bgm_game_chill.ogg | Chill looping BGM | Endless-runner, glide |

### Casual / Fun (2 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_casual_fun1.ogg | Light fun loop | Flappy variants, casual |
| music_casual_fun2.ogg | Playful fun loop | Bubble-shooter, balloon-pop |

### Fantasy (5 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_fantasy_cool-adventure.ogg | Upbeat fantasy adventure | Platform-escape, adventure |
| music_fantasy_chamber-of-jewels.ogg | Mysterious jewel cave | Puzzle, treasure games |
| music_fantasy_castle-of-despair.ogg | Dark fantasy castle | Horror, dungeon |
| music_fantasy_goblin-loop.ogg | Quirky goblin march | Comical, tower defense |
| music_fantasy_mysterious-magic.ogg | Ethereal mysterious | Magic, spell games |

### NES / Retro (5 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_nes_mars.ogg | Epic sci-fi NES | Orbit-dash, space menus |
| music_nes_mercury.ogg | Tense sci-fi NES | Asteroid-miner, space |
| music_nes_venus.ogg | Haunting sci-fi NES | Meteor-defense, sci-fi |
| music_nes_boss.ogg | Intense boss NES | Boss battles, climax |
| music_nes_map.ogg | Exploration NES | World map, maze-runner |

### Puzzle (8 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_puzzle_cozy1.ogg | Warm cozy puzzle | Stack, 2048-merge |
| music_puzzle_cozy2.ogg | Gentle cozy puzzle | Memory-match, minesweeper |
| music_puzzle_arcade-fantasy.ogg | Arcade-flavoured puzzle | Piano-tiles, plinko |
| music_puzzle_analog-nostalgia.ogg | Nostalgic analog | Sliding-puzzle, retro puzzle |
| music_puzzle_crystal-caverns.ogg | Crystal underground | Breakout, cave games |
| music_puzzle_game-menu.ogg | Friendly puzzle menu | Puzzle game menus |
| music_puzzle_hypnotic-puzzle.ogg | Hypnotic looping | Abstract puzzle, 2048 |
| music_puzzle_puzzle-dreams.ogg | Dreamy puzzle | Memory, calm puzzle |
| music_puzzle_quirky-game-menu.ogg | Quirky menu | Casual menus, whimsical |

### Racing (1 track)
| File | Mood | Best for |
|------|------|---------|
| music_racing_hyperflight.ogg | Fast hyperspeed racing | Top-down-racer, speed |

### Retro Arcade (7 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_retro_arcade1.ogg | Classic arcade loop | Snake gameplay |
| music_retro_arcade2.ogg | Punchy retro arcade | Breakout gameplay |
| music_retro_arcade3.ogg | Driving retro arcade | Piano-tiles gameplay |
| music_retro_arcade4.ogg | Bouncy retro arcade | Pong gameplay |
| music_retro_arcade5.ogg | Groovy retro arcade | Plinko gameplay |
| music_retro_arcade6.ogg | Funky retro arcade | Pinball gameplay |
| music_retro_arcade7.ogg | Intense retro arcade | Platform-escape gameplay |

### RPG / Cinematic (14 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_rpg_title.ogg | Grand chiptune intro | Main menu, title screen |
| music_rpg_town.ogg | Cheerful cozy town | Casual gameplay, shop screen |
| music_rpg_castle.ogg | Epic majestic castle | Intense gameplay, climax |
| music_rpg_dungeon.ogg | Dark mysterious dungeon | Maze, endless-faller |
| music_rpg_evil.ogg | Dark menacing | Laser-dodge, boss, danger |
| music_rpg_battle1.ogg | Driving RPG battle | Combat, intense gameplay |
| music_rpg_battle2.ogg | Epic RPG battle | Boss, climax battle |
| music_rpg_airship.ogg | Soaring epic airship | Flying, space, glide |
| music_rpg_shrine.ogg | Ethereal mystical | Bonus, secret areas |
| music_rpg_royal.ogg | Stately regal | Leaderboard, high score |
| music_rpg_pavane.ogg | Graceful medieval | Calm puzzle, archery |
| music_rpg_fairy.ogg | Magical light fairy | Casual, children-friendly |
| music_rpg_bittersweet.ogg | Emotional melancholic | Story moment, shop |
| music_rpg_rain.ogg | Melancholic rainy | Game over, sad moment |
| music_rpg_gameover.ogg | RPG-style game over | Game over screen |

### Sci-Fi / Synthwave (3 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_space_synthwave.ogg | Pulsing space synthwave | Orbit-dash, sci-fi gameplay |
| music_scifi_automation.ogg | Mechanical industrial sci-fi | Factory, robot, tech |
| music_scifi_cyber-dream.ogg | Dreamy cyberpunk | Neon, cyber games |

### Misc / Unique (5 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_misc_future-rpg.ogg | Futuristic RPG blend | Sci-fi adventure |
| music_misc_racing-menu.ogg | Racing game menu | Top-down-racer menu |
| music_misc_spooky-island.ogg | Spooky tropical | Horror, Halloween |
| music_misc_urban-future.ogg | Urban electronic future | City runner, cyber |
| music_misc_world-automatons.ogg | Mechanical clockwork | Puzzle, steampunk |

### Named / Hitslab / Branded Packs (11 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_hitslab_upbeat.ogg | Bright upbeat electronic | Geometry-dash, action |
| music_hitslab_game2.ogg | Driving game loop | Jetpack-joyride |
| music_hitslab_retro.ogg | Retro synth | Plinko, retro menu |
| music_hitslab_minecraft.ogg | Block-game vibe | Memory-match, cozy |
| music_slavstar_game1.ogg | Electronic action | Color-switch gameplay |
| music_slavstar_game2.ogg | Tense action | Knife-hit gameplay |
| music_solarflex_retro.ogg | Retro groove | Pinball menu |
| music_mfcc_retro_arcade.ogg | Pure retro arcade | Snake menu |
| music_monda_retro1.ogg | Catchy retro | Breakout menu |
| music_monda_retro2.ogg | Bouncy retro | Pong menu |
| music_monume_arcade1.ogg | Arcade energy | Piano-tiles menu |
| music_monume_arcade2.ogg | Arcade pulse | Minesweeper menu |
| music_monume_casual.ogg | Casual upbeat | Stack menu |
| music_tata_minecraft.ogg | Cozy block-game | Archery, relaxed |
| music_tata_videogame.ogg | Generic game loop | Sliding-puzzle |
| music_jumpingbunny_bg.ogg | Bouncy cartoon | Lane-runner |

### Legacy / Generic (9 tracks)
| File | Mood | Best for |
|------|------|---------|
| music_title.ogg | Upbeat chiptune title | Main menu, any game |
| music_menu.ogg | Chill menu loop | Generic menu fallback |
| music_gameplay.ogg | Action game loop | Generic gameplay fallback |
| music_gameplay_alt.ogg | Alt action loop | Gameplay variety |
| music_gameplay_space.ogg | Space electronic | Space shooters |
| music_game_over.ogg | Tense/sad sting | Game over (all archetypes) |
| music_ambient.ogg | Electronic ambient | Loading, ambient screens |
| music_ambient_relax.ogg | Relaxing ambient | Fishing, calm gameplay |
| music_level1.ogg | Driving chiptune | Level 1-2 gameplay |
| music_level2.ogg | Mid-energy chiptune | Level 3-4 gameplay |
| music_level3.ogg | Intense chiptune | Boss / hard levels |
| music_ending.ogg | Triumphant chiptune | Victory, credits |

---

## SFX — 87 files in `sounds/sfx/`

All 87 SFX are copied to every game. Use whichever fits.

### UI / Navigation (9 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_button_click.ogg | Soft click | Every button tap |
| sfx_button_back.ogg | Back/cancel click | Back button, dismiss |
| sfx_select.ogg | Menu highlight | Cursor moves over option |
| sfx_confirm.ogg | Positive confirm | Confirm, accept, start game |
| sfx_toggle.ogg | Toggle switch | Music/SFX toggle |
| sfx_toggle_alt.ogg | Alt toggle | Secondary toggles |
| sfx_switch.ogg | Option switch | Tab/mode change |
| sfx_close.ogg | Soft close | Panel close, dialog dismiss |
| sfx_error.ogg | Error buzz | Invalid action, wrong answer |

### Gameplay — Core (10 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_coin.ogg | Coin ding | Coin/gem collect |
| sfx_jump.ogg | Jump whoosh | Player jumps |
| sfx_hit.ogg | Dull thud hit | Player takes damage |
| sfx_game_over.ogg | Failure sting | Player dies / game over |
| sfx_level_complete.ogg | Success fanfare | Level/wave complete |
| sfx_power_up.ogg | Rising power-up | Power-up collected |
| sfx_powerup_alt.ogg | Alt power-up | Secondary power-up |
| sfx_powerup_alt2.ogg | Alt power-up 2 | Tertiary power-up |
| sfx_shoot.ogg | Shoot pew | Projectile fired |
| sfx_phase_jump.ogg | Phase jump swoosh | Teleport, phase shift |

### Laser / Weapons (5 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_laser.ogg | Standard laser | Primary laser fire |
| sfx_laser2.ogg | Laser variant | Secondary laser |
| sfx_laser_alt1.ogg | Alt laser 1 | Enemy laser, alt weapon |
| sfx_laser_alt2.ogg | Alt laser 2 | Charged shot, alt weapon |
| sfx_zap.ogg | Electric zap | Electric hit, shock |

### Tonal / Feedback (6 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_high_up.ogg | Rising tone | Score milestone, level up |
| sfx_high_down.ogg | Falling tone | Countdown, timer warning |
| sfx_phaser_up.ogg | Phaser rising | Charge complete, ready |
| sfx_phaser_down.ogg | Phaser falling | Decharge, cooldown |
| sfx_tick.ogg | Clock tick | Timer tick, countdown |
| sfx_bong.ogg | Low bong | Bounce, collision, bell |
| sfx_pluck.ogg | String pluck | Card flip, object place |

### Game-Specific (2 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_snake_eat.ogg | Munch | Snake eats pellet |
| sfx_snake_die.ogg | Death crunch | Snake dies |

### Jingles — Intro / Win / Warp (3 files)
| File | Sound | Use when |
|------|-------|---------|
| sfx_jingle_intro.ogg | Short intro fanfare | Game start, round begin |
| sfx_jingle_win.ogg | Win fanfare | Victory, perfect score |
| sfx_jingle_warp.ogg | Warp swoosh | Level warp, transition |

### Jingles — 8-bit Variations (17 files: sfx_jingle_8bit_00 – 16)
Short 8-bit musical stings. Use for: score milestones, collectibles, combo hits,
special events. Pick any — they are intentionally varied in pitch and energy.
```
sfx_jingle_8bit_00.ogg  sfx_jingle_8bit_06.ogg  sfx_jingle_8bit_12.ogg
sfx_jingle_8bit_01.ogg  sfx_jingle_8bit_07.ogg  sfx_jingle_8bit_13.ogg
sfx_jingle_8bit_02.ogg  sfx_jingle_8bit_08.ogg  sfx_jingle_8bit_14.ogg
sfx_jingle_8bit_03.ogg  sfx_jingle_8bit_09.ogg  sfx_jingle_8bit_15.ogg
sfx_jingle_8bit_04.ogg  sfx_jingle_8bit_10.ogg  sfx_jingle_8bit_16.ogg
sfx_jingle_8bit_05.ogg  sfx_jingle_8bit_11.ogg
```

### Jingles — Hit Variations (17 files: sfx_jingle_hit_00 – 16)
Short impact / hit sounds in 8-bit style. Use for: enemy hits, block breaks,
collisions, taps on targets. Pick any variation for variety.
```
sfx_jingle_hit_00.ogg   sfx_jingle_hit_06.ogg   sfx_jingle_hit_12.ogg
sfx_jingle_hit_01.ogg   sfx_jingle_hit_07.ogg   sfx_jingle_hit_13.ogg
sfx_jingle_hit_02.ogg   sfx_jingle_hit_08.ogg   sfx_jingle_hit_14.ogg
sfx_jingle_hit_03.ogg   sfx_jingle_hit_09.ogg   sfx_jingle_hit_15.ogg
sfx_jingle_hit_04.ogg   sfx_jingle_hit_10.ogg   sfx_jingle_hit_16.ogg
sfx_jingle_hit_05.ogg   sfx_jingle_hit_11.ogg
```

### Jingles — Pizzicato Variations (17 files: sfx_jingle_pizz_00 – 16)
Short plucked-string stings. Use for: card reveals, tile taps, piano-tiles notes,
gentle collectibles, correct answer feedback.
```
sfx_jingle_pizz_00.ogg  sfx_jingle_pizz_06.ogg  sfx_jingle_pizz_12.ogg
sfx_jingle_pizz_01.ogg  sfx_jingle_pizz_07.ogg  sfx_jingle_pizz_13.ogg
sfx_jingle_pizz_02.ogg  sfx_jingle_pizz_08.ogg  sfx_jingle_pizz_14.ogg
sfx_jingle_pizz_03.ogg  sfx_jingle_pizz_09.ogg  sfx_jingle_pizz_15.ogg
sfx_jingle_pizz_04.ogg  sfx_jingle_pizz_10.ogg  sfx_jingle_pizz_16.ogg
sfx_jingle_pizz_05.ogg  sfx_jingle_pizz_11.ogg
```

---

## Quick reference — SFX by game type

| Game type | Recommended SFX |
|-----------|----------------|
| Any button | sfx_button_click, sfx_button_back |
| Runner / jumper | sfx_jump, sfx_coin, sfx_hit, sfx_power_up |
| Shooter | sfx_shoot, sfx_laser, sfx_hit, sfx_laser_alt1 |
| Puzzle / match | sfx_pluck, sfx_jingle_pizz_*, sfx_confirm, sfx_error |
| Rhythm / tiles | sfx_jingle_pizz_* (one per column), sfx_tick |
| Snake | sfx_snake_eat, sfx_snake_die, sfx_coin |
| Breakout / ball | sfx_bong, sfx_hit, sfx_jingle_8bit_*, sfx_level_complete |
| Merge / 2048 | sfx_jingle_8bit_*, sfx_high_up, sfx_coin |
| Card / memory | sfx_pluck, sfx_jingle_pizz_*, sfx_confirm, sfx_error |
| Gravity flip | sfx_phase_jump, sfx_phaser_up, sfx_phaser_down |
| Space | sfx_laser, sfx_zap, sfx_shoot, sfx_power_up |
| Whack / tap target | sfx_jingle_hit_*, sfx_bong, sfx_hit |
| Level complete | sfx_level_complete, sfx_jingle_win, sfx_high_up |
